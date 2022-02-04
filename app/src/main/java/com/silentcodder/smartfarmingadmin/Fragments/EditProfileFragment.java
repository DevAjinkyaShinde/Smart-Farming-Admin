package com.silentcodder.smartfarmingadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    ImageView ProfileImg,BtnEditImg;
    EditText mUserName;
    Button mBtnUpdateProfile;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId;

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Uri profileImgUri;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ProfileImg = view.findViewById(R.id.profile);
        BtnEditImg = view.findViewById(R.id.editImg);
        mUserName = view.findViewById(R.id.userName);
        mBtnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        progressBar = view.findViewById(R.id.loader);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        UserId = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore.collection("Users").document(UserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String UserName = task.getResult().getString("UserName");
                    String ProfileUrl = task.getResult().getString("ProfileImgUrl");

                    if (!TextUtils.isEmpty(ProfileUrl)){
                        Picasso.get().load(ProfileUrl).into(ProfileImg);
                    }

                    mUserName.setText(UserName);
                }
            }
        });

        BtnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserName = mUserName.getText().toString();
                if (!TextUtils.isEmpty(UserName)){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("UserName",UserName);
                    mBtnUpdateProfile.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseFirestore.collection("Users").document(UserId).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Fragment fragment = new SettingFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                                        mBtnUpdateProfile.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(40)
                .setAspectRatio(1,1)
                .start(getActivity(),this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImgUri = result.getUri();
                ProfileImg.setImageURI(profileImgUri);
                progressDialog.dismiss();
                AddImg();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void AddImg() {

        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();
        StorageReference profileImgPath = storageReference.child("Profile").child(UserId + ".jpg");

        profileImgPath.putFile(profileImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImgPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        String ProfileUri = task.getResult().toString();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ProfileImgUrl" , ProfileUri);

                        firebaseFirestore.collection("Users").document(UserId).update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Upload Successfully..", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        Fragment fragment = new SettingFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Storage error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}