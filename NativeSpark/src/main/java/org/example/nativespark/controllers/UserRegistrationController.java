package org.example.nativespark.controllers;

//import com.example.nativespark.entities.User;
//import com.example.nativespark.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserRegistrationController {

    @GetMapping("/select-user-type")
    public String showUserTypeSelectionPage() {
        return "select-user-type"; // Displays the user type selection page
    }

    @PostMapping("/register/user-type")
    public String selectUserType(@RequestParam("userType") String userType, HttpSession session) {
        session.setAttribute("selectedUserType", userType);

        if ("BUSINESS".equals(userType)) {
            return "redirect:/register/business";
        }
        return "redirect:/register/basic";
    }

    @GetMapping("/register/business")
    public String showBusinessRegistrationForm() {
        return "business-register"; // ✅ Ensure this matches the Thymeleaf file name
    }

//    @GetMapping("/register/entrepreneur")
//    public String showEntrepreneurRegistrationForm() {
//        return "entrepreneur-register"; // Renders entrepreneur registration form
//    }
//
//    @GetMapping("/register/basic")
//    public String showBasicUserRegistrationForm() {
//        return "basic-register"; // Renders basic user registration form
//    }

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



