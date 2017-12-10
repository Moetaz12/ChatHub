package com.example.moetaz.chathub.ui.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.ui.activities.MainActivity;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener{


    private Button Regiter;
    private EditText email,UserName;
    private EditText password;
    private TextView signin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);;

        firebaseAuth= FirebaseAuth.getInstance();
        Firebase.setAndroidContext(getContext());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog=new ProgressDialog(getContext());
        if(firebaseAuth.getCurrentUser() != null){
            getActivity().finish();
            startActivity(new Intent(getContext(),MainActivity.class));

        }

        Regiter= (Button) view.findViewById(R.id.buRegister);
        email= (EditText) view.findViewById(R.id.email);
        password= (EditText) view.findViewById(R.id.password);
        signin= (TextView) view.findViewById(R.id.login);
        UserName = (EditText) view.findViewById(R.id.user_name);


        Regiter.setOnClickListener(this);
        signin.setOnClickListener(this);
        return view;
    }

    private void RegiterUser(){
        final String EMAIL=email.getText().toString().trim();
        String PASSWORD=password.getText().toString().trim();

        //new SharedPref(getContext()).SaveItem("FriendId",EMAIL.substring(0, EMAIL.indexOf('@')));
        if(TextUtils.isEmpty(EMAIL)){
            Toast.makeText(getContext(),"Enter email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(PASSWORD)){
            Toast.makeText(getContext(),"Enter password",Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(EMAIL,PASSWORD)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDatabase.child("usersinfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("email").setValue(EMAIL);

                            mDatabase.child("usersinfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("userName").setValue(UserName.getText().toString());
                            mDatabase.child("usersinfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("userid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            getActivity().finish();
                            startActivity(new Intent(getContext(),MainActivity.class));
                        }else{
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            progressDialog.hide();
                            return;
                        }
                    }
                });

    }


    @Override
    public void onClick(View view) {
        if(view == Regiter){
            RegiterUser();
        }
        if(view==signin){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fregiter,new LoginFragment())
                    .commit();
        }
    }
}
