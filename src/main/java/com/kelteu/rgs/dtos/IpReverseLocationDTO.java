package com.kelteu.rgs.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IpReverseLocationDTO {
    Double latitude;
    Double longitude;
    String countryCode;
    String city;
    String region;
    String regionName;
}