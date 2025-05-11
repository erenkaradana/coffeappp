package com.tudio.scregproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private double rating;
    private String category;
    private String id;
    public Product() {} // Firestore için boş constructor

    public Product(String name, String description, String imageUrl, double price, double rating, String category) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rating = rating;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }

    // Parcelable implementasyonu
    protected Product(Parcel in) {
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        price = in.readDouble();
        rating = in.readDouble();
        category = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
