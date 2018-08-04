package com.example.moetaz.chathub.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.models.User;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Context context;
    private List<User> list = new ArrayList<>();

    public MainAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_row, parent, false);

        return new MainAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainAdapter.MyViewHolder holder, int position) {
        final User user = list.get(position);

        holder.name.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView name;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.userimg);
            mView = itemView;
        }

    }
}
