package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VerificationPage extends AppCompatActivity {
String email;
EditText editText;
public static final String JSON_URL = "http://www.til.com.np/track/api/request.php?";
Button button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //below setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        TextView title=(TextView)findViewById(R.id.action_bar_title);
        title.setText("User Activation");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email=getIntent().getStringExtra("email");
        editText=findViewById(R.id.editText2);
        button=findViewById(R.id.button);
        sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
        editPreferences=sharedPreferences.edit();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=editText.getText().toString();
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String source = msg.obj.toString();

                        if(source.equals("1"))
                        {
                            editPreferences.putString("phone",email.toString());
                            editPreferences.commit();
                            Intent intent=new Intent(VerificationPage.this,ShowFriend.class);
                            intent.putExtra("email",email.toString());
                            startActivity(intent);
                            finish();
                            Toast.makeText(VerificationPage.this, "Successfully verified for the app", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(VerificationPage.this, "Enter valid code number", Toast.LENGTH_SHORT).show();
                        }

                    }


                };
                HttpSourceRequest httpSourceRequest = new HttpSourceRequest(handler, JSON_URL + "email="+email+"&code="+code);

            }
        });


    }
}
