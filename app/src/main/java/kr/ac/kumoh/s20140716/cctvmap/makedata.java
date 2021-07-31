package kr.ac.kumoh.s20140716.cctvmap;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

public class makedata {

    private CCTVResponsitory cctvResponsitory;
    private List<CCTV_info> ViewCCTV;
    private List<CCTV_info> All;
    private CCTV_info SelectCCTV;

    public makedata(@NonNull Application application) {

        cctvResponsitory = new CCTVResponsitory(application);
        All = cctvResponsitory.getAllCCTV();
    }

    public List<CCTV_info> getALL() {
        return All;
    }

    public List<CCTV_info> getViewCCTV(double minLat, double maxLat, double minLog, double maxLog) {

        ViewCCTV = cctvResponsitory.LoadAllCCTVBetweenLatLog(minLat,maxLat,minLog,maxLog);

        return ViewCCTV;
    }

    public CCTV_info SeletCCTV(int key) {

        SelectCCTV = cctvResponsitory.SelectCCTV(key);

        return SelectCCTV;
    }
}

