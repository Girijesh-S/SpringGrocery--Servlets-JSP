package com.example.entity;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(name = "available_stock", nullable = false)
    private double availableStock;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_allowed_quantities", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "quantity")
    private Set<Double> allowedQuantities = new HashSet<>();
    
    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Constructors
    public Item() {}

    public Item(String name, String unit, double availableStock, 
               Set<Double> allowedQuantities, double pricePerUnit, String imageUrl) {
        this.name = name;
        this.unit = unit;
        this.availableStock = availableStock;
        this.allowedQuantities = allowedQuantities;
        this.pricePerUnit = pricePerUnit;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getAvailableStock() { return availableStock; }
    public void setAvailableStock(double availableStock) { this.availableStock = availableStock; }

    public Set<Double> getAllowedQuantities() { return allowedQuantities; }
    public void setAllowedQuantities(Set<Double> allowedQuantities) { this.allowedQuantities = allowedQuantities; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public void addAllowedQuantity(double quantity) {
        this.allowedQuantities.add(quantity);
    }

    public boolean isQuantityAllowed(double quantity) {
        return this.allowedQuantities.contains(quantity);
    }
}