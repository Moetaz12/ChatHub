package com.example.moetaz.chathub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moetaz.chathub.models.FavFriend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moetaz on 12/10/2017.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MyViewHolder> {
    Context context;
    private List<FavFriend> list = new ArrayList<>();

    public FavAdapter(Context context,List<FavFriend> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FavFriend favFriend = list.get(position);
        holder.textView.setText(favFriend.getUserName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.username);
        }
    }
}
