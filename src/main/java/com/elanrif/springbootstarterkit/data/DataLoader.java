package com.elanrif.springbootstarterkit.data;

import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
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

    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final CommentRepository commentRepository;
    public final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database for initial data...");
        populateUsers();
        populatePosts();
        populateComments();
    }

    public void populateUsers() {
        if (userRepository.count() == 0) {
            log.info("Populating users...");
            var usersToSave = List.of(
                    User.builder()
                            .email("admin@gmail.com")
                            .firstName("Admin")
                            .lastName("User")
                            .password(passwordEncoder.encode("admin123456"))
                            .phoneNumber("+1234567890")
                            .role(UserRole.ADMIN)
                            .isActive(true)
                            .build(),
                    User.builder()
                            .email("jhon.doe@gmail.com")
                            .firstName("John")
                            .lastName("Doe")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+0987654321")
                            .role(UserRole.USER)
                            .isActive(true)
                            .build(),
                    User.builder()
                            .email("jane@google.com")
                            .firstName("Jane")
                            .lastName("Smith")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+1122334455")
                            .role(UserRole.USER)
                            .isActive(true)
                            .build(),
                    User.builder()
                            .email("eric@gmail.com")
                            .firstName("Eric")
                            .lastName("Dupont")
                            .password(passwordEncoder.encode("Simple123"))
                            .phoneNumber("+5566778899")
                            .role(UserRole.USER)
                            .isActive(true)
                            .build()
            );
            userRepository.saveAll(usersToSave);
            log.info("Users created successfully");
        }
    }

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
                        .title("Welcome to Spring Social Feedback")
                        .imageUrl("welcome-post")
                        .description("A first post to bootstrap the social feedback flow.")
                        .likes(12L)
                        .author(author1)
                        .build(),
                Post.builder()
                        .title("Roadmap and Next Features")
                        .imageUrl("roadmap-post")
                        .description("Share ideas and vote on what should be built next.")
                        .likes(5L)
                        .author(author2)
                        .build(),
                Post.builder()
                        .title("Community Guidelines")
                        .imageUrl("guidelines-post")
                        .description("Please keep feedback constructive and respectful.")
                        .likes(3L)
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
        var post1 = posts.getFirst();
        var post2 = posts.size() > 1 ? posts.get(1) : post1;
        var author1 = users.getFirst();
        var author2 = users.size() > 1 ? users.get(1) : author1;

        var commentsToSave = List.of(
                Comment.builder()
                        .content("Great start, I like this direction!")
                        .post(post1)
                        .author(author2)
                        .build(),
                Comment.builder()
                        .content("Could we add tags and search by topic?")
                        .post(post1)
                        .author(author1)
                        .build(),
                Comment.builder()
                        .content("A voting system would help prioritize features.")
                        .post(post2)
                        .author(author2)
                        .build()
        );

        commentRepository.saveAll(commentsToSave);
        log.info("Comments created successfully");
    }
}
