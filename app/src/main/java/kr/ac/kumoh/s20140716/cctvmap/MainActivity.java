package kr.ac.kumoh.s20140716.cctvmap;


import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.TemplateParams;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.skt.Tmap.MapUtils;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener,
        Button.OnClickListener, RecyclerViewAdapterCallback
{
    private static final String TAG = "Tmap_project";
    private final String SKTMapApiKey = "l7xx0206e1e44ba94bf5bf596c39b069f73b";
    private TMapView tmap;
    private EditText editText;
    private Button bookmark_button;
    private Button sButton;
    private Location currentlocation;
    private Switch GPS;
    private ToggleButton cctvbtn;
    private TMapMarkerItem startmarker = new TMapMarkerItem();
    private TMapMarkerItem endmarker = new TMapMarkerItem();
    private TMapPoint EndPoint, CurrentPoint;

    private int cctv_toggle = 0;
    private double move_latitude = 0.0;
    private double move_longitude = 0.0;
    private double center_latitude = 0.0;
    private double center_longitude = 0.0;
    private double start_latitude = 0.0;
    private double start_longitude = 0.0;
    private double end_latitude = 0.0;
    private double end_longitude = 0.0;
    private double current_latitude = 0.0;
    private double current_longitude = 0.0;
    private int move_zoomlevel;
    private int center_zoomlevel;
    private String end_name;
    private List<CCTV_info> ViewCCTV;
    private makedata makedata;
    private HistoryDatabase db;
    private BookmarkDatabase bdb;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter= new RecyclerViewAdapter();;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;
    private final long DELAY = 500;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = HistoryDatabase.getINSTANCE(getApplication());
        bdb = BookmarkDatabase.getINSTANCE(getApplication());

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        layoutInit();

        FrameLayout frameLayoutTmap = (FrameLayout) findViewById(R.id.Tmap);

        tmap = new TMapView(this);

        editText = findViewById(R.id.search_edit);
        sButton = findViewById(R.id.search_button);
        GPS = findViewById(R.id.GPS);
        cctvbtn = findViewById(R.id.cctv_button);
        bookmark_button = findViewById(R.id.bookmark_button);

        tmap.setSKTMapApiKey(SKTMapApiKey);
        frameLayoutTmap.addView(tmap);

        GPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true)
                {
                    setGps();
                    tmap.setCompassMode(true);
                    tmap.setTrackingMode(true);
                }
                else
                {
                    lm.removeUpdates(mLocationListener);
                    tmap.setCompassMode(false);
                    tmap.setTrackingMode(false);
                }
            }
        });

        editText.setOnEditorActionListener(this);
        sButton.setOnClickListener(this);
        bookmark_button.setOnClickListener(this);
        cctvbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cctvbtn.isChecked()) {
                    cctv_toggle = 1;
                    new Thread() {
                        public void run() {
                            Marker();
                        }
                    }.start();
                } else {
                    cctv_toggle = 0;
                    tmap.removeAllMarkerItem();
                }
            }
        });

        tmap.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                adapter.setClear();
                return false;
            }

        });

        tmap.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                new Thread() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, CCTV_infomation.class);

                        String str = tMapMarkerItem.getID();
                        str = str.substring(8);

                        CCTV_info selectcctv = makedata.SeletCCTV(Integer.parseInt(str));
                        intent.putExtra("CCTV", selectcctv);

                        startActivity(intent);
                    }
                }.start();
            }
        });

        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.

        TMapPoint Centerpoint = tmap.getCenterPoint();
        center_latitude = Centerpoint.getLatitude();
        center_longitude = Centerpoint.getLongitude();
        center_zoomlevel = tmap.getZoomLevel();

        tmap.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {

                move_latitude = centerPoint.getLatitude();
                move_longitude = centerPoint.getLongitude();
                move_zoomlevel = tmap.getZoomLevel();

                if (((move_latitude != center_latitude) || (move_longitude != center_longitude)
                        || (move_zoomlevel != center_zoomlevel)) && (zoom > 14) &&(cctv_toggle == 1))
                {
                    tmap.removeAllMarkerItem();
                    new Thread() {
                        public void run() {
                            Marker();
                        }
                    }.start();
                    center_latitude = move_latitude;
                    center_longitude = move_longitude;
                    center_zoomlevel = move_zoomlevel;
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawble);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void layoutInit()
    {
        setGps();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_button);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawble);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.bookmark:
                        Intent bookmark_intent = new Intent(MainActivity.this, BookmarkActivity.class);
                        startActivityForResult(bookmark_intent,0);
                        break;
                    case R.id.history:
                        Intent history_intent = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivityForResult(history_intent,0);
                        break;
                    case R.id.searchmap:
                        Intent intent = new Intent(MainActivity.this, Search_map.class);
                        intent.putExtra("current_latitude", current_latitude);
                        intent.putExtra("current_longitude", current_longitude);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.help:
                        Intent help_intent = new Intent(MainActivity.this, HelpActivity.class);
                        startActivity(help_intent);
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawble);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        editText = (EditText)findViewById(R.id.search_edit);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                final String keyword = s.toString();

                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable()
                {
                    @Override
                    public void run() {
                        adapter.filter(keyword);
                    }
                };
                handler.postDelayed(workRunnable, DELAY);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.rl_listview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setCallback(this);
    }

    private final LocationListener mLocationListener = new LocationListener()
    {
        public void onLocationChanged(Location location)
        {
            if (location != null)
            {
                currentlocation = location;
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                current_latitude = latitude;
                current_longitude = longitude;
                tmap.setLocationPoint(longitude, latitude);
                CurrentPoint = new TMapPoint(latitude, longitude);
                tmap.setCenterPoint(longitude, latitude);
                adapter.setLocation(latitude, longitude);
                tmap.setSightVisible(true);

                if(EndPoint != null)
                {
                    if(MapUtils.getDistance(CurrentPoint, EndPoint) <= 10) //목적지 근처에 도달하면 경로 삭제
                    {
                        Toast.makeText(getApplicationContext(),"경로안내를 종료합니다", Toast.LENGTH_SHORT).show();
                        tmap.removeAllTMapPolyLine();
                    }
                }
            }
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            tmap.removeAllMarkerItem();
        }
    };

    public void setGps()
    {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
        lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, mLocationListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button :
                if(editText != null)
                {
                    String date = new UtilMethod().setDate();
                    String add = ReverseGeo(currentlocation.getLatitude(),currentlocation.getLongitude());
                    db.historyDao().insert(new History(add, currentlocation.getLatitude(),
                            currentlocation.getLongitude(),editText.getText().toString(),
                            end_latitude,end_longitude, date));

                    start_latitude = currentlocation.getLatitude();
                    start_longitude = currentlocation.getLongitude();
                    new Thread() {
                        public void run() {
                            Tmapnavi();
                        }
                    }.start();
                }
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("파란색 경로 : CCTV 우선경로\n빨간색 경로 : 최단 경로").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;

            case R.id.bookmark_button:
                if (editText.length() > 2) {
                    bdb.bookmarkDao().insert(new Bookmark(editText.getText().toString()));
                    Toast.makeText(getApplication(), "북마크가 추가 되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
    {
        if (textView.getId() == R.id.search_edit)
        {
            List<Address> addressList = null;
            String str = editText.getText().toString();
            end_name = str;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addressList = geocoder.getFromLocationName(str, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null) {
                if (addressList.size() == 0) {
                    Log.d(TAG, "주소 없음");
                } else {
                    Address add = addressList.get(0);
                    end_latitude = add.getLatitude();
                    end_longitude = add.getLongitude();
                    tmap.setLocationPoint(end_longitude, end_latitude);
                    tmap.setCenterPoint(end_longitude, end_latitude);
                    adapter.setClear();
                }
            }
        }
        return false;
    }

    public String ReverseGeo(double Lat,double Lon)
    {
        String returnstr = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(
                    Lat, // 위도
                    Lon, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류");
        }
        if (list != null) {
            if (list.size()==0) {
                Log.d("TAG","해당되는 주소 정보는 없습니다");
            } else {
                returnstr = list.get(0).getAddressLine(0);
            }
        }
        return returnstr;
    }

    public void Tmapnavi()
    {
        tmap.removeAllTMapPolyLine();
        tmap.removeAllMarkerItem();

        TMapPoint tMapPointStart = new TMapPoint(start_latitude, start_longitude); // 출발지 위치
        TMapPoint tMapPointEnd = new TMapPoint(end_latitude, end_longitude); // 도착지 위치

        Bitmap bitmap1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.start);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.end);

        EndPoint = tMapPointEnd;

        startmarker.setTMapPoint(tMapPointStart); // 마커의 좌표 지정
        startmarker.setVisible(TMapMarkerItem.VISIBLE);
        startmarker.setCanShowCallout(true);
        startmarker.setIcon(bitmap1);
        tmap.addMarkerItem("startmarker", startmarker);

        endmarker.setTMapPoint(tMapPointEnd); // 마커의 좌표 지정
        endmarker.setVisible(TMapMarkerItem.VISIBLE);
        endmarker.setCanShowCallout(true);
        endmarker.setIcon(bitmap2);
        tmap.addMarkerItem("endmarker", endmarker);

        TMapPolyLine tMapPolyLine = new TMapPolyLine();

        try {
            tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                    tMapPointStart, tMapPointEnd);
            tMapPolyLine.setLineColor(Color.RED);
            tMapPolyLine.setLineWidth(4);
            tmap.addTMapPolyLine("Line2", tMapPolyLine);

            double Distance_general = tMapPolyLine.getDistance();
            Distance_general = Double.parseDouble(String.format("%.1f", Distance_general / 1000));

            double time_general_hour = Math.floor(Distance_general / 5); //사람의 평균 이동속도 = 5km/h
            double time_general_min = 0;

            if (time_general_hour < 1)
            {
                time_general_min = (Distance_general / 5) * 60;
            } else {
                time_general_min = (Distance_general / 5 - Math.floor(Distance_general / 5)) * 60;
            }

            Log.d("distance1 : ", String.valueOf(Distance_general) + "km");
            Log.d("time_cctv", String.valueOf(Integer.parseInt(String.format("%.0f", time_general_hour)))
                    + "시간 " + String.valueOf(Integer.parseInt(String.format("%.0f", time_general_min))) + "분");
        } catch (Exception e) {
            e.printStackTrace();
        }

        customZoomLevel(tMapPolyLine);

        ArrayList<TMapPoint> passlist = new ArrayList<TMapPoint>();

        //일반 경로를 찾은 후 cctv를 경유하는 경로를 탐색
        passlist = Find_CCTV(); //경유지를 찾아서 리스트로 만들어줌

        if(passlist.size() == 3)
        {
            //가장 짧은 경로를 지도에 표시
            try {
                TMapPolyLine tMapPolyLine1 = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,
                        tMapPointStart, tMapPointEnd, passlist, 0);

                tMapPolyLine1.setLineColor(Color.BLACK);
                tMapPolyLine1.setLineWidth(5);
                tmap.addTMapPolyLine("Line3", tMapPolyLine1);

                double Distance_cctv = tMapPolyLine1.getDistance();
                Distance_cctv = Double.parseDouble(String.format("%.1f", Distance_cctv / 1000));

                double time_cctv_hour = Math.floor(Distance_cctv / 5);
                double time_cctv_min = 0;

                if (time_cctv_hour < 1) {
                    time_cctv_min = (Distance_cctv / 5) * 60;
                } else {
                    time_cctv_min = (Distance_cctv / 5 - Math.floor(Distance_cctv / 5)) * 60;
                }

                Log.d("distance2 : ", String.valueOf(Distance_cctv) + "km");
                Log.d("time_cctv", String.valueOf(Integer.parseInt(String.format("%.0f", time_cctv_hour)))
                        + "시간 " + String.valueOf(Integer.parseInt(String.format("%.0f", time_cctv_min))) + "분");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("카카오톡으로 메세지를 보내시겠습니까?").setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LinkObject link;
                                        link = LinkObject.newBuilder()
                                                .setWebUrl("https://developers.kakao.com")
                                                .setMobileWebUrl("https://developers.kakao.com")
                                                .build();

                                        String address = ReverseGeo(end_latitude, end_longitude);

                                        TemplateParams params = LocationTemplate.newBuilder(
                                                address,
                                                ContentObject.newBuilder(
                                                        end_name,
                                                        "https://www.kakaocorp.com/images/logo/og_daumkakao_151001.png",
                                                        LinkObject.newBuilder()
                                                                .setWebUrl("https://developers.kakao.com")
                                                                .setMobileWebUrl("https://developers.kakao.com")
                                                                .build())
                                                        .setDescrption(end_name + "위치입니다.")
                                                        .build())
                                                .setAddressTitle(end_name)
                                                .build();

                                        KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, new ResponseCallback<KakaoLinkResponse>() {
                                            @Override
                                            public void onFailure(ErrorResult errorResult) {
                                                Log.e("KAKAO_API", "카카오링크 공유 실패: " + errorResult);
                                            }

                                            @Override
                                            public void onSuccess(KakaoLinkResponse result) {
                                                Log.i("KAKAO_API", "카카오링크 공유 성공");

                                                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                                                Log.w("KAKAO_API", "warning messages: " + result.getWarningMsg());
                                                Log.w("KAKAO_API", "argument messages: " + result.getArgumentMsg());
                                            }
                                        });
                                    }
                                }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                        }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

    }

    public ArrayList<TMapPoint> Find_CCTV()
    {
        Application application = getApplication();
        ArrayList<TMapPoint> passslist = new ArrayList<TMapPoint>();
        ArrayList<CCTV_weight> all_cctv = new ArrayList<>();

        int flag = 0;

        makedata = new makedata(application);

        TMapPoint lefttop = tmap.getLeftTopPoint();
        TMapPoint rightbot = tmap.getRightBottomPoint();

        // 오른쪽 아래 위도, 왼쪽 위 위도, 왼쪽 위 경도, 오른쪽 아래 경도 순으로 입력할 것
        ViewCCTV = makedata.getViewCCTV(rightbot.getLatitude(), lefttop.getLatitude(), lefttop.getLongitude(), rightbot.getLongitude());

        int size = ViewCCTV.size();

        if (size <= 10) {
            handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(getApplicationContext(), "CCTV개수가 10개 이하여서 최단경로만 안내합니다.", Toast.LENGTH_LONG).show();
                             }
                         });
            return passslist;
        }

        for (int i = 0; i < size; i++) {
            TMapPoint point = new TMapPoint(ViewCCTV.get(i).getLatitude(), ViewCCTV.get(i).getLongitude());
            CCTV_weight cctv = new CCTV_weight(point.getLatitude(), point.getLongitude(), 0, 0, 0);
            all_cctv.add(i, cctv); //화면 내 모든 cctv를 찾아서 리스트에 추가
        }

        TMapPoint center = new TMapPoint((rightbot.getLatitude() + lefttop.getLatitude()) / 2,
                (rightbot.getLongitude() + lefttop.getLongitude()) / 2);

        for (int i = 0; i < size; i++)
        {
            all_cctv.get(i).cc_weight1 = Math.pow(start_latitude - all_cctv.get(i).cc_latitude,2)
                    + Math.pow(start_longitude - all_cctv.get(i).cc_longtitude,2);
            all_cctv.get(i).cc_weight2 = Math.pow(center.getLatitude() - all_cctv.get(i).cc_latitude,2)
                    + Math.pow(center.getLongitude() - all_cctv.get(i).cc_longtitude,2);
            all_cctv.get(i).cc_weight3 = Math.pow(end_latitude - all_cctv.get(i).cc_latitude,2)
                    + Math.pow(end_longitude - all_cctv.get(i).cc_longtitude,2);

            TMapPoint CCTV1 = new TMapPoint(all_cctv.get(i).cc_latitude,all_cctv.get(i).cc_longtitude);

            for (int j = 0; j< size; j++)
            {
                TMapPoint CCTV2 = new TMapPoint(all_cctv.get(j).cc_latitude,all_cctv.get(j).cc_longtitude);

                if (MapUtils.getDistance(CCTV1,CCTV2) <= 50)
                {
                    all_cctv.get(i).cc_weight1 -= (Math.pow(0.000075,2)+Math.pow(0.000125,2));
                    all_cctv.get(i).cc_weight2 -= (Math.pow(0.000075,2)+Math.pow(0.000125,2));
                    all_cctv.get(i).cc_weight3 -= (Math.pow(0.000075,2)+Math.pow(0.000125,2));
                }
            }
        }

        CCTV_weight.MiniComporator1 comp1 = new CCTV_weight.MiniComporator1();
        CCTV_weight.MiniComporator2 comp2 = new CCTV_weight.MiniComporator2();
        CCTV_weight.MiniComporator3 comp3 = new CCTV_weight.MiniComporator3();

        Collections.sort(all_cctv, comp1); // 가중치가 가장 작은순으로 정렬
        TMapPoint pass1 = new TMapPoint(all_cctv.get(0).cc_latitude, all_cctv.get(0).cc_longtitude);
        Collections.sort(all_cctv, comp2); // 가중치가 가장 작은순으로 정렬

        // pass1과 좌표가 같으면 그 다음 가중치를 가진 값을 저장
        if (pass1.getLatitude() == all_cctv.get(flag).cc_latitude &&
                pass1.getLongitude() == all_cctv.get(flag).cc_longtitude)
        {
            flag += 1;
        }

        TMapPoint pass2 = new TMapPoint(all_cctv.get(flag).cc_latitude, all_cctv.get(flag).cc_longtitude);
        flag = 0;
        Collections.sort(all_cctv, comp3); // 가중치가 가장 작은순으로 정렬

        // pass1, pass2와 좌표가 같으면 그 다음 가중치를 가진 값을 저장
        if (pass2.getLatitude() == all_cctv.get(flag).cc_latitude &&
                pass2.getLongitude() == all_cctv.get(flag).cc_longtitude)
        {
            flag += 1;
        }
        if (pass1.getLatitude() == all_cctv.get(flag).cc_latitude &&
                pass1.getLongitude() == all_cctv.get(flag).cc_longtitude)
        {
            flag += 1;
        }

        TMapPoint pass3 = new TMapPoint(all_cctv.get(flag).cc_latitude, all_cctv.get(flag).cc_longtitude);

        passslist.add(0, pass1); // 출발지 근처
        passslist.add(1, pass2); // 중간 지점
        passslist.add(2, pass3); // 도착지 근처

        /*Bitmap bitmap1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pass);

        for(int i = 0; i < 3 ; i++)
        {
            TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
            mapMarkerItem.setTMapPoint(passslist.get(i));
            mapMarkerItem.setCanShowCallout(true);
            mapMarkerItem.setAutoCalloutVisible(false);
            mapMarkerItem.setIcon(bitmap1);
            tmap.addMarkerItem("Passlist" + i, mapMarkerItem);
        }*/

        Log.d("size_pass", String.valueOf(passslist.size()));
        all_cctv.clear();
        return passslist;
    }

    public void Marker()
    {
        Application application = getApplication();

        makedata = new makedata(application);

        TMapPoint lefttop = tmap.getLeftTopPoint();
        TMapPoint rightbot = tmap.getRightBottomPoint();

        // 오른쪽 아래 위도, 왼쪽 위 위도, 왼쪽 위 경도, 오른쪽 위 경도 순으로 입력할 것
        ViewCCTV = makedata.getViewCCTV(rightbot.getLatitude(), lefttop.getLatitude(), lefttop.getLongitude(), rightbot.getLongitude());

        int size = ViewCCTV.size();

        Log.d("CCTV", String.valueOf(size));

        for (int i = 0; i < size; i++) {
            TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
            TMapPoint point = new TMapPoint(ViewCCTV.get(i).getLatitude(), ViewCCTV.get(i).getLongitude());

            //Log.d("TAG", "위도 : " + point.getLatitude() + "경도 : " + point.getLongitude() + "id : " + ViewCCTV.get(i).getKey());
            mapMarkerItem.setTMapPoint(point);
            mapMarkerItem.setCalloutTitle(ViewCCTV.get(i).getControl_name());
            mapMarkerItem.setCanShowCallout(true);
            mapMarkerItem.setAutoCalloutVisible(false);

            Bitmap bitmap1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.i_go);
            mapMarkerItem.setCalloutRightButtonImage(bitmap1);

            tmap.addMarkerItem("markerID" + ViewCCTV.get(i).getKey(), mapMarkerItem);
        }
    }

    @Override
    public void Transportedit(String title) {
        editText.setText(title);
    }

    public void customZoomLevel(TMapPolyLine polyLine)
    {
        TMapInfo info = tmap.getDisplayTMapInfo(polyLine.getLinePoint());

        int zoom = info.getTMapZoomLevel();

        tmap.setZoomLevel(zoom);
        tmap.setCenterPoint(info.getTMapPoint().getLongitude(),info.getTMapPoint().getLatitude());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode)
        {
            case 1:
                double[] start_address = data.getDoubleArrayExtra("start");
                start_latitude = start_address[0];
                start_longitude = start_address[1];

                end_name = data.getStringExtra("end_name");
                double[] end_address = data.getDoubleArrayExtra("end");
                end_latitude = end_address[0];
                end_longitude = end_address[1];

                new Thread()
                {
                    public void run() {
                        Tmapnavi();
                    }
                }.start();

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("파란색 경로 : CCTV 우선경로\n빨간색 경로 : 최단 경로").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;

            case RESULT_OK:
                History history = (History) data.getSerializableExtra("search_history");
                start_latitude = history.getStartLatitude();
                start_longitude = history.getStartLongitude();
                end_latitude = history.getEndLatitude();
                end_longitude = history.getEndLongitude();
                end_name = history.getEndAddress();

                new Thread()
                {
                    public void run() {
                        Tmapnavi();
                    }
                }.start();

                AlertDialog.Builder alert_confirm2 = new AlertDialog.Builder(MainActivity.this);
                alert_confirm2.setMessage("파란색 경로 : CCTV 우선경로\n빨간색 경로 : 최단 경로").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK
                                return;
                            }
                        });
                AlertDialog alert2 = alert_confirm2.create();
                alert2.show();
                break;

            case 2:
                Bookmark book = (Bookmark) data.getSerializableExtra("search_bookmark");
                editText.setText(book.getBookmark());
                List<Address> addressList = null;
                String str = editText.getText().toString();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addressList = geocoder.getFromLocationName(str, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList != null) {
                    if (addressList.size() == 0) {
                        Log.d(TAG, "주소 없음");
                    } else {
                        Address add = addressList.get(0);
                        end_latitude = add.getLatitude();
                        end_longitude = add.getLongitude();
                        tmap.setLocationPoint(end_longitude, end_latitude);
                        tmap.setCenterPoint(end_longitude, end_latitude);
                        adapter.setClear();
                    }
                }
                break;
        }
    }
}

