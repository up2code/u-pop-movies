package com.up2up.popmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.up2up.popmovie.R;
import com.up2up.popmovie.model.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Context context, int resource, ArrayList<Trailer> objects) {
        super(context, resource, objects);
    }

    class ViewHolder {
        TextView txtName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_trailer, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.item_trailer_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Trailer trailer = getItem(position);

        if(trailer != null) {
            holder.txtName.setText(trailer.getName());
        }

        return convertView;
    }
}
