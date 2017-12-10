package com.example.moetaz.chathub.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.moetaz.chathub.FavAdapter;
import com.example.moetaz.chathub.R;
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

import static com.example.moetaz.chathub.help.FirebaseConstants.FB_ROOT;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteListFragment extends Fragment {
    private GridLayoutManager gridLayoutManager;
    FavAdapter favAdapter;
    RecyclerView recyclerView;
    private List<FavFriend> favFriendList = new ArrayList<>();
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    private Firebase mFavUser ;
    public FavouriteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_list, container, false);
        ButterKnife.bind(this,view);
        mFavUser = new Firebase(FB_ROOT+ Utilities.getUserId()+"/FavList");
        recyclerView =   view.findViewById(R.id.fav_list);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        SetGridManager();
        favAdapter = new FavAdapter(getActivity(), favFriendList);
        recyclerView.setAdapter(favAdapter);

        mFavUser.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.child("favUserName").getValue(String.class);
                FavFriend favFriend = new FavFriend();
                favFriend.setId("cdlc");
                favFriend.setUserName(value);
                favFriendList.add(favFriend);
                 favAdapter.notifyItemInserted(favFriendList.size()-1);

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
}
