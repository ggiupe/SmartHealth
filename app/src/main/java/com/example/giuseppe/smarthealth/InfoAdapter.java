package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by giuseppe on 14/11/16.
 */
public class InfoAdapter extends ArrayAdapter<SlotInfo> {

    public InfoAdapter(Context context, int textViewResourceId,
                         List objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.infoslotlayout, null);
        TextView titolo = (TextView)convertView.findViewById(R.id.info_titolo);
        TextView descrizione = (TextView)convertView.findViewById(R.id.info_descrizione);
        SlotInfo d = getItem(position);
        titolo.setText(d.getTitolo());
        descrizione.setText(d.getDescrizione());
        return convertView;
    }

}