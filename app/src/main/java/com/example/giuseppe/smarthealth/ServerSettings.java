package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ServerSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String server_addr = sharedPref.getString(getString(R.string.server_addr_current), defaultServer);
        String defaultBroker = getResources().getString(R.string.broker_addr_default);
        String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
        String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
        String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
        String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
        String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
        String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
        String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);

        //ViewGroup layout = (ViewGroup) findViewById(R.id.activity_server_settings);
        EditText serverTextBox = (EditText) findViewById(R.id.server_address);
        serverTextBox.setText(server_addr);
        EditText brokerTextBox = (EditText) findViewById(R.id.broker_address);
        brokerTextBox.setText(broker_addr);
        EditText mqttPortTextBox = (EditText) findViewById(R.id.mqtt_port);
        mqttPortTextBox.setText(mqtt_port);
        EditText brokerUsernameTextBox = (EditText) findViewById(R.id.broker_username);
        brokerUsernameTextBox.setText(broker_username);
        EditText brokerPasswordTextBox = (EditText) findViewById(R.id.broker_password);
        brokerPasswordTextBox.setText(broker_password);
    }

    public void save_connection_settings(View view) {
        EditText new_server_addr_box = (EditText) findViewById(R.id.server_address);
        String new_server_addr = new_server_addr_box.getText().toString();
        EditText new_broker_addr_box = (EditText) findViewById(R.id.broker_address);
        String new_broker_addr = new_broker_addr_box.getText().toString();
        EditText new_mqtt_port_box = (EditText) findViewById(R.id.mqtt_port);
        String new_mqtt_port = new_mqtt_port_box.getText().toString();
        EditText new_broker_username_box = (EditText) findViewById(R.id.broker_username);
        String new_broker_username = new_broker_username_box.getText().toString();
        EditText new_broker_password_box = (EditText) findViewById(R.id.broker_password);
        String new_broker_password = new_broker_password_box.getText().toString();

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.server_addr_current), new_server_addr);
        editor.commit();
        editor.putString(getString(R.string.broker_addr_current), new_broker_addr);
        editor.commit();
        editor.putString(getString(R.string.mqtt_port_current), new_mqtt_port);
        editor.commit();
        editor.putString(getString(R.string.broker_username_current), new_broker_username);
        editor.commit();
        editor.putString(getString(R.string.broker_password_current), new_broker_password);
        editor.commit();

        MainActivity.cambia_broker = true;
        super.onBackPressed();
    }
}