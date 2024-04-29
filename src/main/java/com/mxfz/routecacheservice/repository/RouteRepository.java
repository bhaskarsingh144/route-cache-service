package com.mxfz.routecacheservice.repository;

import com.mxfz.routecacheservice.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    Route findBySourceAndDestination(String source, String destination);
}