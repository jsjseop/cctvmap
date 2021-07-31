package kr.ac.kumoh.s20140716.cctvmap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class CCTV_infomation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cctv_infomation);

        TextView tx1 = findViewById(R.id.cctv_name);
        TextView tx2 = findViewById(R.id.cctv_number);
        TextView tx3 = findViewById(R.id.cctv_angle);
        TextView tx4 = findViewById(R.id.cctv_address_road);
        TextView tx5 = findViewById(R.id.cctv_address_bungi);
        TextView tx6 = findViewById(R.id.cctv_save);

        Intent intent = getIntent();

        CCTV_info select = (CCTV_info)intent.getSerializableExtra("CCTV");

        tx1.setText(select.getControl_name());
        tx2.setText(select.getControl_number());
        tx3.setText(select.getCamera());
        tx4.setText(select.getAddress_road());
        tx5.setText(select.getAddress_bunji());
        tx6.setText(select.getSave());
    }

}
