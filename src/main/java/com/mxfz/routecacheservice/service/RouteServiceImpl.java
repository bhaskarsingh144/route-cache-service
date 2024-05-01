package com.mxfz.routecacheservice.service;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.mxfz.routecacheservice.model.Leg;
import com.mxfz.routecacheservice.model.Route;
import com.mxfz.routecacheservice.model.RouteDetails;
import com.mxfz.routecacheservice.model.Step;
import com.mxfz.routecacheservice.model.pincode.Pincode;
import com.mxfz.routecacheservice.repository.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/*
    https://developers.google.com/maps/documentation/directions/get-directions#DirectionsResponses
*/

/**
 * <p>
 * Route: A route represents the path between a starting point (origin) and a destination.
 * It includes information such as distance, duration, and steps (turn-by-turn directions)
 * to navigate from the origin to the destination.
 * <p>
 * Step: A step is an individual segment of a route that provides specific instructions on how to move from
 * one location to another. For example, "Turn left onto Main Street" or "Continue straight for 500 meters."
 * Each step typically corresponds to a change in direction or action required to follow the route.
 * <p>
 * Instruction: An instruction is the specific guidance provided to the user for navigating a step in the route.
 * It usually describes a maneuver or action to take, such as turning, merging, or continuing straight.
 * Instructions are often presented in a human-readable format to make navigation easier for users.
 * <p>
 * Distance: The distance is the length of the route segment, typically measured in meters or kilometers.
 * It represents the physical distance that needs to be traveled to complete the step or the entire route.
 */

