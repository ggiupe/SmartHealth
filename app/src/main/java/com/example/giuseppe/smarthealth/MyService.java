package com.example.giuseppe.smarthealth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {
    private IBinder mBinder = new MyBinder();

    public static Context context;
    private static String clientId;
    private static MemoryPersistence memPer;
    public static MqttAndroidClient client;
    private static IMqttToken subToken;
    public static Service service_main;
    public static boolean client_exists=false;
    public static boolean set_up_flag=false;
    public static String mem_cl_id="";
    public static String save_tpc="";
    public static String save_ackn="";
    public static int retry = 0;

    private MyThread mythread;   //////////
    public static boolean isRunning = false;  //////////

    public MyService() {
    }

    @Override
    public void onCreate() {

        System.out.println("Esecuzione della callback onCreate del Servizio MyService.");
        service_main=this;
        super.onCreate();
        context=getApplicationContext();

        if (set_up_flag==false) {
            System.out.println("SETUP INIZIALE (fatto all'interno di onCreate)");
            clientId = MqttClient.generateClientId();
            memPer = new MemoryPersistence();

            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
            String subscr = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");
            String defaultBroker = getResources().getString(R.string.broker_addr_default);
            String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
            String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
            String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);

            if (client_exists == false) {
                String broker_address = broker_addr + ":" + mqtt_port;
                client = new MqttAndroidClient(context, broker_address, clientId, memPer);
            } else {
                if (client.isConnected() == false) {
                    String broker_address = broker_addr + ":" + mqtt_port;
                    client = new MqttAndroidClient(context, broker_address, clientId, memPer);
                }
            }
            String defaultBrokerUsername = getResources().getString(R.string.broker_username_default);
            String broker_username = sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
            String defaultBrokerPassword = getResources().getString(R.string.broker_password_default);
            String broker_password = sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);

            if (!subscr.equals("")) {
                connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr, "no");
            }

            set_up_flag = true;
            System.out.println("set_up_flag=true   Fine SETUP INIZIALE (fatto all'interno di onCreate)");
        }
        mythread  = new MyThread(); //////////
    }

    //////////////////
    class MyThread extends Thread{
        static final long DELAY = 120000;
        static final long DELAYB = 60000;
        @Override
        public void run(){
            while(isRunning){
                try {
                    Thread.sleep(DELAY);
                    if (isRunning) {
                        aggiorna_sottoscrizione_a();
                    }
                    Thread.sleep(DELAYB);
                    if (isRunning) {
                        aggiorna_sottoscrizione_b();
                    }
                } catch (InterruptedException e) {
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        }
    }
    //////////////////

    @Override
    public void onDestroy() {
        //////////////
        isRunning=false;
        //////////////
        System.out.println("e' stata chiamata la callback onDestroy del service.");
        disconnetti();
        set_up_flag=false;
        System.out.println("Il service MyService e' terminato.");
        super.onDestroy();
    }

    //////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Esecuzione della callback onStartCommand del Servizio MyService.");
        if (set_up_flag==false) {
            System.out.println("SETUP INIZIALE (fatto all'interno di onStartCommand)");
            service_main=this;
            context=getApplicationContext();
            clientId= MqttClient.generateClientId();
            memPer=new MemoryPersistence();

            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
            String subscr = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");
            String defaultBroker = getResources().getString(R.string.broker_addr_default);
            String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
            String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
            String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);

            if (client_exists==false) {
                String broker_address = broker_addr + ":" + mqtt_port;
                client = new MqttAndroidClient(context, broker_address, clientId, memPer);
            } else {
                if (client.isConnected() == false) {
                    String broker_address = broker_addr + ":" + mqtt_port;
                    client = new MqttAndroidClient(context, broker_address, clientId, memPer);
                }
            }
            String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
            String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
            String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
            String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);

            if (!subscr.equals("")) {
                connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr, "no");
            }
            set_up_flag=true;
            System.out.println("set_up_flag=true   Fine SETUP INIZIALE (fatto all'interno di onStartCommand)");
        }
        //set_up_flag=false;
        System.out.println("Esecuzione della callback onStartCommand del servizio MyService");
        if(!isRunning){
            mythread.start();
            isRunning = true;
        }
        return START_STICKY;
    }
    /////////////////

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("binding al service MyService effettuato.");
        return mBinder;
    }


    public class MyBinder extends Binder {
        MyService getService() {
            System.out.println("Restituzione all'activity client da parte del service di un binder per le comunicazioni.");
            return MyService.this;
        }
    }

    /////////////////
    public void aggiorna_sottoscrizione_a () {
        System.out.println("Aggiornamento sottoscrizione Topic Test.");
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String subscr = sharedPref.getString(getString(R.string.mem_topic_sub_test), "");

        if (!subscr.equals("")) {
            String defaultBroker = getResources().getString(R.string.broker_addr_default);
            String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
            String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
            String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
            String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
            String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
            String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
            String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);
            connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr, "no");
        }
    }
    /////////////////

    /////////////////
    public void aggiorna_sottoscrizione_b () {
        System.out.println("Aggiornamento sottoscrizione Topic Personale.");
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String subscr = sharedPref.getString(getString(R.string.mem_personal_topic), "");

        if (!subscr.equals("")) {
            String defaultBroker = getResources().getString(R.string.broker_addr_default);
            String broker_addr = sharedPref.getString(getString(R.string.broker_addr_current), defaultBroker);
            String defaultMqttPort = getResources().getString(R.string.mqtt_port_default);
            String mqtt_port = sharedPref.getString(getString(R.string.mqtt_port_current), defaultMqttPort);
            String defaultBrokerUsername=getResources().getString(R.string.broker_username_default);
            String broker_username=sharedPref.getString(getString(R.string.broker_username_current), defaultBrokerUsername);
            String defaultBrokerPassword=getResources().getString(R.string.broker_password_default);
            String broker_password=sharedPref.getString(getString(R.string.broker_password_current), defaultBrokerPassword);
            connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr, "no");
        }
    }
    ////////////////

    static void visualizza_notifica_android (String titolo, String contenuto) {
        if ((titolo.equals(""))&&(contenuto.equals(""))) {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder mBuilder= new Notification.Builder(service_main)
                    .setSound(uri);
            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) service_main.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } else {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Intent intent=new Intent(service_main,MainActivity.class);
            PendingIntent pending= PendingIntent.getActivity(service_main, 0, intent, 0);
            Notification.Builder mBuilder= new Notification.Builder(service_main)
                    .setSmallIcon(R.drawable.ic_local_hospital_black_24dp)
                    .setContentTitle(titolo)
                    .setContentIntent(pending)
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setVibrate(new long[] { 200, 300, 200, 300, 200 })
                    .setLights(Color.BLUE, 3000, 3000)
                    .setStyle(new Notification.BigTextStyle().bigText(contenuto));

            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) service_main.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }

    }

    static void connetti(String url, String port, final String mqtt_username, final String mqtt_password, final String topic, final String ackn) {
        if ((client.isConnected() == false)&&(!clientId.equals(mem_cl_id))) {
            System.out.println("AVVIO tentativo connessione client.");
            mem_cl_id = clientId;
            final String broker_address = url + ":" + port;
            if (client_exists == false) {
                System.out.println("Generazione nuova istanza di client");
                client = new MqttAndroidClient(context, broker_address, clientId, memPer);
                try {
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            final byte[] payload = message.getPayload();
                            final String message_content = new String(payload);
                            System.out.println("Nuovo Messaggio Ricevuto. (Topic: " + topic + " ) Messaggio: " + message_content);
                            if (!topic.equals(message_content)) {
                                if (message_content.equals("connection_is_ok")) {
                                    MainActivity.tv_status_mqtt.setText("Notifiche Push: Attive.");
                                    MainActivity.broker_esatto = true;
                                } else {

                                    try {
                                        JSONObject curr_notif = new JSONObject(message_content);
                                        String tipo_notifica = curr_notif.getString("type");
                                        if (tipo_notifica.equals("message")) {
                                            JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                            String mittente = datimessaggio.getString("from");
                                            String msg_content = datimessaggio.getString("msg");
                                            String msg_date = datimessaggio.getString("date");
                                            String alert_id = datimessaggio.getString("id_alert");
                                            String tipologia = "Messaggio da:";
                                            MainActivity.listp.add(0, new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                            visualizza_notifica_android("Smart Health", "Nuovo Messaggio da " + mittente + "\n" + msg_content);
                                        }
                                        if (tipo_notifica.equals("notif")) {
                                            JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                            String mittente = "";
                                            String msg_content = datimessaggio.getString("msg");
                                            String msg_date = datimessaggio.getString("date");
                                            String alert_id = datimessaggio.getString("id_alert");
                                            String tipologia = "Notifica";
                                            MainActivity.listp.add(0, new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                            visualizza_notifica_android("Smart Health", "Nuova Notifica\n" + msg_content);
                                        }
                                    } catch (JSONException e) {
                                        String tpc_message_content = "Topic: " + topic + "\n\n" + message_content;
                                        String notif_tpc_msg = "@ " + topic + "\n" + message_content;
                                        MainActivity.liste.add(0, new Notifica("Messaggio", "esterno alla piattaforma", "", tpc_message_content, ""));
                                        SharedPreferences sharedPref = service_main.getSharedPreferences(service_main.getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                                        String msg_esterni_json = sharedPref.getString(service_main.getString(R.string.messaggi_fuori_piattaforma_json), "{\"ext_msg\":[]}");
                                        String new_ext_msg_str;
                                        try {
                                            JSONObject ext_msgs_ob = new JSONObject(msg_esterni_json);
                                            JSONArray lista_msg_ext = ext_msgs_ob.getJSONArray("ext_msg");
                                            lista_msg_ext.put(tpc_message_content);
                                            ext_msgs_ob.put("ext_msg", lista_msg_ext);
                                            new_ext_msg_str = ext_msgs_ob.toString();
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString(service_main.getString(R.string.messaggi_fuori_piattaforma_json), new_ext_msg_str);
                                            editor.commit();
                                        } catch (JSONException g) {
                                            g.printStackTrace();
                                        }
                                        visualizza_notifica_android("Smart Health", "Nuovo Messaggio\n" + notif_tpc_msg);
                                        e.printStackTrace();
                                    }
                                    MainActivity.adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    if ((!mqtt_username.equals("")) && (!mqtt_password.equals(""))) {
                        options.setUserName(mqtt_username);
                        options.setPassword(mqtt_password.toCharArray());
                    }

                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            //Log.d(TAG, "onSuccess");
                            client_exists = true;
                            mem_cl_id = "";
                            System.out.println("Avvia il Service MqttService.");
                            System.out.println("Connesso: " + broker_address + " - username: " + mqtt_username + " - password: " + mqtt_password);
                            String cl_id = client.getClientId();
                            System.out.println("L'ID del Client MQTT e':  " + cl_id);
                            if (MainActivity.cambia_broker == true) {
                                MainActivity.cambia_broker = false;
                            }
                            sottoscrivi(topic, ackn);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            System.out.println("Non connesso");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            } else {
                reimposta_tutto_a();
                System.out.println("Istanza del client già presente. Inspiegabilmente il client non e' piu' connesso. Avvio Reset totale.");
            }
        } else {
            System.out.println("Ha tentato la riconnessione, ma e' stata evitata!!!");
            sottoscrivi(topic, ackn);
        }
    }
    static void connetti_invia_disconnetti(String url, String port, final String mqtt_username, final String mqtt_password, final String topic, final String messaggio) {
        if ((client.isConnected() == false)&&(!clientId.equals(mem_cl_id))) {


            mem_cl_id=clientId;
            final String broker_address = url+":"+port;
            client=new MqttAndroidClient(context, broker_address, clientId, memPer);

            try {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        final byte[] payload = message.getPayload();
                        final String message_content = new String(payload);
                        System.out.println("Nuovo Messaggio Ricevuto. (Topic: "+topic+" ) Messaggio: "+message_content);
                        if (!topic.equals(message_content)) {
                            if (message_content.equals("connection_is_ok")) {
                                MainActivity.tv_status_mqtt.setText("Notifiche Push: Attive.");
                                MainActivity.broker_esatto=true;
                            } else {
                                try {
                                    JSONObject curr_notif = new JSONObject(message_content);
                                    String tipo_notifica = curr_notif.getString("type");
                                    if (tipo_notifica.equals("message")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = datimessaggio.getString("from");
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Messaggio da:";
                                        MainActivity.listp.add(0, new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                        visualizza_notifica_android("Smart Health", "Nuovo Messaggio da " + mittente + "\n" + msg_content);
                                    }
                                    if (tipo_notifica.equals("notif")) {
                                        JSONObject datimessaggio = curr_notif.getJSONObject("data");
                                        String mittente = "";
                                        String msg_content = datimessaggio.getString("msg");
                                        String msg_date = datimessaggio.getString("date");
                                        String alert_id = datimessaggio.getString("id_alert");
                                        String tipologia = "Notifica";
                                        MainActivity.listp.add(0, new Notifica(tipologia, mittente, msg_date, msg_content, alert_id));
                                        visualizza_notifica_android("Smart Health", "Nuova Notifica\n" + msg_content);
                                    }
                                } catch (JSONException e) {
                                    String tpc_message_content= "Topic: "+topic+"\n\n"+message_content;
                                    String notif_tpc_msg="@ "+topic+"\n"+message_content;
                                    MainActivity.liste.add(0, new Notifica("Messaggio", "esterno alla piattaforma", "", tpc_message_content, ""));
                                    SharedPreferences sharedPref = service_main.getSharedPreferences(service_main.getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
                                    String msg_esterni_json =sharedPref.getString(service_main.getString(R.string.messaggi_fuori_piattaforma_json),"{\"ext_msg\":[]}");
                                    String new_ext_msg_str;
                                    try {
                                        JSONObject ext_msgs_ob = new JSONObject(msg_esterni_json);
                                        JSONArray lista_msg_ext = ext_msgs_ob.getJSONArray("ext_msg");
                                        lista_msg_ext.put(message_content);
                                        ext_msgs_ob.put("ext_msg",lista_msg_ext);
                                        new_ext_msg_str=ext_msgs_ob.toString();
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString(service_main.getString(R.string.messaggi_fuori_piattaforma_json), new_ext_msg_str);
                                        editor.commit();
                                    } catch (JSONException g) {
                                        g.printStackTrace();
                                    }
                                    visualizza_notifica_android("Smart Health", "Nuovo Messaggio\n" + notif_tpc_msg);
                                    e.printStackTrace();
                                }
                                MainActivity.adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                if ((!mqtt_username.equals(""))&&(!mqtt_password.equals(""))) {
                    options.setUserName(mqtt_username);
                    options.setPassword(mqtt_password.toCharArray());
                }

                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        //Log.d(TAG, "onSuccess");
                        client_exists=true;
                        mem_cl_id="";
                        System.out.println("Avvia il Service MqttService.");
                        System.out.println("Connesso: "+broker_address+" - username: "+mqtt_username+" - password: "+mqtt_password);
                        if (MainActivity.cambia_broker==true) {
                            MainActivity.cambia_broker=false;
                        }
                        MqttMessage message = new MqttMessage(messaggio.getBytes());
                        message.setQos(2);
                        message.setRetained(false);
                        try {
                            client.publish(topic, message);
                            System.out.println("Messaggio pubblicato: "+messaggio+"(topic: "+topic+" )");

                        } catch (MqttPersistenceException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        try {
                            IMqttToken disconToken = client.disconnect();
                            disconToken.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    client_exists=false;
                                    System.out.println("client disconnesso");
                                    client.close();
                                    System.out.println("client closed. Termina il Service MqttService.");
                                    clientId = MqttClient.generateClientId();
                                    memPer = new MemoryPersistence();
                                    System.out.println("nuovo clientID pronto per l'uso");
                                    // we are now successfully disconnected
                                    if (MainActivity.cambia_broker == true) {
                                        MainActivity.cambia_broker = false;
                                    }
                                    if (MainActivity.broker_esatto == true) {
                                        MainActivity.broker_esatto = false;
                                    }
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken,
                                                      Throwable exception) {
                                    // something went wrong, but probably we are disconnected anyway
                                }
                            });
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        System.out.println("Non connesso");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Ha tentato la riconnessione, ma e' stata evitata!!!");
        }
    }


    static void sottoscrivi(final String topic, final String ackn) {
        //retry=retry+1;
        try {
            int qos = 2;
            subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    System.out.println("Sottoscrizione AVVENUTA CON SUCCESSO. Topic: "+topic);
                    if (ackn.equals("yes")) {
                        invia_messaggio(topic, topic);
                    }
                    if (!save_tpc.equals("")) {
                        if (retry<10) {
                            int num= retry+1;
                            System.out.println("Tentativo numero "+num+" di sottoscrizione del Topic: "+save_tpc);
                            sottoscrivi(save_tpc, save_ackn);
                        } else {
                            retry = 0;
                        }
                        save_tpc="";
                        save_ackn="";
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    System.out.println("Sottoscrizione FALLITA. Topic: "+topic);
                    save_ackn=ackn;
                    save_tpc=topic;
                    retry=retry+1;
                    reimposta_tutto_a();
                }
            });
        } catch (Exception e) {
            System.out.println("Excpetion catched! :(     Sottoscrizione FALLITA. Topic: "+topic);
            save_ackn=ackn;
            save_tpc=topic;
            retry=retry+1;
            e.printStackTrace();
        }
    }

    static void reimposta_tutto_a () {
        if (client.isConnected() == false) {
            System.out.println("Il client e' gia' disconnesso");
            reimposta_tutto_b();
        } else {
            try {
                IMqttToken disconToken = client.disconnect();
                disconToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        System.out.println("client disconnesso");
                        reimposta_tutto_b();
                    }
                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // something went wrong, but probably we are disconnected anyway
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void reimposta_tutto_b () {
        client.close();
        client_exists = false;
        System.out.println("client closed. Termina il Service MqttService.");
        clientId = MqttClient.generateClientId();
        memPer = new MemoryPersistence();
        System.out.println("nuovo clientID pronto per l'uso");

        mem_cl_id="";
        save_tpc="";
        save_ackn="";
        retry = 0;

        SharedPreferences sharedPref = service_main.getSharedPreferences(service_main.getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String subscr_test = sharedPref.getString(service_main.getString(R.string.mem_topic_sub_test), "");
        String subscr_pers = sharedPref.getString(service_main.getString(R.string.mem_personal_topic), "");
        String defaultBroker = service_main.getResources().getString(R.string.broker_addr_default);
        String broker_addr = sharedPref.getString(service_main.getString(R.string.broker_addr_current), defaultBroker);
        String defaultMqttPort = service_main.getResources().getString(R.string.mqtt_port_default);
        String mqtt_port = sharedPref.getString(service_main.getString(R.string.mqtt_port_current), defaultMqttPort);

        String broker_address = broker_addr + ":" + mqtt_port;
        client = new MqttAndroidClient(context, broker_address, clientId, memPer);

        String defaultBrokerUsername = service_main.getResources().getString(R.string.broker_username_default);
        String broker_username = sharedPref.getString(service_main.getString(R.string.broker_username_current), defaultBrokerUsername);
        String defaultBrokerPassword = service_main.getResources().getString(R.string.broker_password_default);
        String broker_password = sharedPref.getString(service_main.getString(R.string.broker_password_current), defaultBrokerPassword);

        if (!subscr_test.equals("")) {
            save_tpc=subscr_pers;
            save_ackn="yes";
            connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr_test, "no");
        } else {
            if (!subscr_pers.equals("")) {
                connetti(broker_addr, mqtt_port, broker_username, broker_password, subscr_pers, "yes");
            }
        }
    }

    static void unsottoscrivi (final String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Unsubscribe al topic avvenuta con SUCCESSO. Topic: "+topic);
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    System.out.println("Unsubscribe al topic FALLITA. Topic: "+topic);
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    static void unsottoscrivi_e_sottoscrivi (final String topic_old, final String topic_new, final String ackn) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic_old);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Unsubscribe al topic avvenuta con SUCCESSO. Topic: "+topic_old);
                    // The subscription could successfully be removed from the client
                    sottoscrivi(topic_new, ackn);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    System.out.println("Unsubscribe al topic FALLITA. Topic: "+topic_old);
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    static void disconnetti () {
        if (client.isConnected() == true) {
            try {
                IMqttToken disconToken = client.disconnect();
                disconToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        client_exists=false;
                        System.out.println("client disconnesso");
                        client.close();
                        System.out.println("client closed. Termina il Service MqttService.");
                        clientId = MqttClient.generateClientId();
                        memPer = new MemoryPersistence();
                        //client=new MqttAndroidClient(context, "tcp://giupe.webfactional.com:22243", clientId, memPer);
                        System.out.println("nuovo clientID pronto per l'uso");
                        // we are now successfully disconnected
                        if (MainActivity.cambia_broker == true) {
                            MainActivity.cambia_broker = false;
                        }
                        if (MainActivity.broker_esatto == true) {
                            MainActivity.broker_esatto = false;
                            //MainActivity.tv_status_mqtt.setText("");
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken,
                                          Throwable exception) {
                        // something went wrong, but probably we are disconnected anyway
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    static void invia_messaggio (String topic, String message_str) {
        MqttMessage message = new MqttMessage(message_str.getBytes());
        message.setQos(2);
        message.setRetained(false);
        try {
            client.publish(topic, message);
            System.out.println("Messaggio pubblicato: "+message_str+"(topic: "+topic+" )");
            if (!message_str.equals(topic)) {
                Toast.makeText(context, "Messaggio Pubblicato", Toast.LENGTH_LONG).show();
            }

        } catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void cambiabroker (final String url, final String port, final String mqtt_username, final String mqtt_password, final String topic, final String ackn) {

        if (client.isConnected() == false) {
            connetti(url, port, mqtt_username, mqtt_password, topic, ackn);
        } else {
            try {
                IMqttToken disconToken = client.disconnect();
                disconToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        client_exists=false;
                        System.out.println("client disconnesso");
                        client.close();
                        System.out.println("client closed. Termina il Service MqttService.");
                        clientId = MqttClient.generateClientId();
                        memPer = new MemoryPersistence();
                        //client=new MqttAndroidClient(context, "tcp://giupe.webfactional.com:22243", clientId, memPer);
                        System.out.println("nuovo clientID pronto per l'uso");
                        // we are now successfully disconnected
                        if (!topic.equals("")){
                            connetti(url, port, mqtt_username, mqtt_password, topic, ackn);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken,
                                          Throwable exception) {
                        // something went wrong, but probably we are disconnected anyway
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }
}
