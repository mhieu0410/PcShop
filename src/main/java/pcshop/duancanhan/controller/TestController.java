package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/test/bcrypt")
    public String testBcrypt() {
        String password = "12345";
        String encodedPassword = passwordEncoder.encode(password);
        
        System.out.println("=== DEBUG: Password: " + password + " ===");
        System.out.println("=== DEBUG: Encoded: " + encodedPassword + " ===");
        
        // Test verify
        boolean matches = passwordEncoder.matches(password, encodedPassword);
        System.out.println("=== DEBUG: Matches: " + matches + " ===");
        
        return "Password: " + password + "<br>Encoded: " + encodedPassword + "<br>Matches: " + matches;
    }
} 