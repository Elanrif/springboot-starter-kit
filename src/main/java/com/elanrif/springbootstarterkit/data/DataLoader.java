package com.elanrif.springbootstarterkit.data;

import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.entity.Product;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import com.elanrif.springbootstarterkit.repository.ProductRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    public final CategoryRepository categoryRepository;
    public final ProductRepository productRepository;
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database for initial data...");
        populateDatabase();
        populateUsers();
    }

    public void populateDatabase() {
        if (categoryRepository.count() == 0) {
            log.info("Populating categories...");
            var categoriesToSave = java.util.List.of(
                Category.builder().name("Electronics").slug("electronics").description("Electronic devices").isActive(true).sortOrder(0).build(),
                Category.builder().name("Books").slug("books").description("Books and literature").isActive(true).sortOrder(1).build(),
                Category.builder().name("Clothing").slug("clothing").description("Clothing and accessories").isActive(true).sortOrder(2).build()
            );
            categoryRepository.saveAll(categoriesToSave);
        }

        if (productRepository.count() == 0) {
            log.info("Populating products...");
            var categories = categoryRepository.findAll();
            var cat1 = categories.stream().filter(c -> c.getSlug().equals("electronics")).findFirst().orElse(null);
            var cat2 = categories.stream().filter(c -> c.getSlug().equals("books")).findFirst().orElse(null);
            var cat3 = categories.stream().filter(c -> c.getSlug().equals("clothing")).findFirst().orElse(null);

            var productsToSave = java.util.List.of(
                Product.builder().name("Smartphone").slug("smartphone").description("Latest smartphone").price(new BigDecimal("699.99")).stock(50).isActive(true).category(cat1).build(),
                Product.builder().name("Laptop").slug("laptop").description("High performance laptop").price(new BigDecimal("1299.99")).stock(30).isActive(true).category(cat2).build(),
                Product.builder().name("Headphones").slug("headphones").description("Noise cancelling headphones").price(new BigDecimal("199.99")).stock(100).isActive(true).build(),
                Product.builder().name("Novel").slug("novel").description("Bestselling novel").price(new BigDecimal("19.99")).stock(200).isActive(true).category(cat2).build(),
                Product.builder().name("Textbook").slug("textbook").description("Educational textbook").price(new BigDecimal("49.99")).stock(80).isActive(true).category(cat3).build(),
                Product.builder().name("T-shirt").slug("tshirt").description("Cotton t-shirt").price(new BigDecimal("14.99")).stock(150).isActive(true).build(),
                Product.builder().name("Jeans").slug("jeans").description("Denim jeans").price(new BigDecimal("39.99")).stock(60).isActive(true).category(cat2).build()
            );
            productRepository.saveAll(productsToSave);
        }
    }

    public void populateUsers() {
        if (userRepository.count() == 0) {
            log.info("Populating users...");
            var usersToSave = java.util.List.of(
                User.builder()
                    .email("admin@gmail.com")
                    .firstName("Admin")
                    .lastName("User")
                    .password(passwordEncoder.encode("admin123456"))
                    .phoneNumber("+1234567890")
                    .avatarUrl("https://avatar.example.com/admin.jpg")
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build(),
                User.builder()
                    .email("user@gmail.com")
                    .firstName("John")
                    .lastName("Doe")
                    .password(passwordEncoder.encode("user123456"))
                    .phoneNumber("+0987654321")
                    .avatarUrl("https://avatar.example.com/john.jpg")
                    .role(UserRole.USER)
                    .isActive(true)
                    .build(),
                User.builder()
                    .email("jane@example.com")
                    .firstName("Jane")
                    .lastName("Smith")
                    .password(passwordEncoder.encode("jane123456"))
                    .phoneNumber("+1122334455")
                    .avatarUrl("https://avatar.example.com/jane.jpg")
                    .role(UserRole.USER)
                    .isActive(true)
                    .build(),
                User.builder()
                    .email("test@gmail.com")
                    .firstName("Test")
                    .lastName("Account")
                    .password(passwordEncoder.encode("test123456"))
                    .phoneNumber("+5566778899")
                    .role(UserRole.USER)
                    .isActive(true)
                    .build()
            );
            userRepository.saveAll(usersToSave);
            log.info("Users created successfully");
        }
    }
}
