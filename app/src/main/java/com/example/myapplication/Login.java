package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity  implements LocationListener {
    public static final String JSON_URL = "http://www.til.com.np/track/api/request.php?";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editPreferences;
    LocationManager locationManager;
    Location currentLocation;
    boolean locationStatus = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationStatus = true;
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    locationManager.requestSingleUpdate(criteria,Login.this,null);
                }
            }
        }
    }

    public static boolean isLocationServicesAvailable(Context context) {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        boolean coarsePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isAvailable && (coarsePermissionCheck || finePermissionCheck);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = new Location("");
        locationStatus = isLocationServicesAvailable(this);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            locationManager.requestSingleUpdate(criteria,Login.this,null);
        }
        sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
        editPreferences=sharedPreferences.edit();

        title.setText("Login");
        final EditText emailt=findViewById(R.id.txtemail);
        final EditText pass=findViewById(R.id.txtpass);
        Button button=findViewById(R.id.btnLogin);
        TextView textView=findViewById(R.id.txtsignup);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Login.this,SignUpForm.class));
            }
        });
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                final String email=emailt.getText().toString();
                final String code=pass.getText().toString();

                if(email.isEmpty() || code.isEmpty()){
                    Toast.makeText(Login.this,"Please Fill all the form",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i("---------","---");
                Log.i("hello", String.valueOf(locationStatus));

                if(!locationStatus){
                    Toast.makeText(Login.this,"Please Enable Location", Toast.LENGTH_SHORT).show();
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    }
                    else{
                        locationManager.requestSingleUpdate(criteria,Login.this,null);
                    }
                    return;
                }
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String source = msg.obj.toString();
                        try {
                            JSONArray jsonArray = new JSONArray(source);
                            JSONObject userData = jsonArray.getJSONObject(0);
                            if(userData.getString("status").equals("1"))
                            {
                                editPreferences.putString("phone",email.toString());
                                editPreferences.putString("pass",code.toString());
                                editPreferences.putString("id",userData.getString("id"));
                                editPreferences.putString("uname",userData.getString("uname"));
                                editPreferences.commit();
                                Toast.makeText(Login.this, "Successfully login", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Login.this,ShowFriend.class);
                                intent.putExtra("currentLatitude",currentLocation.getLatitude());
                                intent.putExtra("currentLongitude",currentLocation.getLongitude());
                                startActivity(intent);
                                finish();
                            }
                            else if(userData.getString("status").equals("0"))
                            {
                                Intent intent=new Intent(Login.this,VerificationPage.class);
                                intent.putExtra("email",email.toString());
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(Login.this, "Something is Incorrect plz try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "emailid="+email+"&password="+code);

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
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
