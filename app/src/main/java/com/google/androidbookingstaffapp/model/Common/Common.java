package com.google.androidbookingstaffapp.model.Common;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;

import com.google.androidbookingstaffapp.model.entities.Barber;
import com.google.androidbookingstaffapp.model.entities.BookingInformation;
import com.google.androidbookingstaffapp.model.entities.Salon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {

    public static final int TIME_SLOT_TOTAL = 20 ;
    public static final String DISPLAY_TAG = "DISPLAY_TAG";
    public static final String LOGGED_KEY = "LOGGED_KEY";
    public static final String STATE_KEY = "STATE_KEY";
    public static final String SALON_KEY = "SALON_KEY";
    public static final String BARBER_KEY =  "BARBER_KEY";
    public static final int MAX_NOTIFICTION_PER_LOAD = 10;
    public static final String SERVICE_ADDED = "SERVICE_ADDED";
    public static final double DEFAULT_PRICE = 30 ;
    public static final String MONEY_SIGN = "$" ;
    public static final String SHOPPING_ITEMS = "SHOPPING_ITEMS" ;
    public static final String IMAGE_URL = "image_url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String DOWNLOAD_IMAGE = "Downloadable_Url";


    public static final String RATING_STATE_KEY = "rating_state";
    public static final String RATING_SALON_ID = "rating_salon";
    public static final String RATING_SALON_NAME = "rating_salon_name";
    public static final String RATING_BARBER_ID = "rating_barber_id" ;


    public static String state_name;
    public static Salon selectedSalon;
    public static Barber currentBarber;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyy");

    public static Calendar bookingDate = Calendar.getInstance();
    public static BookingInformation currentBookingInformation;

    public static String ConvertTimeSlotsToString(int position) {
        switch (position){
            case 0 :
                return "9:00_9:30";
            case 1 :
                return "9:30_10:00";
            case 2 :
                return "10:00_10:30";

            case 3 :
                return "10:30_11:00";
            case 4:
                return "11:00_11:30";
            case 5 :
                return "11:30_12:00";
            case 6 :
                return "12:00_12:30";
            case 7 :
                return "12:30_13:00";
            case 8 :
                return "13:00_13:30";

            case 9 :
                return "13:30_14:00";
            case 10 :
                return "14:00_14:30";
            case 11 :
                return "14:30_15:00";
            case 12 :
                return "15:00_15:30";
            case 13 :
                return "15:30_16:00";
            case 14 :
                return "16:00_16:30";

            case 15 :
                return "16:30_17:00";
            case 16 :
                return "17:00_17:30";
            case 17 :
                return "17:30_18:00";
            case 18 :
                return "18:00_18:30";
            case 19:
                return "18:30_19:00";

            default:return "Closed";
        }
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0 ,10)).append("....")
                .toString() : name ;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;

        if (fileUri.getScheme().equals("content")){

            Cursor cursor = contentResolver.query(fileUri , null , null , null );

            try {
                if (cursor!=null && cursor.moveToFirst()){

                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }}finally {

                cursor.close();
            }
        }
        if (result==null){

            result = fileUri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut!=-1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }
}
