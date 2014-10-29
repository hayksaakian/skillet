package com.hayksaakian.skillet;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hayk on 10/28/2014.
 */
public class Recipe implements Serializable{
    String name;
    String url;
    String picture_url;
    String[] pictures;

    public Recipe(){}

    public Recipe(String name, String url, String picture_url, String[] pictures){
        this.name = name;
        this.url = url;
        this.picture_url = picture_url;
        this.pictures = pictures;
    }

}
