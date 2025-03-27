package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
import org.example.nativespark.entities.dtos.CartItemRequest;
import org.example.nativespark.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;


    public CartController(CartRepository cartRepository, UserRepository userRepository,
                          ProductRepository productRepository, CartItemRepository cartItemRepository,
                          TransactionRepository transactionRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.transactionRepository = transactionRepository;
    }


    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ No authenticated user found, redirecting to login.");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("No user found for the user");
        }
        User user = userOptional.get();
        // Fetch the user's cart
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isEmpty()) {
            // Handle case where the user doesn't have a cart
            throw new RuntimeException("No cart found for the user");
        }
        Cart cart = cartOptional.get();

        // Fetch cart items for the cart
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        // Add cart items to the model
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest cartItemRequest,
                                            Authentication authentication) {
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("No user found for the user");
        }
        User user = userOptional.get();

        // Fetch the user's cart (if it doesn't exist, create a new one)
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isEmpty()) {
            // Handle case where the user doesn't have a cart
            throw new RuntimeException("No cart found for the user");
        }
        Cart cart = cartOptional.get();

        // Fetch the product to add to the cart
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the item is already in the cart
        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (existingItem != null) {
            // If the product is already in the cart, update the quantity
            existingItem.setQuantity(existingItem.getQuantity() + cartItemRequest.getQuantity());
            cartItemRepository.save(existingItem);
            return ResponseEntity.ok("Product updated in cart");
        } else {
            // If the product isn't in the cart, create a new cart item
            CartItem newItem = new CartItem(cart, product, cartItemRequest.getQuantity());
            cartItemRepository.save(newItem);
            return ResponseEntity.ok("Product added to cart");
        }
    }

    @PostMapping("/cart/updateQuantity")
    public String updateQuantity(@RequestParam Long cartItemId, @RequestParam int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return "redirect:/cart";
    }

    @PostMapping("/cart/removeItem")
    public String removeItem(@RequestParam Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItemRepository.delete(cartItem);
        return "redirect:/cart";
    }

    @GetMapping("/cart/summary")
    public String summary(
            Model model, Authentication authentication) {
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ No authenticated user found, redirecting to login.");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("No user found for the user");
        }
        User user = userOptional.get();

        // Fetch the user's cart (if it doesn't exist, create a new one)
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isEmpty()) {
            // Handle case where the user doesn't have a cart
            throw new RuntimeException("No cart found for the user");
        }
        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cart.getCartItems();
        int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity()).sum();


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalPrice", totalPrice);
        return "cart-summary";
    }


    @GetMapping("/cart/checkout")
    public String getCheckout() {
        return "cart-checkout";
    }

    @PostMapping("/cart/checkout")
    public String completeCheckout(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam String province,
            @RequestParam String streetAddress,
            @RequestParam String postalCode,
            @RequestParam String phoneNumber,
            @RequestParam String cardNumber,
            @RequestParam String expiryDate,
            @RequestParam String cvv,
            Model model, Authentication authentication) {

        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ No authenticated user found, redirecting to login.");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("No user found for the user");
        }
        User user = userOptional.get();

        // Fetch the user's cart (if it doesn't exist, create a new one)
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isEmpty()) {
            // Handle case where the user doesn't have a cart
            throw new RuntimeException("No cart found for the user");
        }
        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cart.getCartItems();
        // Create a new transaction and set the shipping details
        Transaction transaction = Transaction.builder().buyer(user).country(country).city(city).province(province)
                .streetAddress(streetAddress).postalCode(postalCode).phoneNumber(phoneNumber).status("SUCCESS")
                .transactionDate(LocalDateTime.now()).build();

        // Calculate total price and add cart items to the transaction
        double totalPrice = 0.0;
        List<TransactionProduct> productsInTransaction = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            double productTotal = product.getPrice() * quantity;
            totalPrice += productTotal;

            // Create a TransactionProduct and set the properties
            TransactionProduct transactionProduct = new TransactionProduct();
            transactionProduct.setProduct(product);
            transactionProduct.setQuantity(quantity);
            transactionProduct.setPrice(product.getPrice());

            // Add the TransactionProduct to the list of products in the transaction
            productsInTransaction.add(transactionProduct);

            // Set the transaction reference in TransactionProduct (bidirectional relationship)
            transactionProduct.setTransaction(transaction);
        }

        transaction.setTransactionProducts(productsInTransaction);
        transaction.setQuantity(cartItems.size());  // Optional: total number of products
        transaction.setTotalPrice(totalPrice);

        transactionRepository.save(transaction);

        // Remove all items from the cart
        cartItemRepository.deleteAll(cartItems);
        cartItems.clear();
        cartRepository.save(cart);

        model.addAttribute("orderNumber", transaction.getId());
        return "cart-success";
    }
}