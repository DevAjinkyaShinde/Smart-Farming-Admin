package com.silentcodder.smartfarmingadmin.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ProductId {
    @Exclude
    public String ProductId;
    public <T extends ProductId> T withId(@NonNull final String id){
        this.ProductId = id;
        return (T)this;
    }
}
