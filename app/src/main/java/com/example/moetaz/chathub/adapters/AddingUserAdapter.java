package com.example.moetaz.chathub.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.models.User;

import java.util.ArrayList;
import java.util.List;

public class AddingUserAdapter extends RecyclerView.Adapter<AddingUserAdapter.MyViewHolder> {
    private Context context;
    private List<User> list = new ArrayList<>();

    public AddingUserAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AddingUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_row, parent, false);

        return new AddingUserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddingUserAdapter.MyViewHolder holder, int position) {
        final User user = list.get(position);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.userimg);
            name = (TextView) itemView.findViewById(R.id.username);
            mView = itemView;
        }
    }
}
