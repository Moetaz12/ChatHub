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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.User;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.example.moetaz.chathub.ui.fragments.ConversationFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FRIEND_PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.NAME_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Context context;
    private List<User> list = new ArrayList<>();
    private String type ="";

    public MainAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    public MainAdapter(Context context, List<User> list ,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
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
                if (type.equals("addUser")) {
                    initializefirebasedata(user.getKey(),user.getName(),"");
                }
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

    private void initializefirebasedata(String friendId,String friendUsername ,String friendprofilePic){
        Toast.makeText(context, friendUsername
                , Toast.LENGTH_SHORT).show();

        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(USERINFO_NODE);

        DatabaseRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                .child(NAME_NODE).setValue(friendUsername);

        DatabaseRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                .child(NAME_NODE)
                .setValue(new SharedPref(context).GetItem(context.getString(R.string.usrename_pref)));

        DatabaseRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                .child(FRIEND_PROFILE_PIC).setValue(new SharedPref(context).GetItem("profilepickey"));

        DatabaseRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                .child(FRIEND_PROFILE_PIC).setValue(friendprofilePic);
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
