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
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.SharedPref;
import com.example.moetaz.chathub.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements SearchView.OnQueryTextListener {
    Firebase fConvInfo;
    private FirebaseAuth firebaseAuth;
    private RecyclerView UsersList;

    @BindView(R.id.app_bar) Toolbar toolbar;
    private DatabaseReference mDatabase;
    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usersinfo");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        UsersList = (RecyclerView) view.findViewById(R.id.searched_list);
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
                                fConvInfo = new Firebase("https://chathub-635f9.firebaseio.com/usersinfo");

                                fConvInfo.child(Utilities.getUserId()).child("conversationInfo").child(ComKey)
                                        .child("name").setValue(model.getUserName());
                                fConvInfo.child(ComKey).child("conversationInfo").child(Utilities.getUserId())
                                        .child("name").setValue(new SharedPref(getContext()).GetItem("UserName"));
                                Intent intent= new Intent(getContext(),ConversationActivity.class);
                                intent.putExtra("keyPass",ComKey);
                                intent.putExtra("keyuser",model.getName());
                                getActivity().startActivity(intent);
                            }
                        });

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);

        return view;
    }
    public static class UserHolder extends RecyclerView.ViewHolder{

        TextView name;
        View mView;
        public UserHolder(View itemView) {
            super(itemView);

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
}
