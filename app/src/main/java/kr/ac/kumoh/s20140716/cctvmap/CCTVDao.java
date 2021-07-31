package kr.ac.kumoh.s20140716.cctvmap;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CCTVDao {

    @Query("SELECT * FROM CCTV_info")
    List<CCTV_info> getAllCCTV();

    @Insert
    void insert(CCTV_info CCTV_info);

    @Delete
    void delete(CCTV_info CCTV_info);

    @Query("SELECT * FROM CCTV_info WHERE (Latitude BETWEEN :arg0 AND :arg1) AND (Longitude BETWEEN :arg2 AND :arg3)")
    List<CCTV_info> LoadAllCCTVBetweenLatLog(double arg0, double arg1, double arg2, double arg3);

    @Query("SELECT * FROM CCTV_info WHERE `key` == :arg0")
    CCTV_info Select_CCTV(int arg0);


}
