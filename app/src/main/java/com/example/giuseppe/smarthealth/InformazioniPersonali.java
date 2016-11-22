package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InformazioniPersonali extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informazioni_personali);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String u_nome = sharedPref.getString(getString(R.string.mem_usrnome), "");
        String u_cognome = sharedPref.getString(getString(R.string.mem_usrcognome), "");
        String u_nascita = sharedPref.getString(getString(R.string.mem_usrnacita), "");
        String u_sesso = sharedPref.getString(getString(R.string.mem_usrsesso), "");
        String u_cod_fisc = sharedPref.getString(getString(R.string.mem_usrcodicefiscale), "");
        String u_patologie_json = sharedPref.getString(getString(R.string.mem_usrpatologie), "");
        String u_username = sharedPref.getString(getString(R.string.mem_usrusername), "");
        String u_password = sharedPref.getString(getString(R.string.mem_usrpassword), "");
        String u_telefono = sharedPref.getString(getString(R.string.mem_usrtelefono), "");
        String u_email = sharedPref.getString(getString(R.string.mem_usremail), "");
        String u_citta = sharedPref.getString(getString(R.string.mem_usrcitta), "");
        String u_indirizzo = sharedPref.getString(getString(R.string.mem_usrindirizzo), "");
        String u_cap = sharedPref.getString(getString(R.string.mem_usrcap), "");

        String u_patologie="";
        try {
            JSONObject u_patologie_obj = new JSONObject(u_patologie_json);
            JSONArray diseases=u_patologie_obj.getJSONArray("diseases_list");
            for (int i = 0; i < diseases.length(); i++) {
                String curr_patologia = diseases.getString(i);
                if (u_patologie.equals("")){
                    u_patologie=curr_patologia;
                } else {
                    u_patologie=u_patologie+"\n"+curr_patologia;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (u_sesso.equals("Male")) {
            u_sesso="Uomo";
        }
        if (u_sesso.equals("Female")) {
            u_sesso="Donna";
        }

        TextView t_nome = (TextView) findViewById(R.id.nome_val);
        t_nome.setText(u_nome);
        TextView t_cognome = (TextView) findViewById(R.id.cognome_val);
        t_cognome.setText(u_cognome);
        TextView t_nascita = (TextView) findViewById(R.id.nascita_val);
        t_nascita.setText(u_nascita);
        TextView t_sesso = (TextView) findViewById(R.id.sesso_val);
        t_sesso.setText(u_sesso);
        TextView t_cod_fisc = (TextView) findViewById(R.id.cod_fisc_val);
        t_cod_fisc.setText(u_cod_fisc);
        TextView t_patologie = (TextView) findViewById(R.id.patologie_val);
        t_patologie.setText(u_patologie);
        TextView t_username = (TextView) findViewById(R.id.username_val);
        t_username.setText(u_username);
        TextView t_password = (TextView) findViewById(R.id.password_val);
        t_password.setText(u_password);
        TextView t_telefono = (TextView) findViewById(R.id.telefono_val);
        t_telefono.setText(u_telefono);
        TextView t_email = (TextView) findViewById(R.id.email_val);
        t_email.setText(u_email);
        TextView t_citta = (TextView) findViewById(R.id.citta_val);
        t_citta.setText(u_citta);
        TextView t_indirizzo = (TextView) findViewById(R.id.indirizzo_val);
        t_indirizzo.setText(u_indirizzo);
        TextView t_cap = (TextView) findViewById(R.id.cap_val);
        t_cap.setText(u_cap);
    }
}
