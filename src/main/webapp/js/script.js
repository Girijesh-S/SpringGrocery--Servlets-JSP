/**
 * SpringGrocery - Complete JavaScript
 * All features: forms, cart, shop, animations, order history, stock management
 */

// Global variables
let cartCount = 0;
let isLoading = false;

// DOM Content Loaded - Initialize everything
document.addEventListener('DOMContentLoaded', function() {
    console.log('SpringGrocery initialized - complete version');
    initializeNavigation();
    initializeForms();
    initializeCart();
    initializeShop();
    initializeAnimations();
    initializeImageHandling();
    initializeOrderHistory();
    updateCartCount();
    addScrollEffects();
});

/**
 * Navigation functionality
 */
function initializeNavigation() {
    const navbar = document.querySelector('.navbar');
    
    if (navbar) {
        // Add scroll effect to navbar
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                navbar.style.boxShadow = '0 4px 20px rgba(0,0,0,0.15)';
            } else {
                navbar.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
            }
        });
    }
}

/**
 * Form validation and enhancement - NO FORM SUBMISSION INTERFERENCE
 */
function initializeForms() {
    // Phone number validation
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Remove non-digits
            this.value = this.value.replace(/\D/g, '');
            
            // Limit to 10 digits
            if (this.value.length > 10) {
                this.value = this.value.substring(0, 10);
            }
            
            // Visual feedback
            validatePhoneNumber(this);
        });
    });
    
    // Pincode validation
    const pincodeInputs = document.querySelectorAll('input[name="pincode"]');
    pincodeInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Remove non-digits
            this.value = this.value.replace(/\D/g, '');
            
            // Limit to 6 digits
            if (this.value.length > 6) {
                this.value = this.value.substring(0, 6);
            }
            
            // Visual feedback
            validatePincode(this);
            
            // Auto-suggest city based on pincode
            if (this.value.length === 6) {
                suggestCityFromPincode(this.value);
            }
        });
    });
    
    // Form submission with loading states - EXCLUDING add-to-cart forms
    const forms = document.querySelectorAll('form:not(.add-to-cart-form)');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = this.querySelector('button[type="submit"]');
            
            // Validate form before submission
            if (!validateForm(this)) {
                e.preventDefault();
                return false;
            }
            
            // Show loading state
            if (submitBtn && !isLoading) {
                showLoadingState(submitBtn);
            }
        });
    });
}

/**
 * Cart functionality - MINIMAL INTERFERENCE
 */
function initializeCart() {
    // Update cart count in navigation
    updateCartCount();
    
    // Quantity selector enhancement - VISUAL ONLY
    const quantitySelectors = document.querySelectorAll('select[name="quantity"]');
    quantitySelectors.forEach(select => {
        select.addEventListener('change', function() {
            const form = this.closest('form');
            const addButton = form ? form.querySelector('.btn-add-cart') : null;
            
            if (this.value && addButton) {
                addButton.style.opacity = '1';
                addButton.disabled = false;
                addButton.textContent = 'Add to Cart';
                addButton.style.backgroundColor = '#4CAF50';
                
                // Calculate and show total price
                updatePricePreview(this);
            } else if (addButton) {
                addButton.style.opacity = '0.6';
                addButton.disabled = true;
                addButton.textContent = 'Select Quantity';
                addButton.style.backgroundColor = '#ccc';
                
                // Remove price preview
                const pricePreview = form.querySelector('.price-preview');
                if (pricePreview) pricePreview.remove();
            }
        });
        
        // Initial state
        const form = select.closest('form');
        const addButton = form ? form.querySelector('.btn-add-cart') : null;
        if (addButton) {
            if (select.value) {
                addButton.style.opacity = '1';
                addButton.disabled = false;
                addButton.textContent = 'Add to Cart';
                addButton.style.backgroundColor = '#4CAF50';
            } else {
                addButton.style.opacity = '0.6';
                addButton.disabled = true;
                addButton.textContent = 'Select Quantity';
                addButton.style.backgroundColor = '#ccc';
            }
        }
    });
    
    // Cart item animations
    animateCartItems();
}

/**
 * Shop functionality
 */
