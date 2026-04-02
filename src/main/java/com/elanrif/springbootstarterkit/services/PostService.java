package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.PostMapper;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    /**
     * Liste des posts (Response avec author Summary et commentCount)
     */
    @Transactional(readOnly = true)
    public PageResponse<PostDto.Response> getPosts(PostDto.Filter filter, int page, int size) {
        log.debug("Fetching posts with filter - page: {}, size: {}, search: {}, authorId: {}",
                page, size, filter != null ? filter.search() : null, filter != null ? filter.authorId() : null);
        PageRequest pageRequest = PageRequest.of(page, size, toSort(filter));
        Specification<Post> specification = buildSpecification(filter);
        Page<PostDto.Response> result = postRepository.findAll(specification, pageRequest)
                .map(postMapper::toResponse);
        log.debug("Found {} posts (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return PageResponse.from(result);
    }

    /**
     * Détail d'un post avec commentaires (DetailResponse)
     */
    @Transactional(readOnly = true)
    public PostDto.DetailResponse getPostById(Long id) {
        log.debug("Fetching post detail with id: {}", id);
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", id);
                    return new ResourceNotFoundException("Post not found: " + id);
                });
        return postMapper.toDetailResponse(post);
    }

    @Transactional
    public PostDto.Response createPost(PostDto.CreateRequest request) {
        log.debug("Creating post with title: {}", request.title());
        if (request.authorId() == null) {
            throw new BadRequestException("authorId is required");
        }

        Post post = postMapper.toEntity(request);
        User author = resolveAuthor(request.authorId());
        post.setAuthor(author);

        PostDto.Response response = postMapper.toResponse(postRepository.save(post));
        log.info("Post created successfully with id: {}", response.id());
        return response;
    }

    @Transactional
    public PostDto.Response updatePost(Long id, PostDto.UpdateRequest request) {
        log.debug("Updating post with id: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - post not found with id: {}", id);
                    return new ResourceNotFoundException("Post not found: " + id);
                });

        postMapper.updateFromRequest(request, post);

        if (request.authorId() != null) {
            User author = resolveAuthor(request.authorId());
            post.setAuthor(author);
        }

        PostDto.Response response = postMapper.toResponse(postRepository.save(post));
        log.info("Post updated successfully with id: {}", id);
        return response;
    }

    @Transactional
    public void deletePost(Long id) {
        log.debug("Deleting post with id: {}", id);
        if (!postRepository.existsById(id)) {
            log.warn("Delete failed - post not found with id: {}", id);
            throw new ResourceNotFoundException("Post not found: " + id);
        }
        postRepository.deleteById(id);
        log.info("Post deleted successfully with id: {}", id);
    }

    private User resolveAuthor(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", authorId);
                    return new ResourceNotFoundException("Author not found: " + authorId);
                });
    }

    private Sort toSort(PostDto.Filter filter) {
        if (filter == null || filter.sortBy() == null || filter.sortBy().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String sortBy = filter.sortBy();
        Sort.Direction direction = sortBy.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = sortBy.startsWith("-") ? sortBy.substring(1) : sortBy;
        return Sort.by(direction, property);
    }

    private Specification<Post> buildSpecification(PostDto.Filter filter) {
        if (filter == null) {
            return (root, query, cb) -> null;
        }
        return Specification.where(search(filter.search()))
                .and(author(filter.authorId()));
    }

    private Specification<Post> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    private Specification<Post> author(Long authorId) {
        return (root, query, cb) -> authorId == null
                ? null
                : cb.equal(root.get("author").get("id"), authorId);
    }
}
