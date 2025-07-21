package com.example.servlet;

import com.example.dao.OrderDAO;
import com.example.dao.ItemDAO;
import com.example.entity.Order;
import com.example.entity.UserDetails;
import com.example.entity.Item;
import com.example.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartServlet extends HttpServlet {
    
    private OrderDAO orderDAO = new OrderDAO();
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
        
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        if (cart != null && !cart.isEmpty()) {
            // Create a safe version of cart items with extracted data
            List<CartItem> safeCart = new ArrayList<>();
            
            for (CartItem cartItem : cart) {
                try {
                    Item originalItem = cartItem.getItem();
                    
                    // Get fresh item data from database
                    Item freshItem = itemDAO.findByIdWithQuantities(originalItem.getId());
                    if (freshItem != null) {
                        safeCart.add(new CartItem(freshItem, cartItem.getQuantity()));
                    } else {
                        System.err.println("Item with ID " + originalItem.getId() + " no longer exists");
                    }
                } catch (Exception e) {
                    System.err.println("Error reloading cart item: " + e.getMessage());
                    // Keep original item if reload fails
                    safeCart.add(cartItem);
                }
            }
            
            if (!safeCart.isEmpty()) {
                double totalAmount = 0;
                for (CartItem cartItem : safeCart) {
                    totalAmount += cartItem.getTotalPrice();
                }
                
                double deliveryCharge = totalAmount >= 500 ? 0 : 100;
                double finalAmount = totalAmount + deliveryCharge;
                
                request.setAttribute("cart", safeCart);
                request.setAttribute("totalAmount", totalAmount);
                request.setAttribute("deliveryCharge", deliveryCharge);
                request.setAttribute("finalAmount", finalAmount);
                
                // Update session with safe cart
                session.setAttribute("cart", safeCart);
            }
        }
        
        request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        if ("placeOrder".equals(action)) {
            if (cart == null || cart.isEmpty()) {
                request.setAttribute("error", "Your cart is empty. Add some items before placing order.");
                doGet(request, response);
                return;
            }
            
            try {
                System.out.println("Starting order placement for user: " + user.getName());
                
                // Reload cart items with complete entity relationships
                List<CartItem> reloadedCart = new ArrayList<>();
                for (CartItem cartItem : cart) {
                    Item reloadedItem = itemDAO.findByIdWithQuantities(cartItem.getItem().getId());
                    if (reloadedItem != null) {
                        reloadedCart.add(new CartItem(reloadedItem, cartItem.getQuantity()));
                    } else {
                        request.setAttribute("error", "Item " + cartItem.getItem().getName() + " is no longer available.");
                        doGet(request, response);
                        return;
                    }
                }
                
                // Validate stock availability before placing order
                for (CartItem cartItem : reloadedCart) {
                    Item currentItem = cartItem.getItem();
                    
                    if (cartItem.getQuantity() > currentItem.getAvailableStock()) {
                        request.setAttribute("error", "Not enough stock for " + currentItem.getName() + 
                                           ". Available: " + currentItem.getAvailableStock() + " " + currentItem.getUnit());
                        doGet(request, response);
                        return;
                    }
                }
                
                // Create orders for each cart item
                boolean orderSuccess = true;
                StringBuilder errorMessages = new StringBuilder();
                
                for (CartItem cartItem : reloadedCart) {
                    try {
                        Item item = cartItem.getItem();
                        String categoryName = "Unknown Category";
                        
                        // Safely get category name
                        try {
                            if (item.getCategory() != null) {
                                categoryName = item.getCategory().getName();
                            }
                        } catch (Exception e) {
                            System.err.println("Could not get category name for item: " + item.getName());
                        }
                        
                        Order order = new Order(
                            user,
                            item.getName(),
                            item.getUnit(),
                            categoryName,
                            cartItem.getQuantity(),
                            item.getPricePerUnit()
                        );
                        order.setOrderDate(LocalDateTime.now());
                        
                        System.out.println("Creating order: " + order.getItemName() + " - Qty: " + order.getQuantity());
                        
                        boolean saved = orderDAO.save(order);
                        if (!saved) {
                            orderSuccess = false;
                            errorMessages.append("Failed to save order for ").append(item.getName()).append(". ");
                            continue;
                        }
                        
                        // Update item stock
                        double newStock = item.getAvailableStock() - cartItem.getQuantity();
                        item.setAvailableStock(Math.max(0, newStock));
                        boolean updated = itemDAO.update(item);
                        
                        System.out.println("Updated stock for " + item.getName() + 
                                         " from " + (item.getAvailableStock() + cartItem.getQuantity()) + 
                                         " to " + item.getAvailableStock());
                        
                        if (!updated) {
                            errorMessages.append("Failed to update stock for ").append(item.getName()).append(". ");
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Error processing cart item: " + cartItem.getItem().getName());
                        e.printStackTrace();
                        orderSuccess = false;
                        errorMessages.append("Error processing ").append(cartItem.getItem().getName()).append(": ").append(e.getMessage()).append(". ");
                    }
                }
                
                if (orderSuccess && errorMessages.length() == 0) {
                    // Clear cart only if all orders were successful
                    session.removeAttribute("cart");
                    request.setAttribute("success", "Order placed successfully! Your items will be delivered soon.");
                    System.out.println("Order placement completed successfully");
                } else {
                    request.setAttribute("error", "Order placement failed: " + errorMessages.toString());
                    System.err.println("Order placement failed: " + errorMessages.toString());
                }
                
            } catch (Exception e) {
                System.err.println("Unexpected error during order placement:");
                e.printStackTrace();
                request.setAttribute("error", "Failed to place order due to system error: " + e.getMessage() + ". Please try again.");
            }
            
        } else if ("removeItem".equals(action)) {
            String itemIdStr = request.getParameter("itemId");
            try {
                Long itemId = Long.parseLong(itemIdStr);
                if (cart != null) {
                    boolean removed = cart.removeIf(cartItem -> cartItem.getItem().getId().equals(itemId));
                    if (removed) {
                        request.setAttribute("success", "Item removed from cart.");
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid item ID.");
            }
        }
        
        doGet(request, response);
    }
}