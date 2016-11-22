package com.example.giuseppe.smarthealth;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

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

/**
 * Created by giuseppe on 10/11/16.
 */
public class GestoreStatoNotifiche   extends Application {

    //private static Context context_b;

    public void onCreate () {
        super.onCreate();
        //context_b = getApplicationContext();
    }

    static void cambia_stato_notifica (String id_notifica, String id_paziente, final String stato_originale, final int posizione, String server_url) {

        String change_status_string_tmp = "";
        try {
            JSONObject change_notif_status_string = new JSONObject();
            change_notif_status_string.put("pat_sitewhere_id", id_paziente);
            change_notif_status_string.put("alert_id", id_notifica);
            change_status_string_tmp = change_notif_status_string.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String change_status_string = change_status_string_tmp;
        String url = server_url;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("ok")) {
                            if (stato_originale.equals("letta")) {
                                NotificheLette.listp_old.remove(posizione);
                                NotificheLette.adapter_old.notifyDataSetChanged();
                                if (NotificheLette.listp_old.size()==0) {
                                    NotificheLette.tv_status_mqtt_old.setText("");
                                }
                                Toast.makeText(MainActivity.activity_main, "Notifica contrassegnata come NON LETTA", Toast.LENGTH_LONG).show();
                            }
                            if (stato_originale.equals("non_letta")) {
                                MainActivity.listp.remove(posizione);
                                MainActivity.adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.activity_main, "Notifica contrassegnata come LETTA", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (response.equals("ko")) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse err_ = error.networkResponse;
                        if (err_ != null && err_.data != null) {

                        } else {

                        }
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("app_change_notif_status", change_status_string);
                        return params;
                    }
                };
                Volley.newRequestQueue(MainActivity.activity_main).add(stringRequest);
    }
}
