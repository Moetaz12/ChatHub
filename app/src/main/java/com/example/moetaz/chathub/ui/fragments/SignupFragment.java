package com.example.moetaz.chathub.ui.fragments;


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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moetaz.chathub.R;
import com.example.moetaz.chathub.help.Utilities;
import com.example.moetaz.chathub.ui.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.moetaz.chathub.help.FirebaseConstants.EMAIL_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.PROFILE_PIC;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERID_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERINFO_NODE;
import static com.example.moetaz.chathub.help.FirebaseConstants.USERNAME_NODE;
import static com.example.moetaz.chathub.help.Utilities.saveUserName;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.buRegister)
    Button Regiter;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.user_name)
    EditText UserName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    TextView signin;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ;
        ButterKnife.bind(this, view);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar.setVisibility(View.GONE);
        if (firebaseAuth.getCurrentUser() != null) {
            getActivity().finish();
            startActivity(new Intent(getContext(), MainActivity.class));

        }

        Regiter.setOnClickListener(this);
        signin.setOnClickListener(this);
        return view;
    }

    private void RegiterUser() {
        final String emailStr = email.getText().toString().trim();
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
        firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child(USERINFO_NODE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(EMAIL_NODE).setValue(emailStr);

                            mDatabase.child(USERINFO_NODE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(USERNAME_NODE).setValue(UserName.getText().toString());

                            mDatabase.child(USERINFO_NODE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(USERID_NODE).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            mDatabase.child(USERINFO_NODE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(PROFILE_PIC).setValue("");

                            saveUserName(getActivity());
                            Utilities.saveProfilePicUrl(getActivity());
                            getActivity().finish();
                            startActivity(new Intent(getContext(), MainActivity.class));
                        } else {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                    }
                });

    }


    @Override
    public void onClick(View view) {
        if (view == Regiter) {
            try {
                RegiterUser();
            } catch (Exception e) {
                Utilities.message(getContext(),"Enter valid data");
            }
        }
        if (view == signin) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fregiter, new LoginFragment())
                    .commit();
        }
    }
}
