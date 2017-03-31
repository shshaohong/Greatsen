package com.sunyie.android.greatsen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunyie.android.greatsen.R;

/**
 * Created by yukunlin on 2017/2/7.
 */

public class MenuLeftAdapter extends BaseAdapter {
    private Context context;

    public MenuLeftAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        TextView text = (TextView) convertView.findViewById(R.id.textView);
        if (position == 0) {
            text.setText("查         找");
        } else {
            text.setText("退出账号");
        }
        return convertView;
    }
}
