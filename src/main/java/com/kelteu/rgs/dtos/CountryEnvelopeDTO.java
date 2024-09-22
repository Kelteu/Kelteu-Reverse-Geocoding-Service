package com.kelteu.rgs.dtos;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CountryEnvelopeDTO {
    String iso;
    Point centroid;
    Geometry envelope;
}
