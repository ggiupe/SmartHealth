package com.example.giuseppe.smarthealth;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class VisualizzaRiepilogo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_riepilogo);

        Intent intento =getIntent();
        final String user_data_STRING = intento.getStringExtra(Registrazione.EXTRA_MESSAGE);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.contenuto);
        Boolean formFilled = true;
        Boolean passwords_different=false;
        String server_addr="";

        int indexID = 0;

        try {
            JSONObject user_data_OBJ = new JSONObject(user_data_STRING);

            String password_check = user_data_OBJ.getString("password");
            String password_repeat_check = user_data_OBJ.getString("password_repeat");
            if (password_check.equals(password_repeat_check)) {
                passwords_different = false;
            }
            else {
                passwords_different = true;
            }
            //int indexID = 0;

            if (passwords_different==false) {
                indexID = 0;
                String server = user_data_OBJ.getString("server");
                String server_full = ("Indirizzo Server: " + server);
                TextView text_server = new TextView(this);
                text_server.setText(server_full);
                text_server.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_server = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                text_server.setLayoutParams(lprams_server);
                layout.addView(text_server);
                if (server.equals("")) {
                    formFilled = false;
                } else {
                    server_addr = server;
                }
                indexID++;

                String username = user_data_OBJ.getString("username");
                String username_full = ("Username: " + username);
                TextView text_username = new TextView(this);
                text_username.setText(username_full);
                text_username.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_username = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_username.addRule(RelativeLayout.BELOW, indexID);
                text_username.setLayoutParams(lprams_username);
                layout.addView(text_username);
                if (username.equals("")) {
                    formFilled = false;
                }
                indexID++;

                String password = user_data_OBJ.getString("password");
                int len_password = password.length();
                String pswd_tmp = "";
                for (int i = 0; i < len_password; i++) {
                    pswd_tmp = pswd_tmp + "*";
                }
                String password_full = ("Password: " + pswd_tmp);
                TextView text_password = new TextView(this);
                text_password.setText(password_full);
                text_password.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_password = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_password.addRule(RelativeLayout.BELOW, indexID);
                text_password.setLayoutParams(lprams_password);
                layout.addView(text_password);
                if (password.equals("")) {
                    formFilled = false;
                }
                indexID++;


                String first_name = user_data_OBJ.getString("first_name");
                String first_name_full = ("Nome: " + first_name);
                TextView text_first = new TextView(this);
                text_first.setText(first_name_full);
                text_first.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_first = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_first.addRule(RelativeLayout.BELOW, indexID);
                text_first.setLayoutParams(lprams_first);
                layout.addView(text_first);
                if (first_name.equals(""))
                    formFilled = false;
                indexID++;

                String last_name = user_data_OBJ.getString("last_name");
                String last_name_full = ("Cognome: " + last_name);
                TextView text_last = new TextView(this);
                text_last.setText(last_name_full);
                text_last.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_last = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_last.addRule(RelativeLayout.BELOW, indexID);
                text_last.setLayoutParams(lprams_last);
                layout.addView(text_last);
                if (last_name.equals(""))
                    formFilled = false;
                indexID++;

                String sex = user_data_OBJ.getString("sex");
                if (sex.equals("Male")) {
                    sex="Uomo";
                }
                if (sex.equals("Female")) {
                    sex="Donna";
                }
                String sex_full = ("Sesso: " + sex);
                TextView text_sex = new TextView(this);
                text_sex.setText(sex_full);
                text_sex.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_sex = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_sex.addRule(RelativeLayout.BELOW, indexID);
                text_sex.setLayoutParams(lprams_sex);
                layout.addView(text_sex);
                if (sex.equals(""))
                    formFilled = false;
                indexID++;

                String diseases = user_data_OBJ.getString("diseases");
                String diseases_full = ("Patologia/e: " + diseases);
                TextView text_diseases = new TextView(this);
                text_diseases.setText(diseases_full);
                text_diseases.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_diseases = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_diseases.addRule(RelativeLayout.BELOW, indexID);
                text_diseases.setLayoutParams(lprams_diseases);
                layout.addView(text_diseases);
                indexID++;

                int d = user_data_OBJ.getInt("day");
                int m = user_data_OBJ.getInt("month");
                int y = user_data_OBJ.getInt("year");
                String birthdate = (d + "/" + m + "/" + y);
                String birthdate_full = ("Nascita: " + birthdate);
                TextView text_birthdate = new TextView(this);
                text_birthdate.setText(birthdate_full);
                text_birthdate.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_birthdate = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_birthdate.addRule(RelativeLayout.BELOW, indexID);
                text_birthdate.setLayoutParams(lprams_birthdate);
                layout.addView(text_birthdate);
                if (birthdate.equals(""))
                    formFilled = false;
                indexID++;

                String tax_code = user_data_OBJ.getString("tax_code");
                String tax_code_full = ("Codice Fiscale: " + tax_code);
                TextView text_tax_code = new TextView(this);
                text_tax_code.setText(tax_code_full);
                text_tax_code.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_tax_code = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_tax_code.addRule(RelativeLayout.BELOW, indexID);
                text_tax_code.setLayoutParams(lprams_tax_code);
                layout.addView(text_tax_code);
                if (tax_code.equals(""))
                    formFilled = false;
                indexID++;

                String email = user_data_OBJ.getString("email");
                String email_full = ("Indirizzo e-mail: " + email);
                TextView text_email = new TextView(this);
                text_email.setText(email_full);
                text_email.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_email = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_email.addRule(RelativeLayout.BELOW, indexID);
                text_email.setLayoutParams(lprams_email);
                layout.addView(text_email);
                indexID++;

                String phone = user_data_OBJ.getString("phone");
                String phone_full = ("Telefono: " + phone);
                TextView text_phone = new TextView(this);
                text_phone.setText(phone_full);
                text_phone.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_phone = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_phone.addRule(RelativeLayout.BELOW, indexID);
                text_phone.setLayoutParams(lprams_phone);
                layout.addView(text_phone);
                if (phone.equals(""))
                    formFilled = false;
                indexID++;

                String city = user_data_OBJ.getString("city");
                String city_full = ("Città: " + city);
                TextView text_city = new TextView(this);
                text_city.setText(city_full);
                text_city.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_city = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_city.addRule(RelativeLayout.BELOW, indexID);
                text_city.setLayoutParams(lprams_city);
                layout.addView(text_city);
                indexID++;

                String address = user_data_OBJ.getString("address");
                String address_full = ("Indirizzo: " + address);
                TextView text_address = new TextView(this);
                text_address.setText(address_full);
                text_address.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_address = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_address.addRule(RelativeLayout.BELOW, indexID);
                text_address.setLayoutParams(lprams_address);
                layout.addView(text_address);
                indexID++;

                String zip_code = user_data_OBJ.getString("zip_code");
                String zip_code_full = ("CAP: " + zip_code);
                TextView text_zip_code = new TextView(this);
                text_zip_code.setText(zip_code_full);
                text_zip_code.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_zip_code = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_zip_code.addRule(RelativeLayout.BELOW, indexID);
                text_zip_code.setLayoutParams(lprams_zip_code);
                layout.addView(text_zip_code);
                indexID++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (passwords_different==false) {
            if (formFilled == true) {
                final Button register = new Button(this);
                register.setText("REGISTRAMI");
                register.setId(indexID + 1);
                RelativeLayout.LayoutParams lprams_register = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_register.addRule(RelativeLayout.BELOW, indexID);
                lprams_register.addRule(RelativeLayout.CENTER_HORIZONTAL);
                register.setLayoutParams(lprams_register);
                layout.addView(register);
                indexID++;

                final String finalServer_addr = server_addr;
                final RelativeLayout finallayout = layout;
                final int indexID_final = indexID;
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        register.setEnabled(false);
                        Toast.makeText(MainActivity.activity_main, "Attendi", Toast.LENGTH_LONG).show();
                        String url = finalServer_addr;

                        // Request a string response
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        // Result handling
                                        //System.out.println(response);
                                        //System.out.println("funziona!!!!");
                                        TextView text_ans = new TextView(getApplicationContext());
                                        text_ans.setText("\n\n\nRichiesta inviata con successo!\n\nRispsosta del Server:\n" + response);
                                        text_ans.setId(indexID_final + 1);
                                        text_ans.setTextColor(Color.BLACK);
                                        RelativeLayout.LayoutParams lprams_ans = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        lprams_ans.addRule(RelativeLayout.BELOW, indexID_final);
                                        lprams_ans.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                        text_ans.setLayoutParams(lprams_ans);
                                        finallayout.addView(text_ans);
                                        register.setEnabled(false);


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // Error handling
                                System.out.println("Qualcosa è andato storto!");
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
                                        System.out.println(err_msg);
                                    }
                                    display_err_user_msg = (display_err_user_msg + "\n\nCODICE: " + err_status_code_str + "\nMESSAGGIO:\n" + err_msg);
                                } else {
                                    display_err_user_msg = (display_err_user_msg + "\n\nPossibili cause:\nindirizzo del Server non corretto;\nil Server è down.");
                                }

                                error.printStackTrace();

                                TextView text_ans_err = new TextView(getApplicationContext());
                                text_ans_err.setText(display_err_user_msg);
                                text_ans_err.setId(indexID_final + 1);
                                text_ans_err.setTextColor(Color.RED);
                                RelativeLayout.LayoutParams lprams_ans_err = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lprams_ans_err.addRule(RelativeLayout.BELOW, indexID_final);
                                lprams_ans_err.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                text_ans_err.setLayoutParams(lprams_ans_err);
                                finallayout.addView(text_ans_err);
                                register.setEnabled(false);

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("new_user_data", user_data_STRING);
                                return params;
                            }

                        };

                        // Add the request to the queue
                        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
                    }
                });
            } else {
                TextView text_err = new TextView(this);
                text_err.setText("\nMancano informazioni!\nPer favore, torna indietro e completa tutti i campi obbligatori.");
                text_err.setId(indexID + 1);
                text_err.setTextColor(Color.RED);
                RelativeLayout.LayoutParams lprams_err = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lprams_err.addRule(RelativeLayout.BELOW, indexID);
                lprams_err.addRule(RelativeLayout.CENTER_HORIZONTAL);
                text_err.setLayoutParams(lprams_err);
                layout.addView(text_err);
            }
        } else {
            TextView text_err = new TextView(this);
            text_err.setText("\nLe password inserite sono diverse!");
            text_err.setId(indexID + 1);
            text_err.setTextColor(Color.RED);
            RelativeLayout.LayoutParams lprams_err = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lprams_err.addRule(RelativeLayout.BELOW, indexID);
            lprams_err.addRule(RelativeLayout.CENTER_HORIZONTAL);
            text_err.setLayoutParams(lprams_err);
            layout.addView(text_err);
        }


    }


}
