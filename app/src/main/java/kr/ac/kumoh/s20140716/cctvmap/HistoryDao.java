package kr.ac.kumoh.s20140716.cctvmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM History")
    LiveData<List<History>> getAll();

    @Insert
    void insert(History history);

    @Delete
    void delete(History history);

    @Update
    void update(History history);
}

