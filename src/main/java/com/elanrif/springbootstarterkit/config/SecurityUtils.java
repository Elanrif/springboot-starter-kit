package com.elanrif.springbootstarterkit.config;

import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public User getCurrentUser() {
        Authentication auth = getAuthentication();

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return auth != null
                && auth.isAuthenticated()
                && auth.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role)
                );
    }


    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Not authenticated"
            );
        }

        return auth;
    }

    // check the ownership
    public boolean isPostOwner(Long postId) {
        Long currentUserId = getCurrentUserId();

        return postRepository.findById(postId)
                .map(post ->
                        post.getAuthor() != null
                                && post.getAuthor().getId().equals(currentUserId)
                )
                .orElse(false);
    }

    public boolean isCommentOwner(Long commentId) {
        Long currentUserId = getCurrentUserId();

        return commentRepository.findById(commentId)
                .map(comment ->
                        comment.getAuthor() != null
                                && comment.getAuthor().getId().equals(currentUserId)
                )
                .orElse(false);
    }
}