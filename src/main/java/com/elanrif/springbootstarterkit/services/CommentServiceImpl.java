package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CommentMapper;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentDto.Response> getComments(CommentDto.Filter filter, int page, int size) {
        log.debug("Fetching comments with filter - page: {}, size: {}, search: {}, postId: {}, authorId: {}",
                page, size,
                filter != null ? filter.search() : null,
                filter != null ? filter.postId() : null,
                filter != null ? filter.authorId() : null);

        PageRequest pageRequest = PageRequest.of(page, size, toSort(filter));
        Specification<Comment> specification = buildSpecification(filter);
        Page<CommentDto.Response> result = commentRepository.findAll(specification, pageRequest)
                .map(commentMapper::toResponse);

        log.debug("Found {} comments (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return PageResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto.Response getCommentById(Long id) {
        log.debug("Fetching comment with id: {}", id);
        Comment comment = commentRepository.findByIdWithAuthorAndPost(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found with id: {}", id);
                    return new ResourceNotFoundException("Comment not found: " + id);
                });
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public CommentDto.Response createComment(CommentDto.CreateRequest request) {
        log.debug("Creating comment for post id: {}", request.postId());
        Comment comment = commentMapper.toEntity(request);

        Post post = resolvePost(request.postId());
        User author = resolveAuthor(request.authorId());
        comment.setPost(post);
        comment.setAuthor(author);

        CommentDto.Response response = commentMapper.toResponse(commentRepository.save(comment));
        log.info("Comment created successfully with id: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public CommentDto.Response updateComment(Long id, CommentDto.UpdateRequest request) {
        log.debug("Updating comment with id: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - comment not found with id: {}", id);
                    return new ResourceNotFoundException("Comment not found: " + id);
                });

        commentMapper.updateFromRequest(request, comment);
        CommentDto.Response response = commentMapper.toResponse(commentRepository.save(comment));

        log.info("Comment updated successfully with id: {}", id);
        return response;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        log.debug("Deleting comment with id: {}", id);
        if (!commentRepository.existsById(id)) {
            log.warn("Delete failed - comment not found with id: {}", id);
            throw new ResourceNotFoundException("Comment not found: " + id);
        }
        commentRepository.deleteById(id);
        log.info("Comment deleted successfully with id: {}", id);
    }

    private Post resolvePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", postId);
                    return new ResourceNotFoundException("Post not found: " + postId);
                });
    }

    private User resolveAuthor(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", authorId);
                    return new ResourceNotFoundException("Author not found: " + authorId);
                });
    }

    private Sort toSort(CommentDto.Filter filter) {
        if (filter == null || filter.sortBy() == null || filter.sortBy().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String sortBy = filter.sortBy();
        Sort.Direction direction = sortBy.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = sortBy.startsWith("-") ? sortBy.substring(1) : sortBy;
        return Sort.by(direction, property);
    }

    private Specification<Comment> buildSpecification(CommentDto.Filter filter) {
        if (filter == null) {
            return (root, query, cb) -> null;
        }
        return Specification.where(search(filter.search()))
                .and(post(filter.postId()))
                .and(author(filter.authorId()));
    }

    private Specification<Comment> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("content")), like);
        };
    }

    private Specification<Comment> post(Long postId) {
        return (root, query, cb) -> postId == null
                ? null
                : cb.equal(root.get("post").get("id"), postId);
    }

    private Specification<Comment> author(Long authorId) {
        return (root, query, cb) -> authorId == null
                ? null
                : cb.equal(root.get("author").get("id"), authorId);
    }
}
