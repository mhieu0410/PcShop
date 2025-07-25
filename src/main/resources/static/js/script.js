// Enhanced JavaScript file for the application
console.log('Application script loaded successfully');

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded successfully');

    // Initialize all functions
    initNavbar();
    initSearch();
    initAnimations();
    initTooltips();
    initCartFunctionality();
    initProductInteractions();
    initSmoothScrolling();
});

// ===== NAVBAR ENHANCEMENTS =====
function initNavbar() {
    const navbar = document.querySelector('.navbar');

    // Navbar scroll effect
    window.addEventListener('scroll', function() {
        if (navbar) {
            if (window.scrollY > 50) {
                navbar.classList.add('navbar-scrolled');
                navbar.style.background = 'rgba(33, 37, 41, 0.95)';
            } else {
                navbar.classList.remove('navbar-scrolled');
                navbar.style.background = '';
            }
        }
    });

    // Mobile menu enhancement
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    if (navbarToggler && navbarCollapse) {
        navbarToggler.addEventListener('click', function() {
            setTimeout(() => {
                if (navbarCollapse.classList.contains('show')) {
                    navbarCollapse.style.animation = 'slideInLeft 0.3s ease-out';
                }
            }, 50);
        });
    }
}

// ===== SEARCH FUNCTIONALITY =====
function initSearch() {
    const searchInput = document.querySelector('.search-input');
    const searchForm = document.querySelector('.search-form');

    if (searchInput) {
        let searchTimeout;

        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();

            // Clear previous timeout
            clearTimeout(searchTimeout);

            // Debounce search
            searchTimeout = setTimeout(() => {
                if (searchTerm.length > 2) {
                    performSearch(searchTerm);
                } else {
                    hideSearchResults();
                }
            }, 300);
        });

        // Handle search form submission
        if (searchForm) {
            searchForm.addEventListener('submit', function(e) {
                e.preventDefault();
                const searchTerm = searchInput.value.trim();
                if (searchTerm) {
                    window.location.href = `/products?search=${encodeURIComponent(searchTerm)}`;
                }
            });
        }
    }
}

function performSearch(searchTerm) {
    console.log('Searching for:', searchTerm);

    // Show loading state
    showSearchLoading();

    // Simulate API call (replace with actual endpoint)
    fetch(`/api/search?q=${encodeURIComponent(searchTerm)}`)
        .then(response => response.json())
        .then(data => {
            displaySearchResults(data);
        })
        .catch(error => {
            console.error('Search error:', error);
            hideSearchResults();
        });
}

function showSearchLoading() {
    const searchResults = getOrCreateSearchResults();
    searchResults.innerHTML = `
        <div class="p-3 text-center">
            <div class="spinner-border spinner-border-sm text-primary" role="status">
                <span class="visually-hidden">Đang tìm...</span>
            </div>
            <div class="ms-2 d-inline">Đang tìm kiếm...</div>
        </div>
    `;
    searchResults.style.display = 'block';
}

function displaySearchResults(results) {
    const searchResults = getOrCreateSearchResults();

    if (results && results.length > 0) {
        searchResults.innerHTML = results.map(product => `
            <div class="search-result-item p-2 border-bottom">
                <div class="d-flex align-items-center">
                    <img src="${product.image || '/images/no-image.jpg'}" 
                         alt="${product.name}" 
                         class="search-result-image me-3"
                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px;">
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${product.name}</h6>
                        <p class="mb-0 text-success fw-bold">${formatPrice(product.price)}</p>
                    </div>
                </div>
            </div>
        `).join('');
    } else {
        searchResults.innerHTML = `
            <div class="p-3 text-center text-muted">
                <i class="fas fa-search me-2"></i>Không tìm thấy sản phẩm nào
            </div>
        `;
    }

    searchResults.style.display = 'block';
}

function hideSearchResults() {
    const searchResults = document.querySelector('#search-results');
    if (searchResults) {
        searchResults.style.display = 'none';
    }
}

