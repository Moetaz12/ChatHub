package com.example.moetaz.chathub.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.adapters.FavAdapter;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.models.FavFriend;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.FAVID_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FAVLIST_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.FAVUSERNAME;
import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteListFragment extends Fragment {

    FavAdapter favAdapter;
    @BindView(R.id.fav_list)
    RecyclerView recyclerView;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    private GridLayoutManager gridLayoutManager;
    private List<FavFriend> favFriendList = new ArrayList<>();
    private Firebase mFavUser;

    public FavouriteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_list, container, false);
        ButterKnife.bind(this, view);
        mFavUser = new Firebase(FB_ROOT + Utilities.getUserId() +"/"+ FAVLIST_NODE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        SetGridManager();
        setHasOptionsMenu(true);
        favAdapter = new FavAdapter(getActivity(), favFriendList);
        recyclerView.setAdapter(favAdapter);

        mFavUser.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.child(FAVUSERNAME).getValue(String.class);
                String value2 = dataSnapshot.child(FAVID_NODE).getValue(String.class);
                FavFriend favFriend = new FavFriend();
                favFriend.setId(value2);
                favFriend.setUserName(value);
                favFriendList.add(favFriend);
                favAdapter.notifyItemInserted(favFriendList.size() - 1);

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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }

    private void SetGridManager() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().finish();
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

            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {

        }
    }
}
