package com.mulkearn.kevin.pixelcolor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String>{

    String singleColor;
    TextView hexText;

    CustomAdapter(Context context, String[] cols) {
        super(context,R.layout.custom_row , cols);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater colorInflator = LayoutInflater.from(getContext());
        View customView = colorInflator.inflate(R.layout.custom_row, parent, false);

        singleColor = getItem(position);
        hexText = (TextView) customView.findViewById(R.id.hexText);

        hexText.setText(singleColor);
        hexText.setBackgroundColor(Color.parseColor(singleColor));

        return customView;

    }

}

