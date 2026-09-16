package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.elanrif.springbootstarterkit.repository.PostLikeRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void toggleLike_shouldAddThenRemoveLikeForCurrentUser() throws Exception {
        User user = userRepository.save(User.builder()
                .email("alice@example.com")
                .status(UserStatus.ACTIVE)
                .build());

        Post post = postRepository.save(Post.builder()
                .title("Example post")
                .description("This is a test post")
                .likes(0L)
                .author(user)
                .build());

        mockMvc.perform(post("/api/v1/posts/{postId}/like", post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("alice@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", is(1)))
                .andExpect(jsonPath("$.liked", is(true)));

        mockMvc.perform(post("/api/v1/posts/{postId}/like", post.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("alice@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.liked", is(false)));
    }

    @Test
    void toggleLike_shouldReturn401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/like")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice@example.com")
    void toggleLike_shouldReturn404_whenPostDoesNotExist() throws Exception {
        User user = userRepository.save(User.builder()
                .email("alice@example.com")
                .status(UserStatus.ACTIVE)
                .build());

        mockMvc.perform(post("/api/v1/posts/9999/like")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}
