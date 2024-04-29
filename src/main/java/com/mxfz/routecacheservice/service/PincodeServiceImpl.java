package com.mxfz.routecacheservice.service;

import com.mxfz.routecacheservice.model.pincode.Pincode;
import com.mxfz.routecacheservice.repository.PincodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PincodeServiceImpl implements PincodeService {


    private final PincodeRepository pincodeRepository;

    public PincodeServiceImpl(PincodeRepository pincodeRepository) {
        this.pincodeRepository = pincodeRepository;
    }

    @Override
    public Pincode savePincode(Pincode pincode) {
        return pincodeRepository.save(pincode);
    }

    @Override
    public Pincode findByPincode(String pincode) {
        return pincodeRepository.findByPincode(pincode);
    }

    @Override
    public List<Pincode> getAllPincodes() {
        return pincodeRepository.findAll();
    }

    @Override
    public void deletePincode(String pincode) {
        pincodeRepository.deleteById(pincode);
    }
}
