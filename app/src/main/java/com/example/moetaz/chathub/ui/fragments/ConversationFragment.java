package com.example.moetaz.chathub.ui.fragments;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.moetaz.chathub.provider.ConvProivderConstants.CONTENT_URI_1;
import static com.example.moetaz.chathub.provider.ConvProivderConstants.MESSAGE;
import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;
import static com.example.moetaz.chathub.help.FirebaseConstants.MESSAGESINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.MESSAGE_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.SENDER_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    private Firebase mRef,mCovRef;
    @BindView(R.id.msg)
    EditText mesgToSend;
    @BindView(R.id.send_img)
    ImageView img;
    @BindView(R.id.conv_list)
    RecyclerView usersList;
    private String friendId;
    private String friendUserName;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private ContentResolver contentResolver;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
        Intent intent = getActivity().getIntent();

        if (Utilities.isTablet(getContext())) {
            friendId = getArguments().getString(getString(R.string.friend_id_envelope));
            friendUserName = getArguments().getString(getString(R.string.friend_username_envelope));

        } else {
            friendId = intent.getExtras().getString(getString(R.string.friend_id_envelope));
            friendUserName = intent.getExtras().getString(getString(R.string.friend_username_envelope));
        }

        getActivity().setTitle(friendUserName);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, view);
        mRef = new Firebase(FB_ROOT);
        mCovRef = new Firebase(FB_ROOT+"/"+Utilities.getUserId()+"/"+CONVERSATIONINFO_NODE
                +"/"+friendId+"/"+MESSAGESINFO_NODE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE).child(Utilities.getUserId())
                .child(CONVERSATIONINFO_NODE).child(friendId).child(MESSAGESINFO_NODE);
        setHasOptionsMenu(true);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<messagesInfo, ConversationFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, ConversationFragment.UserHolder>(
                        messagesInfo.class
                        , R.layout.conv_list_row
                        , ConversationFragment.UserHolder.class
                        , mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final ConversationFragment.UserHolder viewHolder, final messagesInfo model, final int position) {


                        viewHolder.message.setText(model.getMsg());
                        if (model.getSender().equals(new SharedPref(getContext()).GetItem("UserName"))) {
                            viewHolder.img2.setVisibility(View.INVISIBLE);
                            setImage(Utilities.getUserId(),viewHolder.img1);
                        } else {
                            viewHolder.img1.setVisibility(View.INVISIBLE);
                            setImage(friendId,viewHolder.img2);
                        }
                    }
                };

        usersList.setAdapter(firebaseRecyclerAdapter);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mesgToSend.getText().toString();

                String CurrentTime = getCurrentTime();
                mRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(MESSAGE_NODE).setValue(String.valueOf(msg));
                mRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(SENDER_NODE).setValue(new SharedPref(getContext()).GetItem("UserName"));

                mRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(MESSAGE_NODE).setValue(String.valueOf(msg));
                mRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(SENDER_NODE).setValue(new SharedPref(getContext()).GetItem("UserName"));
                mesgToSend.setText("");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        usersList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                usersList.scrollToPosition(usersList.getAdapter().getItemCount() - 1);
            }
        });
        return view;
    }

    private void setImage(final String id, final ImageView imageView){
        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(id);
        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StorageReference filepath = storageReference.child("usersProfilePic/" + id + ".jpg");
                if (dataSnapshot.hasChild("hasProfilePic")) {
                    Glide.with(getActivity()).using(new FirebaseImageLoader())
                            .load(filepath).into(imageView);
                }else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate.replaceAll("-", "").replaceAll(" ", "")
                .replaceAll(":", "").replaceAll("\\.", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().finish();

                break;
            case R.id.fav_list_action:
                mRef.child(Utilities.getUserId()).child("FavList").child(friendId)
                        .child("favId")
                        .setValue(friendId);
                mRef.child(Utilities.getUserId()).child("FavList").child(friendId)
                        .child("favUserName")
                        .setValue(friendUserName);
                break;
            case R.id.addwidget:addToWidget();break;
            case R.id.deleteconversation: mCovRef.removeValue();
            default:
                break;
        }
        return true;
    }

    private void addToWidget() {
        new SharedPref(getContext()).SaveItem("userWidget",friendUserName);
        contentResolver.delete(CONTENT_URI_1,
                null, null);
        final ContentValues cv = new ContentValues();
        mCovRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                String sender = dataSnapshot.child("sender").getValue(String.class);
                if (sender.equals(Utilities.getUserName(getContext()))) {
                    sender = "me";
                }
                String msg = dataSnapshot.child("msg").getValue(String.class);
                String finalString = sender+" : "+msg;
                cv.put(MESSAGE,finalString);
                  contentResolver.insert(CONTENT_URI_1,cv);
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.conversation_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!Utilities.isTablet(getContext())) {
            try {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (Exception e) {

            }
        }
    }

}
