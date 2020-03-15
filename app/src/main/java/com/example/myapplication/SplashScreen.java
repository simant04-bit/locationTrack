package com.example.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity implements LocationListener {

    ImageView imageView;

    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;
    Location currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        imageView = (ImageView) findViewById(R.id.ImageGif);

        imageView.setVisibility(View.VISIBLE);
        Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        imageView.startAnimation(animFade);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if(ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            locationManager.requestSingleUpdate(criteria,SplashScreen.this,null);
        }
          new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                try {
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();
                    sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
                    String name=sharedPreferences.getString("phone",null);
                    if(name!=null)
                    {
                        Intent intent = new Intent(SplashScreen.this, ShowFriend.class);
                        intent.putExtra("currentLatitude",currentLocation.getLatitude());
                        intent.putExtra("currentLongitude",currentLocation.getLongitude());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(SplashScreen.this,
                                Login.class);
                        intent.putExtra("currentLatitude",currentLocation.getLatitude());
                        intent.putExtra("currentLongitude",currentLocation.getLongitude());
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        finish();
                    }

                }
                catch (Exception ex)
                {

                }

            }
        }, 3000);

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
