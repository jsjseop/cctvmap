package kr.ac.kumoh.s20140716.cctvmap;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<List<History>> historyList;
    private HistoryDatabase historyDatabase;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyDatabase = HistoryDatabase.getINSTANCE(getApplication());
        historyList = historyDatabase.historyDao().getAll();
    }

    public LiveData<List<History>> getHistoryList() {
        return historyList;
    }

    public void DeleteData(List<History> History_list) {
        historyDatabase.historyDao().delete(History_list.get(0));
    }
}