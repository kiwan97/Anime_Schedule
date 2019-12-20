package com.example.kiwankim.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<String> sample;
    public MyAdapter(Context context, ArrayList<String> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInflater.inflate(R.layout.listview_component,null);

        TextView textview = view.findViewById(R.id.title_item);
        textview.setText(sample.get(position));

        textview = view.findViewById(R.id.time_item);
        //여기 부터textview.setText()
        return view;
    }
}
