package com.hel.guessword.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hel.guessword.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hel
 * @date 2018/2/23
 * 文件 GuessWord
 * 描述
 */

public class GridViewAdapter extends ArrayAdapter<Map<String, String>> {

    private Context mContext;
    private int layoutResourceId;
    private List<Map<String, String>> mGridData = new ArrayList<>();


    public GridViewAdapter(Context context, int resource, List<Map<String, String>> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
    }

    public void setGridData(List<Map<String, String>> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mGridData.get(position).get("word1"));
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}
