package com.mxfz.routecacheservice.model;

import com.google.maps.model.Fare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDetails {

    private String summary;
    private List<Leg> legs;
    private int[] waypointOrder;
    //  private EncodedPolyline overviewPolyline;
//    private Bounds bounds;
    private String copyrights;
    private Fare fare;
    private String[] warnings;
}
