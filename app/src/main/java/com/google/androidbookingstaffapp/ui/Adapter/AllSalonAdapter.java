package com.google.androidbookingstaffapp.ui.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Common.CustomLoginDialog;
import com.google.androidbookingstaffapp.model.Interface.IDialogeClickListner;
import com.google.androidbookingstaffapp.model.Interface.IGetBarberListner;
import com.google.androidbookingstaffapp.model.Interface.IUserLoginRememberListner;
import com.google.androidbookingstaffapp.model.Interface.IrecyclerItemSelectedListner;
import com.google.androidbookingstaffapp.model.entities.Barber;
import com.google.androidbookingstaffapp.model.entities.Salon;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.ui.activities.StaffHomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

public class AllSalonAdapter extends RecyclerView.Adapter<AllSalonAdapter.viewHolder> implements IDialogeClickListner {

    private List<Salon>salonList;
    private Context context;
    private List<CardView> cardViewList;
    private LocalBroadcastManager localBroadcastManager;
    private IGetBarberListner iGetBarberListner;
    private IUserLoginRememberListner iUserLoginRememberListner;



    public AllSalonAdapter(Context context , List<Salon> salonList , IUserLoginRememberListner iUserLoginRememberListner  , IGetBarberListner iGetBarberListner) {
          this.context=context;
          this.salonList=salonList;
          cardViewList=new ArrayList<>();
          this.iUserLoginRememberListner=iUserLoginRememberListner;
          this.iGetBarberListner=iGetBarberListner;
    }

    @NonNull
    @Override
    public AllSalonAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_salon_item , parent , false);

        return new AllSalonAdapter.viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AllSalonAdapter.viewHolder holder, int i) {

         final Salon salon = salonList.get(i);

         holder.txt_name.setText(salon.getName());
         holder.txt_address.setText(salon.getAddress());

         if (!cardViewList.contains(holder.cardView)){
             cardViewList.add(holder.cardView);
         }

         holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
             @Override
             public void onItemSelectedListner(View view, int position) {

                 Common.selectedSalon= salon;

                  showLogInDialog();
             }
         });
    }

    private void showLogInDialog() {

        CustomLoginDialog.getInstance().showLoginDialoge("STAFF LOGIN" , "LOGIN"
                , "CANCEL" , context , this);
    }


    @Override
    public int getItemCount() {
        return salonList.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, final String userName, String passWord) {
        final AlertDialog loading = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();

        loading.show();

        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.state_name).collection("Branch")
                .document(Common.selectedSalon.getSalonId()).collection("Barber")
                .whereEqualTo("username" , userName)
                .whereEqualTo("password" , passWord).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().size() > 0){

                                dialogInterface.dismiss();
                                loading.dismiss();

                                iUserLoginRememberListner.OnUserLoginSuccess(userName);

                                Barber barber = new Barber();

                                for (DocumentSnapshot staffIntent : task.getResult()){

                                    barber = staffIntent.toObject(Barber.class);
                                    barber.setBarberId(staffIntent.getId());
                                }
                                iGetBarberListner.OnGetBarberSuccess(barber);


                                Intent staffIntent = new Intent(context , StaffHomeActivity.class);
                                staffIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                staffIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(staffIntent);


                            }else {
                                loading.dismiss();
                                Toast.makeText(context , "wrong username/password or wrong salon " , Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 loading.dismiss();
                Toast.makeText(context , e.getMessage() , Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {

         dialogInterface.dismiss();

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private IrecyclerItemSelectedListner irecyclerItemSelectedListner;

        private TextView txt_name;
        private TextView txt_address;
        private CardView cardView;

        public viewHolder setIrecyclerItemSelectedListner(IrecyclerItemSelectedListner irecyclerItemSelectedListner) {
            this.irecyclerItemSelectedListner = irecyclerItemSelectedListner;
            return this;
        }

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_address=itemView.findViewById(R.id.txt_address);
            cardView=itemView.findViewById(R.id.salon_card_view);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

             irecyclerItemSelectedListner.onItemSelectedListner(v , getAdapterPosition());
        }
    }
}
