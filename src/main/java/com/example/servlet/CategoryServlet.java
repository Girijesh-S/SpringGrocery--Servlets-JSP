package com.example.servlet;

import com.example.dao.CategoryDAO;
import com.example.dao.ItemDAO;
import com.example.entity.Category;
import com.example.entity.Item;
import com.example.entity.UserDetails;
import com.example.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class CategoryServlet extends HttpServlet {
    
    private CategoryDAO categoryDAO = new CategoryDAO();
    private ItemDAO itemDAO = new ItemDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String categoryIdStr = request.getParameter("id");
        if (categoryIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }
        
        try {
            Long categoryId = Long.parseLong(categoryIdStr);
            
            // First, get items directly to check if they exist
            List<Item> existingItems = itemDAO.findByCategoryId(categoryId);
            System.out.println("Found " + existingItems.size() + " existing items for category " + categoryId);
            
            // If no items exist, initialize them ONCE
            if (existingItems.isEmpty()) {
                System.out.println("No items found. Initializing category items...");
                Category category = categoryDAO.findById(categoryId);
                if (category != null) {
                    initializeCategoryItems(category);
                    // Reload items after initialization
                    existingItems = itemDAO.findByCategoryId(categoryId);
                }
            }
            
            // Now get the category with its items
            Category category = categoryDAO.findByIdWithItems(categoryId);
            
            if (category == null) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            
            // Update available stock based on cart contents
            updateAvailableStockForDisplay(category, session);
            
            request.setAttribute("category", category);
            request.getRequestDispatcher("/jsp/category.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shop");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== CategoryServlet POST Debug ===");
        System.out.println("ItemId: " + request.getParameter("itemId"));
        System.out.println("Quantity: " + request.getParameter("quantity"));
        
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        
        if (user == null) {
            System.out.println("User is null - redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String itemIdStr = request.getParameter("itemId");
        String quantityStr = request.getParameter("quantity");
        
        try {
            Long itemId = Long.parseLong(itemIdStr);
            double quantity = Double.parseDouble(quantityStr);
            
            System.out.println("Parsed - ItemId: " + itemId + ", Quantity: " + quantity);
            
            Item item = itemDAO.findByIdWithQuantities(itemId);
            if (item == null) {
                System.out.println("Item not found for ID: " + itemId);
                request.setAttribute("error", "Item not found.");
                doGet(request, response);
                return;
            }
            
            System.out.println("Found item: " + item.getName());
            
            if (!item.isQuantityAllowed(quantity)) {
                System.out.println("Quantity not allowed: " + quantity);
                request.setAttribute("error", "Invalid quantity selected.");
                doGet(request, response);
                return;
            }
            
            // Check available stock (considering current cart contents)
            double availableStock = getAvailableStock(item, session);
            System.out.println("Available stock (after cart): " + availableStock);
            
            if (quantity > availableStock) {
                System.out.println("Insufficient stock. Available: " + availableStock + ", Requested: " + quantity);
                request.setAttribute("error", "Not enough stock available. Only " + availableStock + " " + item.getUnit() + " available.");
                doGet(request, response);
                return;
            }
            
            // Add to cart
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            
            // Check if item already in cart
            boolean found = false;
            for (CartItem cartItem : cart) {
                if (cartItem.getItem().getId().equals(itemId)) {
                    System.out.println("Item already in cart. Current quantity: " + cartItem.getQuantity());
                    // Check if adding this quantity would exceed available stock
                    double newQuantity = cartItem.getQuantity() + quantity;
                    if (newQuantity > item.getAvailableStock()) {
                        System.out.println("Total quantity would exceed stock: " + newQuantity + " > " + item.getAvailableStock());
                        request.setAttribute("error", "Cannot add more. Total would exceed available stock.");
                        doGet(request, response);
                        return;
                    }
                    cartItem.setQuantity(newQuantity);
                    System.out.println("Updated quantity to: " + cartItem.getQuantity());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("Adding new item to cart");
                cart.add(new CartItem(item, quantity));
            }
            
            System.out.println("Cart now has " + cart.size() + " different items");
            request.setAttribute("success", "Item added to cart successfully!");
            doGet(request, response);
            
        } catch (NumberFormatException e) {
            System.out.println("Number format error: " + e.getMessage());
            request.setAttribute("error", "Invalid input.");
            doGet(request, response);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
            doGet(request, response);
        }
    }
    
    /**
     * Calculate available stock considering items already in cart
     */
    private double getAvailableStock(Item item, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        double cartQuantity = 0;
        if (cart != null) {
            for (CartItem cartItem : cart) {
                if (cartItem.getItem().getId().equals(item.getId())) {
                    cartQuantity = cartItem.getQuantity();
                    break;
                }
            }
        }
        
        return item.getAvailableStock() - cartQuantity;
    }
    
    /**
     * Update the displayed available stock for all items in category based on cart contents
     * For each user, show original stock minus what's in their cart
     */
    private void updateAvailableStockForDisplay(Category category, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        // For each item, show the base stock (10) minus what this user has in cart
        for (Item item : category.getItems()) {
            double originalStock = 10.0; // Reset to original stock for each user
            double cartQuantity = 0;
            
            if (cart != null && !cart.isEmpty()) {
                for (CartItem cartItem : cart) {
                    if (cartItem.getItem().getId().equals(item.getId())) {
                        cartQuantity = cartItem.getQuantity();
                        break;
                    }
                }
            }
            
            // Each user sees original stock minus their cart items
            double displayStock = originalStock - cartQuantity;
            item.setAvailableStock(Math.max(0, displayStock));
        }
    }
    
    /**
     * Initialize category items - only adds items that don't already exist
     */
    private void initializeCategoryItems(Category category) {
        try {
            // ALWAYS check if items already exist to prevent duplicates
            List<Item> existingItems = itemDAO.findByCategoryId(category.getId());
            if (!existingItems.isEmpty()) {
                System.out.println("Category " + category.getName() + " already has " + existingItems.size() + " items. Skipping initialization completely.");
                return;
            }
            
            System.out.println("No items found for category: " + category.getName() + ". Starting initialization...");
            
            Set<Double> qtyKg = new HashSet<>(Arrays.asList(0.25, 0.5, 1.0, 2.0, 5.0));
            Set<Double> qtyLitre = new HashSet<>(Arrays.asList(0.25, 0.5, 1.0, 2.0));
            
            // Use synchronized block to prevent concurrent initialization
            synchronized (this) {
                // Double-check after getting the lock
                existingItems = itemDAO.findByCategoryId(category.getId());
                if (!existingItems.isEmpty()) {
                    System.out.println("Items were created by another thread. Skipping initialization.");
                    return;
                }
                
                if (category.getName().equals("Fruits")) {
                    addUniqueItem(category, "Banana - Yelakki", "kg", 10, qtyKg, 90, "images/products/banana-yelakki.jpg");
                    addUniqueItem(category, "Banana - Red Banana", "kg", 10, qtyKg, 100, "images/products/banana-red.jpg");
                    addUniqueItem(category, "Mango - Banganapalli", "kg", 10, qtyKg, 60, "images/products/mango.jpg");
                    addUniqueItem(category, "Papaya", "kg", 10, qtyKg, 30, "images/products/papaya.jpg");
                    addUniqueItem(category, "Guava", "kg", 10, qtyKg, 70, "images/products/guava.jpg");
                } else if (category.getName().equals("Vegetables")) {
                    addUniqueItem(category, "Onion", "kg", 10, qtyKg, 30, "images/products/onion.jpg");
                    addUniqueItem(category, "Tomato", "kg", 10, qtyKg, 25, "images/products/tomato.jpg");
                    addUniqueItem(category, "Potato", "kg", 10, qtyKg, 40, "images/products/potato.jpg");
                    addUniqueItem(category, "Carrot", "kg", 10, qtyKg, 70, "images/products/carrot.jpg");
                    addUniqueItem(category, "Brinjal", "kg", 10, qtyKg, 50, "images/products/brinjal.jpg");
                } else if (category.getName().equals("Rice & Cereals")) {
                    addUniqueItem(category, "Basmati Rice", "kg", 10, qtyKg, 120, "images/products/basmati-rice.jpg");
                    addUniqueItem(category, "Idli Rice", "kg", 10, qtyKg, 50, "images/products/idli-rice.jpg");
                    addUniqueItem(category, "Ragi", "kg", 10, qtyKg, 70, "images/products/ragi.jpg");
                    addUniqueItem(category, "Wheat", "kg", 10, qtyKg, 35, "images/products/wheat.jpg");
                } else if (category.getName().equals("Oils & Ghee")) {
                    addUniqueItem(category, "Sunflower Oil", "litre", 10, qtyLitre, 180, "images/products/sunflower-oil.jpg");
                    addUniqueItem(category, "Coconut Oil", "litre", 10, qtyLitre, 250, "images/products/coconut-oil.jpg");
                    addUniqueItem(category, "Ghee", "litre", 10, qtyLitre, 300, "images/products/ghee.jpg");
                }
                
                System.out.println("Successfully initialized " + category.getName() + " category with items.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing category items: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add item only if it doesn't already exist
     */
    private void addUniqueItem(Category category, String name, String unit, double stock, 
                              Set<Double> quantities, double price, String imageUrl) {
        try {
            // Final check before adding
            if (itemDAO.existsByNameAndCategory(name, category.getId())) {
                System.out.println("Item " + name + " already exists in category " + category.getName() + ". Skipping.");
                return;
            }
            
            Item item = new Item(name, unit, stock, quantities, price, imageUrl);
            item.setCategory(category);
            boolean saved = itemDAO.save(item);
            if (saved) {
                System.out.println("Successfully added unique item: " + name);
            } else {
                System.err.println("Failed to save item: " + name);
            }
        } catch (Exception e) {
            System.err.println("Error adding unique item " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}