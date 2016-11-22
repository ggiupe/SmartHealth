package com.example.giuseppe.smarthealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginLogout extends AppCompatActivity {
    MyService mBoundServicec;
    boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_logout);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultUsername = getResources().getString(R.string.user_username_default);
        String username = sharedPref.getString(getString(R.string.user_username_current), defaultUsername);
        String defaultPassword = getResources().getString(R.string.user_password_default);
        String password = sharedPref.getString(getString(R.string.user_password_current), defaultPassword);
        String default_logged_status="not_logged";
        String logged_status=sharedPref.getString(getString(R.string.logged_in), default_logged_status);

        //ViewGroup layout = (ViewGroup) findViewById(R.id.activity_server_settings);
        EditText usernameTextBox = (EditText) findViewById(R.id.username_login);
        if (username.equals("_-_-_")) {
            username="";
        }
        usernameTextBox.setText(username);
        EditText passwordTextBox = (EditText) findViewById(R.id.password_login);
        if (password.equals("_-_-_")) {
            password="";
        }
        passwordTextBox.setText(password);
        if (logged_status.equals("not_logged")) {
            Button l_in = (Button) findViewById(R.id.button_login);
            Button l_out = (Button) findViewById(R.id.button_logout);
            l_in.setEnabled(true);
            l_out.setEnabled(false);
        }
        if (logged_status.equals("logged")) {
            Button l_in = (Button) findViewById(R.id.button_login);
            Button l_out = (Button) findViewById(R.id.button_logout);
            l_in.setEnabled(false);
            l_out.setEnabled(true);
        }
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            mBoundServicec = myBinder.getService();
            mServiceBound = true;
            System.out.println("SI e CONNESSO al SERVIZIO");
        }
    };


    public void try_login(View view)  {
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_login_logout);

        EditText new_username_box = (EditText) findViewById(R.id.username_login); //(R.id.username_login);
        String new_username = new_username_box.getText().toString();
        EditText new_password_box = (EditText) findViewById(R.id.password_login);
        String new_password = new_password_box.getText().toString();

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user_username_current), new_username);
        editor.commit();
        editor.putString(getString(R.string.user_password_current), new_password);
        editor.commit();
        super.onBackPressed();

    }

    public void try_logout(View view)  {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user_username_current), "");
        editor.commit();
        editor.putString(getString(R.string.user_password_current), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrusername), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrpassword), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrindirizzo), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrnacita), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrcitta), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrnome), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrcognome), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrcap), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrtelefono), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrnomefull), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrsitewhere_id), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrsesso), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrpatologie), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usrcodicefiscale), "");
        editor.commit();
        editor.putString(getString(R.string.mem_usremail), "");
        editor.commit();
        editor.putString(getString(R.string.mem_not_read_notif_json), "");
        editor.commit();
        editor.putString(getString(R.string.mem_read_notif_json), "");
        editor.commit();
        String pers_tpc = sharedPref.getString(getString(R.string.mem_personal_topic), "");
        String test_tpc = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");
        if (!pers_tpc.equals("")) {
            editor.putString(getString(R.string.mem_personal_topic), "");
            editor.commit();
            if (test_tpc.equals("")) {
                try {
                    mBoundServicec.disconnetti();
                    //cambia_broker=false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mBoundServicec.unsottoscrivi(pers_tpc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        System.out.println("On_Stop_callback_di loginlogout");
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }
}