function getOrCreateSearchResults() {
    let searchResults = document.querySelector('#search-results');

    if (!searchResults) {
        searchResults = document.createElement('div');
        searchResults.id = 'search-results';
        searchResults.className = 'position-absolute bg-white shadow-lg rounded-3 mt-1 w-100';
        searchResults.style.zIndex = '1050';
        searchResults.style.display = 'none';

        const searchInput = document.querySelector('.search-input');
        if (searchInput && searchInput.parentNode) {
            searchInput.parentNode.appendChild(searchResults);
            searchInput.parentNode.style.position = 'relative';
        }
    }

    return searchResults;
}

// ===== CART FUNCTIONALITY =====
function initCartFunctionality() {
    // Add to cart buttons
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('add-to-cart') || e.target.closest('.add-to-cart')) {
            e.preventDefault();
            const button = e.target.classList.contains('add-to-cart') ? e.target : e.target.closest('.add-to-cart');
            const productId = button.getAttribute('data-product-id');

            if (productId) {
                addToCart(productId, button);
            }
        }
    });

    // Update cart badge on page load
    updateCartBadge();
}

function addToCart(productId, button) {
    // Show loading state
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang thêm...';
    button.disabled = true;

    fetch('/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            productId: productId,
            quantity: 1
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã thêm vào giỏ hàng!', 'success');
                updateCartBadge(data.cartCount);

                // Success animation
                button.innerHTML = '<i class="fas fa-check me-1"></i>Đã thêm!';
                button.classList.add('btn-success');

                setTimeout(() => {
                    button.innerHTML = originalText;
                    button.classList.remove('btn-success');
                    button.disabled = false;
                }, 2000);
            } else {
                throw new Error(data.message || 'Có lỗi xảy ra');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast(error.message || 'Có lỗi xảy ra khi thêm vào giỏ hàng!', 'danger');

            button.innerHTML = originalText;
            button.disabled = false;
        });
}

function updateCartBadge(count) {
    if (count === undefined) {
        // Fetch current cart count
        fetch('/api/cart/count')
            .then(response => response.json())
            .then(data => updateCartBadgeDisplay(data.count))
            .catch(error => console.error('Error fetching cart count:', error));
    } else {
        updateCartBadgeDisplay(count);
    }
}

function updateCartBadgeDisplay(count) {
    const badges = document.querySelectorAll('.cart-badge, .badge');
    badges.forEach(badge => {
        if (badge.closest('.nav-link[href*="cart"]')) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        }
    });
}

// ===== ANIMATIONS =====
function initAnimations() {
    // Intersection Observer for scroll animations
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const element = entry.target;

                // Add animation classes based on data attributes
                if (element.dataset.animation) {
                    element.classList.add(element.dataset.animation);
                } else {
                    element.classList.add('fade-in-up');
                }

                // Unobserve after animation
                observer.unobserve(element);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    // Observe elements for animation
    document.querySelectorAll('.card, .product-card, .hero-content, .section-title').forEach(el => {
        observer.observe(el);
    });
}

// ===== PRODUCT INTERACTIONS =====
function initProductInteractions() {
    // Product image hover effects
    document.querySelectorAll('.product-image, .card-img-top').forEach(img => {
        img.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
        });

        img.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });

    // Product quick view
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('quick-view')) {
            e.preventDefault();
            const productId = e.target.getAttribute('data-product-id');
            showQuickView(productId);
        }
    });
}

// ===== SMOOTH SCROLLING =====
function initSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));

            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// ===== TOOLTIPS AND POPOVERS =====
function initTooltips() {
    // Initialize Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize Bootstrap popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

// ===== UTILITY FUNCTIONS =====
function showToast(message, type = 'success') {
    // Create toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '1055';
        document.body.appendChild(toastContainer);
    }

    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                    data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast, { delay: 4000 });
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', function() {
        toastContainer.removeChild(toast);
    });
}