@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    Map<String, Route> routeCache = new ConcurrentHashMap<>();

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RouteRepository routeRepository;
    private final MongoOperations mongoOperations;
    private final PincodeService pincodeService;

    public RouteServiceImpl(RouteRepository routeRepository, MongoOperations mongoOperations,
                            PincodeService pincodeService) {
        this.routeRepository = routeRepository;
        this.mongoOperations = mongoOperations;
        this.pincodeService = pincodeService;
    }

    @Override
    public Route fetchRoute(String source, String destination) {
        if (source.replaceAll("\\s", "").equals(destination.replaceAll("\\s", ""))){
            throw new IllegalArgumentException("Pincodes cannot be same");
        }
        validatePincodes(List.of(source, destination));
        String cacheKey = generateCacheKey(source, destination);
        Route cachedRoute = routeCache.get(cacheKey);
        // if present in cache return from it
        if (cachedRoute != null) {
            log.info("Fetching Route from cache");
            return cachedRoute;
        }
        // if present in db return from it
        Route existingRoute = findBySourceAndDestination(source, destination);
        if (existingRoute != null) {
            log.info("Fetching Route from db");
            routeCache.put(cacheKey, existingRoute);
            return existingRoute;
        } else {
            // if not present in db/cache fetch form Google API return from it
            log.info("Fetching Route from externalAPI");
            DirectionsRoute fetchedRoute = fetchRouteFromExternalAPI(source, destination);
            Route route = mapDirectionsRouteToRoute(fetchedRoute, source, destination);
            route.setId(UUID.randomUUID().toString());
            // Cache the response for future requests
            routeCache.put(cacheKey, route);
            CompletableFuture.runAsync(() -> handlePincodeSave(fetchedRoute, source, destination));// persisting pincodes asynchronously
            // persist in database
            return routeRepository.save(route);
        }
    }


    @Override
    public void clearCache() {
        routeCache.clear();
    }

    private DirectionsRoute fetchRouteFromExternalAPI(String source, String destination) {
        byte[] bytes = Base64.getDecoder().decode(apiKey);
        String key = new String(bytes, StandardCharsets.UTF_8);
        DirectionsResult directionResult = null;
        try {
            directionResult = DirectionsApi.newRequest(new GeoApiContext.Builder().apiKey(key).build())
                    .origin(source)
                    .destination(destination)
                    .mode(TravelMode.DRIVING)
                    .await();
        } catch (ApiException | IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        DirectionsRoute[] routes = directionResult.routes;
        DirectionsRoute route = routes[0]; // Assuming there is only one route
        return route;
    }

    private static Route mapDirectionsRouteToRoute(DirectionsRoute directionsRoute, String source,
                                                   String destination) {
        Route route = new Route();
        route.setSource(source);
        route.setDestination(destination);

        int totalDurationSeconds = 0;
        double totalDistanceMeters = 0.0;

        List<Leg> legs = new ArrayList<>();

        for (DirectionsLeg leg : directionsRoute.legs) {
            totalDurationSeconds += leg.duration.inSeconds;
            totalDistanceMeters += leg.distance.inMeters;
            List<Step> steps = mapSteps(leg.steps);
            legs.add(new Leg(steps, leg.distance, leg.duration, leg.durationInTraffic, leg.arrivalTime, leg.departureTime,
                    leg.startLocation, leg.endLocation, leg.startAddress, leg.endAddress));
        }

        route.setDistance(String.valueOf(totalDistanceMeters));
        route.setDuration(String.valueOf(totalDurationSeconds));
        RouteDetails routeDetails = new RouteDetails();

        routeDetails.setLegs(legs);
//        routeDetails.setBounds(directionsRoute.bounds);
        routeDetails.setWaypointOrder(directionsRoute.waypointOrder);
        routeDetails.setCopyrights(directionsRoute.copyrights);
        routeDetails.setFare(directionsRoute.fare);
        routeDetails.setWarnings(directionsRoute.warnings);
        route.setRouteDetails(routeDetails);
        return route;
    }

    private static List<Step> mapSteps(DirectionsStep[] steps) {
        if (steps == null || steps.length == 0) {
            return Collections.emptyList();
        }
        List<Step> stepList = new ArrayList<>(steps.length);
        for (DirectionsStep directionsStep : steps) {
            Step step = new Step(
                    directionsStep.htmlInstructions,
                    directionsStep.distance,
                    directionsStep.duration,
                    directionsStep.startLocation,
                    directionsStep.endLocation,
                    directionsStep.steps == null ? Collections.emptyList() : mapSteps(directionsStep.steps),
                    directionsStep.travelMode,
                    directionsStep.transitDetails);
            stepList.add(step);
        }
        return stepList;
    }

    private String generateCacheKey(String source, String destination) {
        return "route_" + source + "_" + destination;
    }

    private Route findBySourceAndDestination(String fromPinCode, String toPinCode) {
        Criteria criteria = Criteria.where("source").is(fromPinCode)
                .and("destination").is(toPinCode);
        Query query = new Query(criteria);
        return mongoOperations.findOne(query, Route.class);
    }

    private void handlePincodeSave(DirectionsRoute fetchedRoute, String source, String destination) {

        List<Pincode> pinCodes = extractPincodeInfo(fetchedRoute, source, destination);
        pinCodes.forEach(pincode -> {
            Pincode fetchedPincode = pincodeService.findByPincode(pincode.getPincode());
            if (fetchedPincode == null) {
                pincode.setId(UUID.randomUUID().toString());
                pincodeService.savePincode(pincode);
            }
        });
    }

    private List<Pincode> extractPincodeInfo(DirectionsRoute route, String source, String destination) {

        var firstLeg = route.legs[0];
        var lastLeg = route.legs[route.legs.length - 1];
        double originLatitude = firstLeg.startLocation.lat;
        double originLongitude = firstLeg.startLocation.lng;
        double destinationLatitude = lastLeg.endLocation.lat;
        double destinationLongitude = lastLeg.endLocation.lng;
        String address1 = firstLeg.startAddress;
        String address2 = lastLeg.endAddress;
        return List.of(new Pincode(source, originLatitude, originLongitude, address1, null),
                new Pincode(destination, destinationLatitude, destinationLongitude, address2, null));
    }

    private void validatePincodes(List<String> pincodes) {
        if (pincodes == null || pincodes.isEmpty()) {
            throw new IllegalArgumentException("Pincode list cannot be null or empty");
        }
        String regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$";
        for (String pincode : pincodes) {
            if (pincode == null || pincode.isEmpty()) {
                throw new IllegalArgumentException("Individual pincode cannot be null or empty");
            }
            if (!pincode.matches(regex)) {
                throw new IllegalArgumentException("Invalid pincode format: " + pincode);
            }
        }
    }
}