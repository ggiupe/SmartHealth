package com.example.giuseppe.smarthealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class InformazioniApplicazione extends AppCompatActivity {

    public static List list_info_slots;
    public static InfoAdapter adapter_info_slots;
    public static ListView mylist_info_slost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informazioni_applicazione);

        list_info_slots=new LinkedList();
        mylist_info_slost = (ListView) findViewById(R.id.list_infos);

        String json_string_info = getResources().getString(R.string.json_lista_info);
        try {
            JSONObject info_list_OBJ = new JSONObject(json_string_info);
            JSONArray info_list=info_list_OBJ.getJSONArray("info_list");
            for (int i = 0; i < info_list.length(); i++) {
                String curr_info = info_list.get(i).toString();// getString(i);
                System.out.println("NOME DELLA STRINGA"+curr_info);
                String name_titolo = curr_info;
                int titolo_id = getResId(name_titolo, R.string.class);
                String titolo = getResources().getString(titolo_id);
                String name_descrizione = name_titolo+"_descrizione";
                int descrizione_id = getResId(name_descrizione, R.string.class);
                String descrizione = getResources().getString(descrizione_id);

                list_info_slots.add(new SlotInfo(titolo, "", descrizione_id));

                mylist_info_slost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          SlotInfo slot_pressato = (SlotInfo) list_info_slots.get(position);
                          String titolo_n = slot_pressato.getTitolo();
                          String descrizione_n = slot_pressato.getDescrizione();
                          int descrizione_name_n = slot_pressato.getNome_descr();
                          if (descrizione_n.equals("")) {
                              String descr = getResources().getString(descrizione_name_n);
                              slot_pressato.setDescrizione(descr);
                          } else {
                              slot_pressato.setDescrizione("");
                          }
                          adapter_info_slots.notifyDataSetChanged();
                      }
                  }
                );
            }
            adapter_info_slots=new InfoAdapter(getApplicationContext(), R.layout.infoslotlayout,list_info_slots);
            mylist_info_slost.setAdapter(adapter_info_slots);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
