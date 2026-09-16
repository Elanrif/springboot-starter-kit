package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.config.SecurityUtils;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.PostLike;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.repository.PostLikeRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PostLikeServiceImpl postLikeService;

    @Test
    void toggleLike_whenNotLiked_addsLike() {
        User user = User.builder().id(7L).email("alice@example.com").build();
        Post post = Post.builder().id(12L).title("Test post").description("Hello").likes(0L).author(user).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(postRepository.findById(12L)).thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserIdAndPostId(7L, 12L)).thenReturn(Optional.empty());
        when(postLikeRepository.saveAndFlush(any(PostLike.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postLikeRepository.countByPostId(12L)).thenReturn(1L);
        when(postRepository.save(post)).thenReturn(post);

        PostDto.LikeResponse response = postLikeService.toggleLike(12L);

        assertEquals(1L, response.likes());
        assertEquals(true, response.liked());
    }

    @Test
    void toggleLike_whenAlreadyLiked_removesLike() {
        User user = User.builder().id(7L).email("alice@example.com").build();
        Post post = Post.builder().id(12L).title("Test post").description("Hello").likes(1L).author(user).build();
        PostLike postLike = PostLike.builder().id(99L).user(user).post(post).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(postRepository.findById(12L)).thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserIdAndPostId(7L, 12L)).thenReturn(Optional.of(postLike));
        when(postLikeRepository.countByPostId(12L)).thenReturn(0L);
        when(postRepository.save(post)).thenReturn(post);

        PostDto.LikeResponse response = postLikeService.toggleLike(12L);

        assertEquals(0L, response.likes());
        assertEquals(false, response.liked());
    }
}
