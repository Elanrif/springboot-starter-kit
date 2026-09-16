package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.config.SecurityUtils;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.PostLike;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.repository.PostLikeRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public PostDto.LikeResponse toggleLike(Long postId) {
        User currentUser = securityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Like toggle failed - post not found with id: {}", postId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Post not found: " + postId
                    );
                });

        Optional<PostLike> existingLike =
                postLikeRepository.findByUserIdAndPostId(
                        currentUser.getId(),
                        postId
                );

        if (existingLike.isPresent()) {

            log.debug(
                    "Removing like for user {} on post {}",
                    currentUser.getId(),
                    postId
            );

            postLikeRepository.delete(existingLike.get());

            long likesCount = postLikeRepository.countByPostId(postId);

            return new PostDto.LikeResponse(likesCount, false);
        }

        try {
            log.debug(
                    "Adding like for user {} on post {}",
                    currentUser.getId(),
                    postId
            );

            postLikeRepository.saveAndFlush(
                    PostLike.builder()
                            .user(currentUser)
                            .post(post)
                            .build()
            );

            long likesCount = postLikeRepository.countByPostId(postId);

            return new PostDto.LikeResponse(likesCount, true);

        } catch (DataIntegrityViolationException ex) {

            log.warn(
                    "Duplicate like attempt prevented for user {} and post {}",
                    currentUser.getId(),
                    postId
            );

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Post already liked by user"
            );
        }
    }
}