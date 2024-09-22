package com.kelteu.rgs.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kelteu.rgs.configs.ApplicationConfigs;
import com.kelteu.rgs.dtos.CountryEnvelopeDTO;
import com.kelteu.rgs.dtos.CountryInfoDTO;
import com.kelteu.rgs.dtos.IpReverseLocationDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReverseLocationService {
    private Map<String, String> geonameidToIsoMap = null;
    private Map<String, CountryInfoDTO> isoToCcountryInfoMap = null;
    private Map<String, Geometry> isoToPoligonMap = null;
    private Map<String, Geometry> canadaIsoToPoligonMap = null;
    private Map<String, Geometry> usIsoToPoligonMap = null;
    private Map<Integer, List<CountryEnvelopeDTO>> sectionsMap = null;
    List<CountryEnvelopeDTO> canadaMap = null;
    List<CountryEnvelopeDTO> usMap = null;
    private static GeometryFactory gf = new GeometryFactory();
    
    public static final double PI = 3.14159265;
    public static final double TWOPI = 2 * PI;
    public static final double EARTH_RADIUS_IN_METERS = 6378137;
    public static final double EARTH_CIRCUMFERENCE_IN_METERS = 2*EARTH_RADIUS_IN_METERS*PI;

    @Autowired
    ApplicationConfigs applicationConfigs;
    
    private Map<String, String> getGeonameidToIsoMap() throws IOException {
        if (geonameidToIsoMap == null) {
            initcountryInfoMaps();
        }
        return geonameidToIsoMap;
    }
    
    private Map<String, CountryInfoDTO> getIsoToCcountryInfoMap() throws IOException {
        if (isoToCcountryInfoMap == null) {
            initcountryInfoMaps();
        }
        return isoToCcountryInfoMap;
    }
    
    private void initcountryInfoMaps() throws IOException {
        geonameidToIsoMap = new HashMap<>();
        isoToCcountryInfoMap = new HashMap<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("geojson/countryInfo.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while(reader.ready()) {
            String line = reader.readLine();
            if (!line.startsWith("#")) {
                String[] splitLine = line.split("\t");
                geonameidToIsoMap.put(splitLine[16], splitLine[0]);
                isoToCcountryInfoMap.put(splitLine[0], new CountryInfoDTO(splitLine));
                
            }
        }
    }
    
    private Map<String, Geometry> getIsoToGeonameidMap() throws IOException {
        if (isoToPoligonMap == null) {
            initMaps();
        }
        return isoToPoligonMap;
    }
    
    private Map<Integer, List<CountryEnvelopeDTO>> getSectionsMap() throws IOException {
        if (sectionsMap == null) {
            initMaps();
        }
        return sectionsMap;
    }
    
    private Map<String, Geometry> getCanadaIsoToGeonameidMap() throws IOException {
        if (canadaIsoToPoligonMap == null) {
            initCanadaMap();
        }
        return canadaIsoToPoligonMap;
    }
    
    private List<CountryEnvelopeDTO> getCanadaMap() throws IOException {
        if (canadaMap == null) {
            initCanadaMap();
        }
        return canadaMap;
    }
    
    private Map<String, Geometry> getUSIsoToGeonameidMap() throws IOException {
        if (usIsoToPoligonMap == null) {
            initUsMap();
        }
        return usIsoToPoligonMap;
    }
    
    private List<CountryEnvelopeDTO> getUSMap() throws IOException {
        if (usMap == null) {
            initUsMap();
        }
        return usMap;
    }
    
    private void initCanadaMap() throws IOException {
        canadaIsoToPoligonMap = new HashMap<>();
        canadaMap = new ArrayList<>();
        initMap(canadaIsoToPoligonMap, canadaMap, "geojson/ca.json", "prov_name_en");
    }
    
    private void initUsMap() throws IOException {
        usIsoToPoligonMap = new HashMap<>();
        usMap = new ArrayList<>();
        initMap(usIsoToPoligonMap, usMap, "geojson/us.json", "NAME");
    }
    
    private void initMap(Map<String, Geometry> isoToPoligonMap, List<CountryEnvelopeDTO> map, String location, String nameProperty) throws IOException {
        FeatureJSON fjson = new FeatureJSON();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location);
        FeatureCollection provincesCollection = fjson.readFeatureCollection(in);
        try (SimpleFeatureIterator iterator = (SimpleFeatureIterator) provincesCollection.features()) {
            while (iterator.hasNext()) {
                SimpleFeature provinceFeature = iterator.next();
                Property provName = provinceFeature.getProperty(nameProperty);
                String name = (String) provName.getValue();
                
                Geometry province = (Geometry) provinceFeature.getDefaultGeometry();
                CountryEnvelopeDTO provinceEnvelope = new CountryEnvelopeDTO();
                provinceEnvelope.setEnvelope(province.getEnvelope());
                provinceEnvelope.setCentroid(province.getCentroid());
                provinceEnvelope.setIso(name);
                map.add(provinceEnvelope);
                isoToPoligonMap.put(name, province);
            }
        }
    }
    
    private void initMaps() throws IOException {
        GeometryJSON gjson = new GeometryJSON();
        isoToPoligonMap = new HashMap<>();
        sectionsMap = new HashMap<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("geojson/shapes_all_low.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while(reader.ready()) {
            String line = reader.readLine();
            if (!line.startsWith("#")) {
                String[] splitLine = line.split("\t");
                String json = splitLine[1];
                Reader lineReader = new StringReader(json);
                Geometry geometry = null;
                geometry = gjson.readMultiPolygon(lineReader);

                String iso = getGeonameidToIsoMap().get(splitLine[0]);
                isoToPoligonMap.put(iso, geometry);
                
                CountryEnvelopeDTO countryEnvelope = getCountryEnvelope(geometry, iso);
                Set<Integer> envelopeSections = getSections(countryEnvelope.getEnvelope());
                for (int i : envelopeSections) {
                    if (sectionsMap.containsKey(i)) {
                        sectionsMap.get(i).add(countryEnvelope);
                    } else {
                        List<CountryEnvelopeDTO> envelopesList = new ArrayList<>();
                        envelopesList.add(countryEnvelope);
                        sectionsMap.put(i, envelopesList);
                    }
                }
            }
        }
    }
    
    
    private CountryEnvelopeDTO getCountryEnvelope(Geometry geometry, String iso) {
        CountryEnvelopeDTO result = new CountryEnvelopeDTO();
        
        Point centroid = geometry.getCentroid();
        Geometry envelope = geometry.getEnvelope();
        
        result.setCentroid(centroid);
        result.setEnvelope(envelope);
        result.setIso(iso);
        return result;
    }
    
    private Set<Integer> getSections(Geometry envelope) {
        Set<Integer> result = new LinkedHashSet<>();
        Coordinate[] coordinates = envelope.getCoordinates();
        
        for (int i=0; i< coordinates.length;i++) {
            result.add(getCoordinateSection(coordinates[i]));
        }
        
        return result;
    }
    
    private Integer getCoordinateSection(Coordinate coordinate) {
        if (coordinate.x >= 0 && coordinate.y >= 0) {
            return 0;
        } else if (coordinate.x >= 0 && coordinate.y < 0) {
            return 1;
        } else if (coordinate.x < 0 && coordinate.y < 0) {
            return 2;
        } else {
            return 3;
        } 
    }
    
    public IpReverseLocationDTO getLocationByIp(Double latitude, Double longitude, String countryCode, String city, String region, String regionName) throws IOException {
        IpReverseLocationDTO result = new IpReverseLocationDTO();
        result.setCity(city);
        result.setCountryCode(countryCode);
        result.setLatitude(latitude);
        result.setLongitude(longitude);
        result.setRegion(regionName);
        result.setRegionName(regionName);
        return result;
    }
    
    public CountryInfoDTO getCountry(Double latitude, Double longitude) throws IOException {
        if (latitude != null && longitude != null) {
            Map<Integer, List<CountryEnvelopeDTO>> envelopesMap =  getSectionsMap();
            Coordinate coordinate = new Coordinate(longitude, latitude);
            Integer pointSection = getCoordinateSection(coordinate);
            Point point = gf.createPoint(coordinate);
            List<CountryEnvelopeDTO> sectionEnvelopes = envelopesMap.get(pointSection);
            String countryIso = getLocationCountryIso(sectionEnvelopes, getIsoToGeonameidMap(), point, latitude, longitude);
            if (StringUtils.isNoneBlank(countryIso)) {
                CountryInfoDTO country =  getCountryForIso(countryIso);
                if (country != null && country.getHasFiscalDivisions()) {
                    String stateName = null;
                    if ("CA".equals(countryIso)) {
                        stateName = getLocationCountryIso(getCanadaMap(), getCanadaIsoToGeonameidMap(), point, latitude, longitude);
                    } else if ("US".equals(countryIso)) {
                        stateName = getLocationCountryIso(getUSMap(), getUSIsoToGeonameidMap(), point, latitude, longitude);
                    }
                    if (StringUtils.isNoneBlank(stateName)) {
                        country.setStateName(stateName);
                    }
                }
                return country;
            }
        }
        return null;
    }
    
    private CountryInfoDTO getCountryForIso(String iso2) throws IOException {
        return getIsoToCcountryInfoMap().get(iso2);
    }
    
    private String getLocationCountryIso(List<CountryEnvelopeDTO> sectionEnvelopes, Map<String, Geometry> isoToPoligonMap, Point point, double latitude, double longitude) throws IOException {
        String countryIso = null;
        List<CountryEnvelopeDTO> reducedEnvelopes = new ArrayList<>();
        for (int i=0; i < sectionEnvelopes.size();i++) {
            if (sectionEnvelopes.get(i).getEnvelope().contains(point)) {
                reducedEnvelopes.add(sectionEnvelopes.get(i));
            }
        }
        
        double minDistance = 370;
        if (reducedEnvelopes.size() == 1) {
            countryIso = reducedEnvelopes.get(0).getIso();
        } else if (reducedEnvelopes.size() > 1) {
            outerloop:
            for (int i=0;i<reducedEnvelopes.size();i++) {
                Geometry polygon = isoToPoligonMap.get(reducedEnvelopes.get(i).getIso());
                if (polygon.getGeometryType().equals("MultiPolygon")) {
                    MultiPolygon multiPolygons = (MultiPolygon) polygon;
                    for (int j = 0; j < multiPolygons.getNumGeometries(); j++) {
                        Polygon tempPolygon = (Polygon) multiPolygons.getGeometryN(j);
                        Coordinate[] coordinates = tempPolygon.getCoordinates();
                        if (coordinateIsInsidePolygon(latitude, longitude, coordinates)) {
                            countryIso = reducedEnvelopes.get(i).getIso();
                            break outerloop;
                        }
                    }
                    double distance = multiPolygons.distance(point);
                    if (distance <= applicationConfigs.getReverseCountryMaxDistanceThresholdInDegrees() && minDistance > distance) {
                        minDistance = distance;
                        countryIso = reducedEnvelopes.get(i).getIso();
                    }
                } else {
                    Coordinate[] coordinates = polygon.getCoordinates();
                    if (coordinateIsInsidePolygon(latitude, longitude, coordinates)) {
                        countryIso = reducedEnvelopes.get(i).getIso();
                        break;
                    }
                    double distance = polygon.distance(point);
                    if (distance <= applicationConfigs.getReverseCountryMaxDistanceThresholdInDegrees() && minDistance > distance) {
                        minDistance = distance;
                        countryIso = reducedEnvelopes.get(i).getIso();
                    }
                }
            }
        }
        
        return countryIso;
    }
    
    public boolean coordinateIsInsidePolygon(double latitude, double longitude, Coordinate[] coordinates) {
        double angle = 0;
        double point1_lat;
        double point1_long;
        double point2_lat;
        double point2_long;
        int n = coordinates.length;
        for (int i = 0; i < n; i++) {
            point1_lat = coordinates[i].y - latitude;
            point1_long = coordinates[i].x - longitude;
            point2_lat = coordinates[(i + 1) % n].y - latitude;
            point2_long = coordinates[(i + 1) % n].x - longitude;
            angle += Angle2D(point1_lat, point1_long, point2_lat, point2_long);
        }
        if (Math.abs(angle) < PI) {
            return false;
        } else {
            return true;
        }
    }

    private double Angle2D(double y1, double x1, double y2, double x2) {
        double dtheta, theta1, theta2;
        theta1 = Math.atan2(y1, x1);
        theta2 = Math.atan2(y2, x2);
        dtheta = theta2 - theta1;
        while (dtheta > PI) {
            dtheta -= TWOPI;
        }
        while (dtheta < -PI) {
            dtheta += TWOPI;
        }
        return (dtheta);
    }

    public Double areaClaculator(Coordinate[] points) {
        Double area = null;
        if (points != null && points.length > 2) {
            Coordinate p0 = points[0];
            ArrayList<Coordinate> newPoints = new ArrayList<>();
            for (int i=1; i<points.length; i++) {
                Coordinate p = points[i];
                
                Double y = (p.y - p0.y) / 360 * EARTH_CIRCUMFERENCE_IN_METERS;
                Double x = (p.x - p0.x) / 360 * EARTH_CIRCUMFERENCE_IN_METERS * Math.cos(rad(p.y));
                Coordinate entry = new Coordinate();
                entry.x = x;
                entry.y = y;
                newPoints.add(entry);
            }
            
            if (!newPoints.isEmpty() && newPoints.size() > 1) {
                area = 0d;
                for (int i=0;i< newPoints.size() - 1; i++) {
                    Coordinate p1 = newPoints.get(i);
                    Coordinate p2 = newPoints.get(i+1);
                    
                    area += ((p1.y * p2.x) - (p1.x*p2.y))/2;
                }
                area = Math.abs(area);
            }
        }
        return area;
    }

    private double rad(double degrees) {
      return degrees * PI / 180;
    }
    
    public Coordinate getCentroid(Coordinate[] points)  {
        double centroidX = 0, centroidY = 0;
    
            for (Coordinate knot : points) {
                centroidX += knot.getX();
                centroidY += knot.getY();
            }
        return new Coordinate(centroidX / points.length, centroidY / points.length);
    }

    public boolean arePolygonsOverlapped(Coordinate[] polygon1, Coordinate[] polygon2) {
        ArrayList<Coordinate> poly1 =  new ArrayList<Coordinate>(List.of(polygon1));
        ArrayList<Coordinate> poly2 =  new ArrayList<Coordinate>(List.of(polygon2));

        if (poly1.size() >= 3 && poly2.size() >= 3) {
            //close polygons
            Coordinate p1Close = new Coordinate(poly1.get(0).x, poly1.get(0).y);
            Coordinate p2Close = new Coordinate(poly2.get(0).x, poly2.get(0).y);
            poly1.add(p1Close);
            poly2.add(p2Close);
            
            for (int i = 0; i < poly1.size()-1;i++) {
                for (int k = 0; k < poly2.size()-1; k++) {
                    if (simplePolylineIntersection(poly1.get(i),poly1.get(i+1),poly2.get(k),poly2.get(k+1)) != null) {
                         return true;
                    }
                }
            }
            Coordinate[] poly2c = new Coordinate[poly2.size()];
            poly2c = poly2.toArray(poly2c);
            if (coordinateIsInsidePolygon(poly1.get(0).y, poly1.get(0).x,  poly2c)) {
                return true;
            }
            Coordinate[] poly1c = new Coordinate[poly1.size()];
            poly1c = poly1.toArray(poly1c);
            if (coordinateIsInsidePolygon(poly2.get(0).y, poly2.get(0).x,  poly1c)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public Coordinate simplePolylineIntersection(Coordinate latlong1, Coordinate latlong2, Coordinate latlong3, Coordinate latlong4) {
        //Line segment 1 (p1, p2)
        Double A1 = latlong2.y - latlong1.y;
        Double B1 = latlong1.x - latlong2.x;
        Double C1 = A1*latlong1.x + B1*latlong1.y;
        
        //Line segment 2 (p3,  p4)
        Double A2 = latlong4.y - latlong3.y;
        Double B2 = latlong3.x - latlong4.x;
        Double C2 = A2*latlong3.x + B2*latlong3.y;

        Double determinate = A1*B2 - A2*B1;

        Coordinate intersection;
        if (determinate != 0) {
            Double x = (B2*C1 - B1*C2)/determinate;
            Double y = (A1*C2 - A2*C1)/determinate;
            
            Coordinate intersect = new Coordinate(x, y);
            
            if (inBoundedBox(latlong1, latlong2, intersect) && inBoundedBox(latlong3, latlong4, intersect)) {
                intersection = intersect;
            } else {
                intersection = null;
            }
        } else {
            //lines are parrallel
            intersection = null; 
        }
        return intersection;
    }

    //latlong1 and latlong2 represent two coordinates that make up the bounded box
    //latlong3 is a point that we are checking to see is inside the box
    public Boolean inBoundedBox(Coordinate latlong1, Coordinate latlong2, Coordinate latlong3) {
        Boolean betweenLats;
        Boolean betweenLons;
        
        if(latlong1.y < latlong2.y) {
            betweenLats = (latlong1.y <= latlong3.y && latlong2.y >= latlong3.y);
        } else {
            betweenLats = (latlong1.y >= latlong3.y && latlong2.y <= latlong3.y);
        }
            
        if(latlong1.x < latlong2.x) {
            betweenLons = (latlong1.x <= latlong3.x && latlong2.x >= latlong3.x);
        } else {
            betweenLons = (latlong1.x >= latlong3.x && latlong2.x <= latlong3.x);
        }
        
        return (betweenLats && betweenLons);
    }
}
