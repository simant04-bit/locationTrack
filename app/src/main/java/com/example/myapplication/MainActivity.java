package com.example.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_ID = 44;
    ImageView imageView;

    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;
    Location currentLocation;
    FusedLocationProviderClient mFusedLocationClient;
   // TextView latTextView, lonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imageView = (ImageView) findViewById(R.id.ImageGif);
        imageView.setVisibility(View.VISIBLE);
        Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        imageView.startAnimation(animFade);
        getSupportActionBar().hide();
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(Task<Location> task) {
                                final Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            try {
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                finish();
                                                sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
                                                String name=sharedPreferences.getString("phone",null);
                                                if(name==null)
                                                {
                                                    Intent intent = new Intent(MainActivity.this,
                                                            Login.class);
                                                    intent.putExtra("currentLatitude",location.getLatitude());
                                                    intent.putExtra("currentLongitude",location.getLongitude());
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                    finish();

                                                }
                                                else
                                                {
                                                    Intent intent = new Intent(MainActivity.this, ShowFriend.class);
                                                    intent.putExtra("currentLatitude",location.getLatitude());
                                                    intent.putExtra("currentLongitude",location.getLongitude());
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


                                    //Toast.makeText(MainActivity.this, ""+location.getLatitude()+location.getLongitude(), Toast.LENGTH_SHORT).show();
                                    //latTextView.setText(location.getLatitude()+"");
                                    //lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            final Location mLastLocation = locationResult.getLastLocation();

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
                            Intent intent = new Intent(MainActivity.this, ShowFriend.class);
                            intent.putExtra("currentLatitude",mLastLocation.getLatitude());
                            intent.putExtra("currentLongitude",mLastLocation.getLongitude());
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(MainActivity.this,
                                    Login.class);
                            intent.putExtra("currentLatitude",mLastLocation.getLatitude());
                            intent.putExtra("currentLongitude",mLastLocation.getLongitude());
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

            Toast.makeText(MainActivity.this, ""+mLastLocation.getLatitude()+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    };

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }
}