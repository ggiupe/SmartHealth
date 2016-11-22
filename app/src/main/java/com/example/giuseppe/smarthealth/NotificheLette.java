package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificheLette extends AppCompatActivity {

    public static List listp_old;
    public static CustomAdapter adapter_old;
    public static ListView mylist_old;


    public static TextView tv_status_mqtt_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiche_lette);

        listp_old=new LinkedList();

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String serverString = sharedPref.getString(getString(R.string.server_addr_current), defaultServer);
        String defaultUsername = getResources().getString(R.string.user_username_default);
        String username = sharedPref.getString(getString(R.string.user_username_current), defaultUsername);
        String defaultPassword = getResources().getString(R.string.user_password_default);
        String password = sharedPref.getString(getString(R.string.user_password_current), defaultPassword);
        final String nomeecognome = sharedPref.getString(getString(R.string.mem_usrnomefull), "");
        final String sitw_id = sharedPref.getString(getString(R.string.mem_usrsitewhere_id), "");

        tv_status_mqtt_old = (TextView) findViewById(R.id.stato_connessione_mqtt_old);
        final TextView tv_id_titolo_old = (TextView) findViewById(R.id.id_title_old);
        final TextView tv_id_valore_old = (TextView) findViewById(R.id.id_value_old);
        final TextView tv_saluto_old = (TextView) findViewById(R.id.saluto_old);
        final TextView tv_nome_completo_old = (TextView) findViewById(R.id.nome_completo_old);
        final TextView tv_esclamativo_old = (TextView) findViewById(R.id.esclamativo_old);
        final TextView tv_status_http_old = (TextView) findViewById(R.id.stato_connessione_http_old);


        if ((username.equals("_-_-_")) || (password.equals("_-_-_")) || (username.equals("")) || (password.equals(""))) {
            tv_id_titolo_old.setText("");
            tv_id_valore_old.setText("");
            tv_saluto_old.setText("");
            tv_nome_completo_old.setText("Non sei connesso.");
            tv_nome_completo_old.setTextColor(Color.RED);
            tv_esclamativo_old.setText("");
            tv_status_http_old.setText("Torna indietro ed");
            tv_status_mqtt_old.setText("effettua il login o registrati.");
        } else {
            if (nomeecognome.equals("")) {
                tv_id_titolo_old.setText("");
                tv_id_valore_old.setText("");
                tv_saluto_old.setText("");
                tv_nome_completo_old.setText("");
                tv_esclamativo_old.setText("");
                tv_status_http_old.setText("");
                tv_status_mqtt_old.setText("");
            } else {
                tv_id_titolo_old.setText("ID: ");
                tv_id_valore_old.setText(sitw_id);
                tv_saluto_old.setText("Ciao ");
                tv_nome_completo_old.setText(nomeecognome);
                tv_esclamativo_old.setText(" !");
                tv_status_http_old.setText("");
                tv_status_mqtt_old.setText("");
            }
            String loginSTRING_tmp = "";
            try {
                JSONObject loginJSON = new JSONObject();
                loginJSON.put("username", username);
                loginJSON.put("password", password);
                loginSTRING_tmp = loginJSON.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String loginSTRING = loginSTRING_tmp;
            final String url = serverString;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            tv_status_http_old.setText("Sei connesso.");
                            try {
                                JSONObject user_data_OBJ = new JSONObject(response);
                                JSONArray notifiche=user_data_OBJ.getJSONArray("read_notif");
                                for (int i = 0; i < notifiche.length(); i++) {
                                    JSONObject curr_notif=notifiche.getJSONObject(i);
                                    String tipo_notifica=curr_notif.getString("type");
                                    if (tipo_notifica.equals("message")){
                                        JSONObject datimessaggio=curr_notif.getJSONObject("data");
                                        String mittente=datimessaggio.getString("from");
                                        String msg_content=datimessaggio.getString("msg");
                                        String msg_date=datimessaggio.getString("date");
                                        String alert_id=datimessaggio.getString("id_alert");
                                        String tipologia="Messaggio da:";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist_old = (ListView) findViewById(R.id.list_old);
                                if (notifiche.length()>0) {
                                    tv_status_mqtt_old.setText("Tocca una notifica per marcarla come NON LETTA.");
                                    mylist_old.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Notifica notifica_pressata = (Notifica) listp_old.get(position);
                                            String notif_alert_id = notifica_pressata.getalert_id();
                                            String notif_msg = notifica_pressata.getMessaggio();
                                            String notif_type = notifica_pressata.getTipologia();
                                            String notif_date = notifica_pressata.getData();
                                            String notif_sender = notifica_pressata.getNome();
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"letta",position,url);
                                            }
                                        }
                                    );
                                }

                                adapter_old=new CustomAdapter(getApplicationContext(), R.layout.rowcustom,listp_old);
                                mylist_old.setAdapter(adapter_old);

                                SharedPreferences sharedPref_f = NotificheLette.this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref_f.edit();

                                JSONObject saved_read_notif_list_json_ob = new JSONObject();
                                saved_read_notif_list_json_ob.put("read_notif",notifiche);
                                String saved_read_notif_list_json_str = saved_read_notif_list_json_ob.toString();
                                editor.putString(getString(R.string.mem_read_notif_json), saved_read_notif_list_json_str);
                                editor.commit();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // Error handling

                    System.out.println("Qualcosa è andato storto nella richiesta!");
                    NetworkResponse err_ = error.networkResponse;
                    String display_err_user_msg = "\n\n\nErrore nell'invio della richiesta.";
                    if (err_ != null && err_.data != null) {
                        int err_status_code = err_.statusCode;
                        String err_status_code_str = ("" + err_status_code);
                        String err_stringa = new String(err_.data);
                        String err_msg = "";
                        int err_stringa_A = err_stringa.indexOf("<p>");
                        err_stringa_A = err_stringa_A + ("<p>").length();
                        int err_stringa_B = err_stringa.indexOf("</p>");
                        if (err_stringa_A > 0 && err_stringa_B > err_stringa_A && err_stringa_B <= err_stringa.length()) {
                            err_msg = err_stringa.substring(err_stringa_A, err_stringa_B);
                            System.out.println("Errore: "+err_msg);
                        }
                        if (nomeecognome.equals("")) {
                            tv_id_titolo_old.setText("");
                            tv_id_valore_old.setText("");
                            tv_saluto_old.setText("");
                            tv_nome_completo_old.setText("ERRORE. Non Connesso.");
                            tv_nome_completo_old.setTextColor(Color.RED);
                            tv_esclamativo_old.setText("");
                            if ((err_status_code!=400)&&(err_status_code!=404)) {
                                tv_status_http_old.setText("L'indirizzo del server non è corretto, oppure il server è down.");
                            } else {
                                tv_status_http_old.setText(err_msg);
                            }
                        } else {
                            tv_id_titolo_old.setText("ID: ");
                            tv_id_valore_old.setText(sitw_id);
                            tv_saluto_old.setText("Ciao ");
                            tv_nome_completo_old.setText(nomeecognome);
                            tv_esclamativo_old.setText(" !");
                            if ((err_status_code!=400)&&(err_status_code!=404)) {
                                tv_status_http_old.setText("ERRORE. Non Connesso. L'indirizzo del Server non è corretto, oppure il server è down.");
                                tv_status_http_old.setTextColor(Color.RED);
                                tv_status_http_old.setTypeface(null, Typeface.BOLD);
                            } else {
                                if (err_msg.equals("")){
                                    tv_status_http_old.setText("ERRORE. Non Connesso. Controlla l'indirizzo del Server.");
                                    tv_status_http_old.setTextColor(Color.RED);
                                    tv_status_http_old.setTypeface(null, Typeface.BOLD);
                                } else {
                                    tv_status_http_old.setText("ERRORE. Non Connesso. "+err_msg);
                                    tv_status_http_old.setTextColor(Color.RED);
                                    tv_status_http_old.setTypeface(null, Typeface.BOLD);
                                }
                            }
                        }
                        SharedPreferences sharedPref = NotificheLette.this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                        String str_json_notif = sharedPref.getString(getString(R.string.mem_read_notif_json), "");
                        if (!str_json_notif.equals("")) {
                            try {
                                JSONObject json_notif=new JSONObject(str_json_notif);
                                JSONArray notifiche=json_notif.getJSONArray("read_notif");
                                for (int i = 0; i < notifiche.length(); i++) {
                                    JSONObject curr_notif=notifiche.getJSONObject(i);
                                    String tipo_notifica=curr_notif.getString("type");
                                    if (tipo_notifica.equals("message")){
                                        JSONObject datimessaggio=curr_notif.getJSONObject("data");
                                        String mittente=datimessaggio.getString("from");
                                        String msg_content=datimessaggio.getString("msg");
                                        String msg_date=datimessaggio.getString("date");
                                        String alert_id=datimessaggio.getString("id_alert");
                                        String tipologia="Messaggio da:";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist_old = (ListView) findViewById(R.id.list_old);
                                if (notifiche.length()>0) {
                                    tv_status_mqtt_old.setText("Tocca una notifica per marcarla come NON LETTA.");
                                    mylist_old.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                            Notifica notifica_pressata = (Notifica) listp_old.get(position);
                                            String notif_alert_id = notifica_pressata.getalert_id();
                                            String notif_msg = notifica_pressata.getMessaggio();
                                            String notif_type = notifica_pressata.getTipologia();
                                            String notif_date = notifica_pressata.getData();
                                            String notif_sender = notifica_pressata.getNome();
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"letta",position,url);
                                        }
                                    });
                                }
                                adapter_old=new CustomAdapter(getApplicationContext(), R.layout.rowcustom,listp_old);
                                mylist_old.setAdapter(adapter_old);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        display_err_user_msg = (display_err_user_msg + "\n\nCODICE: " + err_status_code_str + "\nMESSAGGIO:\n" + err_msg);
                    } else {
                        SharedPreferences sharedPref = NotificheLette.this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                        String str_json_notif = sharedPref.getString(getString(R.string.mem_read_notif_json), "");
                        if (!str_json_notif.equals("")) {
                            try {
                                JSONObject json_notif=new JSONObject(str_json_notif);
                                JSONArray notifiche=json_notif.getJSONArray("read_notif");
                                for (int i = 0; i < notifiche.length(); i++) {
                                    JSONObject curr_notif=notifiche.getJSONObject(i);
                                    String tipo_notifica=curr_notif.getString("type");
                                    if (tipo_notifica.equals("message")){
                                        JSONObject datimessaggio=curr_notif.getJSONObject("data");
                                        String mittente=datimessaggio.getString("from");
                                        String msg_content=datimessaggio.getString("msg");
                                        String msg_date=datimessaggio.getString("date");
                                        String alert_id=datimessaggio.getString("id_alert");
                                        String tipologia="Messaggio da:";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp_old.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist_old = (ListView) findViewById(R.id.list_old);
                                if (notifiche.length()>0) {
                                    tv_status_mqtt_old.setText("Tocca una notifica per marcarla come NON LETTA.");
                                    mylist_old.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                            Notifica notifica_pressata = (Notifica) listp_old.get(position);
                                            String notif_alert_id = notifica_pressata.getalert_id();
                                            String notif_msg = notifica_pressata.getMessaggio();
                                            String notif_type = notifica_pressata.getTipologia();
                                            String notif_date = notifica_pressata.getData();
                                            String notif_sender = notifica_pressata.getNome();
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"letta",position,url);
                                        }
                                    });
                                }
                                adapter_old=new CustomAdapter(getApplicationContext(), R.layout.rowcustom,listp_old);
                                mylist_old.setAdapter(adapter_old);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (nomeecognome.equals("")) {
                            tv_saluto_old.setText("");
                            tv_esclamativo_old.setText("");
                            tv_id_titolo_old.setText("");
                            tv_id_valore_old.setText("");
                            tv_nome_completo_old.setText("Non Connesso");
                            tv_nome_completo_old.setTextColor(Color.RED);
                            tv_status_http_old.setText("Controlla l'indirizzo del Server o la connessione.");
                        } else {
                            tv_saluto_old.setText("Ciao ");
                            tv_esclamativo_old.setText(" !");
                            tv_id_titolo_old.setText("ID: ");
                            tv_id_valore_old.setText(sitw_id);
                            tv_nome_completo_old.setText(nomeecognome);
                            tv_status_http_old.setText("Non Connesso. Controlla l'indirizzo del Server o la connessione.");
                        }
                    }
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("app_get_old_notif", loginSTRING);
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        }
    }
}
