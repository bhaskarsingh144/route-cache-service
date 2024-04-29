package com.mxfz.routecacheservice.service;

import com.mxfz.routecacheservice.model.pincode.Pincode;

import java.util.List;

public interface PincodeService {

    Pincode savePincode(Pincode pincode);

    Pincode findByPincode(String pincode);

    List<Pincode> getAllPincodes();

    void deletePincode(String pincode);
}
