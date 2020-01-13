
package com.example.foodrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hit implements Parcelable {

    @SerializedName("recipe")
    @Expose
    private Recipe recipe;

    protected Hit(Parcel in) {
        recipe = in.readParcelable(Recipe.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recipe, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Hit> CREATOR = new Creator<Hit>() {
        @Override
        public Hit createFromParcel(Parcel in) {
            return new Hit(in);
        }

        @Override
        public Hit[] newArray(int size) {
            return new Hit[size];
        }
    };

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


}
