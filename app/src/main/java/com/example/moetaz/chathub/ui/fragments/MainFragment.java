package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.example.moetaz.chathub.ui.activities.AddUserActivity;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.example.moetaz.chathub.ui.activities.RegiteringActivity;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.help.Utilities.getUserName;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.nav) NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @BindView(R.id.app_bar)  Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    @BindView(R.id.chat_list)  RecyclerView UsersList;
    @BindView(R.id.add_fab) FloatingActionButton FAB;
    private DatabaseReference mDatabase;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getContext());
        getUserName(getActivity());
          firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), RegiteringActivity.class));

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.colse);
        SetListnerToDrawer();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logoutnav:LogOut(); break;
                }
                return true;
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE).child(Utilities.getUserId())
        .child(CONVERSATIONINFO_NODE);

        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<messagesInfo,UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, UserHolder>(
                        messagesInfo.class
                        ,R.layout.main_list_row
                        ,UserHolder.class
                        ,mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final UserHolder viewHolder, final messagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getName());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(getContext(),ConversationActivity.class);
                                intent.putExtra("keyPass",ComKey);
                                intent.putExtra("keyuser",model.getName());
                                getActivity().startActivity(intent);
                            }
                        });


                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddUserActivity.class));
            }
        });
        return view;
    }

    private void SetListnerToDrawer() {
        if(Build.VERSION.SDK_INT >= 23)
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        else
            drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
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

    private void LogOut(){
        firebaseAuth.signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), RegiteringActivity.class));
    }

}
