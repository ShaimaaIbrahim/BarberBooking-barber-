package com.google.androidbookingstaffapp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Salon implements Parcelable {

    public Salon() {
    }

    private String name , address ,salonId , phone , website , openHour;

    public String getName() {
        return name;
    }

    public Salon setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Salon setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getSalonId() {
        return salonId;
    }

    public Salon setSalonId(String salonId) {
        this.salonId = salonId;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Salon setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public Salon setWebsite(String website) {
        this.website = website;
        return this;
    }

    public String getOpenHour() {
        return openHour;
    }

    public Salon setOpenHour(String openHour) {
        this.openHour = openHour;
        return this;
    }

    public static Creator<Salon> getCREATOR() {
        return CREATOR;
    }

    protected Salon(Parcel in) {
        name = in.readString();
        address = in.readString();
        salonId = in.readString();
        phone = in.readString();
        website = in.readString();
        openHour = in.readString();
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(salonId);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(openHour);
    }
}