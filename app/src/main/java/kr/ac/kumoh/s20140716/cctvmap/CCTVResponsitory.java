package kr.ac.kumoh.s20140716.cctvmap;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class CCTVResponsitory {

    private CCTVDao cctvDao;
    private List<CCTV_info> allCCTV;

    public CCTVResponsitory(Application application) {
        CCTVDatabase cctvDatabase = CCTVDatabase.getINSTANCE(application);
        cctvDao = cctvDatabase.cctvDao();
        allCCTV = cctvDao.getAllCCTV();
    }


    public void insert(CCTV_info cctv_info) {

    }

    public void delete(CCTV_info cctv_info) {

    }

    public List<CCTV_info> getAllCCTV() {
        return allCCTV;
    }

    public List<CCTV_info> LoadAllCCTVBetweenLatLog(double minLat, double maxLat, double minLog, double maxLog) {

        List<CCTV_info> ViewCCTV;

        ViewCCTV = cctvDao.LoadAllCCTVBetweenLatLog(minLat,maxLat,minLog,maxLog);

        return ViewCCTV;
    }
    public CCTV_info SelectCCTV(int key){

        CCTV_info selectCCTV;

        selectCCTV = cctvDao.Select_CCTV(key);

        return  selectCCTV;
    }

    private static class InsertCCTVAsyncTask extends AsyncTask<CCTV_info, Void, Void> {

        private CCTVDao cctvDao;

        private InsertCCTVAsyncTask(CCTVDao cctvDao) {
            this.cctvDao = cctvDao;
        }

        @Override
        protected Void doInBackground(CCTV_info... cctv_infos) {
            cctvDao.insert(cctv_infos[0]);
            return null;
        }
    }
}
