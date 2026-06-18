package com.elanrif.springbootstarterkit.data;

import com.elanrif.springbootstarterkit.entity.*;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    public final AddressRepository addressRepository;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final CommentRepository commentRepository;
    public final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database for initial data...");
        populateUsers();
        populateAddresses();
        populatePosts();
        populateComments();
    }

    public void populateUsers() {
        if (userRepository.count() == 0) {
            var avatar = "https://res.cloudinary.com/dvjg8gm48/image/upload/v1777478590/nextjs-starter/f5476capb63o0jzeqshf.png";
            log.info("Populating users...");
            var usersToSave = List.of(
                    User.builder()
                            .avatarUrl(avatar)
                            .email("admin@gmail.com")
                            .firstName("Admin")
                            .lastName("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .phoneNumber("+212600000001")
                            .role(UserRole.ADMIN)
                            .status(UserStatus.ACTIVE)
                            .build(),
                    User.builder()
                            .avatarUrl(avatar)
                            .email("visitor@gmail.com")
                            .firstName("Visitor")
                            .lastName("visit")
                            .password(passwordEncoder.encode("visitor123"))
                            .phoneNumber("+212600000002")
                            .role(UserRole.ADMIN)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .avatarUrl(avatar)
                            .email("john.doe@gmail.com")
                            .firstName("John")
                            .lastName("Doe")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000002")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("jane.smith@gmail.com")
                            .firstName("Jane")
                            .lastName("Smith")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000003")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("eric.dupont@gmail.com")
                            .firstName("Eric")
                            .lastName("Dupont")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000004")
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .email("alice.johnson@gmail.com")
                            .firstName("Alice")
                            .lastName("Johnson")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000005")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("michael.brown@gmail.com")
                            .firstName("Michael")
                            .lastName("Brown")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000006")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("emma.wilson@gmail.com")
                            .firstName("Emma")
                            .lastName("Wilson")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000007")
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .email("david.miller@gmail.com")
                            .firstName("David")
                            .lastName("Miller")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000008")
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .email("olivia.moore@gmail.com")
                            .firstName("Olivia")
                            .lastName("Moore")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000009")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("daniel.taylor@gmail.com")
                            .firstName("Daniel")
                            .lastName("Taylor")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000010")
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .email("sophia.anderson@gmail.com")
                            .firstName("Sophia")
                            .lastName("Anderson")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000011")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("james.thomas@gmail.com")
                            .firstName("James")
                            .lastName("Thomas")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000012")
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build(),

                    User.builder()
                            .email("charlotte.jackson@gmail.com")
                            .firstName("Charlotte")
                            .lastName("Jackson")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000013")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("lucas.white@gmail.com")
                            .firstName("Lucas")
                            .lastName("White")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000014")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build(),

                    User.builder()
                            .email("amelia.harris@gmail.com")
                            .firstName("Amelia")
                            .lastName("Harris")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+212600000015")
                            .role(UserRole.USER)
                            .status(UserStatus.INACTIVE)
                            .build()
            );
            userRepository.saveAll(usersToSave);
            log.info("Users created successfully");
        }
    }
    public void populateAddresses() {
        if (addressRepository.count() > 0) {
            return;
        }

        var users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("Skipping addresses population: no users found");
            return;
        }

        log.info("Populating realistic and unique addresses for all users...");

        // Liste d'adresses réelles et variées à distribuer
        var mockLocations = List.of(
                new MockLocation("12 Avenue Mohammed V", "93000", "Tétouan", "Morocco"),
                new MockLocation("45 Boulevard d'Anfa", "20250", "Casablanca", "Morocco"),
                new MockLocation("8 Rue de la Paix", "75002", "Paris", "France"),
                new MockLocation("10 Main Street", "10001", "New York", "USA"),
                new MockLocation("24 Piccadilly Circus", "W1J 9HP", "London", "United Kingdom"),
                new MockLocation("55 Corso Vittorio Emanuele", "00186", "Rome", "Italy"),
                new MockLocation("102 Avenida de la Constitución", "41004", "Seville", "Spain"),
                new MockLocation("33 Friedrichstraße", "10117", "Berlin", "Germany"),
                new MockLocation("18 Rue du Marché", "1204", "Geneva", "Switzerland"),
                new MockLocation("77 Rue de la Loi", "1040", "Brussels", "Belgium"),
                new MockLocation("99 Orchard Road", "238839", "Singapore", "Singapore"),
                new MockLocation("14 George Street", "NSW 2000", "Sydney", "Australia"),
                new MockLocation("5-1 Chome Marunouchi", "100-0005", "Tokyo", "Japan"),
                new MockLocation("220 Bay Street", "ON M5J 2W4", "Toronto", "Canada"),
                new MockLocation("88 Av. del Libertador", "C1425", "Buenos Aires", "Argentina"),
                new MockLocation("11 Sheikh Zayed Road", "00000", "Dubai", "UAE")
        );

        var addressesToSave = new java.util.ArrayList<Address>();

        for (int i = 0; i < users.size(); i++) {
            var currentUser = users.get(i);

            // On utilise le modulo (%) pour boucler sur notre liste de villes
            // si jamais il y a plus d'utilisateurs que de villes disponibles
            var loc = mockLocations.get(i % mockLocations.size());

            var address = Address.builder()
                    .street(loc.street)
                    .postalCode(loc.postalCode)
                    .city(loc.city)
                    .country(loc.country)
                    .isDefault(true)
                    .user(currentUser)
                    .build();

            addressesToSave.add(address);
        }

        addressRepository.saveAll(addressesToSave);
        log.info("Successfully created " + addressesToSave.size() + " realistic addresses.");
    }

    // Petite classe interne utilitaire (Record) pour structurer nos fausses adresses
    private record MockLocation(String street, String postalCode, String city, String country) {}

    public void populatePosts() {
        if (postRepository.count() > 0) {
            return;
        }

        var users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("Skipping posts population: no users found");
            return;
        }

        log.info("Populating posts...");
        var author1 = users.getFirst();
        var author2 = users.size() > 1 ? users.get(1) : author1;

        var postsToSave = List.of(
                Post.builder()
                        .title("Welcome to Spring Boot Starter Kit")
                        .imageUrl("spring-boot-starter")
                        .description("Discover the features included in this Spring Boot starter project.")
                        .likes(15L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Building Secure REST APIs")
                        .imageUrl("secure-rest-api")
                        .description("Learn how to secure your REST APIs using Spring Security and JWT.")
                        .likes(28L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Introduction to Docker")
                        .imageUrl("docker-introduction")
                        .description("Containerize your applications for easier deployment.")
                        .likes(21L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Getting Started with React")
                        .imageUrl("react-guide")
                        .description("A beginner's guide to building modern user interfaces with React.")
                        .likes(42L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Why Use TypeScript?")
                        .imageUrl("typescript-benefits")
                        .description("Understand the advantages of adding static typing to JavaScript.")
                        .likes(19L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Deploying with Docker Compose")
                        .imageUrl("docker-compose")
                        .description("Run multiple services together using Docker Compose.")
                        .likes(35L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Mastering Spring Data JPA")
                        .imageUrl("spring-data-jpa")
                        .description("Simplify database access with Spring Data repositories.")
                        .likes(17L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Clean Code Principles")
                        .imageUrl("clean-code")
                        .description("Write maintainable and readable code by following clean code practices.")
                        .likes(50L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Pagination Best Practices")
                        .imageUrl("pagination")
                        .description("Improve application performance using efficient pagination.")
                        .likes(23L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Understanding Microservices")
                        .imageUrl("microservices")
                        .description("Explore the benefits and challenges of microservice architecture.")
                        .likes(31L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Introduction to Redis")
                        .imageUrl("redis-cache")
                        .description("Speed up your application with Redis caching.")
                        .likes(12L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Authentication vs Authorization")
                        .imageUrl("auth-security")
                        .description("Learn the difference between authentication and authorization.")
                        .likes(44L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Writing Better SQL Queries")
                        .imageUrl("sql-tips")
                        .description("Optimize your SQL queries for better performance.")
                        .likes(18L)
                        .author(author1)
                        .build(),

                Post.builder()
                        .title("Unit Testing with JUnit")
                        .imageUrl("junit-testing")
                        .description("Create reliable applications by writing unit tests.")
                        .likes(27L)
                        .author(author2)
                        .build(),

                Post.builder()
                        .title("Continuous Integration Explained")
                        .imageUrl("ci-cd")
                        .description("Automate testing and deployment with CI/CD pipelines.")
                        .likes(39L)
                        .author(author1)
                        .build()
        );

        postRepository.saveAll(postsToSave);
        log.info("Posts created successfully");
    }

    public void populateComments() {
        if (commentRepository.count() > 0) {
            return;
        }

        var posts = postRepository.findAll();
        var users = userRepository.findAll();
        if (posts.isEmpty() || users.isEmpty()) {
            log.warn("Skipping comments population: missing posts or users");
            return;
        }

        log.info("Populating comments...");

        var commentsToSave = List.of(
                Comment.builder()
                        .content("Excellent article, very informative!")
                        .post(posts.get(0))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("This tutorial helped me solve my issue.")
                        .post(posts.get(1))
                        .author(users.get(2))
                        .build(),

                Comment.builder()
                        .content("I would love to see a follow-up on this topic.")
                        .post(posts.get(2))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("Great explanation, thanks for sharing!")
                        .post(posts.get(3))
                        .author(users.get(2))
                        .build(),

                Comment.builder()
                        .content("Very useful for beginners.")
                        .post(posts.get(4))
                        .author(users.get(3))
                        .build(),

                Comment.builder()
                        .content("I learned something new today.")
                        .post(posts.get(5))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("Could you provide more code examples?")
                        .post(posts.get(6))
                        .author(users.get(2))
                        .build(),

                Comment.builder()
                        .content("This is exactly what I was looking for.")
                        .post(posts.get(7))
                        .author(users.get(3))
                        .build(),

                Comment.builder()
                        .content("Very clear and well written.")
                        .post(posts.get(8))
                        .author(users.get(3))
                        .build(),

                Comment.builder()
                        .content("I successfully implemented this in my project.")
                        .post(posts.get(9))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("Thanks for the detailed explanation.")
                        .post(posts.get(10))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("Looking forward to more content like this.")
                        .post(posts.get(11))
                        .author(users.get(2))
                        .build(),

                Comment.builder()
                        .content("Very practical advice.")
                        .post(posts.get(12))
                        .author(users.get(3))
                        .build(),

                Comment.builder()
                        .content("This made a complex topic much easier to understand.")
                        .post(posts.get(13))
                        .author(users.get(1))
                        .build(),

                Comment.builder()
                        .content("Amazing work, keep it up!")
                        .post(posts.get(14))
                        .author(users.get(2))
                        .build()
        );

        commentRepository.saveAll(commentsToSave);
        log.info("Comments created successfully");
    }
}
