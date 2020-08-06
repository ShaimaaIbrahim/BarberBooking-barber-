package com.google.androidbookingstaffapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.entities.BookingInformation;
import com.google.androidbookingstaffapp.ui.Adapter.AllTimeSlotAdapter;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.androidbookingstaffapp.model.Common.SpaceItemDecoration;
import com.google.androidbookingstaffapp.model.Interface.INotifiacationCountListner;
import com.google.androidbookingstaffapp.model.Interface.ITimeSlotLoadListner;
import com.google.androidbookingstaffapp.databinding.ActivityStaffHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StaffHomeActivity extends AppCompatActivity implements ITimeSlotLoadListner,INotifiacationCountListner {

    private ActivityStaffHomeBinding binding;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DocumentReference barberDoc;
    private ITimeSlotLoadListner iTimeSlotLoadListner;
    private android.app.AlertDialog alertDialog;
    private SimpleDateFormat simpleDateFormat;
    private View view;

    private TextView txt_notification_badge;
    //todo listnen to notification collection
    private CollectionReference notificationCollection;
    private EventListener<QuerySnapshot> eventNotification;
    private ListenerRegistration notificationListner;

    //todo listnen to currentBooking collection
    private CollectionReference currentBookingDateCollection;
    private EventListener<QuerySnapshot> eventBooking;
    private ListenerRegistration bookingRealTimeListner;


    private INotifiacationCountListner iNotifiacationCountListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this , R.layout.activity_staff_home);

        view=LayoutInflater.from(this).inflate(R.layout.activity_staff_home , null);

        iTimeSlotLoadListner=this;
        iNotifiacationCountListner = this;


        initBookingRealtimeUpdate();
        initNotificationRealtimeUpdate();



        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE , 0);

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyy");

        loadAvailableTimeSlotsForEachBarber(Common.currentBarber.getBarberId() ,

                simpleDateFormat.format(calendar.getTime()));

        setUpCalendar(view);
        initView();
    }

    private void initBookingRealtimeUpdate() {


        if (Common.state_name!=null && Common.selectedSalon!=null && Common.currentBarber!=null) {

            barberDoc = FirebaseFirestore.getInstance().collection("AllSalon").document(Common.state_name).collection("Branch").
                    document(Common.selectedSalon.getSalonId()).collection("Barber").document(Common.currentBarber.getBarberId());

        }



        final Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE , 0);

        eventBooking = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               loadAvailableTimeSlotsForEachBarber(Common.currentBarber.getBarberId() , Common.simpleDateFormat.format(date.getTime()));

            }
        };

        if (Common.simpleDateFormat!=null){
            currentBookingDateCollection = barberDoc.collection(Common.simpleDateFormat.format(date.getTime()));

            bookingRealTimeListner= currentBookingDateCollection.addSnapshotListener(eventBooking);
        }


    }

    private void initNotificationRealtimeUpdate() {

        notificationCollection =  FirebaseFirestore.getInstance().collection("AllSalon").document(Common.state_name)
                .collection("Branch").document(Common.selectedSalon.getSalonId())
                .collection("Barber").document(Common.currentBarber.getBarberId())
                .collection("Notification");


        // todo to  // listnen to notification collection where read => false
        // todo evenListner
        eventNotification = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
              if (queryDocumentSnapshots.size()   > 0){
                  loadNotification();
              }
            }
        };
        //todo listnerRegistration to notificationCollection
     notificationListner=  notificationCollection.whereEqualTo("read" , false)
                .addSnapshotListener(eventNotification);

    }

    private void setUpCalendar(View view) {
        //startDate
        Calendar start_date = Calendar.getInstance();
        start_date.add(Calendar.DATE , 0);
        //EndDate
        Calendar end_date = Calendar.getInstance();
        end_date.add(Calendar.DATE , 2);

        //setUpCalendar
        HorizontalCalendar horizontalCalendarView = new HorizontalCalendar.Builder(this , R.id.calendar_view)
                .range(start_date , end_date).datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS).defaultSelectedDate(start_date)
                .build();

        horizontalCalendarView.show();

        if (Common.bookingDate!=null && Common.currentBarber!=null){

            horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
                @Override
                public void onDateSelected(Calendar date, int position) {
                    if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis()){
                        Common.bookingDate = date;
                        loadAvailableTimeSlotsForEachBarber(Common.currentBarber.getBarberId() , Common.simpleDateFormat.format(date.getTime()));
                    }
                }
            });

        }

    }

    private void loadAvailableTimeSlotsForEachBarber(String barberId, final String bookDate) {

        alertDialog.show();




        barberDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){

                        if (Common.state_name!=null && Common.selectedSalon!=null && Common.currentBarber!=null){
                            CollectionReference date =   FirebaseFirestore.getInstance().collection("AllSalon")
                                    .document(Common.state_name).collection("Branch").document(Common.selectedSalon.getSalonId())
                                    .collection("Barber").document(Common.currentBarber.getBarberId()).collection(bookDate);


                            date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){


                                        QuerySnapshot querySnapshot = task.getResult();

                                        assert querySnapshot != null;
                                        if (querySnapshot.isEmpty()){
                                            iTimeSlotLoadListner.LoadTimeSlotEmpty();
                                        }
                                        else {

                                            List<BookingInformation> timeSlotList = new ArrayList<>();

                                            for (DocumentSnapshot documentSnapshot : task.getResult()){

                                                timeSlotList.add(documentSnapshot.toObject(BookingInformation.class));
                                                iTimeSlotLoadListner.LoadTimeSlotSuccess(timeSlotList);

                                            } } } }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iTimeSlotLoadListner.LoadTimeSlotFailed(e.getMessage());
                                }
                            });


                        } }}

                        }});
    }


    private void initView() {

        binding.recyclerTimeSlot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this , 3);
        binding.recyclerTimeSlot.setLayoutManager(gridLayoutManager);
        binding.recyclerTimeSlot.addItemDecoration(new SpaceItemDecoration(9));


               binding.bottomNavigator.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                   @Override
                   public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                       if (menuItem.getItemId()==R.id.action_exit){
                           logOut();
                       }
                       return true;
                   }
               });

         TextView barber_name=   binding.bottomNavigator.getHeaderView(0).findViewById(R.id.txt_barber_name);
         barber_name.setText(Common.currentBarber.getName());

        //todo code for navigation drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this , binding.drawerLayout
        , R.string.open , R.string.close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void logOut() {

        Paper.init(this);
        Paper.book().delete(Common.LOGGED_KEY );
        Paper.book().delete(Common.STATE_KEY );
        Paper.book().delete(Common.SALON_KEY );
        Paper.book().delete(Common.BARBER_KEY);


        new  AlertDialog.Builder(this).setMessage("Are You Sure You Want to logOut ?")

                .setCancelable(false).

                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(StaffHomeActivity.this , MainActivity.class));
                        finish();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }


    @Override
    public void onBackPressed() {

       new  AlertDialog.Builder(this).setMessage("Are You Sure You Want to Exit ?")

               .setCancelable(false).

                setPositiveButton("YES", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               Toast.makeText(StaffHomeActivity.this , "fake function exist" , Toast.LENGTH_LONG).show();

           }
       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           dialog.dismiss();
           }
       }).show();

    }

    @Override
    public void LoadTimeSlotSuccess(List<BookingInformation> timeSlotList) {

        AllTimeSlotAdapter allTimeSlotAdapter = new AllTimeSlotAdapter(this , timeSlotList);
        binding.recyclerTimeSlot.setAdapter(allTimeSlotAdapter);
        alertDialog.dismiss();
    }

    @Override
    public void LoadTimeSlotFailed(String message) {
        Toast.makeText(this , message , Toast.LENGTH_LONG).show();
    }

    @Override
    public void LoadTimeSlotEmpty() {
        AllTimeSlotAdapter allTimeSlotAdapter = new AllTimeSlotAdapter(this);
       binding.recyclerTimeSlot.setAdapter(allTimeSlotAdapter);
        alertDialog.dismiss();
    }


    //todo code for navigation drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        if (item.getItemId()==R.id.new_notification){

            txt_notification_badge.setText(" ");
            startActivity(new Intent(this , NotificationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.staff_home , menu);

          final MenuItem menuItem = menu.findItem(R.id.new_notification);



        txt_notification_badge = menuItem.getActionView().findViewById(R.id.notification_badge);

        loadNotification();

         menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 onOptionsItemSelected(menuItem);
             }
         });

        return super.onCreateOptionsMenu(menu);
    }

   private void loadNotification() {

notificationCollection.whereEqualTo("read" , false)
        .get().addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {

    Toast.makeText(StaffHomeActivity.this , e.getMessage() , Toast.LENGTH_LONG).show();
    }
}).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
         if (task.isSuccessful()){

             iNotifiacationCountListner.onNotificationCountSuccess(task.getResult().size());
         }
    }
});
    }

    @Override
    public void onNotificationCountSuccess(int count) {
     if (count==0){
          txt_notification_badge.setVisibility(View.INVISIBLE);
      }else {
          txt_notification_badge.setVisibility(View.VISIBLE);
          if (count<=9){
              txt_notification_badge.setText(String.valueOf(count));

          }else {
              txt_notification_badge.setText("+9");

          } }
    }

    @Override
    public void onNotificationCountFailed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initBookingRealtimeUpdate();
        initNotificationRealtimeUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
      if (bookingRealTimeListner!=null){
          bookingRealTimeListner.remove();
      }
      if (notificationListner!=null){
          notificationListner.remove();
      }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bookingRealTimeListner!=null){
            bookingRealTimeListner.remove();
        }
        if (notificationListner!=null){
            notificationListner.remove();
        }
    }
}
