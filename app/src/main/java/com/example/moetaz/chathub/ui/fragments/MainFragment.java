package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.adapters.MainAdapter;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.User;
import com.example.moetaz.chathub.ui.activities.AddUserActivity;
import com.example.moetaz.chathub.ui.activities.RegiteringActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FRIEND_PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.NAME_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    @BindView(R.id.app_bar)
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    @BindView(R.id.chat_list)
    RecyclerView usersList;
    @BindView(R.id.add_fab)
    FloatingActionButton fab;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private MainAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private List<User> users = new ArrayList<>();
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), RegiteringActivity.class));
        }
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        setNavigationDrawer();

        mDatabase = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(Utilities.getUserId())
                .child(CONVERSATIONINFO_NODE);

        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));


        initList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddUserActivity.class));
            }
        });

        return view;
    }

    private void setNavigationDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("sck");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("nlnc");


        Drawer result = new DrawerBuilder()
                .withActivity(Objects.requireNonNull(getActivity()))
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("mkmd")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                         return false;
                    }
                })
                .build();
    }

    private void initList() {
        adapter = new MainAdapter(getContext(),users);
        usersList.setAdapter(adapter);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.child(NAME_NODE).getValue(String.class);
                String imageurl = dataSnapshot.child(FRIEND_PROFILE_PIC).getValue(String.class);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/*
        *//*View view1 = navigationView.getHeaderView(0);
        final TextView tView = (TextView) view1.findViewById(R.id.user);
        final ImageView imageView = view1.findViewById(R.id.userpic);*//*
        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                .child(Utilities.getUserId());
        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StorageReference filepath = storageReference.child(getString(R.string.picsFolderFirebase)
                        + Utilities.getUserId() + getString(R.string.jpgExt));
                if (dataSnapshot.hasChild(HASPROFILEPIC)) {
                    Glide.with(getActivity()).using(new FirebaseImageLoader())
                            .load(filepath).into(imageView);
                } else {
                    imageView.setBackgroundResource(R.drawable.avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tView.setText(Utilities.getUserName(getContext()));*/

    }


   /* private void setListnerToNavigationViewItems() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logoutnav:
                        logOut();
                        break;
                    case R.id.profile:
                        drawerLayout.closeDrawer(Gravity.START);
                        startActivity(new Intent(getActivity(), ProfileActivity.class));
                        break;
                    case R.id.fav:
                        drawerLayout.closeDrawer(Gravity.START);
                        startActivity(new Intent(getActivity(), FavouriteListActivity.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(getActivity(), AboutActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }*/



    private void logOut() {
        firebaseAuth.signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), RegiteringActivity.class));
    }




    private void SetGridManager() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        usersList.setLayoutManager(gridLayoutManager);
    }


}
