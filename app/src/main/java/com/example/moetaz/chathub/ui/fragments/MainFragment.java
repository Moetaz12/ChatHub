package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.messagesInfo;
import com.example.moetaz.chathub.ui.activities.AboutActivity;
import com.example.moetaz.chathub.ui.activities.AddUserActivity;
import com.example.moetaz.chathub.ui.activities.ConversationActivity;
import com.example.moetaz.chathub.ui.activities.FavouriteListActivity;
import com.example.moetaz.chathub.ui.activities.ProfileActivity;
import com.example.moetaz.chathub.ui.activities.RegiteringActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.CONVERSATIONINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.HASPROFILEPIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.help.Utilities.isTablet;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav)
    NavigationView navigationView;
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
    ;

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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.colse);

        setListnerToDrawer();
        setListnerToNavigationViewItems();

        mDatabase = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE).child(Utilities.getUserId())
                .child(CONVERSATIONINFO_NODE);

        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        setDrawerLayoutHeight();
            final FirebaseRecyclerAdapter<messagesInfo, UserHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<messagesInfo, UserHolder>(
                            messagesInfo.class
                            , R.layout.main_list_row
                            , UserHolder.class
                            , mDatabase
                    ) {
                        @Override
                        protected void populateViewHolder(final UserHolder viewHolder, final messagesInfo model, final int position) {

                            DatabaseReference ComRef = getRef(position);
                            final String ComKey = ComRef.getKey();
                            viewHolder.name.setText(model.getName());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isTablet(getContext())) {
                                        ConversationFragment conversationFragment = new ConversationFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(getString(R.string.friend_id_envelope), ComKey);
                                        bundle.putString(getString(R.string.friend_username_envelope), model.getName());
                                        conversationFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fconv, conversationFragment).commit();

                                    } else {
                                        Intent intent = new Intent(getContext(), ConversationActivity.class);
                                        intent.putExtra(getString(R.string.friend_id_envelope), ComKey);
                                        intent.putExtra(getString(R.string.friend_username_envelope), model.getName());
                                        getActivity().startActivity(intent);
                                    }
                                }
                            });

                            DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(USERINFO_NODE)
                                    .child(ComKey);
                            DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    StorageReference filepath = storageReference
                                            .child(getString(R.string.picsFolderFirebase) + ComKey + getString(R.string.jpgExt));
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

            usersList.setAdapter(firebaseRecyclerAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddUserActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view1 = navigationView.getHeaderView(0);
        final TextView tView = (TextView) view1.findViewById(R.id.user);
        final ImageView imageView = view1.findViewById(R.id.userpic);
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

        tView.setText(Utilities.getUserName(getContext()));

    }

    private void setDrawerLayoutHeight() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayViewHeight = size.y;
        int displayViewwidth = size.x;
        ViewGroup.LayoutParams params = drawerLayout.getLayoutParams();
        params.height = displayViewHeight;
        params.width = displayViewwidth;
        drawerLayout.setLayoutParams(params);
    }

    private void setListnerToNavigationViewItems() {
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
    }

    private void setListnerToDrawer() {
        if (Build.VERSION.SDK_INT >= 23)
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        else
            drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    private void logOut() {
        firebaseAuth.signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), RegiteringActivity.class));
    }

    public static class UserHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        View mView;

        public UserHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.userimg);
            mView = itemView;
        }

    }


}
