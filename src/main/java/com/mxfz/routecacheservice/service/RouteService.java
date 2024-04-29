package com.mxfz.routecacheservice.service;

import com.mxfz.routecacheservice.model.Route;

public interface RouteService {

    Route fetchRoute(String origin, String destination);

    void clearCache();
}
