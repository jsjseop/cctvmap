package kr.ac.kumoh.s20140716.cctvmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private HistoryViewModel historyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.history_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        // 리스트를 역순으로 출력하기 위해 LayoutManager 설정
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        HistoryAdapter adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        historyViewModel.getHistoryList().observe(this, new Observer<List<History>>() {

            @Override
            public void onChanged(List<History> histories) {
                adapter.setHistoryList(histories);
                int size = adapter.getItemCount();
                if (size > 20)
                {
                    historyViewModel.DeleteData(histories);
                }
            }
        });

        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, History his) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("search_history",his);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}