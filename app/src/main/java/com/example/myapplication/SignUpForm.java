package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpForm extends AppCompatActivity implements LocationListener, BottomModelSheet.BottomSheetListener {

    ProgressDialog progressDialog;
    public static final String JSON_URL = "http://www.til.com.np/track/api/signup.php";
    private EditText signupInputName, signupInputEmail,signupInputPassword,signupInputNumber;
    private Button btnSignUp,btnUplaod;
    LocationManager locationManager;
    Location currentLocation;
    ImageView userImage;
    int REQUEST_CAMERA = 1000;
    int REQUEST_GALLERY = 1001;
    String encodedImage = "";


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    locationManager.requestSingleUpdate(criteria,SignUpForm.this,null);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("User Signup");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = new Location("");
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }
        else{
            locationManager.requestSingleUpdate(criteria,SignUpForm.this,null);
        }

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        signupInputName = (EditText) findViewById(R.id.signup_input_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email1);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);

        signupInputNumber = (EditText) findViewById(R.id.signup_input_email);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        userImage = (ImageView) findViewById(R.id.userImage);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnUplaod = (Button) findViewById(R.id.btn_upload);
        btnUplaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomModelSheet bottomModelSheet = new BottomModelSheet();
                bottomModelSheet.show(getSupportFragmentManager(),"CameraBottomModalSheet");
            }
        });
    }

    private void submitForm() {



        final String name=signupInputName.getText().toString(),password=signupInputPassword.getText().toString(),number=signupInputNumber.getText().toString(),email=signupInputEmail.getText().toString(),lat = String.valueOf(currentLocation.getLatitude()), longitude = String.valueOf(currentLocation.getLongitude());
        Log.d("----------->>>","Hello");
        Log.d("----------->>>","");
        Log.d("----------->>>",name);
        Log.d("----------->>>",email);
        Log.d("----------->>>",password);


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")) {
                        Toast.makeText(SignUpForm.this, "Successfully register your account enter activation key from email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpForm.this, VerificationPage.class);
                        intent.putExtra("email", email.toString());
                        startActivity(intent);
                    } else {

                    }
                        // response
                        Log.d("----------Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response",""+error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password",password);
                params.put("phone",number);
                params.put("lat",lat);
                params.put("longitude",longitude);
                params.put("imageUrl",encodedImage);
                return params;
            }
        };
        queue.add(postRequest);
       /* if(name!=null || email!=null || password!=null) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg)
                {
                    String source = msg.obj.toString();
//                    if (source.equals("1")) {
//                        Toast.makeText(SignUpForm.this, "Successfully register your account enter activation key from email", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(SignUpForm.this, VerificationPage.class);
//                        intent.putExtra("email", email.toString());
//                        startActivity(intent);
//                    } else {
//
//                    }
                    Log.d("----------->>>","Hello");
                    Log.d("Hello",source);

                }
            };
            HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "name=" + name + "&email=" + email + "&password=" + password + "&phone=" + number + "&lat=" + lat + "&longitude=" + longitude);
        }
        else
        {
            Toast.makeText(this, "Enter valid data", Toast.LENGTH_SHORT).show();
        }*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CAMERA){
                Bundle bundle = data.getExtras();
                final Bitmap btmp = (Bitmap) bundle.get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                btmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                userImage.setImageBitmap(btmp);
            }
            else if (requestCode == REQUEST_GALLERY){
                Uri imageUri = data.getData();
                try{
                    Bitmap bitmap= Images.Media.getBitmap(getContentResolver(),imageUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    userImage.setImageBitmap(bitmap);
                }
                catch (IOException e){
                    Log.d("Error",e.getMessage().toString());
                }
            }
        }
    }


    void checkPermissionForCamera(){
        int permissioncheck = ContextCompat.checkSelfPermission(SignUpForm.this,Manifest.permission.CAMERA);
        if(permissioncheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SignUpForm.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
        }
        else{
            int permissioncheckRead = ContextCompat.checkSelfPermission(SignUpForm.this,Manifest.permission.READ_EXTERNAL_STORAGE);
            if(permissioncheckRead != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(SignUpForm.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
            }
            else{
                int permissioncheckWrite = ContextCompat.checkSelfPermission(SignUpForm.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permissioncheckWrite != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SignUpForm.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
                }
                else{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent,REQUEST_CAMERA);
                    }
                }
            }
        }
    }

    void checkPermissionForGallery(){
        int permissioncheckRead = ContextCompat.checkSelfPermission(SignUpForm.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissioncheckRead != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SignUpForm.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
        }
        else{
            int permissioncheckWrite = ContextCompat.checkSelfPermission(SignUpForm.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissioncheckWrite != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(SignUpForm.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
            }
            else{

                Intent intent = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(Intent.createChooser(intent,"Select File"),REQUEST_GALLERY);
                }
            }
        }
    }

    @Override
    public void onBottomClicked(int btnId) {

   if(btnId == 1){
            checkPermissionForCamera();
        }
        else if(btnId == 2){
         checkPermissionForGallery();
        }
    }
}
