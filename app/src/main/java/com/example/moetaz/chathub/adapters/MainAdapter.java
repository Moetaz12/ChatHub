package com.example.moetaz.chathub.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.User;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.example.moetaz.chathub.ui.fragments.ConversationFragment;

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
        if(user.getImgeUrl() !=null && user.getImgeUrl().equals("")){
            Glide.with(context)
                    .load(user.getImgeUrl()).into(holder.imageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isTablet(context)) {
                    ConversationFragment conversationFragment = new ConversationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("userkey", user);

                    conversationFragment.setArguments(bundle);
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fconv, conversationFragment).commit();

                } else {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("userkey", user);
                    context.startActivity(intent);
                }
            }
        });

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
