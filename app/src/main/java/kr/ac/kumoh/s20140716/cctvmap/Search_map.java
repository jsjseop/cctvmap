package kr.ac.kumoh.s20140716.cctvmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.MapUtils;
import com.skt.Tmap.TMapPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Search_map extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener,
        RecyclerViewAdapterCallback {
    private static final String TAG = "Tmap_project";
    private EditText start;
    private EditText end;
    private Button search_btn;
    private int start_or_end = 1;

    private RecyclerView recyclerView1, recyclerView2;
    private RecyclerViewAdapter adapter1, adapter2;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;
    private final long DELAY = 500;
    private double current_latitude = 0.0;
    private double current_longitude = 0.0;

    private static double[] start_address = {0.0, 0.0};
    private static double[] end_address = {0.0, 0.0};
    private static String start_name;
    private static String end_name;

    private HistoryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);

        db = HistoryDatabase.getINSTANCE(getApplication());

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.Search_Map);

        start = findViewById(R.id.start_edit);
        end = findViewById(R.id.end_edit);
        search_btn = findViewById(R.id.search_btn);

        start.setOnEditorActionListener(this);
        end.setOnEditorActionListener(this);
        search_btn.setOnClickListener(this);

        layoutInit();

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                adapter1.setClear();
                adapter2.setClear();
                return false;
            }
        });
    }

    @Override
    public void Transportedit(String title) {
        Address address = searchAddress(title);
        if(start_or_end == 1){
            start.setText(title);
            String str1 = start.getText().toString();

            start_address[0] = address.getLatitude();
            start_address[1] = address.getLongitude();
            start_name = str1;

            adapter1.setClear();
        }
        else if(start_or_end == 2){
            end.setText(title);
            String str2 = end.getText().toString();

            end_address[0] = address.getLatitude();
            end_address[1] = address.getLongitude();
            end_name = str2;

            adapter2.setClear();
        }

    }

    private void layoutInit() {
        start = (EditText) findViewById(R.id.start_edit);
        end = (EditText) findViewById(R.id.end_edit);
        recyclerView1 = (RecyclerView) findViewById(R.id.rl_listview1);
        recyclerView2 = (RecyclerView) findViewById(R.id.rl_listview2);

        Intent intent_current = getIntent();
        current_latitude = intent_current.getDoubleExtra("current_latitude",0);
        current_longitude = intent_current.getDoubleExtra("current_longitude",0);

        start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                final String keyword = s.toString();
                start_or_end = 1;

                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        adapter1.filter(keyword);
                    }
                };
                handler.postDelayed(workRunnable, DELAY);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter1 = new RecyclerViewAdapter();
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setAdapter(adapter1);
        adapter1.setLocation(current_latitude, current_longitude);
        adapter1.setCallback(this);

        end.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                final String keyword = s.toString();
                start_or_end = 2;

                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run()
                    {
                        adapter2.filter(keyword);
                    }
                };
                handler.postDelayed(workRunnable, DELAY);
            }
        });

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        adapter2 = new RecyclerViewAdapter();
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(adapter2);
        adapter2.setLocation(current_latitude, current_longitude);
        adapter2.setCallback(this);
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if(textView.getId()==R.id.start_edit && i== EditorInfo.IME_ACTION_SEARCH) { // 뷰의 id를 식별, 키보드의 완료 키 입력 검출

            Address address = searchAddress(start.getText().toString());
            String str1 = start.getText().toString();

            start_address[0] = address.getLatitude();
            start_address[1] = address.getLongitude();
            start_name = str1;

            adapter2.setClear();
        }
        else if(textView.getId()==R.id.end_edit && i== EditorInfo.IME_ACTION_SEARCH) { // 뷰의 id를 식별, 키보드의 완료 키 입력 검출

            Address address = searchAddress(end.getText().toString());
            String str2 = end.getText().toString();

            end_address[0] = address.getLatitude();
            end_address[1] = address.getLongitude();
            end_name = str2;

            adapter2.setClear();

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                Intent intent_search = new Intent(Search_map.this, MainActivity.class);
                intent_search.putExtra("start", (double[]) start_address);
                intent_search.putExtra("end",(double[]) end_address);
                intent_search.putExtra("end_name", end_name);
                String date = new UtilMethod().setDate();

                db.historyDao().insert(new History(start_name,start_address[0],start_address[1],
                        end_name,end_address[0],end_address[1],date));

                setResult(1, intent_search);
                finish();
                break;
        }
    }

    public Address searchAddress(String title){
        List<Address> addressList = null;
        Address address = null;
        String str = title;
        Geocoder geocoder= new Geocoder(this, Locale.getDefault());

        try{
            addressList = geocoder.getFromLocationName(str,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null)
        {
            if(addressList.size() == 0) {
                Log.d(TAG,"주소 없음");
            }
            else {
                address = addressList.get(0);
            }
        }
        return address;
    }
}