package com.google.androidbookingstaffapp.ui.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.ui.Adapter.AllSalonAdapter;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Common.SpaceItemDecoration;
import com.google.androidbookingstaffapp.model.Interface.IBrabchLoadListner;
import com.google.androidbookingstaffapp.model.Interface.IGetBarberListner;
import com.google.androidbookingstaffapp.model.Interface.ILoadCountSalon;
import com.google.androidbookingstaffapp.model.Interface.IUserLoginRememberListner;
import com.google.androidbookingstaffapp.model.entities.Barber;
import com.google.androidbookingstaffapp.model.entities.Salon;
import com.google.androidbookingstaffapp.databinding.ActivitySalonListBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SalonListActivity extends AppCompatActivity implements ILoadCountSalon , IBrabchLoadListner
, IGetBarberListner , IUserLoginRememberListner {

    private ILoadCountSalon iLoadCountSalon;
    private IBrabchLoadListner iBrabchLoadListner;
    private AlertDialog alertDialog;
    private ActivitySalonListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       binding = DataBindingUtil.setContentView(this , R.layout.activity_salon_list);

       iLoadCountSalon=this;
       iBrabchLoadListner=this;


   //    getSupportActionBar().setTitle("All Appointments");

       alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

       getSupportActionBar().hide();

       initView();
       loadSalonBaseOnCity(Common.state_name);
    }

    private void loadSalonBaseOnCity(String state_name) {
        alertDialog.show();

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(state_name).collection("Branch")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()){
                     List<Salon> salons = new ArrayList<>();
                     iLoadCountSalon.onLoadCountSalonSuccess(task.getResult().size());
                     for (QueryDocumentSnapshot snapshot : task.getResult()){

                         Salon salon = snapshot.toObject(Salon.class);
                         salon.setSalonId(snapshot.getId());

                         salons.add(salon);
                     }
                     iBrabchLoadListner.onBranchLoadSuccess(salons);
                 }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              iBrabchLoadListner.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    private void initView() {
        binding.recyclerState.setHasFixedSize(true);
        binding.recyclerState.setLayoutManager(new GridLayoutManager(this , 2));
        binding.recyclerState.addItemDecoration(new SpaceItemDecoration(8));

    }


    @Override
    public void onLoadCountSalonSuccess(int count) {

     binding.txtSalonCount.setText(new StringBuilder("All Salons ( ").append(count).append(" )"));
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {

        AllSalonAdapter allSalonAdapter = new AllSalonAdapter(this , salonList , this , this );
        binding.recyclerState.setAdapter(allSalonAdapter);

        if (alertDialog.isShowing()) alertDialog.dismiss();

    }

    @Override
    public void onBranchLoadFailed(String message) {
        if (alertDialog.isShowing()) alertDialog.dismiss();
        Toast.makeText(this , message , Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnGetBarberSuccess(Barber barber) {
       Common.currentBarber=barber;
        Paper.book().write(Common.BARBER_KEY , new Gson().toJson(Common.currentBarber));

    }

    @Override
    public void OnUserLoginSuccess(String user) {

        Paper.init(this);
        Paper.book().write(Common.LOGGED_KEY , user);
        Paper.book().write(Common.STATE_KEY , Common.state_name);
        //convrt salon object to string
        Paper.book().write(Common.SALON_KEY , new Gson().toJson(Common.selectedSalon));


    }
}
