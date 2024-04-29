package com.mxfz.routecacheservice.controller;

import com.mxfz.routecacheservice.model.pincode.Pincode;
import com.mxfz.routecacheservice.service.PincodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pincode")
public class PincodeController {


    private final PincodeService pincodeService;

    public PincodeController(PincodeService pincodeService) {
        this.pincodeService = pincodeService;
    }

    @PostMapping("/save")
    public ResponseEntity<Pincode> savePincode(@RequestBody Pincode pincode) {
        Pincode savedPincode = pincodeService.savePincode(pincode);
        return ResponseEntity.ok(savedPincode);
    }

    @GetMapping("/{pincode}")
    public ResponseEntity<Pincode> getPincodeByPincode(@PathVariable String pincode) {
        Pincode retrievedPincode = pincodeService.findByPincode(pincode);
        if (retrievedPincode == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(retrievedPincode);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Pincode>> getAllPincodes() {
        List<Pincode> pincodes = pincodeService.getAllPincodes();
        return ResponseEntity.ok(pincodes);
    }

    @DeleteMapping("/delete/{pincode}")
    public ResponseEntity<Void> deletePincode(@PathVariable String pincode) {
        pincodeService.deletePincode(pincode);
        return ResponseEntity.noContent().build();
    }
}
