package com.mxfz.routecacheservice.service;

import com.mxfz.routecacheservice.model.Route;
import com.mxfz.routecacheservice.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class RouteServiceTest {

    @InjectMocks
    private RouteServiceImpl routeService;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private PincodeService pincodeService;
    @Mock
    private MongoOperations mongoOperations;

    @Test
    void testFetchRoute_SUCCESS() {

        String source = "141106";
        String destination = "560023";

        Route actualRoute = routeService.fetchRoute(source, destination);

        assertEquals(source, actualRoute.getSource());
        assertEquals(destination, actualRoute.getDestination());
        assertEquals("2488874", actualRoute.getDistance());
        assertNotNull(actualRoute.getId());
        assertNotNull(actualRoute.getId());
    }

    @Test
    void testFetchRoute_FAIURE1() {

//       INVALID_PINCODE

        String source = "14110";
        String destination = "56023";

        Route actualRoute = routeService.fetchRoute(source, destination);

        assertEquals(source, actualRoute.getSource());
        assertEquals(destination, actualRoute.getDestination());
        assertEquals("2488874", actualRoute.getDistance());
        assertNotNull(actualRoute.getId());
        assertNotNull(actualRoute.getId());
    }

    @Test
    void testFetchRoute_FAIURE2() {

//       source = destination

        String source = "141106";
        String destination = "141106";

        Route actualRoute = routeService.fetchRoute(source, destination);

        assertEquals(source, actualRoute.getSource());
        assertEquals(destination, actualRoute.getDestination());
        assertEquals("2488874", actualRoute.getDistance());
        assertNotNull(actualRoute.getId());
        assertNotNull(actualRoute.getId());
    }

//    @Test
//    void testFetchRouteFromCache() {
//        // Setup (prepare mocked data if needed)
//        String source = "source";
//        String destination = "destination";
//        Route expectedRoute = createDummyRoute(source, destination);
//        routeCache.put(generateCacheKey(source, destination), expectedRoute);
//
//        Route actualRoute = routeService.fetchRoute(source, destination);
//
//        // Assertions
//        assertEquals(expectedRoute, actualRoute);
//        verifyNoInteractions(routeRepository); // Verify no interaction with repository
//        verifyNoInteractions(mongoOperations); // Verify no interaction with mocked MongoOperations
//    }
//
//    @Test
//    public void testFetchRouteFromDB() {
//        // Setup (prepare mocked data if needed)
//        String source = "source";
//        String destination = "destination";
//        Route expectedRoute = createDummyRoute(source, destination);
//        when(mongoOperations.findOne(any(Query.class), eq(Route.class))).thenReturn(expectedRoute);
//
//        // Test
//        Route actualRoute = routeService.fetchRoute(source, destination);
//
//        // Assertions
//        assertEquals(expectedRoute, actualRoute);
//        verify(routeRepository, times(1)).findBySourceAndDestination(source, destination); // Verify interaction with repository
//        verify(mongoOperations, times(1)).findOne(any(Query.class), eq(Route.class)); // Verify interaction with mocked MongoOperations
//    }
//
//    @Test
//    public void testFetchRouteFromExternalAPI() throws ApiException, IOException, InterruptedException {
//        // Setup (mock external API call if needed)
//        String source = "source";
//        String destination = "destination";
//        DirectionsRoute directionsRoute = mock(DirectionsRoute.class);
//        when(routeService.fetchRouteFromExternalAPI(source, destination)).thenReturn(directionsRoute);
//
//        // Test
//        routeService.fetchRoute(source, destination);
//
//        // Assertions
//        verify(routeRepository, times(1)).save(any(Route.class)); // Verify interaction with repository to save retrieved route
//        verify(pincodeService, times(1)).savePincode(any(Pincode.class)); // Verify interaction with PincodeService to save pincodes
//    }
//
//
//
//    private Route createDummyRoute(String source, String destination) {
//        // Implement logic to create a dummy Route object with source, destination, etc.
//        // You can use builders or setters to create the object
//        return new Route();
//    }
//
//    private String generateCacheKey(String source, String destination) {
//        // Implement logic to generate the cache key based on source and destination
//        return "route_" + source + "_" + destination;
//    }
}
