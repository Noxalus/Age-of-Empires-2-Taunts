package com.noxalus.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String> {

    Context context;
    int resource;
    String[] adapterData;

    public MyAdapter(Context ctx, int res, String[] data) {
        super(ctx, res, data);
        context = ctx;
        resource = res;
        adapterData = data;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return adapterData.length;
    }

    @Override
    public String getItem(int position) {
        return adapterData[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            holder.textView = (TextView) convertView.findViewById(R.id.sound_list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(adapterData[position]);

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}