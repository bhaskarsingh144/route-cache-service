package com.mxfz.routecacheservice.model;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class Leg {

    private List<Step> steps;
    private Distance distance;
    private Duration duration;
    private Duration durationInTraffic;
    private ZonedDateTime arrivalTime;
    private ZonedDateTime departureTime;
    private LatLng startLocation;
    private LatLng endLocation;
    private String startAddress;
    private String endAddress;

    public Leg(List<Step> steps, Distance distance, Duration duration, Duration durationInTraffic,
               ZonedDateTime arrivalTime, ZonedDateTime departureTime, LatLng startLocation,
               LatLng endLocation, String startAddress, String endAddress) {
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.durationInTraffic = durationInTraffic;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }
}
