package com.silentcodder.smartfarmingadmin.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {

    Button mBtnEdit;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    CircleImageView mProfileImg;
    TextView mUserName,mMobileNumber;
    int pending,complete,cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mBtnEdit = view.findViewById(R.id.btnEdit);
        mProfileImg = view.findViewById(R.id.profileImg);
        mUserName = view.findViewById(R.id.userName);
        mMobileNumber = view.findViewById(R.id.mobileNumber);


        //Edit profile
        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });

        firebaseFirestore.collection("Users").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String UserName = task.getResult().getString("UserName");
                            String MobileNumber = task.getResult().getString("MobileNumber");
                            String ProfileImg = task.getResult().getString("ProfileImgUrl");

                            mUserName.setText(UserName);
                            mMobileNumber.setText(MobileNumber);

                            if (!TextUtils.isEmpty(ProfileImg)){
                                Picasso.get().load(ProfileImg).into(mProfileImg);
                            }
                        }
                    }
                });

        firebaseFirestore.collection("Orders").whereEqualTo("Status","Pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        pending = value.size();
                    }
                });
        firebaseFirestore.collection("Orders").whereEqualTo("Status","Complete")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        complete = value.size();
                    }
                });
        firebaseFirestore.collection("Orders").whereEqualTo("Status","Cancel")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        cancel = value.size();
                    }
                });

        EditText ad = view.findViewById(R.id.ad);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Ad = ad.getText().toString().trim();
                HashMap<String,Object> map = new HashMap<>();
                map.put("Ad",Ad);
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("UserId",UserId);

                firebaseFirestore.collection("Advertisement").document(UserId).set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        });
            }
        });

        firebaseFirestore.collection("Advertisement").document(UserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String Ad = task.getResult().getString("Ad");
                    ad.setText(Ad);

                }
            }
        });
        return view;
    }
}