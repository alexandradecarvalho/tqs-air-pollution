package tqs.midterm.project.model;

import org.springframework.data.annotation.Persistent;

import javax.persistence.*;

public class City {
    // auto generated id
    private static int id = 0;
    // coordinates
    private float latitude;
    private float longitude;
    // name and country
    private String name;
    private String country;
    // pollutants concentration
    private AirQuality quality;

    // constructors
    public City(float latitude, float longitude, String name, String country){
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
        this.country=country;
        this.id++;
    }

    public City() {this.id++;}

    // getters and setters

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AirQuality getQuality(){
        return quality;
    }

    public void setQuality(AirQuality quality){
        this.quality = quality;
    }


    public void setId(int id) {
        this.id = id;
    }

    @Id
    public int getId() {
        return id;
    }
}
