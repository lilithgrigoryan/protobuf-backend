package com.example.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

@Entity
@Table(
        name = "fruits",
        schema = "lilit_schema",
        uniqueConstraints = {@UniqueConstraint(
                name = "unique_fruitname",
                columnNames = "name"
        ),}
)
public class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "fruitcolor")
    private String color;
    @JsonIgnore
    @Column(name = "location")
    private Geometry location;

    private String locationText;

    public void setLocationText(String locationText) throws ParseException {
        WKTReader reader = new WKTReader();

        this.locationText = locationText;
        this.location = reader.read(locationText);
    }

    @JsonProperty()
    public String getLocationText() {
        return location.toText();
    }

    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location = location;
        this.locationText = location.toText();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
