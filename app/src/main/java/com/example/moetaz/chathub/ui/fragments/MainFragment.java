package com.example.moetaz.chathub.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moetaz.chathub.GlobalVariables;
import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.models.messagesInfo;
import com.example.moetaz.chathub.ui.activities.RegiteringActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    private RecyclerView UsersList;

    private DatabaseReference mDatabase;
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(),GlobalVariables.getUserId(),Toast.LENGTH_LONG).show();
          firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), RegiteringActivity.class));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("usersinfo").child(GlobalVariables.getUserId())
        .child("conversationInfo");


        UsersList = (RecyclerView) view.findViewById(R.id.chat_list);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<messagesInfo,UserHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<messagesInfo, UserHolder>(
                        messagesInfo.class
                        ,R.layout.chat_list_row
                        ,UserHolder.class
                        ,mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final UserHolder viewHolder, final messagesInfo model, final int position) {

                        DatabaseReference ComRef = getRef(position);
                        final String ComKey = ComRef.getKey();
                        viewHolder.name.setText(model.getName());
                        Toast.makeText(getContext(),model.getName(),Toast.LENGTH_LONG).show();

                    }
                };

        UsersList.setAdapter(firebaseRecyclerAdapter);
        return view;
    }

    public static class UserHolder extends RecyclerView.ViewHolder{

        TextView name;
        public UserHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.username);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            firebaseAuth.signOut();
            getActivity().finish();
            startActivity(new Intent(getActivity(), RegiteringActivity.class));
            return true;

        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

}
