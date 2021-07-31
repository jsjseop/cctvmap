package kr.ac.kumoh.s20140716.cctvmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CCTV_info implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int key;

    @NonNull
    private String Control_name; // 관리기관명
    @NonNull
    private String Control_number; // 관리기관전화번호
    @NonNull
    private String save; // 보관일수
    @NonNull
    private String address_road; // 도로명주소
    @NonNull
    private String address_bunji; // 지번주소
    @NonNull
    private String camera; // 촬영방면
    @NonNull
    private double Latitude; // 위도
    @NonNull
    private double Longitude; // 경도

    public CCTV_info(@NonNull String Control_name, @NonNull String Control_number, @NonNull String save, @NonNull String address_road,
                     @NonNull String address_bunji, @NonNull String camera, @NonNull double Latitude, @NonNull double Longitude) {
        this.Control_name = Control_name;
        this.Control_number = Control_number;
        this.save = save;
        this.address_road = address_road;
        this.address_bunji = address_bunji;
        this.camera = camera;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @NonNull
    public String getControl_name() {
        return Control_name;
    }

    public void setControl_name(@NonNull String control_name) {
        Control_name = control_name;
    }

    @NonNull
    public String getControl_number() {
        return Control_number;
    }

    public void setControl_number(@NonNull String control_number) {
        Control_number = control_number;
    }

    @NonNull
    public String getSave() {
        return save;
    }

    public void setSave(@NonNull String save) {
        this.save = save;
    }

    @NonNull
    public String getAddress_road() {
        return address_road;
    }

    public void setAddress_road(@NonNull String address_road) {
        this.address_road = address_road;
    }

    @NonNull
    public String getAddress_bunji() {
        return address_bunji;
    }

    public void setAddress_bunji(@NonNull String address_bunji) {
        this.address_bunji = address_bunji;
    }

    @NonNull
    public String getCamera() {
        return camera;
    }

    public void setCamera(@NonNull String camera) {
        this.camera = camera;
    }

    @NonNull
    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(@NonNull double latitude) {
        Latitude = latitude;
    }

    @NonNull
    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(@NonNull double longitude) {
        Longitude = longitude;
    }
}
