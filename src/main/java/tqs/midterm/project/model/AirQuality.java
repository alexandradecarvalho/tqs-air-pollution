package tqs.midterm.project.model;

import javax.persistence.*;
import java.util.List;

public class AirQuality {
    private static int qualityId = 0;
    // air quality parameters
    private double co;   // carbon monoxide
    private double no2;  // nitrogen dioxide
    private double o3;   // ozone
    private double so2;  // sulfur dioxide
    private double pm25; // fine particulate matter (<2.5µm)
    private double pm10; // inhalable particulate matter (<10µm)
    // cities

    // constructor
    public AirQuality(double co, double o3, double no2, double so2, double pm10, double pm25){
        this.co = co;
        this.o3 = o3;
        this.no2 = no2;
        this.so2 = so2;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.qualityId++;
    }

    public AirQuality() {
        this.qualityId++;
    }

    // getters and setters

    public double getCo() {
        return co;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public double getNo2() {
        return no2;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public double getO3() {
        return o3;
    }

    public void setO3(double o3) {
        this.o3 = o3;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public void setId(int qualityId) {
        this.qualityId = qualityId;
    }

    @Id
    public int getId() {
        return qualityId;
    }
}
