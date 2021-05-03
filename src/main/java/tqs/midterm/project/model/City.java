package tqs.midterm.project.model;

public class City {
    private float latitude;
    private float longitude;
    private String name;
    private String country;
    private AirQuality quality;

    public City(float latitude, float longitude, String name, String country, AirQuality quality){
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
        this.country=country;
        this.quality=quality;
    }

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


}