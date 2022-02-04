package com.silentcodder.smartfarmingadmin.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.silentcodder.smartfarmingadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class AddProductFragment extends Fragment {

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
    ProgressBar progressBar;
    Uri profileImgUri;
    String ProfileUri=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBar = view.findViewById(R.id.loader);
        progressDialog = new ProgressDialog(getContext());
        mProductName = view.findViewById(R.id.productName);
        mDescription = view.findViewById(R.id.productDesc);
        mPrice = view.findViewById(R.id.price);
        mBtnSelectImg = view.findViewById(R.id.btnUploadImg);
        mBtnAddProduct = view.findViewById(R.id.btnAddProduct);
        mProductImg = view.findViewById(R.id.productImg);

        Spinner categorySpinner = view.findViewById(R.id.productCategory);
        Spinner unitSpinner = view.findViewById(R.id.Units);

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
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("ProductName",ProductName);
                    map.put("Description",Description);
                    map.put("Price", Price);
                    map.put("ProductImgUrl",ProfileUri);
                    map.put("Category",CategoryName);
                    map.put("Unit",UnitName);
                    map.put("TimeStamp",System.currentTimeMillis());

                    firebaseFirestore.collection("Products").add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Upload Product", Toast.LENGTH_SHORT).show();
                                        Fragment fragment = new HomeFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
                                    }
                                }
                            });
                }
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

                        ProfileUri = task.getResult().toString();
//                        HashMap<String,Object> map = new HashMap<>();
//                        map.put("ProfileImgUrl" , ProfileUri);
//
//                        firebaseFirestore.collection("Products").document(UserId).update(map)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        progressDialog.dismiss();
//                                        Toast.makeText(getContext(), "Upload Successfully..", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                        Fragment fragment = new SettingFragment();
//                                        getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getContext(), "Storage error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
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