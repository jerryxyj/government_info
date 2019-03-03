package com.jerry.jerry.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jerry on 8/1/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView name;
    public TextView party;


    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        name = (TextView) view.findViewById(R.id.name);
        party = (TextView) view.findViewById(R.id.party);


    }
}
