package com.silentcodder.smartfarmingadmin.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.silentcodder.smartfarmingadmin.Adapter.OrderProductListAdapter;
import com.silentcodder.smartfarmingadmin.Model.CardData;
import com.silentcodder.smartfarmingadmin.Notification.APIService;
import com.silentcodder.smartfarmingadmin.Notification.Client;
import com.silentcodder.smartfarmingadmin.Notification.Data;
import com.silentcodder.smartfarmingadmin.Notification.NotificationSender;
import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderProductViewFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String CustomerUserId;
    TextView mBtnCancel,mBtnComplete;

    RecyclerView recyclerView;
    List<CardData> cardData;
    OrderProductListAdapter orderProductListAdapter;
    String OrderId,Status;
    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_product_view, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        mBtnCancel = view.findViewById(R.id.btnCancel);
        mBtnComplete = view.findViewById(R.id.btnComplete);

        cardData = new ArrayList<>();
        orderProductListAdapter = new OrderProductListAdapter(cardData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderProductListAdapter);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            OrderId = bundle.getString("OrderId");
            Status = bundle.getString("Status");
            CustomerUserId = bundle.getString("UserId");

            if (Status.equals("Cancel") || Status.equals("Complete")){
                mBtnComplete.setVisibility(View.GONE);
                mBtnCancel.setVisibility(View.GONE);
            }
        }



        firebaseFirestore.collection("Orders")
                .document(OrderId).collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String CardId = doc.getDocument().getId();
                        CardData mData = doc.getDocument().toObject(CardData.class).withId(CardId);
                        cardData.add(mData);
                        orderProductListAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String , Object> map = new HashMap<>();
                map.put("Status","Cancel");
                firebaseFirestore.collection("Orders").document(OrderId)
                        .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Cancel Order", Toast.LENGTH_SHORT).show();
                            firebaseFirestore.collection("Tokens").document(CustomerUserId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String Token = task.getResult().getString("token");
                                        String Msg = "Your order cancel.";
                                        String Title = "Order Cancel";
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("Msg",Msg);
                                        map.put("Title",Title);
                                        map.put("TimeStamp",System.currentTimeMillis());
                                        map.put("NotificationSender",firebaseAuth.getCurrentUser().getUid());
                                        map.put("NotificationReceiver",CustomerUserId);
                                        map.put("Status","Cancel");
                                        firebaseFirestore.collection("Notification").add(map)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        sendNotification(Token,Title,Msg);
                                                        getFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment())
                                                                .commit();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String , Object> map = new HashMap<>();
                map.put("Status","Complete");
                firebaseFirestore.collection("Orders").document(OrderId)
                        .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            firebaseFirestore.collection("Tokens").document(CustomerUserId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Complete Order", Toast.LENGTH_SHORT).show();
                                        String Token = task.getResult().getString("token");
                                        String Msg = "Your order Complete.";
                                        String Title = "Order Complete";
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("Msg",Msg);
                                        map.put("Title",Title);
                                        map.put("TimeStamp",System.currentTimeMillis());
                                        map.put("NotificationSender",firebaseAuth.getCurrentUser().getUid());
                                        map.put("NotificationReceiver",CustomerUserId);
                                        map.put("Status","Complete");
                                        firebaseFirestore.collection("Notification").add(map)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        sendNotification(Token,Title,Msg);
                                                        getFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment())
                                                                .commit();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }

    private void sendNotification(String token, String title, String msg) {
        Data data = new Data(title,msg);
        NotificationSender notificationSender = new NotificationSender(data,token);

        APIService apiService = Client.getRetrofit(fcmUrl).create(APIService.class);

        apiService.sendNotification(notificationSender).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}