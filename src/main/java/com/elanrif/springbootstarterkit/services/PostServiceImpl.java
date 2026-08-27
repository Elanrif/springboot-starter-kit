package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CommentMapper;
import com.elanrif.springbootstarterkit.mapper.PostMapper;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
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
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public PostDto.CommentsResponse getPostComments(
            Long postId,
            CommentDto.Filter filter,
            int page,
            int size
    ) {
        log.debug(
                "Fetching comments for post - postId: {}, page: {}, size: {}, search: {}, authorId: {}",
                postId,
                page,
                size,
                filter != null ? filter.search() : null,
                filter != null ? filter.authorId() : null
        );

        // Vérifier que le Post existe
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", postId);
                    return new ResourceNotFoundException(
                            "Post not found: " + postId
                    );
                });

        // Pagination + tri
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                toSort(filter)
        );

        // Filtres + postId
        Specification<Comment> specification =
                buildCommentSpecification(postId, filter);

        // Récupérer uniquement les commentaires de CE post
        Page<CommentDto.Response> comments =
                commentRepository
                        .findAll(specification, pageRequest)
                        .map(commentMapper::toResponse);

        log.debug(
                "Found {} comments for post {} (total: {})",
                comments.getNumberOfElements(),
                postId,
                comments.getTotalElements()
        );

        return postMapper.toCommentsResponse(
                post,
                PageResponse.from(comments)
        );
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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
        if (filter == null || filter.sort() == null || filter.sort().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String sort = filter.sort();
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, field);
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

    private Sort toSort(CommentDto.Filter filter) {
        if (filter == null || filter.sort() == null || filter.sort().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String sort = filter.sort();
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, field);
    }

    private Specification<Comment> buildCommentSpecification(Long postId, CommentDto.Filter filter) {
        if (filter == null) {
            return post(postId);
        }
        return Specification.where(commentSearch(filter.search()))
                .and(post(postId))
                .and(commentAuthor(filter.authorId()));
    }

    private Specification<Comment> commentSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("content")), like);
        };
    }

    private Specification<Comment> post(Long postId) {
        return (root, query, cb) -> cb.equal(root.get("post").get("id"), postId);
    }

    private Specification<Comment> commentAuthor(Long authorId) {
        return (root, query, cb) -> authorId == null
                ? null
                : cb.equal(root.get("author").get("id"), authorId);
    }
}
