package com.mxfz.routecacheservice.model.pincode;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("pincodes")
public class Pincode {

    @Id
    private String id;
    private String pincode;
    private double latitude;
    private double longitude;
    private String address;
    private Polygon polygon;  // Assuming Polygon represents a geographical area

    public Pincode(String pincode, double latitude, double longitude,
                   String address, Polygon polygon) {
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.polygon = polygon;
    }

}
