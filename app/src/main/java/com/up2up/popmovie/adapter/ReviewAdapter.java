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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_review, null);

        Review review = getItem(position);

        TextView txtAuthor = (TextView) convertView.findViewById(R.id.item_review_author);
        TextView txtContent = (TextView) convertView.findViewById(R.id.item_review_content);

        txtAuthor.setText(review.getAuthor());
        txtContent.setText(review.getContent());

        return convertView;
    }
}
