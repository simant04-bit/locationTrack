package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



public class RequestActivity extends AppCompatActivity implements LocationListener , SwipeRefreshLayout.OnRefreshListener {
    final static String JSON_URL = "http://www.til.com.np/track/api/request.php?";
    RecyclerView friendRequest;
    //RecyclerView showFriend;
    FriendRequestAdapter1 friendRequestAdapter;
    FriendListAdapter1 friendListAdapter1;
    HttpSourceRequest httpSourceRequest;
    Handler handler;
    String ownId;
    String email;
    LocationManager locationManager;
    Location currentLocation;
    SharedPreferences setting;
    ArrayList<Name> friendRequestArray = new ArrayList<>();
    ArrayList<Name> friendListArray = new ArrayList<>();
    FloatingActionButton fabAddFriend;
    ImageView friendRequestImage,friendViewImage;
    Handler distanceHandler;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        friendRequest = (RecyclerView) findViewById(R.id.showFriendRequest);
        //showFriend =(RecyclerView) findViewById(R.id.showFriendView);
        setting = getSharedPreferences("logindata",MODE_PRIVATE);
        ownId = setting.getString("id",null);
        email = setting.getString("phone",null);
        //statement1(ownId,null);
        //statement2(ownId,null);
        currentLocation = new Location("");
        currentLocation.setLatitude(getIntent().getDoubleExtra("currentLatitude",0.0));
        currentLocation.setLongitude(getIntent().getDoubleExtra("currentLongitude",0.0));

