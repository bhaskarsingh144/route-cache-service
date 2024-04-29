package com.mxfz.routecacheservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Document(collection = "routes")
public class Route {

    @Id
    private String id;
    private String source;
    private String destination;
    private String distance;
    private String duration;
    private RouteDetails routeDetails;
}