package com.laundry.app.config;

import com.laundry.app.model.*;
import com.laundry.app.repo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner seed(AccountRepository accounts, LaundryServiceRepository services,
            ClothingItemRepository clothes, PasswordEncoder encoder,
            @Value("${APP_ADMIN_EMAIL:admin@laundry.local}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:}") String adminPassword) {
        return args -> {
            if (!accounts.existsByEmail(adminEmail)) {
                if (adminPassword == null || adminPassword.isBlank()) {
                    throw new IllegalStateException("Set APP_ADMIN_PASSWORD before the first application start.");
                }
                Account admin=new Account(); admin.setName("Laundry Administrator"); admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode(adminPassword)); admin.setRole("ADMIN"); accounts.save(admin);
            }
            if (services.count()==0) for (String[] row : new String[][] {
                {"Washing","Everyday wash and rinse","6"},{"Dry Cleaning","Specialist care for delicate garments","15"},
                {"Ironing","Pressed and neatly finished","4"},{"Washing and Ironing","Washed, dried and pressed","9"},{"Folding","Clean folding service","3"}}) {
                LaundryService s=new LaundryService(); s.setName(row[0]); s.setDescription(row[1]); s.setPricePerItem(Double.valueOf(row[2])); services.save(s);
            }
            for(String name : new String[]{"Shirt","Pant","T-Shirt","Saree","Bedsheet","Towel","Dress","Blanket","Duvet","Curtains"}) {
                if(clothes.findAll().stream().noneMatch(c -> name.equalsIgnoreCase(c.getName()))) {
                    ClothingItem c=new ClothingItem(); c.setName(name); clothes.save(c);
                }
            }
        };
    }
}

