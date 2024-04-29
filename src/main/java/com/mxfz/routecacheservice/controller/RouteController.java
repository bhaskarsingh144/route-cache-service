package com.mxfz.routecacheservice.controller;

import com.mxfz.routecacheservice.model.Route;
import com.mxfz.routecacheservice.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/fetch-one-by-src-dest")
    public ResponseEntity<Route> getRouteBetweenPinCodes(@RequestParam("source") String source,
                                                         @RequestParam("destination") String destination) {
        Route route = routeService.fetchRoute(source, destination);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/evict-route-cache")
    public ResponseEntity<Map<String, Object>> getRouteBetweenPinCodes() {
        routeService.clearCache();
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Cache cleared"));
    }
}
