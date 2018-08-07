package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.adapters.MainAdapter;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.MessagesInfo;
import com.example.moetaz.chathub.models.User;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FRIEND_PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.NAME_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERNAME_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements SearchView.OnQueryTextListener {
    @BindView(R.id.searched_list)
    RecyclerView UsersList;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    private StorageReference storageReference;
    private Firebase fConvInfo;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private List<User> users = new ArrayList<>();
    private MainAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private static final String TYPE = "addUser";
    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.add_user_title));
        setHasOptionsMenu(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = Utilities.getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        initList();
        /*final FirebaseRecyclerAdapter<MessagesInfo, AddUserFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MessagesInfo, AddUserFragment.UserHolder>(
                        MessagesInfo.class
                        , R.layout.main_list_row
                        , AddUserFragment.UserHolder.class
                        , mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final AddUserFragment.UserHolder viewHolder, final MessagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getUserName());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fConvInfo = new Firebase(FB_ROOT);
                                launchConvFragment(ComKey, model.getUserName()
                                        ,model.getProfilePic());

                            }
                        });
                        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                                .child(ComKey);
                        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StorageReference filepath = storageReference.child(getString(R.string.picsFolderFirebase)
                                        + ComKey + getString(R.string.jpgExt));
                                if (dataSnapshot.hasChild(HASPROFILEPIC)) {
                                    Glide.with(getActivity()).using(new FirebaseImageLoader())
                                            .load(filepath).into(viewHolder.imageView);
                                } else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);

*/        return view;
    }

    private void initList() {
        adapter = new MainAdapter(getContext(),users,TYPE);
        UsersList.setAdapter(adapter);

        mDatabase
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        String name = dataSnapshot.child(USERNAME_NODE).getValue(String.class);
                        String imageurl = dataSnapshot.child(PROFILE_PIC).getValue(String.class);
                        String key = dataSnapshot.getKey();
                        User user = new User();

                        if (!key.equals(firebaseAuth.getCurrentUser().getUid())) {
                            user.setName(name);
                            user.setImgeUrl(imageurl);
                            user.setKey(key);
                            users.add(user);
                            adapter.notifyItemInserted(users.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void launchConvFragment(String friendId, String friendUsername
            ,String profilePic) {
        fConvInfo.child(currentUserId).child(CONVERSATIONINFO_NODE).child(friendId)
                .child(NAME_NODE).setValue(friendUsername);

        fConvInfo.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                .child(NAME_NODE).setValue(new SharedPref(getContext()).GetItem(getString(R.string.usrename_pref)));

        fConvInfo.child(friendId).child(CONVERSATIONINFO_NODE).child(Utilities.getUserId())
                .child(FRIEND_PROFILE_PIC).setValue(new SharedPref(getContext()).GetItem("profilepickey"));

        fConvInfo.child(currentUserId).child(CONVERSATIONINFO_NODE).child(friendId)
                .child(FRIEND_PROFILE_PIC).setValue(profilePic);

        if (Utilities.isTablet(getContext())) {
            ConversationFragment conversationFragment = new ConversationFragment();
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.friend_id_envelope), friendId);
            bundle.putString(getString(R.string.friend_username_envelope), friendUsername);
            conversationFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fconv, conversationFragment).commit();
        } else {
            Intent intent = new Intent(getContext(), ConversationActivity.class);
            intent.putExtra(getString(R.string.friend_id_envelope), friendId);
            intent.putExtra(getString(R.string.friend_username_envelope), friendUsername);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final FirebaseRecyclerAdapter<MessagesInfo, AddUserFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MessagesInfo, AddUserFragment.UserHolder>(
                        MessagesInfo.class
                        , R.layout.main_list_row
                        , AddUserFragment.UserHolder.class
                        , mDatabase.orderByChild(USERNAME_NODE).equalTo(newText)
                ) {
                    @Override
                    protected void populateViewHolder(final AddUserFragment.UserHolder viewHolder, final MessagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getUserName());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                launchConvFragment(ComKey, model.getUserName()
                                        ,model.getProfilePic());
                            }
                        });
                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (getActivity() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

        }
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        View mView;

        public UserHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.userimg);
            name = (TextView) itemView.findViewById(R.id.username);
            mView = itemView;
        }

    }
}
