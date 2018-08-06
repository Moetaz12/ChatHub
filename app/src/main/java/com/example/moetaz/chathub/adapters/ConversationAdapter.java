package com.example.moetaz.chathub.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.MessagesInfo;
import com.example.moetaz.chathub.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {
    private Context context;
    private List<MessagesInfo> messagesInfos = new ArrayList<>();
    private User friend;

    public ConversationAdapter(Context context, List<MessagesInfo> messagesInfos, User friend) {
        this.context = context;
        this.messagesInfos = messagesInfos;
        this.friend = friend;
    }

    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conv_list_row, parent, false);

        return new ConversationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.MyViewHolder holder, int position) {
        final MessagesInfo messagesInfo = messagesInfos.get(position);

        if (messagesInfo.getMsg() != null && messagesInfo.getSender() != null) {
            holder.message.setText(messagesInfo.getMsg());
            if (messagesInfo.getSender().equals(new SharedPref(context).GetItem("UserName"))) {
                holder.img2.setVisibility(View.INVISIBLE);
                setImage(Utilities.getUserId(), holder.img1);
            } else {
                holder.img1.setVisibility(View.INVISIBLE);
                setImage(friend.getKey(), holder.img2);
            }
        }
    }

    private void setImage(final String id, final ImageView imageView) {
        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(id);

    }

    @Override
    public int getItemCount() {
        return messagesInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img1;
        CircleImageView img2;
        TextView message;

        public MyViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.user_msg);
            img1 = itemView.findViewById(R.id.user_img1);
            img2 = itemView.findViewById(R.id.user_img2);

        }

    }
}
