package com.mxfz.routecacheservice.repository;

import com.mxfz.routecacheservice.model.pincode.Pincode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PincodeRepository extends MongoRepository<Pincode, String> {

    Pincode findByPincode(String pincode);
}