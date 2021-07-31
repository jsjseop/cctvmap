package kr.ac.kumoh.s20140716.cctvmap;

public class SearchEntity {
    private String title;
    private String address;
    private double Latitude;
    private double Longitude;

    public SearchEntity(String title, String address) {
        this.title = title;
        this.address = address;
    }
    public SearchEntity(String title, String address, double Latitude, double Longitude) {
        this.title = title;
        this.address = address;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}