package com.hisu.androidteamproject.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.User;

public class LoginFragment extends Fragment {

    private MainActivity containerActivity;
    private TextView txtRegisterNow;
    private EditText edtEmail, edtPwd;
    private Button btnLogin;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;

    private boolean isToggleShowPwd = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        initFragmentUI(loginView);

        txtRegisterNow.setOnClickListener(view -> switchToRegisterScreen());

        btnLogin.setOnClickListener(view -> login(
                edtEmail.getText().toString().trim(),
                edtPwd.getText().toString().trim()
        ));

        toggleShowPassword();

        return loginView;
    }

    private void initFragmentUI(View loginView) {
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        containerActivity = (MainActivity) getActivity();
        txtRegisterNow = loginView.findViewById(R.id.txvRegisterNow);
        edtEmail = loginView.findViewById(R.id.edtEmail);
        edtPwd = loginView.findViewById(R.id.edtPwd);
        btnLogin = loginView.findViewById(R.id.btnLogin);
    }

    private void switchToRegisterScreen() {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(containerActivity.getFrmContainer().getId(), new RegisterFragment())
                .commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void toggleShowPassword() {
        edtPwd.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //0 -> drawable_left, 1 -> drawable_top, 2 -> drawable_right, 3 -> drawable_bottom
                if (event.getRawX() >=
                        (edtPwd.getRight() -
                                edtPwd.getCompoundDrawables()[2].getBounds().width()) - 20
                ) {
                    isToggleShowPwd = !isToggleShowPwd;

                    if (isToggleShowPwd) {
                        edtPwd.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_lock, 0, R.drawable.ic_eye_close, 0);
                        edtPwd.setTransformationMethod(null);
                    } else {
                        edtPwd.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_lock, 0, R.drawable.icon_open_eye, 0);
                        edtPwd.setTransformationMethod(new PasswordTransformationMethod());
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void login(String email, String password) {
        if (preValidate(email, password))
            loginWithFirebase(email, password);
    }

    private boolean preValidate(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showError("Vui lòng điền gmail để đăng nhập!", edtEmail);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Gmail không hợp lệ!", edtEmail);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Vui lòng điền mật khẩu để đăng nhập!", edtPwd);
            return false;
        }

        return true;
    }

    private void showError(String errMsg, EditText field) {
        field.setError(errMsg);
        field.requestFocus();
    }

    private void loginWithFirebase(String email, String password) {

//      Deprecated but still good :v Customize layout if u want to
        ProgressDialog dia = new ProgressDialog(getContext());
        dia.setMessage("Logging in! Please wait...");
        dia.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    fireStore.collection("Users")
                            .whereEqualTo("email", email).get()
                            .addOnSuccessListener(snapshots -> {
                                dia.dismiss();
                                goToNewFeed(snapshots.getDocuments().get(0).toObject(User.class));
                            });
                })
                .addOnFailureListener(e -> {
                    dia.dismiss();
                    showAlert(e.getMessage());
                });
    }

    private void goToNewFeed(User user) {
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerActivity.getFrmContainer().getId(),
                        new NewFeedFragment(user))
                .addToBackStack("new_feed_ui")
                .commit();
    }

    private void showAlert(String msg) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_alert)
                .setTitle("Something went wrong!")
                .setMessage(msg).setPositiveButton("OK", null).show();
    }
}