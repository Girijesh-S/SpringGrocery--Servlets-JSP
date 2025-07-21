package com.example.model;

import com.example.entity.Item;

public class CartItem {
    private Item item;
    private double quantity;

    public CartItem() {}

    public CartItem(Item item, double quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    // Getters and setters
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return quantity * item.getPricePerUnit();
    }
}