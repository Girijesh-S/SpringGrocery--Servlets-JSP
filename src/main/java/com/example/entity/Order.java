package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDetails user;
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "item_unit", nullable = false)
    private String itemUnit;
    
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    
    @Column(nullable = false)
    private double quantity;
    
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;
    
    @Column(name = "total_price", nullable = false)
    private double totalPrice;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(UserDetails user, String itemName, String itemUnit, 
                String categoryName, double quantity, double unitPrice) {
        this();
        this.user = user;
        this.itemName = itemName;
        this.itemUnit = itemUnit;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDetails getUser() { return user; }
    public void setUser(UserDetails user) { this.user = user; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemUnit() { return itemUnit; }
    public void setItemUnit(String itemUnit) { this.itemUnit = itemUnit; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { 
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice;
        updateTotalPrice();
    }

    public double getTotalPrice() { return totalPrice; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    private void updateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
}

