package com.laundry.app.web;

import com.laundry.app.model.Account;
import com.laundry.app.repo.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AccountRepository accounts; private final PasswordEncoder encoder;
    public AuthController(AccountRepository a, PasswordEncoder e){accounts=a;encoder=e;}
    @GetMapping("/") String landing(org.springframework.ui.Model model,com.laundry.app.repo.LaundryServiceRepository services){model.addAttribute("services",services.findByActiveTrueOrderByNameAsc());return "index";}
    @GetMapping("/login") String login(){return "login";}
    @GetMapping("/register") String register(Model m){m.addAttribute("account",new Account());return "register";}
    @PostMapping("/register") String register(@Valid @ModelAttribute Account account, BindingResult result,
                                                @RequestParam String confirmPassword, Model model){
        if(account.getPassword()==null || account.getPassword().length()<8) result.rejectValue("password","weak","Password must contain at least 8 characters.");
        if(account.getPassword()==null || !account.getPassword().equals(confirmPassword)) result.rejectValue("password","mismatch","Passwords do not match.");
        if(account.getEmail()!=null && accounts.existsByEmail(account.getEmail())) result.rejectValue("email","duplicate","An account with this email already exists.");
        if(result.hasErrors()) return "register";
        account.setPassword(encoder.encode(account.getPassword())); account.setRole("CUSTOMER"); accounts.save(account);
        return "redirect:/login?registered";
    }
    @GetMapping("/home") String home(org.springframework.security.core.Authentication auth){
        return auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"))?"redirect:/admin":"redirect:/customer";
    }
}
