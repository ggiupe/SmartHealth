package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class Registrazione extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
    }
    

    public void riepilogo(View view) throws JSONException {
        Intent nuovo_intento = new Intent(this, VisualizzaRiepilogo.class);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String serverString = sharedPref.getString(getString(R.string.server_addr_current), defaultServer);

        EditText username = (EditText) findViewById(R.id.user_username);
        String usernameString = username.getText().toString();
        usernameString=togli_spazio_finale(usernameString);
        EditText password = (EditText) findViewById(R.id.user_password);
        String passwordString = password.getText().toString();
        EditText password_repeat = (EditText) findViewById(R.id.user_repeat_password);
        String password_repeatString = password_repeat.getText().toString();
        EditText firstnameText = (EditText) findViewById(R.id.user_first_name);
        String firstnameString = firstnameText.getText().toString();
        firstnameString=togli_spazio_finale(firstnameString);
        EditText lastnameText = (EditText) findViewById(R.id.user_last_name);
        String lastnameString = lastnameText.getText().toString();
        lastnameString=togli_spazio_finale(lastnameString);
        Spinner sexSpinner = (Spinner)findViewById(R.id.user_sex);
        String sexString = sexSpinner.getSelectedItem().toString();
        if (sexString.equals("Uomo")) {
            sexString="Male";
        }
        if (sexString.equals("Donna")) {
            sexString="Female";
        }
        DatePicker birthdateDatePicker = (DatePicker)findViewById(R.id.user_birthdate);
        int day = birthdateDatePicker.getDayOfMonth();
        int month = birthdateDatePicker.getMonth() + 1;
        int year = birthdateDatePicker.getYear();

        EditText tax_code_Text = (EditText) findViewById(R.id.user_tax_code);
        String tax_code_String = tax_code_Text.getText().toString();
        tax_code_String=togli_spazio_finale(tax_code_String);
        EditText email_Text = (EditText) findViewById(R.id.user_email);
        String email_String = email_Text.getText().toString();
        email_String=togli_spazio_finale(email_String);
        EditText phone_Text = (EditText) findViewById(R.id.user_phone);
        String phone_String = phone_Text.getText().toString();
        phone_String=togli_spazio_finale(phone_String);
        EditText city_Text = (EditText) findViewById(R.id.user_city);
        String city_String = city_Text.getText().toString();
        city_String=togli_spazio_finale(city_String);
        EditText address_Text = (EditText) findViewById(R.id.user_address);
        String address_String = address_Text.getText().toString();
        address_String=togli_spazio_finale(address_String);
        EditText zip_code_Text = (EditText) findViewById(R.id.user_zip_code);
        String zip_code_String = zip_code_Text.getText().toString();
        zip_code_String=togli_spazio_finale(zip_code_String);

        String total_diseases = "";

        CheckBox a = (CheckBox) findViewById(R.id.user_malattia_a);
        if (a.isChecked()) {
            String dis = a.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox b = (CheckBox) findViewById(R.id.user_malattia_b);
        if (b.isChecked()) {
            String dis = b.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox c = (CheckBox) findViewById(R.id.user_malattia_c);
        if (c.isChecked()) {
            String dis = c.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox d = (CheckBox) findViewById(R.id.user_malattia_d);
        if (d.isChecked()) {
            String dis = d.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox e = (CheckBox) findViewById(R.id.user_malattia_e);
        if (e.isChecked()) {
            String dis = e.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox f = (CheckBox) findViewById(R.id.user_malattia_f);
        if (e.isChecked()) {
            String dis = f.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox g = (CheckBox) findViewById(R.id.user_malattia_g);
        if (g.isChecked()) {
            String dis = g.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }
        CheckBox h = (CheckBox) findViewById(R.id.user_malattia_h);
        if (h.isChecked()) {
            String dis = h.getText().toString();
            if (total_diseases=="") {
                total_diseases=total_diseases+dis;
            }
            else {
                total_diseases=total_diseases+", "+dis;
            }
        }


        JSONObject user_dataJSON = new JSONObject();
        user_dataJSON.put("server",serverString);
        user_dataJSON.put("username",usernameString);
        user_dataJSON.put("password",passwordString);
        user_dataJSON.put("password_repeat",password_repeatString);
        user_dataJSON.put("first_name",firstnameString);
        user_dataJSON.put("last_name",lastnameString);
        user_dataJSON.put("sex",sexString);
        user_dataJSON.put("day",day);
        user_dataJSON.put("month",month);
        user_dataJSON.put("year",year);
        user_dataJSON.put("diseases",total_diseases);
        user_dataJSON.put("tax_code",tax_code_String);
        user_dataJSON.put("email",email_String);
        user_dataJSON.put("phone",phone_String);
        user_dataJSON.put("city",city_String);
        user_dataJSON.put("address",address_String);
        user_dataJSON.put("zip_code",zip_code_String);
        String user_dataSTRING = user_dataJSON.toString();
        nuovo_intento.putExtra(EXTRA_MESSAGE,user_dataSTRING);
        startActivity(nuovo_intento);
    }

    public String togli_spazio_finale(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)==' ') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

}
