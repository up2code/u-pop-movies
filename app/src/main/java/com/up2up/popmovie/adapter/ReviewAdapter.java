package com.up2up.popmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.up2up.popmovie.R;
import com.up2up.popmovie.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
    }

    class ViewHolder {
        TextView txtAuthor;
        TextView txtContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_review, null);
            holder = new ViewHolder();
            holder.txtAuthor = (TextView) convertView.findViewById(R.id.item_review_author);
            holder.txtContent = (TextView) convertView.findViewById(R.id.item_review_content);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Review review = getItem(position);

        if(review != null) {
            holder.txtAuthor.setText(review.getAuthor());
            holder.txtContent.setText(review.getContent());
        }

        return convertView;
    }
}
