package com.kelteu.rgs.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kelteu.rgs.dtos.CountryInfoDTO;
import com.kelteu.rgs.dtos.IpReverseLocationDTO;
import com.kelteu.rgs.services.ReverseLocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/reverselocation")
public class ReverseLocationController {
    @Autowired
    ReverseLocationService reverseLocationService;
    
    @Operation(summary = "Get country by latitude/longitude location.", description = "Get country by latitude/longitude location.", tags = "Reverse Location")
    @GetMapping("/latlng")
    public CountryInfoDTO getCountryByLatLngLocation(@RequestParam(required = true) Double latitude,
            @RequestParam(required = true) Double longitude) throws InterruptedException, IOException {
        return reverseLocationService.getCountry(latitude, longitude);
    }
    
    @Operation(summary = "Get country by ip location.", description = "Get country by ip location.", tags = "Reverse Location")
    @GetMapping("/ip")
    public IpReverseLocationDTO getLocationByIp(@Parameter(hidden = true) @RequestHeader(required = false, value ="dlat") Double dlat,
            @Parameter(hidden = true) @RequestHeader(required = false, value ="dlng") Double dlng,
            @Parameter(hidden = true) @RequestHeader(required = false, value ="dCountryCode") String dCountryCode,
            @Parameter(hidden = true) @RequestHeader(required = false, value ="dCity") String dCity,
            @Parameter(hidden = true) @RequestHeader(required = false, value ="dRegion") String dRegion,
            @Parameter(hidden = true) @RequestHeader(required = false, value ="dRegionName") String dRegionName) throws InterruptedException, IOException {
        return reverseLocationService.getLocationByIp(dlat, dlng, dCountryCode, dCity, dRegion, dRegionName);
    }
}
