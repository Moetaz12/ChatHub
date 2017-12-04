package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.SharedPref;
import com.example.moetaz.chathub.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
    Firebase ffMesgs;
    @BindView(R.id.msg)
    EditText mesgToSend;
    @BindView(R.id.send_img)
    ImageView img;
    private RecyclerView UsersList;
    String UserId;
    String FriendUserName;
    private DatabaseReference mDatabase;
    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        UserId =  intent.getExtras().getString("keyPass");
        FriendUserName =  intent.getExtras().getString("keyuser");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this,view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usersinfo").child(Utilities.getUserId())
                .child("conversationInfo").child(UserId).child("messagesInfo");


        UsersList = (RecyclerView) view.findViewById(R.id.conv_list);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<messagesInfo,ConversationFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, ConversationFragment.UserHolder>(
                        messagesInfo.class
                        ,R.layout.conv_list_row
                        ,ConversationFragment.UserHolder.class
                        ,mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final ConversationFragment.UserHolder viewHolder, final messagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.message.setText(model.getMsg());

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"hi",Toast.LENGTH_LONG).show();
              String Msg =  mesgToSend.getText().toString();
                ffMesgs = new Firebase("https://chathub-635f9.firebaseio.com/usersinfo");

                String CurrentTime = getCurrentTime();
                ffMesgs.child(Utilities.getUserId()).child("conversationInfo").child(UserId)
                        .child("messagesInfo")
                        .child(CurrentTime).child("msg").setValue(String.valueOf(Msg));
                ffMesgs.child(Utilities.getUserId()).child("conversationInfo").child(UserId)
                        .child("messagesInfo")
                        .child(CurrentTime).child("sender").setValue(new SharedPref(getContext()).GetItem("UserName"));

                ffMesgs.child(UserId).child("conversationInfo").child(Utilities.getUserId())
                        .child("messagesInfo")
                        .child(CurrentTime).child("msg").setValue(String.valueOf(Msg));
                ffMesgs.child(UserId).child("conversationInfo").child(Utilities.getUserId())
                        .child("messagesInfo")
                        .child(CurrentTime).child("sender").setValue(new SharedPref(getContext()).GetItem("UserName"));
                mesgToSend.setText("");
            }
        });
        return view;
    }

    public static class UserHolder extends RecyclerView.ViewHolder{
        CircleImageView img1;
        CircleImageView img2;
        TextView message;
        public UserHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.user_msg);
            img1 = itemView.findViewById(R.id.user_img1);
            img2 = itemView.findViewById(R.id.user_img2);

        }

    }

    public static String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate.replaceAll("-","").replaceAll(" ","")
                .replaceAll(":","").replaceAll("\\.", "");
    }

}
