package com.elanrif.springbootstarterkit.config;

import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownerOrAdmin")
@RequiredArgsConstructor
public class OwnerOrAdmin {

    private final SecurityUtils securityUtils;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public boolean isOwnerOrAdmin(Long id, String entityType) {
        if (securityUtils.hasRole("ADMIN")) {
            return true;
        }
        Long ownerId = switch (entityType) {
            case "post" -> postRepository.findById(id).map(p -> p.getAuthor().getId()).orElse(null);
            case "comment" -> commentRepository.findById(id).map(c -> c.getAuthor().getId()).orElse(null);
            default -> null;
        };
        return ownerId != null && ownerId.equals(securityUtils.getCurrentUserId());
    }
}