package com.silentcodder.smartfarmingadmin.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProductViewFragment extends Fragment {

    String[] Category = {"Select Category" ,"Herbicide","Plant Nutrients","Fungicides","Insecticides","Seeds"};
    String[] Unit = {"Select Unit","50 kg","30 kg","5 Kg","1 Kg", "500 gm", "250 gm", "100 gm", "50 gm","10 gm","10 ml","50 ml","100 ml","250 ml","500 ml","1 L","5 L","10 L"};
    String CategoryName;
    String UnitName;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    EditText mProductName,mDescription,mPrice;
    Button mBtnSelectImg,mBtnAddProduct;
    ImageView mProductImg;

    ProgressDialog progressDialog;
    ProgressBar progressBar,progressBar2;
    Uri profileImgUri;
    String ProfileUri=null;
    String ProductId;
    Spinner categorySpinner,unitSpinner;
    TextView mBtnDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_view, container, false);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            ProductId = bundle.getString("ProductId");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBar = view.findViewById(R.id.loader);
        progressBar2 = view.findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(getContext());
        mProductName = view.findViewById(R.id.productName);
        mDescription = view.findViewById(R.id.productDesc);
        mPrice = view.findViewById(R.id.price);
        mBtnSelectImg = view.findViewById(R.id.btnUploadImg);
        mBtnAddProduct = view.findViewById(R.id.btnAddProduct);
        mProductImg = view.findViewById(R.id.productImg);
        mBtnDelete = view.findViewById(R.id.btnDelete);

        firebaseFirestore.collection("Products").document(ProductId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String ProductName = task.getResult().getString("ProductName");
                    String Description = task.getResult().getString("Description");
                    String Price = task.getResult().getString("Price");
                    ProfileUri = task.getResult().getString("ProductImgUrl");
                    CategoryName = task.getResult().getString("Category");
                    UnitName = task.getResult().getString("Unit");

                    mProductName.setText(ProductName);
                    mDescription.setText(Description);
                    mPrice.setText(Price);
                    Picasso.get().load(ProfileUri).into(mProductImg);
                    TextView Cat = view.findViewById(R.id.productCatText);
                    Cat.setText("Category* (" + CategoryName + " )");
                    TextView Unit = view.findViewById(R.id.unitTxt);
                    Unit.setText("Unit* (" + UnitName + " )");
                }
            }
        });

        categorySpinner = view.findViewById(R.id.productCategory);
        unitSpinner = view.findViewById(R.id.Units);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CategoryName = Category[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UnitName = Unit[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter categoryAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,Category);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter unitAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,Unit);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        unitSpinner.setAdapter(unitAdapter);

        mBtnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        mBtnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ProductName = mProductName.getText().toString();
                String Description = mDescription.getText().toString();
                String Price = mPrice.getText().toString();

                if (TextUtils.isEmpty(ProductName)){
                    mProductName.setError("");
                }else if(TextUtils.isEmpty(Price)){
                    mPrice.setError("");
                }else if (CategoryName.equals("Select Category")){
                    Toast.makeText(getContext(), "Select Category", Toast.LENGTH_SHORT).show();
                }else if (UnitName.equals("Select Unit")){
                    Toast.makeText(getContext(), "Select Unit", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mBtnAddProduct.setVisibility(View.GONE);
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("ProductName",ProductName);
                    map.put("Description",Description);
                    map.put("Price", Price);
                    map.put("ProductImgUrl",ProfileUri);
                    map.put("Category",CategoryName);
                    map.put("Unit",UnitName);
                    map.put("TimeStamp",System.currentTimeMillis());

                    firebaseFirestore.collection("Products").document(ProductId).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        mBtnAddProduct.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), "Upload Product", Toast.LENGTH_SHORT).show();
                                        Fragment fragment = new ProductListFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
                                    }
                                }
                            });
                }
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.order_confirm_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView btnContinue = dialog.findViewById(R.id.btnDelete);
                TextView btnCancel = dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("Products").document(ProductId).delete();
                        dialog.dismiss();
                        Fragment fragment = new ProductListFragment();
                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                    }
                });
            }
        });

        return view;
    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(40)
                .start(getActivity(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImgUri = result.getUri();
                mProductImg.setImageURI(profileImgUri);
                progressDialog.dismiss();
                AddImg();
                progressBar2.setVisibility(View.VISIBLE);
                mBtnSelectImg.setVisibility(View.GONE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void AddImg() {

        StorageReference profileImgPath = storageReference.child("Products").child(System.currentTimeMillis() + ".jpg");

        profileImgPath.putFile(profileImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImgPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        progressBar2.setVisibility(View.GONE);
                        mBtnSelectImg.setVisibility(View.VISIBLE);
                        ProfileUri = task.getResult().toString();
                        Toast.makeText(getContext(), "Save Image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}