package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class AddFriend extends AppCompatActivity {
    final static String JSON_URL = "http://www.til.com.np/track/api/request.php?";
    EditText receiverEmail;
    Button searchFriend;
    String email;
    TextView name;
    TextView phoneNo;
    Button addFriend;
    String ownId,receiverId;
    String receiverName,receiverPhoneno;
    String imageUrl;
    SharedPreferences setting;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        receiverEmail = (EditText) findViewById(R.id.receiverEmail);
        searchFriend = (Button) findViewById(R.id.searchFriend);
        name = (TextView) findViewById(R.id.addfriendName);
        phoneNo = (TextView) findViewById(R.id.addFriendphoneNo);
        addFriend = (Button) findViewById(R.id.addFriend);
        setting = getSharedPreferences("logindata",MODE_PRIVATE);
        ownId = setting.getString("id",null);
        imageView = (ImageView) findViewById(R.id.addFriendImageView);

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        String source = msg.obj.toString();
                        if(source.equals("1")){
                            Toast.makeText(AddFriend.this, "Friend Request Send" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(AddFriend.this, "Something went error!!" , Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "requestedId=" + ownId + "&receiverId=" + receiverId + "&name=" + receiverName + "&phoneNo=" + receiverPhoneno + "&email=" + email);
            }
        });

        searchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = receiverEmail.getText().toString();
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg1) {

                        String source1 = msg1.obj.toString();
                        if(source1.equals("0")){
                            Toast.makeText(AddFriend.this,"No Friend Found",Toast.LENGTH_SHORT).show();
                            name.setText("");
                            phoneNo.setText("");
                            imageView.setVisibility(View.INVISIBLE);
                            addFriend.setVisibility(View.INVISIBLE);
                            return;
                        }
                        try{
                            JSONArray jsonArray = new JSONArray(source1);
                            JSONObject userData = jsonArray.getJSONObject(0);
                            name.setText("Name: " + userData.getString("name"));
                            phoneNo.setText("Phone: " + userData.getString("phone"));
                            receiverId = userData.getString("id");
                            receiverName = userData.getString("name");
                            receiverPhoneno = userData.getString("phone");
                            imageUrl = userData.getString("imageUrl");
                            Glide.with(AddFriend.this).load("http://www.til.com.np/track/api/" + imageUrl)
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                            imageView.setVisibility(View.VISIBLE);
                            addFriend.setVisibility(View.VISIBLE);
                        }
                        catch (Exception e){

                        }

                    }
                };
                HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "searchFriend=" + email);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); //.menu represents id and ,menu represents the object of just above
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, Login.class));
                break;
            //case R.id.menuSettings:
                //Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                //break;
        }
        return true;
    }
}
