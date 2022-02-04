package com.silentcodder.smartfarmingadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.silentcodder.smartfarmingadmin.Fragments.AddProductFragment;
import com.silentcodder.smartfarmingadmin.Fragments.HomeFragment;
import com.silentcodder.smartfarmingadmin.Fragments.NotificationFragment;
import com.silentcodder.smartfarmingadmin.Fragments.ProductListFragment;
import com.silentcodder.smartfarmingadmin.Fragments.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Fragment fragment;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {

                } else {
                    String  token = task.getResult().getToken();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("token",token);
                    firebaseFirestore.collection("Tokens").document(UserId)
                            .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    Log.i("FCM TOKEN", token);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.productList:
                        fragment = new ProductListFragment();
                        break;
                    case R.id.addProduct:
                        fragment = new AddProductFragment();
                        break;
                    case R.id.notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.setting:
                        fragment = new SettingFragment();
                        break;
                }

                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
    }
}