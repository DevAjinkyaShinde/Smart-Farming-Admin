package com.silentcodder.smartfarmingadmin.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class OrderId {
    @Exclude
    public String OrderId;
    public <T extends OrderId> T withId(@NonNull final String id){
        this.OrderId = id;
        return (T)this;
    }
}
