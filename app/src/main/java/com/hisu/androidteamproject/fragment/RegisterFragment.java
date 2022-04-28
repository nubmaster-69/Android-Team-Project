package com.hisu.androidteamproject.fragment;

import android.annotation.SuppressLint;
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

import androidx.fragment.app.Fragment;

import com.hisu.androidteamproject.MainActivity;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.User;

public class RegisterFragment extends Fragment {

    private MainActivity containerActivity;

    private EditText edtUsername, edtEmail, edtPwd, edtAddress;
    private Spinner spGender;
    private TextView txtLoginNow;
    private Button btnRegister;
    private boolean isToggleShowPwd = false;

//    Todo: này chỉ để test, nhớ xoá nhe
    private User user;

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
        containerActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(containerActivity.getFrmContainer().getId(), new LoginFragment())
                .commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void toggleShowPassword() {
        edtPwd.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtPwd.getRight() - edtPwd.getCompoundDrawables()[2].getBounds().width()) - 20) {

                    isToggleShowPwd = !isToggleShowPwd;

                    if (isToggleShowPwd) {
                        edtPwd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_lock, 0, R.drawable.ic_eye_close, 0);
                        edtPwd.setTransformationMethod(null);
                    } else {
                        edtPwd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_lock, 0, R.drawable.icon_open_eye, 0);
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
            int avatar = gender.equalsIgnoreCase("nam")
                    ? R.drawable.ic_male : R.drawable.ic_female;

            user = new User(username, avatar, gender, email, address);

            Log.e(
                    RegisterFragment.class.getName(),
                    user.getUsername() + " - " + user.getGender()
            );

            //Todo: lưu user vào room sau đó lưu lên fire-store, update code sau :)
            registerWithFirebase(email, password);
        }
    }


    private boolean preValidate(String username, String email, String password, String address) {
        if (TextUtils.isEmpty(username)) {
            showError("Vui lòng điền tên người dùng!", edtUsername);
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            showError("Vui lòng điền email của bạn!", edtEmail);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email không hợp lệ!", edtEmail);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Vui lòng điền mật khẩu!", edtPwd);
            return false;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự!", edtPwd);
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            showError("Vui lòng điền nơi sinh sống của bạn!", edtAddress);
            return false;
        }

        return true;
    }

    private void showError(String errMsg, EditText field) {
        field.setError(errMsg);
        field.requestFocus();
    }

    private void registerWithFirebase(String email, String password) {
        //Todo: Viết code xử lý lưu user lên firebase và room ở đây hoặc tách ra :D
        // hiện tại chỉ là demo
        containerActivity.setFragment(new NewFeedFragment(user));
    }
}