function initializeShop() {
    // Category card click handling
    const categoryCards = document.querySelectorAll('.category-card');
    categoryCards.forEach(card => {
        card.addEventListener('click', function() {
            const href = this.getAttribute('onclick');
            if (href) {
                // Extract URL from onclick attribute
                const url = href.match(/location\.href='([^']+)'/);
                if (url && url[1]) {
                    showLoadingOverlay();
                    window.location.href = url[1];
                }
            }
        });
        
        // Add keyboard navigation
        card.setAttribute('tabindex', '0');
        card.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
    });
    
    // Product filtering
    initializeProductFilters();
    
    // Search functionality
    initializeSearch();
}

/**
 * Animation enhancements
 */
function initializeAnimations() {
    // Intersection Observer for scroll animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                entry.target.classList.add('animate-in');
            }
        });
    }, observerOptions);
    
    // Observe elements for animation
    const animatedElements = document.querySelectorAll('.category-card, .product-card, .feature, .area-card, .order-item');
    animatedElements.forEach((el, index) => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        observer.observe(el);
    });
    
    // Hover effects for interactive elements
    const interactiveCards = document.querySelectorAll('.category-card, .product-card');
    interactiveCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

/**
 * Image handling
 */
function initializeImageHandling() {
    // Image error handling
    const allImages = document.querySelectorAll('img');
    allImages.forEach(img => {
        img.addEventListener('error', function() {
            if (!this.hasAttribute('data-error-handled')) {
                this.setAttribute('data-error-handled', 'true');
                this.src = getPlaceholderImage(this);
                this.alt = 'Image not available';
            }
        });
        
        img.addEventListener('load', function() {
            this.style.opacity = '1';
        });
    });
}

/**
 * Initialize order history page functionality
 */
function initializeOrderHistory() {
    if (!window.location.pathname.includes('order-history')) return;
    
    console.log('Initializing order history page...');
    
    // Add search functionality for orders
    const searchInput = document.querySelector('.orders-search input');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(searchOrders, 300));
    }
    
    // Add filter functionality
    const filterSelects = document.querySelectorAll('.orders-filters select');
    filterSelects.forEach(select => {
        select.addEventListener('change', filterOrders);
    });
    
    // Add reorder button functionality
    const reorderButtons = document.querySelectorAll('.btn-reorder');
    reorderButtons.forEach(button => {
        button.addEventListener('click', function() {
            const orderItem = this.closest('.order-item');
            const itemName = orderItem.querySelector('.order-info h4')?.textContent || 'item';
            const category = orderItem.querySelector('.order-category')?.textContent || 'category';
            reorderItem(itemName, category);
        });
    });
    
    // Add view details functionality
    const detailButtons = document.querySelectorAll('.btn-details');
    detailButtons.forEach(button => {
        button.addEventListener('click', function() {
            const orderItem = this.closest('.order-item');
            const orderId = orderItem.querySelector('.order-id')?.textContent || 'Unknown';
            viewOrderDetails(orderId);
        });
    });
    
    // Add load more functionality
    const loadMoreBtn = document.querySelector('.btn-load-more');
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener('click', loadMoreOrders);
    }
    
    // Add empty state actions
    const startShoppingBtn = document.querySelector('.empty-orders .btn-primary');
    if (startShoppingBtn) {
        startShoppingBtn.addEventListener('click', function(e) {
            e.preventDefault();
            showLoadingOverlay();
            setTimeout(() => {
                window.location.href = getContextPath() + '/shop';
            }, 500);
        });
    }
    
    // Animate order items
    animateOrderItems();
}

/**
 * Form validation functions
 */
function validateForm(form) {
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });
    
    return isValid;
}

function validateField(field) {
    const value = field.value.trim();
    const type = field.type || field.tagName.toLowerCase();
    let isValid = true;
    let errorMessage = '';
    
    // Check if required field is empty
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = 'This field is required';
    }
    
    // Type-specific validation
    if (value && isValid) {
        switch (type) {
            case 'tel':
                if (!/^\d{10}$/.test(value)) {
                    isValid = false;
                    errorMessage = 'Please enter a valid 10-digit phone number';
                }
                break;
            case 'email':
                if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
                    isValid = false;
                    errorMessage = 'Please enter a valid email address';
                }
                break;
            case 'text':
                if (field.name === 'pincode' && !/^\d{6}$/.test(value)) {
                    isValid = false;
                    errorMessage = 'Please enter a valid 6-digit pincode';
                }
                if (field.name === 'name' && value.length < 2) {
                    isValid = false;
                    errorMessage = 'Name must be at least 2 characters long';
                }
                break;
        }
    }
    
    // Show/hide error
    if (isValid) {
        clearFieldError(field);
    } else {
        showFieldError(field, errorMessage);
    }
    
    return isValid;
}

