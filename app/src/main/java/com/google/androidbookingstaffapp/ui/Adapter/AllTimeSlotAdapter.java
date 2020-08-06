package com.google.androidbookingstaffapp.ui.Adapter;

import android.content.Context;
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
import com.google.androidbookingstaffapp.model.Interface.IrecyclerItemSelectedListner;
import com.google.androidbookingstaffapp.model.entities.BookingInformation;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.ui.activities.DoneServiceActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

public class AllTimeSlotAdapter extends RecyclerView.Adapter<AllTimeSlotAdapter.viewHolder> {

private Context context;
private List<BookingInformation> timeSlotList;
private LocalBroadcastManager broadcastManager;
private List<CardView > cardViewList;

public AllTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList= new ArrayList<>();
        broadcastManager = LocalBroadcastManager.getInstance(context);
        }


public AllTimeSlotAdapter(Context context, List<BookingInformation> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList= new ArrayList<>();
        broadcastManager = LocalBroadcastManager.getInstance(context);
        }

@NonNull
@Override
public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_time_slot, parent , false);
        return new AllTimeSlotAdapter.viewHolder(view);
        }


@Override
public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        holder.txt_time_slot.setText(new StringBuilder(Common.ConvertTimeSlotsToString(position)));

        if (timeSlotList.size()==0){

        holder.txt_time_slot_description.setText("Available");
        holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
        holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));


        holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
            @Override
            public void onItemSelectedListner(View view, int position) {
                 // fix crash
            }
        });


        }else {

        for (final BookingInformation slotValue : timeSlotList){

        // convert long value to String value then parse it to integer value
        int slot = slotValue.getSlot();

        if (slot == position) {

            if (!slotValue.isDone()) {

            //we set tag for all slots are full
            // so we can set bacGround for all others
            holder.card_time_slot.setTag(Common.DISPLAY_TAG);
            //
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.txt_time_slot_description.setText("Full");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

            holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
                @Override
                public void onItemSelectedListner(View view, int position) {

                    // only add for gray timeSlot
                    // here we get Booking information then start DoneServiceActivity
                    FirebaseFirestore.getInstance().collection("AllSalon").
                             document(Common.state_name).collection("Branch").
                             document(Common.selectedSalon.getSalonId()).collection("Barber")
                            .document(Common.currentBarber.getBarberId())
                            .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime())).
                             document(String.valueOf(slotValue.getSlot())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            if (task.isSuccessful()) {

                                if (task.getResult().exists()) {


                                    Common.currentBookingInformation = task.getResult().toObject(BookingInformation.class);
                                    Common.currentBookingInformation.setBookingId(task.getResult().getId());

                                    context.startActivity(new Intent(context, DoneServiceActivity.class));
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        else {
                holder.card_time_slot.setTag(Common.DISPLAY_TAG);

                holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                holder.txt_time_slot_description.setText("Done");
                holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
                    @Override
                    public void onItemSelectedListner(View view, int position) {
                        //

                    }
                });
            }
        }else {
            // fix crash
            if (holder.getIrecyclerItemSelectedListner() == null){
                holder.setIrecyclerItemSelectedListner(new IrecyclerItemSelectedListner() {
                    @Override
                    public void onItemSelectedListner(View view, int position) {
                        //

                    }
                });
            }
        }

        } }

        if (!cardViewList.contains(holder.card_time_slot)){
        cardViewList.add(holder.card_time_slot);
        }

}


@Override
public int getItemCount() {

        return Common.TIME_SLOT_TOTAL;
        }

public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private IrecyclerItemSelectedListner irecyclerItemSelectedListner;
    private CardView card_time_slot;
    private TextView txt_time_slot , txt_time_slot_description;

    public viewHolder setIrecyclerItemSelectedListner(IrecyclerItemSelectedListner irecyclerItemSelectedListner) {
        this.irecyclerItemSelectedListner = irecyclerItemSelectedListner;
        return this;
    }

    public viewHolder(@NonNull View itemView) {
        super(itemView);

        card_time_slot=itemView.findViewById(R.id.card_time_slot);
        txt_time_slot=itemView.findViewById(R.id.txt_time_slot);
        txt_time_slot_description=itemView.findViewById(R.id.txt_time_slot_discription);

        itemView.setOnClickListener(this);
    }

    public IrecyclerItemSelectedListner getIrecyclerItemSelectedListner() {
        return irecyclerItemSelectedListner;
    }

    @Override
    public void onClick(View v) {
        irecyclerItemSelectedListner.onItemSelectedListner(v , getAdapterPosition());
    }
}
}
