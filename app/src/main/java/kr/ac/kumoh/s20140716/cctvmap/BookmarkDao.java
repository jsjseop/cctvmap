package kr.ac.kumoh.s20140716.cctvmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Query("SELECT * FROM Bookmark")
    LiveData<List<Bookmark>> getAll();

    @Insert
    void insert(Bookmark bookmark);

    @Delete
    void delete(Bookmark bookmark);

    @Update
    void update(Bookmark bookmark);
}
