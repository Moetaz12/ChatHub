package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.dataStorage.SharedPref;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements SearchView.OnQueryTextListener {
    private Firebase fConvInfo;
    @BindView(R.id.searched_list) RecyclerView UsersList;
    @BindView(R.id.app_bar) Toolbar toolbar;
    private DatabaseReference mDatabase;
    StorageReference storageReference;
    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Add user");
        setHasOptionsMenu(true);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usersinfo");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<messagesInfo,AddUserFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, AddUserFragment.UserHolder>(
                        messagesInfo.class
                        ,R.layout.main_list_row
                        ,AddUserFragment.UserHolder.class
                        ,mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final AddUserFragment.UserHolder viewHolder, final messagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getUserName());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fConvInfo = new Firebase(FB_ROOT);

                                fConvInfo.child(Utilities.getUserId()).child("conversationInfo").child(ComKey)
                                        .child("name").setValue(model.getUserName());
                                fConvInfo.child(ComKey).child("conversationInfo").child(Utilities.getUserId())
                                        .child("name").setValue(new SharedPref(getContext()).GetItem("UserName"));

                                if (Utilities.isTablet(getContext())) {
                                    ConversationFragment conversationFragment = new ConversationFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(getString(R.string.friend_id_envelope),ComKey);
                                    bundle.putString(getString(R.string.friend_username_envelope),model.getName());
                                    conversationFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fconv, conversationFragment).commit();
                                }else {
                                    Intent intent= new Intent(getContext(),ConversationActivity.class);
                                    intent.putExtra("keyPass",ComKey);
                                    intent.putExtra("keyuser",model.getName());
                                    getActivity().startActivity(intent);
                                }


                            }
                        });
                        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                                .child(ComKey);
                        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StorageReference filepath = storageReference.child("usersProfilePic/" + ComKey + ".jpg");
                                if (dataSnapshot.hasChild("hasProfilePic")) {
                                    Glide.with(getActivity()).using(new FirebaseImageLoader())
                                            .load(filepath).into(viewHolder.imageView);
                                }else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);

        return view;
    }
    public static class UserHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        View mView;
        public UserHolder(View itemView) {
            super(itemView);

              imageView =itemView.findViewById(R.id.userimg);
            name = (TextView) itemView.findViewById(R.id.username);
            mView = itemView;
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
        final FirebaseRecyclerAdapter<messagesInfo,AddUserFragment.UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, AddUserFragment.UserHolder>(
                        messagesInfo.class
                        ,R.layout.main_list_row
                        ,AddUserFragment.UserHolder.class
                        ,mDatabase.orderByChild("userName").equalTo(newText)
                ) {
                    @Override
                    protected void populateViewHolder(final AddUserFragment.UserHolder viewHolder, final messagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getUserName());

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                getActivity().finish();
                //startActivity(new Intent(getContext(), MainActivity.class));
                break;

            default:break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {

            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {

        }
    }
}
