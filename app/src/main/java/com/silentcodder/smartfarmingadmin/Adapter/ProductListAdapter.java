package com.silentcodder.smartfarmingadmin.Adapter;

import android.annotation.SuppressLint;
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

import com.silentcodder.smartfarmingadmin.Fragments.ProductViewFragment;
import com.silentcodder.smartfarmingadmin.Model.ProductData;
import com.silentcodder.smartfarmingadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{
    List<ProductData> productData;

    public ProductListAdapter(List<ProductData> productData) {
        this.productData = productData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ProductName = productData.get(position).getProductName();
        String Price = productData.get(position).getPrice();
        String Weight = productData.get(position).getUnit();
        String ProductImg = productData.get(position).getProductImgUrl();
        String ProductId = productData.get(position).ProductId;

        Picasso.get().load(ProductImg).into(holder.mProductImg);
        holder.mProductName.setText(ProductName);
        holder.mWeight.setText(Weight);
        holder.mPrice.setText("₹ "+Price);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new ProductViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ProductId",ProductId);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mProductName;
        TextView mPrice;
        TextView mWeight;
        ImageView mProductImg;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.productPrice);
            mProductImg = itemView.findViewById(R.id.productImg);
            mWeight = itemView.findViewById(R.id.productWeight);
            mProductName = itemView.findViewById(R.id.productName);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
