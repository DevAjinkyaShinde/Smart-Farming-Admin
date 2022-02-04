package com.silentcodder.smartfarmingadmin.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silentcodder.smartfarmingadmin.Model.NotificationData;
import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    List<NotificationData> notificationData;
    FirebaseFirestore firebaseFirestore;
    Context context;

    public NotificationAdapter(List<NotificationData> notificationData) {
        this.notificationData = notificationData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Msg = notificationData.get(position).getMsg();
        Long TimeStamp = notificationData.get(position).getTimeStamp();
        String SenderId = notificationData.get(position).getNotificationSender();
        String Status = notificationData.get(position).getStatus();

        if (Status.equals("Cancel")){
            holder.mIcon.setImageResource(R.drawable.red);
        }else if (Status.equals("Pending")){
            holder.mIcon.setImageResource(R.drawable.yellow);
        }

        Date d = new Date(TimeStamp);
        DateFormat dateFormat1 = new SimpleDateFormat("MMM dd , yyyy");
        String Date = dateFormat1.format(d.getTime());
        holder.mDate.setText(Date);
        holder.mMsg.setText(Msg);

        firebaseFirestore.collection("Users").document(SenderId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String Profile = task.getResult().getString("ProfileImgUrl");
                            String UserName = task.getResult().getString("UserName");
                            if (!TextUtils.isEmpty(UserName)){
                                holder.mUserName.setText(UserName);
                            }
                            if (!TextUtils.isEmpty(Profile)){
                                Picasso.get().load(Profile).into(holder.mProfileImg);
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return notificationData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mProfileImg;
        TextView mUserName;
        TextView mMsg;
        TextView mDate;
        ImageView mIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfileImg = itemView.findViewById(R.id.profileImg);
            mUserName = itemView.findViewById(R.id.userName);
            mMsg = itemView.findViewById(R.id.msg);
            mDate = itemView.findViewById(R.id.date);
            mIcon = itemView.findViewById(R.id.icon);
        }
    }
}
