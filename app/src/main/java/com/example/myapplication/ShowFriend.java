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



public class ShowFriend extends AppCompatActivity implements LocationListener , SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {
    final static String JSON_URL = "http://www.til.com.np/track/api/request.php?";
    //RecyclerView friendRequest;
    RecyclerView showFriend;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);

        sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
        editPreferences=sharedPreferences.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        transparentToolbar();


        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        //friendRequest = (RecyclerView) findViewById(R.id.showFriendRequest);
        showFriend =(RecyclerView) findViewById(R.id.showFriendView);
        setting = getSharedPreferences("logindata",MODE_PRIVATE);
        ownId = setting.getString("id",null);
        email = setting.getString("phone",null);
        //statement1(ownId,null);
        //statement2(ownId,null);
        currentLocation = new Location("");
        currentLocation.setLatitude(getIntent().getDoubleExtra("currentLatitude",0.0));
        currentLocation.setLongitude(getIntent().getDoubleExtra("currentLongitude",0.0));
        fabAddFriend = (FloatingActionButton) findViewById(R.id.fab_addFriend);
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowFriend.this,AddFriend.class));
            }
        });
        friendRequestImage = (ImageView) findViewById(R.id.friendRequestImage);
        friendViewImage = (ImageView) findViewById(R.id.friendViewImage);
        showFriend.setLayoutManager(new LinearLayoutManager(ShowFriend.this));
        //friendRequest.setLayoutManager(new LinearLayoutManager(ShowFriend.this));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if(ContextCompat.checkSelfPermission(ShowFriend.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ShowFriend.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            locationManager.requestSingleUpdate(criteria,ShowFriend.this,null);
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
                //statement1(ownId,null);
                statement2(ownId,null);

            }
        });

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.username);
        TextView nav_email = (TextView)hView.findViewById(R.id.emailid);
        nav_user.setText(sharedPreferences.getString("uname",null));
        nav_email.setText(sharedPreferences.getString("phone",null));

    }
    private void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
           // setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            //setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.friendsList) {
            // Handle the camera action
            startActivity(new Intent(ShowFriend.this, ShowFriend.class));
        }

        else if (id == R.id.requests) {
            startActivity(new Intent(ShowFriend.this, RequestActivity.class));
            // order();

        }
        else if(id== R.id.nav_logout)
        {
            editPreferences.clear();
            editPreferences.putString("phone",null);
            editPreferences.commit();
            Intent intent=new Intent(ShowFriend.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        //friendRequest.setAdapter(friendRequestAdapter);
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
       // statement1(ownId,null);
        statement2(ownId,null);

    }

    public void statement2(String id,View view) {
        mSwipeRefreshLayout.setRefreshing(true);
        friendListArray.clear();
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
                        final String Id = jsonArray.getJSONObject(i).getString("id");
                        final String name = jsonArray.getJSONObject(i).getString("name");
                        final String email = jsonArray.getJSONObject(i).getString("email");
                        final String phone = jsonArray.getJSONObject(i).getString("phone");
                        final String latitude = jsonArray.getJSONObject(i).getString("latitude");
                        final String longitude = jsonArray.getJSONObject(i).getString("longitude");
                        final String imageUrl = jsonArray.getJSONObject(i).getString("imageUrl");
                        Log.d("--------",imageUrl);
                        distanceHandler = new Handler()
                        {
                            @Override
                            public void handleMessage(Message msg) {
                                String source = msg.obj.toString();

                                try
                                {
                                    JSONObject jsonObject=new JSONObject(source);
                                    String distance = jsonObject.getJSONArray("routes").
                                            getJSONObject(0).getJSONArray("legs").
                                            getJSONObject(0).getJSONObject("distance").
                                            getString("text");
                                    Log.d("-------source",distance);
                                    friendListArray.add(new Name(Id,name,email,phone,latitude,longitude,imageUrl,distance));
                                    friendListAdapter1=new FriendListAdapter1();
                                    friendListAdapter1.notifyDataSetChanged();
                                    showFriend.setAdapter(friendListAdapter1);

    //                                Toast.makeText(ShowFriend.this,distance,Toast.LENGTH_SHORT).show();

                                    mSwipeRefreshLayout.setRefreshing(false);

                                } catch (Exception e) {

                                }
                            }
                        };
                        new HttpSourceRequest(distanceHandler, getUrl(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),new LatLng(Double.valueOf(latitude),Double.valueOf(longitude)),"driving"));

                    }
                } catch (Exception e) {
                    Log.d("--------",e.toString());
                }


            }


        };
        httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "getFriend=" + id);
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
            Glide.with(ShowFriend.this).load("http://www.til.com.np/track/api/" + friendRequestArray.get(position).getImageUrl())
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
            Glide.with(ShowFriend.this).load("http://www.til.com.np/track/api/" + friendListArray.get(position).getImageUrl())
                    .thumbnail(0.5f)
                   .crossFade()
                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                   .into(holder.imageView2);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShowFriend.this,MapsActivity.class);
                    intent.putExtra("currentLatitude",currentLocation.getLatitude());
                    intent.putExtra("fname",friendListArray.get(position).getName());
                    intent.putExtra("currentLongitude",currentLocation.getLongitude());
                    intent.putExtra("destinationLatitude",Double.valueOf(friendListArray.get(position).getLatitude()));
                    intent.putExtra("destinationLongitude",Double.valueOf(friendListArray.get(position).getLongitude()));
                    startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowFriend.this);
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
