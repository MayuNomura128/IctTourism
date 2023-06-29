package com.example.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SensorEventListener, View.OnClickListener {
    String[] placeName;
    List<Integer> placeDraw;
    String[] wikiURL;
    TextView txtPlace, txtReturn;
    ImageView imgPlace;
    Spinner spnPlace;
    Button btnToWiki;
    int cursor = 0;
    float down = 0;
    float up = 0;
    SensorManager manager;
    boolean flag = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPlaceName();
        setPlaceDraw();
        setWikiURL();
        txtPlace = findViewById(R.id.txtPlace);
        imgPlace = findViewById(R.id.imgPlace);
        spnPlace = findViewById(R.id.spnPlace);

        txtPlace.setText(placeName[cursor]);
        imgPlace.setImageResource(placeDraw.get(cursor));
        spnPlace.setOnItemSelectedListener(this);
        btnToWiki = findViewById(R.id.btnWiki);
        btnToWiki.setOnClickListener(this);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        txtReturn = findViewById(R.id.txtReturnTop);
        txtReturn.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, placeName
        );
        spnPlace.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (manager != null) {
            manager.unregisterListener(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down = event.getX();
                break;

            case MotionEvent.ACTION_UP:

                up = event.getX();
                int direction = getFlick(down, up);
                moveCursor(direction);
                disp();

                break;
        }

        return super.onTouchEvent(event);
    }

    public int getFlick(float down, float up) {
        // -1=右→左  1=左→右  0=動いてない
        int result = 0;

        if (down > up) {
            result = -1;
        } else if (down < up) {
            result = 1;
        }

        return result;
    }

    public void moveCursor(int direction) {
        switch (direction) {
            case -1:
                cursor++;
                if (cursor >= placeName.length) {
                    cursor = 0;
                }
                break;

            case 1:
                cursor--;
                if (cursor < 0) {
                    cursor = placeName.length - 1;
                }
                break;
        }
    }

    public void disp() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anm_disp);
        txtPlace.setText(placeName[cursor]);
        imgPlace.setImageResource(placeDraw.get(cursor));
        imgPlace.setAnimation(animation);
        spnPlace.setSelection(cursor);
    }


    public void setPlaceName() {
        placeName = getResources().getStringArray(R.array.spot);
    }

    public void setPlaceDraw() {
        placeDraw = new ArrayList<>();

        placeDraw.add(R.drawable.castle);
        placeDraw.add(R.drawable.usj);
        placeDraw.add(R.drawable.harukasu);
        placeDraw.add(R.drawable.cupnoodle);
        placeDraw.add(R.drawable.kaiyuukan);
        placeDraw.add(R.drawable.sitennouji);
        placeDraw.add(R.drawable.taiyounotou);
        placeDraw.add(R.drawable.museum);
        placeDraw.add(R.drawable.tuutenkaku);
        placeDraw.add(R.drawable.doutonbori);
    }

    public void setWikiURL() {
        wikiURL = getResources().getStringArray(R.array.wikiURL);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cursor = position;
        disp();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float acceleroX = event.values[0];
            if (acceleroX > 6 && flag) {
                moveCursor(-1);
                disp();
                flag = false;
            } else if (acceleroX < -6 && flag) {
                moveCursor(1);
                disp();
                flag = false;
            } else if (acceleroX > -2 && acceleroX < 2) {
                flag = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do Nothing
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtReturnTop:
                finish();
                break;
            case R.id.btnWiki:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(wikiURL[cursor]));
                startActivity(intent);
        }
    }
}