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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database for initial data...");

        var users = populateUsers();
        if (users.isEmpty()) return;

        var posts = populatePosts(users);
        populateAddresses(users);

        populateComments(users, posts);

        log.info("Database initialization completed.");
    }

    // ---------------- USERS ----------------
    private List<User> populateUsers() {

        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping...");
            return userRepository.findAll();
        }

        var avatar = "https://res.cloudinary.com/dvjg8gm48/image/upload/v1777478590/nextjs-starter/f5476capb63o0jzeqshf.png";

        var usersToSave = List.of(
                User.builder().avatarUrl(avatar).email("admin@gmail.com").firstName("Admin").lastName("admin")
                        .password(passwordEncoder.encode("admin123")).phoneNumber("+212600000001")
                        .role(UserRole.ADMIN).status(UserStatus.ACTIVE).build(),

                User.builder().avatarUrl(avatar).email("visitor@gmail.com").firstName("Visitor").lastName("visit")
                        .password(passwordEncoder.encode("visitor123")).phoneNumber("+212600000002")
                        .role(UserRole.ADMIN).status(UserStatus.ACTIVE).build(),

                User.builder().email("john.doe@gmail.com").firstName("John").lastName("Doe")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000002")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("jane.smith@gmail.com").firstName("Jane").lastName("Smith")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000003")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("eric.dupont@gmail.com").firstName("Eric").lastName("Dupont")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000004")
                        .role(UserRole.USER).status(UserStatus.ACTIVE).build(),

                User.builder().email("alice.johnson@gmail.com").firstName("Alice").lastName("Johnson")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000005")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("michael.brown@gmail.com").firstName("Michael").lastName("Brown")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000006")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("emma.wilson@gmail.com").firstName("Emma").lastName("Wilson")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000007")
                        .role(UserRole.USER).status(UserStatus.ACTIVE).build(),

                User.builder().email("david.miller@gmail.com").firstName("David").lastName("Miller")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000008")
                        .role(UserRole.USER).status(UserStatus.ACTIVE).build(),

                User.builder().email("olivia.moore@gmail.com").firstName("Olivia").lastName("Moore")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000009")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("daniel.taylor@gmail.com").firstName("Daniel").lastName("Taylor")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000010")
                        .role(UserRole.USER).status(UserStatus.ACTIVE).build(),

                User.builder().email("sophia.anderson@gmail.com").firstName("Sophia").lastName("Anderson")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000011")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("james.thomas@gmail.com").firstName("James").lastName("Thomas")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000012")
                        .role(UserRole.USER).status(UserStatus.ACTIVE).build(),

                User.builder().email("charlotte.jackson@gmail.com").firstName("Charlotte").lastName("Jackson")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000013")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("lucas.white@gmail.com").firstName("Lucas").lastName("White")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000014")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build(),

                User.builder().email("amelia.harris@gmail.com").firstName("Amelia").lastName("Harris")
                        .password(passwordEncoder.encode("Simple123")).phoneNumber("+212600000015")
                        .role(UserRole.USER).status(UserStatus.INACTIVE).build()
        );

        var saved = userRepository.saveAll(usersToSave);
        userRepository.flush();

        log.info("Users created: {}", saved.size());

        return saved;
    }

    // ---------------- ADDRESSES ----------------
    private void populateAddresses(List<User> users) {

        if (addressRepository.count() > 0) return;

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

        var addresses = new ArrayList<Address>();

        for (int i = 0; i < users.size(); i++) {
            var u = users.get(i);
            var loc = mockLocations.get(i % mockLocations.size());

            addresses.add(Address.builder()
                    .street(loc.street())
                    .postalCode(loc.postalCode())
                    .city(loc.city())
                    .country(loc.country())
                    .defaultAddress(true)
                    .user(u)
                    .build());
        }

        addressRepository.saveAll(addresses);
    }

    // ---------------- POSTS ----------------
    private List<Post> populatePosts(List<User> users) {

        if (postRepository.count() > 0) {
            return postRepository.findAll();
        }

        var a1 = users.getFirst();
        var a2 = users.size() > 1 ? users.get(1) : a1;

        var posts = List.of(
                Post.builder().title("Spring Boot Guide").description("Learn Spring Boot basics").likes(15L).author(a1).build(),
                Post.builder().title("Docker Intro").description("Containerize apps").likes(21L).author(a2).build(),
                Post.builder().title("React Basics").description("UI development").likes(42L).author(a1).build(),
                Post.builder().title("Microservices").description("Architecture overview").likes(31L).author(a2).build(),
                Post.builder().title("Clean Code").description("Best practices").likes(50L).author(a1).build()
        );

        return postRepository.saveAll(posts);
    }

    // ---------------- COMMENTS ----------------
    private void populateComments(List<User> users, List<Post> posts) {

        if (commentRepository.count() > 0) return;

        var comments = List.of(
                Comment.builder().content("Excellent article").post(posts.get(0)).author(users.get(1)).build(),
                Comment.builder().content("Very useful").post(posts.get(1)).author(users.get(2)).build(),
                Comment.builder().content("Great explanation").post(posts.get(2)).author(users.get(3)).build(),
                Comment.builder().content("Nice work").post(posts.get(3)).author(users.get(1)).build(),
                Comment.builder().content("Very clear").post(posts.get(4)).author(users.get(2)).build()
        );

        commentRepository.saveAll(comments);
    }

    private record MockLocation(String street, String postalCode, String city, String country) {}
}