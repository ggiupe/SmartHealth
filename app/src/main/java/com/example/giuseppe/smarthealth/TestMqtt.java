package com.example.giuseppe.smarthealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TestMqtt extends AppCompatActivity {
    MyService mBoundServiceb;
    boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mqtt);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String publ = sharedPref.getString(getString(R.string.mem_topic_pub_test), "");
        String subscr = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");

        EditText subTextBox = (EditText) findViewById(R.id.topic_test);
        subTextBox.setText(subscr);
        EditText publTextBox = (EditText) findViewById(R.id.topic_publ);
        publTextBox.setText(publ);

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
            mBoundServiceb = myBinder.getService();
            mServiceBound = true;
            System.out.println("SI e CONNESSO al SERVIZIO");
        }
    };


    public void avvia_sottoscrizione(View view)  {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String old_tpc = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");
        EditText subTextBox = (EditText) findViewById(R.id.topic_test);
        String new_tpc=subTextBox.getText().toString();

        String defaultBroker = getResources().getString(R.string.broker_addr_default);
        String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
        String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
        String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
        String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
        String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
        String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
        String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);
        String pers_tpc=sharedPref.getString(getString(R.string.mem_personal_topic),"");

        if ((!broker_addr.equals(""))&&(!mqtt_port.equals(""))) {

            if (!old_tpc.equals(new_tpc)) {
                if (old_tpc.equals("")) {
                    mBoundServiceb.connetti(broker_addr, mqtt_port, broker_username, broker_password, new_tpc, "no");
                    Toast.makeText(TestMqtt.this, "Sottoscrizione Salvata", Toast.LENGTH_LONG).show();
                } else {
                    if (!new_tpc.equals("")) {
                        mBoundServiceb.unsottoscrivi_e_sottoscrivi(old_tpc, new_tpc, "no");
                        Toast.makeText(TestMqtt.this, "Sottoscrizione Aggiornata", Toast.LENGTH_LONG).show();
                    } else {
                        mBoundServiceb.unsottoscrivi(old_tpc);
                        if (pers_tpc.equals("")) {
                            Toast.makeText(TestMqtt.this, "Sottoscrizione Annullata. Client MQTT disattivato", Toast.LENGTH_LONG).show();
                            try {
                                MyService.disconnetti();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(TestMqtt.this, "Sottoscrizione Annullata.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } else {
                Toast.makeText(TestMqtt.this, "Nessuna modifica da salvare", Toast.LENGTH_LONG).show();
            }
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.mem_topic_sub_test), new_tpc);
        editor.commit();
    }

    public void invia_il_messaggio (View view) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultBroker = getResources().getString(R.string.broker_addr_default);
        String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
        String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
        String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
        String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
        String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
        String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
        String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);
        String pers_tpc = sharedPref.getString(getString(R.string.mem_personal_topic), "");
        String test_sub_tpc = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");

        EditText tpc_pubTextBox = (EditText) findViewById(R.id.topic_publ);
        String tpcpub = tpc_pubTextBox.getText().toString();
        EditText msgTextBox = (EditText) findViewById(R.id.msg_test);
        String msg = msgTextBox.getText().toString();
        if (!tpcpub.equals("")) {
            if ((!pers_tpc.equals(""))||(!test_sub_tpc.equals(""))) {
                mBoundServiceb.invia_messaggio(tpcpub, msg);
            } else {
                mBoundServiceb.connetti_invia_disconnetti(broker_addr, mqtt_port, broker_username, broker_password, tpcpub, msg);
            }
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.mem_topic_pub_test), tpcpub);
        editor.commit();
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
