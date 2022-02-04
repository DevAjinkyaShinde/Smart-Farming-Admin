package com.silentcodder.smartfarmingadmin.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.silentcodder.smartfarmingadmin.Adapter.NotificationAdapter;
import com.silentcodder.smartfarmingadmin.Model.NotificationData;
import com.silentcodder.smartfarmingadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    List<NotificationData> notificationData;
    NotificationAdapter notificationAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.recycleView);

        notificationData = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationAdapter);


        firebaseFirestore.collection("Notification").whereNotEqualTo("NotificationSender",UserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.isEmpty()){
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottie);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottieAnimationView.playAnimation();
                        }
                    });
                }

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        NotificationData mData = doc.getDocument().toObject(NotificationData.class);
                        notificationData.add(mData);
                        notificationAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        return view;
    }
}