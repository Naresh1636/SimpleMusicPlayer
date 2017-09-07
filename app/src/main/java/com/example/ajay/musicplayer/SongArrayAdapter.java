package com.example.ajay.musicplayer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajay on 6/9/17.
 */

public class SongArrayAdapter extends ArrayAdapter {

    ArrayList<String> items=new ArrayList();
    Context ctx;

    public SongArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
        ctx=context;
        items=(ArrayList<String>)objects;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater inflaterCompat = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflaterCompat.inflate(R.layout.song_item_layout, parent, false);
        }

        TextView title=convertView.findViewById(R.id.textView2);
        title.setText(items.get(position));
        return convertView;
    }

    public int testAddition(int a,int b)
    {
        return a+b;
    }
}
