package com.google.androidbookingstaffapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.fcm.FcmCommon;
import com.google.androidbookingstaffapp.ui.Adapter.MyStateAdapter;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Common.SpaceItemDecoration;
import com.google.androidbookingstaffapp.model.Interface.IOnstateLoadListner;
import com.google.androidbookingstaffapp.model.entities.Barber;
import com.google.androidbookingstaffapp.model.entities.City;
import com.google.androidbookingstaffapp.model.entities.Salon;
import com.google.androidbookingstaffapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IOnstateLoadListner {

    private ActivityMainBinding binding ;
    private IOnstateLoadListner iOnstateLoadListner;
    private CollectionReference allSalonsRef;
    private MyStateAdapter adapter;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this ,  R.layout.activity_main);

        alertDialog= new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        iOnstateLoadListner=this;


        getSupportActionBar().hide();

        Dexter.withActivity(this)
                .withPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CAMERA}).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {


                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                Log.e("Shaimaa" , instanceIdResult.getToken());

                                FcmCommon.updateToken(MainActivity.this , instanceIdResult.getToken() );
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this , e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });


                Paper.init(MainActivity.this);
                String user= Paper.book().read(Common.LOGGED_KEY);


                if (TextUtils.isEmpty(user)){

                    initView();
                    loadAllstatesFromFirestore();

                }else {


                    Common.state_name = Paper.book().read(Common.STATE_KEY);

                    Gson gson = new Gson();
                    Common.selectedSalon=  gson.fromJson(Paper.book().read(Common.SALON_KEY, " "), new TypeToken<Salon>(){}.getType());
                    Common.currentBarber=  gson.fromJson(Paper.book().read(Common.BARBER_KEY, " "), new TypeToken<Barber>(){}.getType());

                    Intent intent = new Intent(MainActivity.this , StaffHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();



    }

    private void loadAllstatesFromFirestore() {

        alertDialog.show();
        allSalonsRef= FirebaseFirestore.getInstance().collection("AllSalon");

        allSalonsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<City> cities = new ArrayList<>();

                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        City city = snapshot.toObject(City.class);
                        cities.add(city);
                    }
                 iOnstateLoadListner.onLoadStateSuccess(cities);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iOnstateLoadListner.onLoadStateFailed(e.getMessage());
            }
        });
    }

    private void initView() {
        binding.recyclerState.setHasFixedSize(true);
        binding.recyclerState.addItemDecoration(new SpaceItemDecoration(8));
        binding.recyclerState.setLayoutManager(new GridLayoutManager(this , 2));

    }

    @Override
    public void onLoadStateSuccess(List<City> cityList) {
          adapter = new MyStateAdapter(cityList , this);
          binding.recyclerState.setAdapter(adapter);
        if (alertDialog.isShowing()) alertDialog.dismiss();
    }

    @Override
    public void onLoadStateFailed(String message) {

        if (alertDialog.isShowing()) alertDialog.dismiss();

        Toast.makeText(this , message , Toast.LENGTH_LONG).show();
    }
}
