package kr.ac.kumoh.s20140716.cctvmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class History implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int HistoryID;
    private String startAddress;
    private double startLatitude;
    private double startLongitude;
    private String endAddress;
    private double endLatitude;
    private double endLongitude;
    private String date;

    public History(String startAddress, double startLatitude, double startLongitude, String endAddress, double endLatitude, double endLongitude, String date) {
        this.startAddress = startAddress;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endAddress = endAddress;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.date = date;
    }

    public int getHistoryID() {
        return HistoryID;
    }

    public void setHistoryID(int historyID) {
        HistoryID = historyID;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "History{" +
                "endaddress='" + endAddress + '\'' +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                ", date='" + date + '\'' +
                '}';
    }
}