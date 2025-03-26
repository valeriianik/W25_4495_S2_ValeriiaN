package org.example.nativespark.controllers;

import org.example.nativespark.entities.Cart;
import org.example.nativespark.entities.CartItem;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.CartItemRepository;
import org.example.nativespark.repositories.CartRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CartController {
    private  final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public CartController(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository){
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ No authenticated user found, redirecting to login.");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
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
}
