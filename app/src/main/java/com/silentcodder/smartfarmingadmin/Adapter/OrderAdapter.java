package com.silentcodder.smartfarmingadmin.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.silentcodder.smartfarmingadmin.Fragments.OrderProductViewFragment;
import com.silentcodder.smartfarmingadmin.Model.OrderData;
import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
    List<OrderData> orderData;
    FirebaseFirestore firebaseFirestore;
    String Mobile;

    public OrderAdapter(List<OrderData> orderData) {
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Status = orderData.get(position).getStatus();
        long TimeStamp = orderData.get(position).getTimeStamp();
        String DStatus = orderData.get(position).getService();
        String Address = orderData.get(position).getAddress();
        long Total = orderData.get(position).getTotalPrice();
        String UserId = orderData.get(position).getUserId();
        String OrderId = orderData.get(position).OrderId;

        firebaseFirestore.collection("Users").document(UserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String UserName = task.getResult().getString("UserName");
                    Mobile = task.getResult().getString("MobileNumber");
                    holder.mUserName.setText(UserName);
                    holder.mAddress.setText(Address + "\nMobile No : " + Mobile);
                }
            }
        });

        if (Status.equals("Cancel")){
            holder.mIcon.setImageResource(R.drawable.red);
            holder.mStatus.setText("Cancel");
            holder.mStatus.setTextColor(Color.RED);
        }else if (Status.equals("Complete")){
            holder.mIcon.setImageResource(R.drawable.green);
            holder.mStatus.setTextColor(Color.parseColor("#2E8B57"));
            holder.mStatus.setText("Complete");
        }
        Date d = new Date(TimeStamp);
        DateFormat dateFormat1 = new SimpleDateFormat("MMM dd , yyyy");
        String Date = dateFormat1.format(d.getTime());
        holder.mDeliveryStatus.setText(DStatus);
        holder.mTotal.setText("₹ " +String.valueOf(Total));
        holder.mDate.setText(Date);
        holder.mOrderId.setText("#"+String.valueOf(TimeStamp));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("OrderId",OrderId);
                bundle.putString("Status",Status);
                bundle.putString("UserId",UserId);
                Fragment fragment = new OrderProductViewFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTotal;
        TextView mDate;
        TextView mDeliveryStatus,mStatus,mUserName,mAddress;
        ImageView mIcon;
        RelativeLayout relativeLayout;
        TextView mOrderId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTotal = itemView.findViewById(R.id.Total);
            mDate = itemView.findViewById(R.id.date);
            mStatus = itemView.findViewById(R.id.status);
            mIcon = itemView.findViewById(R.id.icon);
            mDeliveryStatus = itemView.findViewById(R.id.deliveryStatus);
            mAddress = itemView.findViewById(R.id.Address);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            mOrderId = itemView.findViewById(R.id.orderId);
            mUserName = itemView.findViewById(R.id.userName);
        }
    }
}