function validatePhoneNumber(input) {
    const value = input.value;
    const isValid = /^\d{10}$/.test(value);
    
    if (value.length === 10) {
        if (isValid) {
            input.style.borderColor = '#4CAF50';
            input.style.backgroundColor = '#f8fff8';
        } else {
            input.style.borderColor = '#f44336';
            input.style.backgroundColor = '#fff8f8';
        }
    } else {
        input.style.borderColor = '#e0e0e0';
        input.style.backgroundColor = 'white';
    }
}

function validatePincode(input) {
    const value = input.value;
    const isValid = /^\d{6}$/.test(value);
    
    if (value.length === 6) {
        if (isValid) {
            input.style.borderColor = '#4CAF50';
            input.style.backgroundColor = '#f8fff8';
        } else {
            input.style.borderColor = '#f44336';
            input.style.backgroundColor = '#fff8f8';
        }
    } else {
        input.style.borderColor = '#e0e0e0';
        input.style.backgroundColor = 'white';
    }
}

function showFieldError(field, message) {
    clearFieldError(field);
    
    field.style.borderColor = '#f44336';
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.style.cssText = 'color:#f44336;font-size:0.8rem;margin-top:5px;';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
}

function clearFieldError(field) {
    field.style.borderColor = '#e0e0e0';
    
    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
}

// City suggestion based on pincode
function suggestCityFromPincode(pincode) {
    const citySelect = document.getElementById('city');
    if (!citySelect) return;
    
    // Simple pincode to city mapping for Tamil Nadu
    const pincodeMap = {
        '600': 'Chennai',
        '601': 'Kancheepuram',
        '602': 'Chengalpattu',
        '603': 'Kancheepuram'
    };
    
    const prefix = pincode.substring(0, 3);
    const suggestedCity = pincodeMap[prefix];
    
    if (suggestedCity) {
        // Check if the city exists in options
        const option = Array.from(citySelect.options).find(opt => opt.value === suggestedCity);
        if (option) {
            citySelect.value = suggestedCity;
            showNotification(`City auto-selected: ${suggestedCity}`, 'info');
        }
    }
}

// Cart functions
function updateCartCount() {
    const cartCountElements = document.querySelectorAll('.cart-count');
    const cartItems = document.querySelectorAll('.cart-item');
    const count = cartItems.length;
    
    cartCountElements.forEach(element => {
        element.textContent = count;
        element.style.display = count > 0 ? 'inline' : 'none';
    });
    
    cartCount = count;
}

function updatePricePreview(quantitySelect) {
    const quantity = parseFloat(quantitySelect.value);
    const form = quantitySelect.closest('form');
    const priceElement = form?.querySelector('.product-price');
    
    if (priceElement && quantity) {
        const priceText = priceElement.textContent;
        const priceMatch = priceText.match(/₹([\d.]+)/);
        
        if (priceMatch) {
            const unitPrice = parseFloat(priceMatch[1]);
            const totalPrice = (unitPrice * quantity).toFixed(2);
            
            // Show price preview
            let pricePreview = form.querySelector('.price-preview');
            if (!pricePreview) {
                pricePreview = document.createElement('div');
                pricePreview.className = 'price-preview';
                pricePreview.style.cssText = 'color:#4CAF50;font-weight:bold;margin-top:8px;font-size:0.9rem;padding:8px 12px;background:rgba(76, 175, 80, 0.1);border-radius:6px;border-left:3px solid #4CAF50;';
                quantitySelect.parentNode.appendChild(pricePreview);
            }
            
            pricePreview.textContent = `Total: ₹${totalPrice}`;
        }
    }
}

function animateCartItems() {
    const cartItems = document.querySelectorAll('.cart-item');
    cartItems.forEach((item, index) => {
        item.style.animationDelay = `${index * 0.1}s`;
        item.classList.add('fade-in');
    });
}

