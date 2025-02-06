package org.example.nativespark.controllers;

//import com.example.nativespark.entities.User;
//import com.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @GetMapping("/select-user-type")
    public String showUserTypeSelectionPage() {
        return "select-user-type"; // Displays the user type selection page
    }

//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("user", new User());
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute User user) {
//        User registeredUser = userService.registerUser(user);
//        return "redirect:/subscription?userId=" + registeredUser.getUserId() + "&userType=" + registeredUser.getUserType();
//    }
}

