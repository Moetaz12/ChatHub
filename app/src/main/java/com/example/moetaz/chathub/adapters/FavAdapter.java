package com.example.moetaz.chathub.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.FavFriend;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.example.moetaz.chathub.ui.fragments.ConversationFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moetaz on 12/10/2017.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MyViewHolder> {
    private Context context;
    private List<FavFriend> list = new ArrayList<>();

    public FavAdapter(Context context, List<FavFriend> list) {
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
        final FavFriend favFriend = list.get(position);
        holder.textView.setText(favFriend.getUserName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isTablet(context)) {
                    ConversationFragment conversationFragment = new ConversationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(context.getString(R.string.friend_id_envelope), favFriend.getId());
                    bundle.putString(context.getString(R.string.friend_username_envelope), favFriend.getUserName());
                    conversationFragment.setArguments(bundle);
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fconv, conversationFragment).commit();
                } else {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra(context.getString(R.string.friend_id_envelope), favFriend.getId());
                    intent.putExtra(context.getString(R.string.friend_username_envelope), favFriend.getUserName());
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
        TextView textView;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.username);
            mView = itemView;
        }
    }
}