function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

function showQuickView(productId) {
    // Implement quick view modal
    console.log('Showing quick view for product:', productId);

    // You can implement a modal here to show product details
    showToast('Tính năng xem nhanh đang được phát triển', 'info');
}

// ===== INITIALIZATION =====
// Additional initialization when everything is loaded
window.addEventListener('load', function() {
    // Hide loading screens
    const loadingElements = document.querySelectorAll('.loading');
    loadingElements.forEach(el => el.classList.remove('loading'));

    // Trigger resize event to recalculate layouts
    window.dispatchEvent(new Event('resize'));
});

// Handle window resize
window.addEventListener('resize', function() {
    // Recalculate any layout-dependent elements
    const searchResults = document.querySelector('#search-results');
    if (searchResults && searchResults.style.display === 'block') {
        // Reposition search results if needed
        hideSearchResults();
    }
});

// ===== PRODUCTS PAGE FUNCTIONALITY =====
function initProductsPage() {
    if (!window.location.pathname.includes('/products')) return;

    // Search functionality
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                filterProducts(this.value);
            }, 500);
        });
    }

    // Sort functionality
    const sortSelect = document.querySelector('.form-select');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            sortProducts(this.value);
        });
    }

    // Add loading animation for product cards
    document.querySelectorAll('.add-to-cart').forEach(button => {
        button.addEventListener('click', function() {
            this.closest('.product-card').classList.add('loading');
        });
    });
}

function filterProducts(searchTerm) {
    const productCards = document.querySelectorAll('.product-card');

    productCards.forEach(card => {
        const productName = card.querySelector('.card-title').textContent.toLowerCase();
        const isVisible = productName.includes(searchTerm.toLowerCase());

        card.closest('.col-12').style.display = isVisible ? 'block' : 'none';
    });

    // Show "no results" message if needed
    const visibleCards = Array.from(productCards).filter(card =>
        card.closest('.col-12').style.display !== 'none'
    );

    if (visibleCards.length === 0 && searchTerm.length > 0) {
        showNoResultsMessage();
    } else {
        hideNoResultsMessage();
    }
}

function sortProducts(sortBy) {
    const container = document.querySelector('.row.g-4');
    const cards = Array.from(container.querySelectorAll('.col-12'));

    cards.sort((a, b) => {
        const cardA = a.querySelector('.product-card');
        const cardB = b.querySelector('.product-card');

        switch(sortBy) {
            case 'price-asc':
                return getPrice(cardA) - getPrice(cardB);
            case 'price-desc':
                return getPrice(cardB) - getPrice(cardA);
            case 'name':
                return getName(cardA).localeCompare(getName(cardB));
            default:
                return 0;
        }
    });

    // Re-append sorted cards
    cards.forEach(card => container.appendChild(card));
}

function getPrice(card) {
    const priceText = card.querySelector('.product-price').textContent;
    return parseInt(priceText.replace(/[^\d]/g, ''));
}

function getName(card) {
    return card.querySelector('.card-title').textContent;
}

function showNoResultsMessage() {
    hideNoResultsMessage(); // Remove existing message

    const container = document.querySelector('.row.g-4');
    const noResults = document.createElement('div');
    noResults.className = 'col-12 no-results-message';
    noResults.innerHTML = `
        <div class="text-center py-5">
            <i class="fas fa-search fa-3x text-muted mb-3"></i>
            <h5 class="text-muted">Không tìm thấy sản phẩm nào</h5>
            <p class="text-muted">Hãy thử tìm kiếm với từ khóa khác</p>
        </div>
    `;
    container.appendChild(noResults);
}

function hideNoResultsMessage() {
    const message = document.querySelector('.no-results-message');
    if (message) {
        message.remove();
    }
}

// Initialize products page functionality
document.addEventListener('DOMContentLoaded', function() {
    initProductsPage();
});
