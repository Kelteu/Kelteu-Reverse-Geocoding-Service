package com.kelteu.rgs.dtos;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CountryInfoDTO {
    String iso2;
    String iso3;
    String isoNumericFIPS;
    String country;
    String capital;
    Double areaInSKM;
    Double population;
    String continent;
    String tld;
    String currencyCode;
    String currencyName;
    String phone;
    String postalCodeFormat;
    String postalCodeRegex;
    List<String> languages;
    String geonameid;
    List<String> neighbours;
    String equivalentFipsCode;
    Boolean hasFiscalDivisions;
    String stateName;
    
    public CountryInfoDTO(String[] splitLine) {
        iso2 = splitLine[0];
        
        if("CA".equals(iso2) || "US".equals(iso2)) {
            hasFiscalDivisions = true;
        }
        
        try {
            iso3 = splitLine[1];
        } catch (Exception e) {
            // hide errors
        }
        try {
            isoNumericFIPS = splitLine[2];
        } catch (Exception e) {
            // hide errors
        }
        try {
            country = splitLine[3];
        } catch (Exception e) {
            // hide errors
        }
        try {
            capital = splitLine[4];
        } catch (Exception e) {
            // hide errors
        }
        try {
            areaInSKM = Double.valueOf(splitLine[5]);
        } catch (Exception e) {
            // hide errors
        }
        try {
            population = Double.valueOf(splitLine[6]);
        } catch (Exception e) {
            // hide errors
        }
        try {
            continent = splitLine[7];
        } catch (Exception e) {
            // hide errors
        }
        try {
            tld = splitLine[8];
        } catch (Exception e) {
            // hide errors
        }
        try {
            currencyCode = splitLine[9];
        } catch (Exception e) {
            // hide errors
        }
        try {
            currencyName = splitLine[10];
        } catch (Exception e) {
            // hide errors
        }
        try {
            phone = splitLine[11];
        } catch (Exception e) {
            // hide errors
        }
        try {
            postalCodeFormat = splitLine[12];
        } catch (Exception e) {
            // hide errors
        }
        try {
            postalCodeRegex = splitLine[13];
        } catch (Exception e) {
            // hide errors
        }
        try {
            languages = Arrays.asList(splitLine[14].split(","));
        } catch (Exception e) {
            // hide errors
        }
        try {
            geonameid = splitLine[15];
        } catch (Exception e) {
            // hide errors
        }
        try {
            neighbours = Arrays.asList(splitLine[16].split(","));
        } catch (Exception e) {
            // hide errors
        }
        try {
            equivalentFipsCode = splitLine[17];
        } catch (Exception e) {
            // hide errors
        }
    }
}

    
    
    
    
    
