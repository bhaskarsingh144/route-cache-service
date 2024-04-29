package com.mxfz.routecacheservice.model;

import com.google.maps.model.*;
import lombok.Data;

import java.util.List;

@Data
public class Step {

    private String htmlInstructions;
    private Distance distance;
    private Duration duration;
    private LatLng startLocation;
    private LatLng endLocation;
    private List<Step> steps;
//    private EncodedPolyline polyline;
    private TravelMode travelMode;
    private TransitDetails transitDetails;

    public Step(String htmlInstructions, Distance distance, Duration duration, LatLng startLocation,
                LatLng endLocation, List<Step> steps, /*EncodedPolyline polyline,*/
                TravelMode travelMode, TransitDetails transitDetails) {
        this.htmlInstructions = htmlInstructions;
        this.distance = distance;
        this.duration = duration;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.steps = steps;
//        this.polyline = polyline;
        this.travelMode = travelMode;
        this.transitDetails = transitDetails;
    }
}
