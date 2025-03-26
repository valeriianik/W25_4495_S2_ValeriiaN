package org.example.nativespark.services;

import org.example.nativespark.entities.Cart;
import org.example.nativespark.entities.Subscription;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private BasicUserRepository basicUserRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private EntrepreneurUserRepository entrepreneurUserRepository;

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public UserService(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionRepository = subscriptionRepository;
        this.cartRepository = cartRepository;
    }

    public User createUser(String email, String password, String userType) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User(email, passwordEncoder.encode(password), userType);
        user = userRepository.save(user);

        Cart cart = new Cart(user);
        cartRepository.save(cart);

        // Assign a free subscription
        Subscription subscription = new Subscription(user, "Free");
        subscriptionRepository.save(subscription);

        System.out.println("✅ Free subscription assigned to user: " + email);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getUserType())
                .build();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword()); // Use matches()
        }
        return false;
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}


