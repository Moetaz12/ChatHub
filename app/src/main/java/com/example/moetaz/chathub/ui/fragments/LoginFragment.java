package com.example.moetaz.chathub.ui.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private Button Signin;
    private EditText email;
    private EditText password;
    private TextView Signup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(getContext());
        if(firebaseAuth.getCurrentUser() != null){

            startActivity(new Intent(getActivity().getApplication(),MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);


        progressDialog=new ProgressDialog(getContext());

        Signin= (Button) view.findViewById(R.id.busiGnin);
        email= (EditText) view.findViewById(R.id.eMail);
        password= (EditText) view.findViewById(R.id.passWord);
        Signup = (TextView) view.findViewById(R.id.sigNup);

        Signup.setOnClickListener(this);
        Signin.setOnClickListener(this);

        return view;
    }

    private void UserLogin(){
        String EMAIL=email.getText().toString().trim();
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

        firebaseAuth.signInWithEmailAndPassword(EMAIL,PASSWORD)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){

                            getActivity().finish();
                            startActivity(new Intent(getContext(),MainActivity.class));
                        }
                        else{

                            Log.e("Error",task.getException().getMessage());
                            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });



    }

    @Override
    public void onClick(View view) {

        if(view == Signin){
            UserLogin();
        }
        if(view == Signup){

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fregiter,new SignupFragment())
                    .commit();
        }
    }
}