// Product filtering
function initializeProductFilters() {
    const priceFilter = document.getElementById('priceFilter');
    const stockFilter = document.getElementById('stockFilter');
    const sortBy = document.getElementById('sortBy');
    const productsGrid = document.getElementById('productsGrid');
    
    if (!productsGrid) return;
    
    function filterAndSortProducts() {
        const products = Array.from(productsGrid.querySelectorAll('.product-card'));
        const priceRange = priceFilter?.value || '';
        const stockFilterValue = stockFilter?.value || '';
        const sortOption = sortBy?.value || '';
        
        // Filter products
        products.forEach(product => {
            let showProduct = true;
            const price = parseFloat(product.dataset.price || '0');
            const stock = parseFloat(product.dataset.stock || '0');
            
            // Price filter
            if (priceRange) {
                const [min, max] = priceRange.split('-').map(p => p === '+' ? Infinity : parseFloat(p));
                if (max !== undefined) {
                    showProduct = price >= min && price < max;
                } else {
                    showProduct = price >= min;
                }
            }
            
            // Stock filter
            if (stockFilterValue === 'instock' && stock <= 0) showProduct = false;
            if (stockFilterValue === 'lowstock' && stock > 2) showProduct = false;
            
            product.style.display = showProduct ? 'block' : 'none';
        });
        
        // Sort products
        if (sortOption) {
            const visibleProducts = products.filter(p => p.style.display !== 'none');
            visibleProducts.sort((a, b) => {
                switch (sortOption) {
                    case 'name':
                        return (a.dataset.name || '').localeCompare(b.dataset.name || '');
                    case 'price-low':
                        return parseFloat(a.dataset.price || '0') - parseFloat(b.dataset.price || '0');
                    case 'price-high':
                        return parseFloat(b.dataset.price || '0') - parseFloat(a.dataset.price || '0');
                    default:
                        return 0;
                }
            });
            
            visibleProducts.forEach(product => productsGrid.appendChild(product));
        }
    }
    
    // Add event listeners
    priceFilter?.addEventListener('change', filterAndSortProducts);
    stockFilter?.addEventListener('change', filterAndSortProducts);
    sortBy?.addEventListener('change', filterAndSortProducts);
}

// Search functionality
function initializeSearch() {
    const searchInput = document.querySelector('#searchInput, .search-input');
    if (!searchInput) return;
    
    const debouncedSearch = debounce(performSearch, 300);
    searchInput.addEventListener('input', debouncedSearch);
}

function performSearch(event) {
    const query = event.target.value.toLowerCase().trim();
    const searchTargets = document.querySelectorAll('.product-card, .category-card, .order-item');
    
    searchTargets.forEach(item => {
        const searchText = item.textContent.toLowerCase();
        const matches = !query || searchText.includes(query);
        item.style.display = matches ? 'block' : 'none';
    });
}

// Order History Functions
function searchOrders(event) {
    const query = event.target.value.toLowerCase();
    const orderItems = document.querySelectorAll('.order-item');
    
    orderItems.forEach(item => {
        const itemName = item.querySelector('.order-info h4')?.textContent.toLowerCase() || '';
        const category = item.querySelector('.order-category')?.textContent.toLowerCase() || '';
        const orderId = item.querySelector('.order-id')?.textContent.toLowerCase() || '';
        
        const matches = itemName.includes(query) || category.includes(query) || orderId.includes(query);
        item.style.display = matches ? 'block' : 'none';
    });
    
    // Show no results message if needed
    const visibleOrders = document.querySelectorAll('.order-item:not([style*="display: none"])');
    const noResultsMsg = document.querySelector('.no-results-message');
    
    if (visibleOrders.length === 0 && query.trim() !== '') {
        if (!noResultsMsg) {
            const msg = document.createElement('div');
            msg.className = 'no-results-message';
            msg.style.cssText = 'text-align: center; padding: 40px; color: #666;';
            msg.innerHTML = `
                <h3>No orders found</h3>
                <p>Try adjusting your search terms or filters.</p>
            `;
            document.querySelector('.orders-list')?.appendChild(msg);
        }
    } else if (noResultsMsg) {
        noResultsMsg.remove();
    }
}

