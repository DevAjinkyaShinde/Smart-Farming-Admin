package com.silentcodder.smartfarmingadmin.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silentcodder.smartfarmingadmin.Adapter.ProductListAdapter;
import com.silentcodder.smartfarmingadmin.Model.ProductData;
import com.silentcodder.smartfarmingadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProductListFragment extends Fragment {

    RecyclerView recyclerView;
    List<ProductData> productData;
    ProductListAdapter productListAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);


        productData = new ArrayList<>();
        productListAdapter = new ProductListAdapter(productData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productListAdapter);

        firebaseFirestore.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int count = value.size();
                TextView textView = view.findViewById(R.id.text);
                textView.setText("Product List (" + String.valueOf(count) + ")");
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String PostId = doc.getDocument().getId();
                        ProductData mData = doc.getDocument().toObject(ProductData.class).withId(PostId);
                        productData.add(mData);
                        productListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}