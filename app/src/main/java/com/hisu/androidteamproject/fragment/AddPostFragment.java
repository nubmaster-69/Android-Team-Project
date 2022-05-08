package com.hisu.androidteamproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hisu.androidteamproject.R;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

import java.util.Date;

public class AddPostFragment extends Fragment {

    public static final String USER_POST = "_user_post";

    private ImageView userAvatar, statusImage;
    private EditText edtStatus;
    private TextView username;
    private Button btnNewPost;
    private Uri imgUri;

    private FirebaseFirestore fireStore;
    private StorageReference storageReference;

    public AddPostFragment(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_POST, user);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancesState) {

        View newPostView = inflater.inflate(R.layout.fragment_add_post, container, false);

        initFragmentUI(newPostView);

        User user = (User) getArguments().getSerializable(USER_POST);

        initFragmentData(user);

        ActivityResultLauncher resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        imgUri = result.getData().getData();
                        statusImage.setImageURI(imgUri);
                    }
                });

        statusImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });

        btnNewPost.setOnClickListener(view -> {
            if(!validatePost()) return;
            addNewPost(user.getEmail());
        });

        return newPostView;
    }

    private void initFragmentUI(View newPostView) {
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userAvatar = newPostView.findViewById(R.id.new_post_user_img);
        username = newPostView.findViewById(R.id.new_post_username);
        statusImage = newPostView.findViewById(R.id.new_post_pick_img);
        edtStatus = newPostView.findViewById(R.id.edt_new_post);
        btnNewPost = newPostView.findViewById(R.id.btn_add_new_post);
    }

    private void initFragmentData(User user) {
        Glide.with(userAvatar).load(user.getAvatar())
                .into(userAvatar);
        username.setText(user.getUsername());
    }

    private boolean validatePost() {
        String status = edtStatus.getText().toString();

        if(TextUtils.isEmpty(status) || imgUri == null) {
            showAlert("Bạn vui lòng điền đầy đủ các trường trước khi đăng nha!");
            return false;
        }

        return true;
    }

    private void addNewPost(String email) {
        ProgressDialog dia = new ProgressDialog(getContext());
        dia.setMessage("Đợi tý nhé \\(^3^)/");
        dia.show();

        StorageReference imageRef = storageReference.child(String.valueOf(new Date().getTime()));
        imageRef.putFile(imgUri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                .getDownloadUrl().addOnCompleteListener(task -> {
                    fireStore.collection("Users").whereEqualTo("email", email).get()
                            .addOnSuccessListener(snapshots -> {

                                String userID = snapshots.getDocuments().get(0).getId();

                                Post post = new Post(userID, edtStatus.getText().toString(),
                                        0, task.getResult().toString(), Timestamp.now());

                                fireStore.collection("Posts").add(post)
                                        .addOnSuccessListener(documentReference -> {
                                            dia.dismiss();
                                            confirmNewPost("Yay! Cảm ơn bạn đã chia sẻ khoảnh khắc của mình!");
                                        });
                            });
                }));
    }

    private void confirmNewPost(String msg) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.thumb_up)
                .setTitle("Đã thêm một khoảnh khắc mới!")
                .setMessage(msg).setPositiveButton(
                "Tiếp tục lướt!", (dialogInterface, i) -> {
                    getActivity().getSupportFragmentManager().popBackStack();
                }).show();
    }

    private void showAlert(String msg) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.new_post_warning)
                .setTitle("Úi! Có sự cố!")
                .setMessage(msg).setPositiveButton(
                "OK", null).show();
    }
}