function filterOrders() {
    const statusFilter = document.querySelector('#statusFilter')?.value || '';
    const dateFilter = document.querySelector('#dateFilter')?.value || '';
    const orderItems = document.querySelectorAll('.order-item');
    
    orderItems.forEach(item => {
        let show = true;
        
        // Status filter
        if (statusFilter) {
            const status = item.querySelector('.order-status')?.textContent.toLowerCase() || '';
            if (!status.includes(statusFilter.toLowerCase())) {
                show = false;
            }
        }
        
        // Date filter (simplified - would need proper date comparison in real app)
        if (dateFilter && show) {
            const orderDate = item.querySelector('.order-date')?.textContent || '';
            // Add date filtering logic here
        }
        
        item.style.display = show ? 'block' : 'none';
    });
}

function animateOrderItems() {
    const orderItems = document.querySelectorAll('.order-item');
    orderItems.forEach((item, index) => {
        item.style.animationDelay = `${index * 0.1}s`;
        item.classList.add('fade-in');
    });
}

// Loading states
function showLoadingState(button) {
    if (isLoading) return;
    
    isLoading = true;
    const originalText = button.textContent;
    
    button.disabled = true;
    button.innerHTML = '<span class="loading"></span> Processing...';
    button.dataset.originalText = originalText;
    
    // Auto-restore after 10 seconds (fallback)
    setTimeout(() => {
        hideLoadingState(button);
    }, 10000);
}

function hideLoadingState(button) {
    if (!button) return;
    
    isLoading = false;
    button.disabled = false;
    button.textContent = button.dataset.originalText || 'Submit';
    delete button.dataset.originalText;
}

function showLoadingOverlay() {
    const overlay = document.createElement('div');
    overlay.id = 'loadingOverlay';
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255,255,255,0.9);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
        backdrop-filter: blur(2px);
    `;
    
    overlay.innerHTML = `
        <div style="text-align: center;">
            <div class="loading" style="width: 50px; height: 50px; margin: 0 auto 20px;"></div>
            <p style="color: #2d5a27; font-weight: 500;">Loading...</p>
        </div>
    `;
    
    document.body.appendChild(overlay);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        const existingOverlay = document.getElementById('loadingOverlay');
        if (existingOverlay) {
            existingOverlay.remove();
        }
    }, 5000);
}

// Notification functions
function showNotification(message, type = 'success', duration = 3000) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        z-index: 10000;
        min-width: 300px;
        max-width: 400px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    `;
    
    // Set background color based on type
    const colors = {
        success: '#4CAF50',
        error: '#f44336',
        warning: '#ff9800',
        info: '#2196F3'
    };
    
    notification.style.backgroundColor = colors[type] || colors.info;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Auto remove
    setTimeout(() => {
        hideNotification(notification);
    }, duration);
}

function hideNotification(notification) {
    notification.style.opacity = '0';
    notification.style.transform = 'translateX(100%)';
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 300);
}

// Helper functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function getPlaceholderImage(img) {
    const width = img.width || 300;
    const height = img.height || 200;
    
    // Return a placeholder based on image context
    if (img.closest('.category-card')) {
        return `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}"><rect width="100%" height="100%" fill="%23f0f0f0"/><text x="50%" y="50%" text-anchor="middle" dy="0.3em" font-family="Arial" font-size="16" fill="%23999">Category Image</text></svg>`;
    } else if (img.closest('.product-card')) {
        return `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}"><rect width="100%" height="100%" fill="%23f0f0f0"/><text x="50%" y="50%" text-anchor="middle" dy="0.3em" font-family="Arial" font-size="16" fill="%23999">Product Image</text></svg>`;
    } else {
        return `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}"><rect width="100%" height="100%" fill="%23f0f0f0"/><text x="50%" y="50%" text-anchor="middle" dy="0.3em" font-family="Arial" font-size="14" fill="%23999">Image not available</text></svg>`;
    }
}

// Scroll effects
function addScrollEffects() {
    let ticking = false;
    
    function updateScrollEffects() {
        const scrolled = window.pageYOffset;
        const rate = scrolled * -0.5;
        
        // Parallax effect for hero section
        const heroSection = document.querySelector('.hero');
        if (heroSection && scrolled < heroSection.offsetHeight) {
            heroSection.style.transform = `translateY(${rate}px)`;
        }
        
        ticking = false;
    }
    
    function requestScrollUpdate() {
        if (!ticking) {
            requestAnimationFrame(updateScrollEffects);
            ticking = true;
        }
    }
    
    window.addEventListener('scroll', requestScrollUpdate);
}

