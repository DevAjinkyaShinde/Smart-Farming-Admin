package com.silentcodder.smartfarmingadmin.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.silentcodder.smartfarmingadmin.Adapter.OrderAdapter;
import com.silentcodder.smartfarmingadmin.Model.OrderData;
import com.silentcodder.smartfarmingadmin.Notification.APIService;
import com.silentcodder.smartfarmingadmin.Notification.Client;
import com.silentcodder.smartfarmingadmin.Notification.Data;
import com.silentcodder.smartfarmingadmin.Notification.NotificationSender;
import com.silentcodder.smartfarmingadmin.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    List<OrderData> orderData;
    OrderAdapter orderAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;
    RecyclerView recyclerView;
    TextView orderCount;
    ExtendedFloatingActionButton mBtnFilter;
    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName;
    float dX,dY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        firebaseAuth = FirebaseAuth.getInstance();
        orderCount = view.findViewById(R.id.text);
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mBtnFilter = view.findViewById(R.id.filter);

        mBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), mBtnFilter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        String status = item.getTitle().toString();
                        if (!status.equals("All Order")){
                            loadData(status, view);
                        }else {
                            allOrder(view);
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu

            }
        });

        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_LOCATION:
                         dX = event.getX();
                         dY = event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        mBtnFilter.setX(dX-mBtnFilter.getWidth()/2);
                        mBtnFilter.setY(dY-mBtnFilter.getHeight()/2);
                        break;
                }
                return true;
            }
        });


        mBtnFilter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(mBtnFilter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(null, myShadow, null, View.DRAG_FLAG_GLOBAL);
                }
                return true;
            }
        });
//        allOrder();
        String Status = "Pending";
        loadData(Status,view);
        return view;
    }

    private void allOrder(View view) {
        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        firebaseFirestore.collection("Orders")
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        
                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String OrderId = doc.getDocument().getId();
                                OrderData mData = doc.getDocument().toObject(OrderData.class).withId(OrderId);
                                orderData.add(mData);
                                orderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void loadData(String status, View view) {

        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        firebaseFirestore.collection("Orders").whereEqualTo("Status",status)
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int count = value.size();
                         orderCount.setText(status + " Order list (" + String.valueOf(count) + ")");

                        if (value.isEmpty()){
                            LottieAnimationView lottieAnimationView = view.findViewById(R.id.empty);
                            TextView textView = view.findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);

                        }else {

                            LottieAnimationView lottieAnimationView = view.findViewById(R.id.empty);
                            TextView textView = view.findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }

                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String OrderId = doc.getDocument().getId();
                                OrderData mData = doc.getDocument().toObject(OrderData.class).withId(OrderId);
                                orderData.add(mData);
                                orderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Are you sure to exit ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                getActivity().moveTaskToBack(true);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}