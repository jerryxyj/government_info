package com.jerry.jerry.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jerry on 8/1/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {


    private static final String TAG = "StockAdapter";
    private List<Official> officialsList;
    private MainActivity mainActivity;


    public RecyclerViewAdapter(List<Official>officialsList, MainActivity mainActivity){
        this.officialsList=officialsList;
        this.mainActivity=mainActivity;
    }



    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.government_list_row, parent, false);
        itemView.setOnClickListener(mainActivity);


        return new MyViewHolder(itemView);

    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Official official=officialsList.get(position);
        holder.title.setText(official.getTitle());
        holder.name.setText(official.getName());
        holder.party.setText("("+official.getParty()+")");


    }
    public int getItemCount() {
        return officialsList.size();
    }


}
