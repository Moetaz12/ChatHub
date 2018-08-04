package com.example.moetaz.chathub.ui.fragments;


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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.ui.activities.MainActivity;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.Utilities.saveUserName;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.bu_signin)
    Button Signin;
    @BindView(R.id.eMail)
    EditText email;
    @BindView(R.id.passWord)
    EditText password;
    @BindView(R.id.tx_signup)
    TextView Signup;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(getContext());
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
            getActivity().finish();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        Signup.setOnClickListener(this);
        Signin.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
        return view;
    }

    private void userLogin() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailStr)) {
            Toast.makeText(getContext(), getString(R.string.enter_email), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(getContext(), getString(R.string.enter_password), Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            saveUserName(getActivity());
                            Utilities.saveProfilePicUrl(getActivity());
                            getActivity().finish();
                            startActivity(new Intent(getContext(), MainActivity.class));
                        } else {

                            Log.e("Error", task.getException().getMessage());
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {

        if (view == Signin) {
            userLogin();
        }
        if (view == Signup) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fregiter, new SignupFragment())
                    .commit();
        }
    }
}
