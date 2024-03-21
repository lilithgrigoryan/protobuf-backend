package com.example.demo.bll;

import com.example.demo.FruitProto;
import com.example.demo.entities.Fruit;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProtobufService {
    public FruitProto.Fruit convert(Fruit fruit) {
        FruitProto.Fruit.Builder fb = FruitProto.Fruit.newBuilder().setId(fruit.getId());
        if (fruit.getName() != null)
            fb.setName(fruit.getName());
        if (fruit.getColor() != null)
            fb.setColor(fruit.getColor());

        FruitProto.Geometry locationProto = convert(fruit.getLocation());
        fb.setLocation(locationProto);

        FruitProto.Fruit fruitProto = fb.build();
        return fruitProto;
    }

    public Geometry convert(FruitProto.Geometry geometryProto) {

        if (geometryProto.getType() == FruitProto.Type.POLYGON) {
            GeometryFactory geometryFactory = new GeometryFactory();

            List<Coordinate> coordinates = new ArrayList<>();
            for (FruitProto.Coordinate coordinateProto : geometryProto.getCoordinatesList())
                coordinates.add(new Coordinate(coordinateProto.getX(), coordinateProto.getY()));

            return geometryFactory.createPolygon(coordinates.toArray(new Coordinate[0]));
        } else {
            throw new RuntimeException("Not implemented");
        }
    }

    public FruitProto.Geometry convert(Geometry geometry) {

        if (geometry instanceof Point) {
            return convert((Point) geometry);
        } else if (geometry instanceof MultiPoint) {
            return convert((MultiPoint) geometry);
        } else if (geometry instanceof LineString) {
            return convert((LineString) geometry);
        } else if (geometry instanceof MultiLineString) {
            return convert((MultiLineString) geometry);
        } else if (geometry instanceof Polygon) {
            return convert((Polygon) geometry);
        } else if (geometry instanceof MultiPolygon) {
            return convert((MultiPolygon) geometry);
        } else if (geometry instanceof GeometryCollection) {
            GeometryCollection geometryCollection = (GeometryCollection) geometry;
            FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
            geometryBuilder.setType(FruitProto.Type.MULTIPOLYGON);

            for (int i = 0; i < geometry.getNumGeometries(); i++)
                geometryBuilder.addGeometries(convert(geometryCollection.getGeometryN(i)));

            return geometryBuilder.build();
        } else {
            throw new RuntimeException("Not implemented");
        }
    }

    public FruitProto.Coordinate convert(Coordinate coordinate) {
        FruitProto.Coordinate.Builder cb = FruitProto.Coordinate.newBuilder();
        if (!Double.isNaN(coordinate.getZ()))
            cb.setX((float) coordinate.getX()).setY((float) coordinate.getY()).setZ((float) coordinate.getZ()).setHasZ(true);
        else
            cb.setX((float) coordinate.getX()).setY((float) coordinate.getY()).setHasZ(false);

        return cb.build();
    }

    public FruitProto.Geometry convert(LinearRing linearRing) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.LINEARRING);
        for (Coordinate coordinate : linearRing.getCoordinates()) {
            FruitProto.Coordinate coordinateProto = convert(coordinate);
            geometryBuilder.addCoordinates(coordinateProto);
        }

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(Polygon polygon) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.POLYGON);

        LinearRing exteriorRing = polygon.getExteriorRing();
        for (Coordinate coordinate : exteriorRing.getCoordinates()) {
            FruitProto.Coordinate coordinateProto = convert(coordinate);
            geometryBuilder.addCoordinates(coordinateProto);
        }

        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LinearRing interiorRing = polygon.getInteriorRingN(i);
            FruitProto.Geometry interiorRingProto = convert(interiorRing);
            geometryBuilder.addGeometries(interiorRingProto);
        }

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(Point point) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.POINT);

        geometryBuilder.addCoordinates(convert(point.getCoordinate()));

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(MultiPoint multiPoint) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.MULTIPOINT);

        for (Coordinate coordinate : multiPoint.getCoordinates())
            geometryBuilder.addCoordinates(convert(coordinate));

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(LineString lineString) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.LINESTRING);

        for (Coordinate coordinate : lineString.getCoordinates())
            geometryBuilder.addCoordinates(convert(coordinate));

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(MultiLineString multiLineString) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.MULTILINESTRING);

        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            geometryBuilder.addGeometries(convert(lineString));
        }

        return geometryBuilder.build();
    }

    public FruitProto.Geometry convert(MultiPolygon multiPolygon) {
        FruitProto.Geometry.Builder geometryBuilder = FruitProto.Geometry.newBuilder();
        geometryBuilder.setType(FruitProto.Type.MULTIPOLYGON);

        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            geometryBuilder.addGeometries(convert(polygon));
        }

        return geometryBuilder.build();
    }
}
