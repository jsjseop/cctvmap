package kr.ac.kumoh.s20140716.cctvmap;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Bookmark.class}, version = 1)
public abstract class BookmarkDatabase extends RoomDatabase {

    private static BookmarkDatabase INSTANCE;

    public abstract BookmarkDao bookmarkDao();

    public static synchronized BookmarkDatabase getINSTANCE(Context context) {


        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BookmarkDatabase.class,"Bookmark-db").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }
}
