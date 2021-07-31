package kr.ac.kumoh.s20140716.cctvmap;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {History.class}, version = 2, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {

    private static HistoryDatabase INSTANCE;

    public abstract HistoryDao historyDao();

    public static synchronized HistoryDatabase getINSTANCE(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    HistoryDatabase.class,"History-db").fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