        friendRequestImage = (ImageView) findViewById(R.id.friendRequestImage);
        friendViewImage = (ImageView) findViewById(R.id.friendViewImage);
        //showFriend.setLayoutManager(new LinearLayoutManager(RequestActivity.this));
        friendRequest.setLayoutManager(new LinearLayoutManager(RequestActivity.this));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if(ContextCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            locationManager.requestSingleUpdate(criteria,RequestActivity.this,null);
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                // loadRecyclerViewData();
                statement1(ownId,null);
                //statement2(ownId,null);

            }
        });

    }

    public void statement1(String id,View view) {
        mSwipeRefreshLayout.setRefreshing(true);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String source = msg.obj.toString();
                Log.d("Result", source);
                try
                {
                    JSONArray jsonArray = new JSONArray(source);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        String Id = jsonArray.getJSONObject(i).getString("id");
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String email = jsonArray.getJSONObject(i).getString("email");
                        String phone = jsonArray.getJSONObject(i).getString("phone");
                        String latitude = jsonArray.getJSONObject(i).getString("latitude");
                        String longitude = jsonArray.getJSONObject(i).getString("longitude");
                        String imageUrl = jsonArray.getJSONObject(i).getString("imageUrl");
                        friendRequestArray.add(new Name(Id,name,email,phone,latitude,longitude,imageUrl,""));
                        friendRequestAdapter=new FriendRequestAdapter1();
                        friendRequestAdapter.notifyDataSetChanged();
                        friendRequest.setAdapter(friendRequestAdapter);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                } catch (Exception e) {

                }
            }
        };
        httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "getPendingRequest=" + id);
    }
    @Override
    public void onRefresh() {

        // Fetching data from server
         statement1(ownId,null);
        //statement2(ownId,null);

    }



    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    public void statement3(String id,View view) {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String source = msg.obj.toString();
                try
                {
                    if(source.equals("1")){
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplication(),"Friend Request Accepted!!",Toast.LENGTH_SHORT).show();
                    }
                    else if(source.equals("0")){
                        Toast.makeText(getApplication(),"Unable to accept friend request",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

        };
        httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "acceptRequestId=" + id);
    }

    public void statement4(String id,View view) {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String source = msg.obj.toString();
                try
                {

                    if(source.equals("1")){
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplication(),"Friend Request Rejected!!",Toast.LENGTH_SHORT).show();
                    }
                    else if(source.equals("0")){
                        Toast.makeText(getApplication(),"Unable to reject friend request",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

        };
        httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "rejectRequestId=" + id);
    }

    public void statement5(String id,View view) {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String source = msg.obj.toString();
                try
                {
                    if(source.equals("1")){
                        finish();
                        startActivity(getIntent());
                        Toast.makeText(getApplication(),"Friend Removed!!",Toast.LENGTH_SHORT).show();
                    }
                    else if(source.equals("0")){
                        Toast.makeText(getApplication(),"Unable to remove friend",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

        };
        httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "rejectRequestId=" + id);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "emailid="+email + "&lat=" + String.valueOf(location.getLatitude()) + "&longitude=" + String.valueOf(location.getLongitude()));
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

    class FriendRequestAdapter1 extends RecyclerView.Adapter<ViewHolder1>{

        @NonNull
        @Override
        public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view =getLayoutInflater().inflate(R.layout.activity_friend_request,parent,false);
            return new ViewHolder1(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder1 holder, final int position) {
            holder.friendName.setText(friendRequestArray.get(position).getName());
            Glide.with(RequestActivity.this).load("http://www.til.com.np/track/api/" + friendRequestArray.get(position).getImageUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView1);
            holder.acceptBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            statement3(friendRequestArray.get(position).getId(),null);
                        }
                    }
            );
            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statement4(friendRequestArray.get(position).getId(),null);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendRequestArray.size();
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView friendName;
        Button acceptBtn;
        Button rejectBtn;
        ImageView imageView1;
        public ViewHolder1(final View itemView) {
            super(itemView);
            friendName = (TextView) itemView.findViewById(R.id.fr_friendName);
            acceptBtn = (Button) itemView.findViewById(R.id.fr_accept);
            rejectBtn = (Button) itemView.findViewById(R.id.fr_reject);
            imageView1 = (ImageView) itemView.findViewById(R.id.friendRequestImage);
        }
    }


    class FriendListAdapter1 extends RecyclerView.Adapter<ViewHolder2>{

        @NonNull
        @Override
        public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view =getLayoutInflater().inflate(R.layout.activity_friendview,parent,false);
            return new ViewHolder2(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder2 holder, final int position)
        {
            holder.friendName.setText(friendListArray.get(position).getName());
            holder.friendViewPhoneNo.setText(friendListArray.get(position).getPhone());
            holder.friendDistance.setText(friendListArray.get(position).getDistance());
            Glide.with(RequestActivity.this).load("http://www.til.com.np/track/api/" + friendListArray.get(position).getImageUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView2);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RequestActivity.this,MapsActivity.class);
                    intent.putExtra("currentLatitude",currentLocation.getLatitude());
                    intent.putExtra("currentLongitude",currentLocation.getLongitude());
                    intent.putExtra("destinationLatitude",Double.valueOf(friendListArray.get(position).getLatitude()));
                    intent.putExtra("destinationLongitude",Double.valueOf(friendListArray.get(position).getLongitude()));
                    startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
                    builder.setMessage("Do you want to remove friend ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    statement5(friendListArray.get(position).getId(),null);
                                }
                            }).setNegativeButton("CANCEL",null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendListArray.size();
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView friendName;
        ImageView imageView2;
        TextView friendViewPhoneNo;
        TextView friendDistance;
        public ViewHolder2(final View itemView) {
            super(itemView);
            imageView2 = (ImageView) itemView.findViewById(R.id.friendViewImage);
            friendName = (TextView) itemView.findViewById(R.id.fv_friendName);
            friendViewPhoneNo = (TextView) itemView.findViewById(R.id.friendViewPhoneNo);
            friendDistance = (TextView) itemView.findViewById(R.id.friendViewDistance);
        }
    }

    public class Name {
        private String id;
        private String Name;
        private String email;
        private String phone;
        private String latitude;
        private String longitude;
        private String imageUrl;
        private String distance;
        public Name(String id, String name, String email, String phone, String latitude, String longitude,String imageUrl,String distance ) {
            this.id = id;
            Name = name;
            this.email = email;
            this.phone = phone;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageUrl = imageUrl;
            this.distance = distance;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return Name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getDistance() {
            return distance;
        }
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