// Order-related functions
function confirmOrder(amount) {
    const user = getUserFromSession();
    const deliveryDate = user?.deliveryDate || 'Today';
    
    const message = `🛒 Confirm your order?

💰 Total Amount: ₹${amount}
💳 Payment: Cash on Delivery
📍 Delivery: ${deliveryDate}

✅ Click OK to place your order`;
    
    return confirm(message);
}

function editAddress() {
    if (confirm('Redirect to registration page to update address?')) {
        window.location.href = getContextPath() + '/register';
    }
}

function reorderItem(itemName, categoryName) {
    if (confirm(`Reorder ${itemName} from ${categoryName}?`)) {
        showNotification('Redirecting to shop...', 'info');
        showLoadingOverlay();
        setTimeout(() => {
            window.location.href = getContextPath() + '/shop';
        }, 500);
    }
}

function viewOrderDetails(orderId) {
    showNotification('Loading order details...', 'info');
    
    setTimeout(() => {
        alert(`Order #${orderId} Details

📦 Order Status: Processing
🚚 Expected Delivery: Tomorrow
📞 Support: 1800-123-4567

This feature will show:
• Complete order breakdown
• Real-time delivery tracking
• Invoice download option
• Customer support contact`);
    }, 800);
}

function loadMoreOrders() {
    const loadBtn = document.querySelector('.btn-load-more');
    if (loadBtn) {
        loadBtn.textContent = 'Loading...';
        loadBtn.disabled = true;
    }
    
    showNotification('Loading more orders...', 'info');
    
    setTimeout(() => {
        showNotification('All orders loaded!', 'success');
        if (loadBtn) {
            loadBtn.textContent = 'No more orders';
            loadBtn.disabled = true;
        }
    }, 1500);
}

// Utility helper functions
function getUserFromSession() {
    const welcomeMsg = document.querySelector('.welcome-msg');
    if (welcomeMsg) {
        const name = welcomeMsg.textContent.replace('Welcome, ', '').replace('!', '');
        return { name };
    }
    return null;
}

function getContextPath() {
    const path = window.location.pathname;
    const contextPath = path.substring(0, path.indexOf('/', 1));
    return contextPath || '';
}

// Captcha refresh function
function refreshCaptcha() {
    const refreshBtn = document.querySelector('.btn-refresh');
    if (refreshBtn) {
        refreshBtn.innerHTML = '⏳ Loading...';
        refreshBtn.disabled = true;
    }
    
    // Redirect to generate new captcha
    window.location.href = getContextPath() + '/login';
}

// Clear filters function
function clearAllFilters() {
    const filters = document.querySelectorAll('#priceFilter, #stockFilter, #sortBy, .search-input');
    filters.forEach(filter => {
        filter.value = '';
        if (filter.dispatchEvent) {
            filter.dispatchEvent(new Event('change'));
            filter.dispatchEvent(new Event('input'));
        }
    });
    
    const products = document.querySelectorAll('.product-card');
    products.forEach(product => {
        product.style.display = 'block';
    });
    
    showNotification('Filters cleared', 'info');
}

// Export functions for global access
window.SpringGrocery = {
    showNotification,
    confirmOrder,
    editAddress,
    reorderItem,
    viewOrderDetails,
    loadMoreOrders,
    clearAllFilters,
    refreshCaptcha
};

// Console welcome message
console.log('%cSpringGrocery 🌱 - Complete Version', 'color: #4CAF50; font-size: 24px; font-weight: bold;');
console.log('%cFresh Organic Groceries Delivered!', 'color: #2d5a27; font-size: 14px;');
console.log('All features loaded: forms, cart, shop, order history, animations!');

// Error handling
window.addEventListener('error', function(event) {
    console.error('JavaScript error:', event.error);
});

// Performance monitoring
window.addEventListener('load', function() {
    const perfData = performance.timing;
    const loadTime = perfData.loadEventEnd - perfData.navigationStart;
    console.log(`Page load time: ${loadTime}ms`);
});