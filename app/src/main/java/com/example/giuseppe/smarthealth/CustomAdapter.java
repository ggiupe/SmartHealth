package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by giuseppe on 09/11/16.
 */
public class CustomAdapter extends ArrayAdapter<Notifica> {

    public CustomAdapter(Context context, int textViewResourceId,
                         List objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.rowcustom, null);
        TextView nome = (TextView)convertView.findViewById(R.id.list_nome);
        TextView messaggio = (TextView)convertView.findViewById(R.id.list_content);
        TextView data = (TextView)convertView.findViewById(R.id.list_data);
        TextView tipologia = (TextView)convertView.findViewById(R.id.list_titolo);
        Notifica c = getItem(position);
        nome.setText(c.getNome());
        messaggio.setText(c.getMessaggio());
        data.setText(c.getData());
        tipologia.setText(c.getTipologia());
        return convertView;
    }

}