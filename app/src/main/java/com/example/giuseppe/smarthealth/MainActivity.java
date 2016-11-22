package com.example.giuseppe.smarthealth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MyService mBoundService;
    boolean mServiceBound = false;

    public static boolean avvio = false;
    public static boolean cambia_broker = false;
    public static boolean broker_esatto = false;
    public static boolean dont_resume = false;

    public static List listp;
    public static List liste=new LinkedList();
    public static CustomAdapter adapter;
    public static ListView mylist;
    public static TextView tv_status_mqtt;


    public static Activity activity_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avvio=true;
        activity_main=this;
        liste=new LinkedList();
        loadActivity();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
            System.out.println("binding di MainActivity al service MyService");
        }
    };

    private void loadActivity() {
        System.out.println("esecuzione parte principale dell'activity MainActivity");
        if (mServiceBound==false) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
            System.out.println("chiamata startService");
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            System.out.println("binding di MainActivity al service MyService");
        }

        listp=new LinkedList();
        liste=new LinkedList();

        final boolean connected = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.contenuto_principale);
        //final RelativeLayout notif_layout = (RelativeLayout) findViewById(R.id.contenuto_notifiche);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String serverString = sharedPref.getString(getString(R.string.server_addr_current), defaultServer);
        String defaultUsername = getResources().getString(R.string.user_username_default);
        String username = sharedPref.getString(getString(R.string.user_username_current), defaultUsername);
        String defaultPassword = getResources().getString(R.string.user_password_default);
        String password = sharedPref.getString(getString(R.string.user_password_current), defaultPassword);
        String defaultBroker = getResources().getString(R.string.broker_addr_default);
        final String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
        String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
        final String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
        String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
        final String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
        String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
        final String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);
        final String sitw_id = sharedPref.getString(getString(R.string.mem_usrsitewhere_id), "");
        final String nomeecognome = sharedPref.getString(getString(R.string.mem_usrnomefull), "");

        String msg_esterni_json =sharedPref.getString(getString(R.string.messaggi_fuori_piattaforma_json),"{\"ext_msg\":[]}");


        tv_status_mqtt= (TextView) findViewById(R.id.stato_connessione_mqtt);
        final TextView tv_id_titolo = (TextView) findViewById(R.id.id_title);
        final TextView tv_id_valore = (TextView) findViewById(R.id.id_value);
        final TextView tv_saluto = (TextView) findViewById(R.id.saluto);
        final TextView tv_nome_completo = (TextView) findViewById(R.id.nome_completo);
        final TextView tv_esclamativo = (TextView) findViewById(R.id.esclamativo);
        final TextView tv_status_http = (TextView) findViewById(R.id.stato_connessione_http);
        //final TextView tv_status_mqtt = (TextView) findViewById(R.id.stato_connessione_mqtt);

        if ((username.equals("_-_-_")) || (password.equals("_-_-_")) || (username.equals("")) || (password.equals(""))) {
            SharedPreferences sharedPref_f = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref_f.edit();
            editor.putString(getString(R.string.logged_in), "not_logged");
            editor.commit();

            tv_id_titolo.setText("");
            tv_id_valore.setText("");
            tv_saluto.setText("");
            tv_esclamativo.setText("");
            tv_nome_completo.setText("Non sei connesso.");
            tv_nome_completo.setTextColor(Color.RED);
            tv_status_http.setText("");
            tv_status_mqtt.setText("Effettua il login o registrati.");

            try {
                JSONObject ext_msgs_ob = new JSONObject(msg_esterni_json);
                JSONArray lista_msg_ext = ext_msgs_ob.getJSONArray("ext_msg");
                for (int i = 0; i < lista_msg_ext.length(); i++) {
                    String curr_ext_m = lista_msg_ext.getString(i);
                    liste.add(0, new Notifica("Messaggio","esterno alla piattaforma","",curr_ext_m,""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean remove_tmp=false;

            if (liste.size()==0) {
                liste.add(0, new Notifica("", "", "", "", ""));
                remove_tmp=true;
            }
            mylist = (ListView) findViewById(R.id.list);

            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    Notifica notifica_pressata = (Notifica) liste.get(position);
                    String notif_alert_id = notifica_pressata.getalert_id();
                    String notif_msg = notifica_pressata.getMessaggio();
                    String notif_type = notifica_pressata.getTipologia();
                    String notif_date = notifica_pressata.getData();
                    String notif_sender = notifica_pressata.getNome();
                    if (notif_alert_id.equals("")) {
                        liste.remove(position);
                        elimina_msg_esterno(notif_msg,position);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            adapter=new CustomAdapter(getApplicationContext(), R.layout. rowcustom,liste);
            mylist.setAdapter(adapter);
            if (remove_tmp==true) {
                liste.remove(0);
                adapter.notifyDataSetChanged();
                remove_tmp=false;
            }

        } else {
            try {
                JSONObject ext_msgs_ob = new JSONObject(msg_esterni_json);
                JSONArray lista_msg_ext = ext_msgs_ob.getJSONArray("ext_msg");
                for (int i = 0; i < lista_msg_ext.length(); i++) {
                    String curr_ext_m = lista_msg_ext.getString(i);
                    liste.add(0, new Notifica("Messaggio","esterno alla piattaforma","",curr_ext_m,""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean remove_tmp=false;

            if (liste.size()==0) {
                liste.add(0, new Notifica("", "", "", "", ""));
                remove_tmp=true;
            }
            mylist = (ListView) findViewById(R.id.list);

            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    Notifica notifica_pressata = (Notifica) liste.get(position);
                    String notif_alert_id = notifica_pressata.getalert_id();
                    String notif_msg = notifica_pressata.getMessaggio();
                    String notif_type = notifica_pressata.getTipologia();
                    String notif_date = notifica_pressata.getData();
                    String notif_sender = notifica_pressata.getNome();
                    if (notif_alert_id.equals("")) {
                        liste.remove(position);
                        elimina_msg_esterno(notif_msg,position);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            adapter=new CustomAdapter(getApplicationContext(), R.layout. rowcustom,liste);
            mylist.setAdapter(adapter);
            if (remove_tmp==true) {
                liste.remove(0);
                adapter.notifyDataSetChanged();
                remove_tmp=false;
            }

            if (nomeecognome.equals("")) {
                tv_id_titolo.setText("");
                tv_id_valore.setText("");
                tv_saluto.setText("");
                tv_nome_completo.setText("");
                tv_esclamativo.setText("");
                tv_status_http.setText("");
                if (broker_esatto==true) {
                    tv_status_mqtt.setText("Notifiche Push: Attive.");
                }
            } else {
                tv_id_titolo.setText("ID: ");
                tv_id_valore.setText(sitw_id);
                tv_saluto.setText("Ciao ");
                tv_nome_completo.setText(nomeecognome);
                tv_esclamativo.setText(" !");
                tv_status_http.setText("");
                if (broker_esatto==true) {
                    tv_status_mqtt.setText("Notifiche Push: Attive.");
                }
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
            final RelativeLayout finallayout = layout;
            final String url = serverString;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            listp=liste;
                            try {
                                int layout_notif_id=0;
                                JSONObject user_data_OBJ = new JSONObject(response);
                                String topic=user_data_OBJ.getString("topic");
                                String usrusername=user_data_OBJ.getString("username");
                                String usrpassword=user_data_OBJ.getString("password");
                                String usrindirizzo=user_data_OBJ.getString("indirizzo");
                                String usrnacita=user_data_OBJ.getString("nascita");
                                String usrcitta=user_data_OBJ.getString("citta");
                                String usrnome=user_data_OBJ.getString("nome");
                                String usrcognome=user_data_OBJ.getString("cognome");
                                String usrcap=user_data_OBJ.getString("cap");
                                String usrtelefono=user_data_OBJ.getString("telefono");
                                String usrnomefull=user_data_OBJ.getString("nome_full");
                                String usrsitewhere_id=user_data_OBJ.getString("sitewhere_id");
                                String usrsesso=user_data_OBJ.getString("sesso");
                                String usrpatologie=user_data_OBJ.getString("patologie");
                                String usrcodicefiscale=user_data_OBJ.getString("cod_fisc");
                                String usremail=user_data_OBJ.getString("email");
                                JSONArray notifiche=user_data_OBJ.getJSONArray("not_read_notif");
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
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist = (ListView) findViewById(R.id.list);
                                mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                        Notifica notifica_pressata = (Notifica) listp.get(position);
                                        String notif_alert_id = notifica_pressata.getalert_id();
                                        String notif_msg = notifica_pressata.getMessaggio();
                                        String notif_type = notifica_pressata.getTipologia();
                                        String notif_date = notifica_pressata.getData();
                                        String notif_sender = notifica_pressata.getNome();
                                        if ((!sitw_id.equals(""))&&(!notif_alert_id.equals(""))){
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"non_letta",position,url);
                                        } else {
                                            if (notif_alert_id.equals("")) {
                                                try {
                                                    liste.remove(position);
                                                    elimina_msg_esterno(notif_msg,position);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                adapter=new CustomAdapter(getApplicationContext(), R.layout. rowcustom,listp);
                                mylist.setAdapter(adapter);

                                tv_id_titolo.setText("ID: ");
                                tv_id_valore.setText(usrsitewhere_id);
                                tv_saluto.setText("Ciao ");
                                tv_nome_completo.setText(usrnomefull);
                                tv_esclamativo.setText(" !");
                                tv_status_http.setText("Sei connesso");

                                SharedPreferences sharedPref_f = MainActivity.this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref_f.edit();
                                editor.putString(getString(R.string.logged_in), "logged");
                                editor.commit();
                                editor.putString(getString(R.string.mem_personal_topic), topic);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrusername), usrusername);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrpassword), usrpassword);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrindirizzo), usrindirizzo);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrnacita), usrnacita);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrcitta), usrcitta);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrnome), usrnome);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrcognome), usrcognome);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrcap), usrcap);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrtelefono), usrtelefono);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrnomefull), usrnomefull);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrsitewhere_id), usrsitewhere_id);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrsesso), usrsesso);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrpatologie), usrpatologie);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usrcodicefiscale), usrcodicefiscale);
                                editor.commit();
                                editor.putString(getString(R.string.mem_usremail), usremail);
                                editor.commit();
                                JSONObject saved_notif_list_json_ob = new JSONObject();
                                saved_notif_list_json_ob.put("not_read_notif",notifiche);
                                String saved_notif_list_json_str = saved_notif_list_json_ob.toString();
                                editor.putString(getString(R.string.mem_not_read_notif_json), saved_notif_list_json_str);
                                editor.commit();

                                if (cambia_broker==false) {
                                    try {
                                        mBoundService.connetti(broker_addr, mqtt_port, broker_username, broker_password, topic, "yes");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        mBoundService.cambiabroker(broker_addr, mqtt_port, broker_username, broker_password, topic, "yes");
                                    } catch (Exception e) {
                                        editor.putString(getString(R.string.mem_topic_sub_test), "");
                                        e.printStackTrace();
                                    }
                                }
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // Error handling
                    tv_status_mqtt.setText("");
                    broker_esatto=false;

                    SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String pers_tpc = sharedPref.getString(getString(R.string.mem_personal_topic), "");
                    String test_tpc = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");
                    if (!pers_tpc.equals("")) {
                        editor.putString(getString(R.string.mem_personal_topic), "");
                        editor.commit();
                        if (test_tpc.equals("")) {
                            try {
                                mBoundService.disconnetti();
                                //cambia_broker=false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                mBoundService.unsottoscrivi(pers_tpc);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    System.out.println("Qualcosa è andato storto nella richiesta http!");
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
                            System.out.println("messaggio dell'errore: "+err_msg);
                        }
                        if (nomeecognome.equals("")) {
                            tv_id_titolo.setText("");
                            tv_id_valore.setText("");
                            tv_saluto.setText("");
                            tv_nome_completo.setText("ERRORE.");
                            tv_nome_completo.setTextColor(Color.RED);
                            tv_esclamativo.setText("");
                            if ((err_status_code!=400)&&(err_status_code!=404)) {
                                tv_status_http.setText("L'indirizzo del server non è corretto,");
                                tv_status_mqtt.setText("oppure il server è down.");
                            } else {
                                tv_status_http.setText("");
                                tv_status_mqtt.setText(err_msg);
                            }
                        } else {
                            tv_id_titolo.setText("ID: ");
                            tv_id_valore.setText(sitw_id);
                            tv_saluto.setText("Ciao ");
                            tv_nome_completo.setText(nomeecognome);
                            tv_esclamativo.setText(" !");
                            if ((err_status_code!=400)&&(err_status_code!=404)) {
                                tv_status_http.setText("ERRORE. Non Connesso.");
                                tv_status_http.setTextColor(Color.RED);
                                tv_status_http.setTypeface(null, Typeface.BOLD);
                                tv_status_mqtt.setText("L'indirizzo del Server non è corretto, oppure il server è down.");
                            } else {
                                tv_status_http.setText("ERRORE. Non Connesso.");
                                tv_status_http.setTextColor(Color.RED);
                                tv_status_http.setTypeface(null, Typeface.BOLD);
                                if (err_msg.equals("")){
                                    tv_status_mqtt.setText("Controlla l'indirizzo del Server.");
                                } else {
                                    tv_status_mqtt.setText(err_msg);
                                }
                            }
                        }
                        String str_json_notif = sharedPref.getString(getString(R.string.mem_not_read_notif_json), "");
                        listp=liste;
                        if (!str_json_notif.equals("")) {
                            try {
                                JSONObject json_notif=new JSONObject(str_json_notif);
                                JSONArray notifiche=json_notif.getJSONArray("not_read_notif");
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
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist = (ListView) findViewById(R.id.list);

                                mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                        Notifica notifica_pressata = (Notifica) listp.get(position);
                                        String notif_alert_id = notifica_pressata.getalert_id();
                                        String notif_msg = notifica_pressata.getMessaggio();
                                        String notif_type = notifica_pressata.getTipologia();
                                        String notif_date = notifica_pressata.getData();
                                        String notif_sender = notifica_pressata.getNome();
                                        if ((!sitw_id.equals(""))&&(!notif_alert_id.equals(""))){
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"non_letta",position,url);
                                        } else {
                                            if (notif_alert_id.equals("")) {
                                                try {
                                                    liste.remove(position);
                                                    elimina_msg_esterno(notif_msg, position);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                adapter=new CustomAdapter(getApplicationContext(), R.layout. rowcustom,listp);
                                mylist.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        display_err_user_msg = (display_err_user_msg + "\n\nCODICE: " + err_status_code_str + "\nMESSAGGIO:\n" + err_msg);
                    } else {
                        String str_json_notif = sharedPref.getString(getString(R.string.mem_not_read_notif_json), "");
                        listp=liste;
                        if (!str_json_notif.equals("")) {
                            try {
                                JSONObject json_notif=new JSONObject(str_json_notif);
                                JSONArray notifiche=json_notif.getJSONArray("not_read_notif");
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
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        listp.add(new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                    }
                                }
                                mylist = (ListView) findViewById(R.id.list);

                                mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                        Notifica notifica_pressata = (Notifica) listp.get(position);
                                        String notif_alert_id = notifica_pressata.getalert_id();
                                        String notif_msg = notifica_pressata.getMessaggio();
                                        String notif_type = notifica_pressata.getTipologia();
                                        String notif_date = notifica_pressata.getData();
                                        String notif_sender = notifica_pressata.getNome();
                                        if ((!sitw_id.equals(""))&&(!notif_alert_id.equals(""))){
                                            GestoreStatoNotifiche.cambia_stato_notifica(notif_alert_id,sitw_id,"non_letta",position,url);
                                        } else {
                                            if (notif_alert_id.equals("")) {
                                                try {
                                                    liste.remove(position);
                                                    elimina_msg_esterno(notif_msg, position);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                adapter=new CustomAdapter(getApplicationContext(), R.layout. rowcustom,listp);
                                mylist.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (nomeecognome.equals("")) {
                            tv_nome_completo.setText("Non Connesso");
                            tv_nome_completo.setTextColor(Color.RED);
                            tv_status_http.setText("L'indirizzo del Server non è corretto, o");
                            tv_status_mqtt.setText("il Server è down, o la connessione è assente.");
                        } else {
                            tv_status_http.setText("Non Connesso. L'indirizzo del Server non è corretto, o");
                            tv_status_mqtt.setText("il Server è down, o la connessione è assente.");
                        }
                    }
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("app_login", loginSTRING);
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        }
    }

    @TargetApi(19)
    public static void elimina_msg_esterno (String msg, int pos) {
        SharedPreferences sharedPref = activity_main.getSharedPreferences(activity_main.getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String msg_esterni_json =sharedPref.getString(activity_main.getString(R.string.messaggi_fuori_piattaforma_json),"{\"ext_msg\":[]}");

        String new_ext_msg_str;
        try {
            JSONObject ext_msgs_ob = new JSONObject(msg_esterni_json);
            JSONArray lista_msg_ext = ext_msgs_ob.getJSONArray("ext_msg");
            int da_eliminare=lista_msg_ext.length()-(pos+1);
            lista_msg_ext.remove(da_eliminare);
            ext_msgs_ob.put("ext_msg",lista_msg_ext);
            new_ext_msg_str=ext_msgs_ob.toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(activity_main.getString(R.string.messaggi_fuori_piattaforma_json), new_ext_msg_str);
            editor.commit();
            Toast.makeText(activity_main, "Messaggio esterno eliminato", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("eseguita callback onResume di MainActivity");
        if (avvio==false) {
            if (dont_resume==false) {
                loadActivity();
            } else {
                dont_resume=false;
            }
        } else {
            avvio = false;
        }
    }

    @Override
    protected void onStop() {
        System.out.println("eseguita callback onStop di MainActivity");
        super.onStop();
        if (mServiceBound) {
            System.out.println("unbinding di MainActivity dal service MyService");
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void reimposta_connessione() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String defaultBroker = getResources().getString(R.string.broker_addr_default);
        String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
        String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
        String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.server_addr_current), defaultServer);
        editor.commit();
        editor.putString(getString(R.string.broker_addr_current), defaultBroker);
        editor.commit();
        editor.putString(getString(R.string.mqtt_port_current), defaultMqttPort);
        editor.commit();
        editor.putString(getString(R.string.broker_username_current), defaultBrokerUsername);
        editor.commit();
        editor.putString(getString(R.string.broker_password_current), defaultBrokerPassword);
        editor.commit();

        cambia_broker = true;
        broker_esatto = false;

        Toast.makeText(activity_main, "Impostazioni connessione resettate", Toast.LENGTH_LONG).show();

        loadActivity();
    }

    public void cancella_tutti_i_mess_esterni () {
        SharedPreferences sharedPref = activity_main.getSharedPreferences(activity_main.getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity_main.getString(R.string.messaggi_fuori_piattaforma_json), "{\"ext_msg\":[]}");
        editor.commit();
        liste= new LinkedList();
        Toast.makeText(activity_main, "Messaggi esterni cancellati", Toast.LENGTH_LONG).show();
        loadActivity();
    }

    public void chiudi_my_service () {
        if (mServiceBound) {
            unbindService(mServiceConnection);
            System.out.println("unbinding di MainActivity dal service MyService");
            mServiceBound = false;
        }
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
        System.out.println("chiamata stopService da MainActivity per MyService.");
        System.out.println("invocazione metodo finish() da MainActivity per MainActivity.");
        MainActivity.activity_main.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent intent = new Intent(this, InformazioniApplicazione.class);
            startActivity(intent);
        }
        if (id == R.id.action_test) {
            Intent intent = new Intent(this, TestMqtt.class);
            startActivity(intent);
        }
        if (id == R.id.action_resetta) {
            //return true;
            reimposta_connessione();
        }
        if (id == R.id.action_esci) {
            chiudi_my_service();
        }
        if (id == R.id.action_cancella_msg_ext) {
            cancella_tutti_i_mess_esterni();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login_logout) {
            Intent intent = new Intent(this, LoginLogout.class);
            startActivity(intent);
        } else if (id == R.id.nav_register) {
            Intent intent = new Intent(this, Registrazione.class);
            startActivity(intent);

        } else if (id == R.id.nav_old_notif) {
            Intent intent = new Intent(this, NotificheLette.class);
            startActivity(intent);

        } else if (id == R.id.nav_ihealth_account) {
            Intent intent = new Intent(this, VisualizzatoreWeb.class);
            startActivity(intent);

        } else if (id == R.id.nav_connection_settings) {
            Intent intent = new Intent(this, ServerSettings.class);
            startActivity(intent);

        } else if (id == R.id.nav_user_settings) {
            Intent intent = new Intent(this, InformazioniPersonali.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
