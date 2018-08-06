package com.example.moetaz.chathub.ui.fragments;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.adapters.ConversationAdapter;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.MessagesInfo;
import com.example.moetaz.chathub.models.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FAVID_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FAVLIST_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FAVUSERNAME;
import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;
import static com.example.moetaz.chathub.help.FirebaseConstants.MESSAGESINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.MESSAGE_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.SENDER_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.provider.ConvProivderConstants.CONTENT_URI_1;
import static com.example.moetaz.chathub.provider.ConvProivderConstants.MESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.msg)
    EditText mesgToSend;
    @BindView(R.id.send_img)
    ImageView sendIcon;
    @BindView(R.id.conv_list)
    RecyclerView conversationList;
    private List<MessagesInfo> messagesInfos = new ArrayList<>();
    private Firebase mRef, mCovRef,newRef;
    private String friendId;
    private String friendUserName;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private ContentResolver contentResolver;
    private User user;
    private ConversationAdapter adapter;
    private com.google.firebase.database.ChildEventListener childEventListener;

    public ConversationFragment() {
        // Required empty public constructor
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate.replaceAll("-", "").replaceAll(" ", "")
                .replaceAll(":", "").replaceAll("\\.", "");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
        Intent intent = getActivity().getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (Utilities.isTablet(getContext())) {
            if (getArguments() != null) {
                user = getArguments().getParcelable("userkey");
                friendId = user.getKey();
                friendUserName = user.getName();
            }

        } else {
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                user = intent.getExtras().getParcelable("userkey");
                friendId = user.getKey();
                friendUserName = user.getName();
            }
        }

        getActivity().setTitle(friendUserName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, view);
        mRef = new Firebase(FB_ROOT);
        mCovRef = new Firebase(FB_ROOT + "/" + Utilities.getUserId() + "/" + CONVERSATIONINFO_NODE
                + "/" + friendId + "/" + MESSAGESINFO_NODE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(USERINFO_NODE)
                .child(Utilities.getUserId())
                .child(CONVERSATIONINFO_NODE)
                .child(friendId)
                .child(MESSAGESINFO_NODE);
        setHasOptionsMenu(true);
        conversationList.setHasFixedSize(true);
        conversationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        initList();

        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mesgToSend.getText().toString();

                String CurrentTime = getCurrentTime();
                    mDatabase.removeEventListener(childEventListener);
                    mRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                            .child(MESSAGESINFO_NODE)
                            .child(CurrentTime).child(MESSAGE_NODE)
                            .setValue(String.valueOf(msg));

                    mRef.child(Utilities.getUserId()).child(CONVERSATIONINFO_NODE).child(friendId)
                            .child(MESSAGESINFO_NODE)
                            .child(CurrentTime).child(SENDER_NODE)
                            .setValue(new SharedPref(getContext()).GetItem(getString(R.string.usrename_pref)));


                mRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(MESSAGE_NODE)
                        .setValue(String.valueOf(msg));

                mRef.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                        .child(MESSAGESINFO_NODE)
                        .child(CurrentTime).child(SENDER_NODE)
                        .setValue(new SharedPref(getContext()).GetItem(getString(R.string.usrename_pref)));

                mesgToSend.setText("");
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                mDatabase.addChildEventListener(childEventListener);
            }
        });
        conversationList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                conversationList.scrollToPosition(conversationList.getAdapter().getItemCount() - 1);
            }
        });
        return view;
    }

    private void initList() {
        adapter = new ConversationAdapter(getContext(),messagesInfos,user);
        conversationList.setAdapter(adapter);

        childEventListener = new
                com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String message = dataSnapshot.child("msg").getValue(String.class);
                String sender = dataSnapshot.child("sender").getValue(String.class);

                if (message != null && sender != null) {
                    MessagesInfo messagesInfo = new MessagesInfo();
                    messagesInfo.setMsg(message);
                    messagesInfo.setSender(sender);

                    messagesInfos.add(messagesInfo);
                    adapter.notifyItemInserted(messagesInfos.size() - 1);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


    }

    private void setImage(final String id, final ImageView imageView) {
        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(id);
        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StorageReference filepath = storageReference.child("usersProfilePic/" + id + ".jpg");
                if (dataSnapshot.hasChild("hasProfilePic")) {
                    Glide.with(getActivity()).using(new FirebaseImageLoader())
                            .load(filepath).into(imageView);
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().finish();

                break;
            case R.id.fav_list_action:
                mRef.child(Utilities.getUserId()).child(FAVLIST_NODE).child(friendId)
                        .child(FAVID_NODE)
                        .setValue(friendId);
                mRef.child(Utilities.getUserId()).child(FAVLIST_NODE).child(friendId)
                        .child(FAVUSERNAME)
                        .setValue(friendUserName);
                Utilities.message(getContext(),getString(R.string.fvav_list_msg));
                break;
            case R.id.addwidget:
                addToWidget();
                break;
            case R.id.deleteconversation:
                newRef = new Firebase(FB_ROOT + "/" + Utilities.getUserId() + "/" + CONVERSATIONINFO_NODE
                        + "/" + friendId );
                newRef.removeValue();
                getActivity().finish();
            default:
                break;
        }
        return true;
    }

    private void addToWidget() {
        new SharedPref(getContext()).SaveItem(getString(R.string.userwidget_pref), friendUserName);
        contentResolver.delete(CONTENT_URI_1,
                null, null);
        final ContentValues cv = new ContentValues();
        mCovRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                String sender = dataSnapshot.child(SENDER_NODE).getValue(String.class);
                if (sender.equals(Utilities.getUserName(getContext()))) {
                    sender = "me";
                }
                String msg = dataSnapshot.child(MESSAGE_NODE).getValue(String.class);
                String finalString = sender + " : " + msg;
                cv.put(MESSAGE, finalString);
                contentResolver.insert(CONTENT_URI_1, cv);
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

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.addChildEventListener(childEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(childEventListener);
    }
}


