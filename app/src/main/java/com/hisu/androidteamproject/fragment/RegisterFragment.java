package com.hisu.androidteamproject.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterFragment extends Fragment {

    private MainActivity containerActivity;

    private EditText edtUsername, edtEmail, edtPwd, edtAddress;
    private Spinner spGender;
    private TextView txtLoginNow;
    private Button btnRegister;
    private boolean isToggleShowPwd = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View registerView =
                inflater.inflate(R.layout.fragment_register, container, false);

        initFragmentUI(registerView);

        txtLoginNow.setOnClickListener(view -> switchToLoginScreen());

        btnRegister.setOnClickListener(view -> register(
                edtUsername.getText().toString().trim(),
                edtEmail.getText().toString().trim(),
                edtPwd.getText().toString().trim(),
                edtAddress.getText().toString().trim(),
                spGender.getSelectedItem().toString()
        ));

        toggleShowPassword();

        return registerView;
    }

    private void initFragmentUI(View registerView) {
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        containerActivity = (MainActivity) getActivity();

        edtUsername = registerView.findViewById(R.id.edt_regis_username);
        edtEmail = registerView.findViewById(R.id.edt_regis_email);
        edtPwd = registerView.findViewById(R.id.edt_regis_pwd);
        edtAddress = registerView.findViewById(R.id.edt_regis_address);
        txtLoginNow = registerView.findViewById(R.id.txt_login_now);
        btnRegister = registerView.findViewById(R.id.btn_register);
        spGender = registerView.findViewById(R.id.sp_gender);
    }

    private void switchToLoginScreen() {
        FragmentManager manager = containerActivity.getSupportFragmentManager();

        manager.popBackStack();

        manager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(containerActivity.getFrmContainer().getId(), new LoginFragment())
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

    private void register(
            String username, String email,
            String password, String address, String gender
    ) {
        if (preValidate(username, email, password, address)) {

            ProgressDialog dia = new ProgressDialog(getContext());
            dia.setMessage("We're working on it! Please wait...");
            dia.show();

            firebaseAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(result -> {
                if (!result.getSignInMethods().isEmpty()) {
                    dia.dismiss();
                    showAlert("C???nh B??o!","Email n??y ???? ???????c d??ng ????? ????ng k?? t??i kho???n tr?????c ????!" +
                            " Vui l??ng ki???m tra l???i!");
                    edtEmail.requestFocus();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                User user = new User(username, gender, email, address, new ArrayList<>());
                                user.setDefaultAvatar();
                                fireStore.collection("Users").add(user)
                                        .addOnSuccessListener(documentReference -> {
                                            dia.dismiss();
                                            showAlert("Congrats!","????ng k?? th??nh c??ng!");
                                            clearAllText();
                                        })
                                        .addOnFailureListener(e -> showAlert("Something went wrong",e.getMessage()));
                            })
                            .addOnFailureListener(e -> {
                                dia.dismiss();
                                showAlert("Something went wrong", e.getMessage());
                            });
                }
            });
        }
    }

    private boolean preValidate(String username, String email, String password, String address) {
        if (TextUtils.isEmpty(username)) {
            showError("Vui l??ng ??i???n t??n ng?????i d??ng!", edtUsername);
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            showError("Vui l??ng ??i???n email c???a b???n!", edtEmail);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email kh??ng h???p l???!", edtEmail);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Vui l??ng ??i???n m???t kh???u!", edtPwd);
            return false;
        }

        if (password.length() < 6) {
            showError("M???t kh???u ph???i c?? ??t nh???t 6 k?? t???!", edtPwd);
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            showError("Vui l??ng ??i???n n??i sinh s???ng c???a b???n!", edtAddress);
            return false;
        }

        return true;
    }

    private void showError(String errMsg, EditText field) {
        field.setError(errMsg);
        field.requestFocus();
    }

    private void showAlert(String title, String msg) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_alert)
                .setTitle(title)
                .setMessage(msg).setPositiveButton("OK",
                (dialogInterface, i) -> switchToLoginScreen())
                .show();
    }

    private void clearAllText() {
        edtUsername.setText("");
        edtEmail.setText("");
        edtAddress.setText("");
        edtPwd.setText("");
    }
}