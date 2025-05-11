package com.tudio.scregproject;

public class CartItem {
    private String name, description, imageUrl, category;
    private double price;
    private long quantity;

    public CartItem() {}
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    private String size;
    private String milk;

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getMilk() { return milk; }
    public void setMilk(String milk) { this.milk = milk; }
    // getter/setter'lar...

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
}

