package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CommentMapper;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.specification.CommentSpecification;
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
    public PageResponse<CommentDto.Response> getComments(
            CommentDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        log.debug(
                "Fetching comments - page: {}, size: {}, search: {}, authorId: {}",
                pagination.page(),
                pagination.size(),
                filter.search(),
                filter.authorId()
        );

        Page<CommentDto.Response> comments = commentRepository
                .findAll(
                        CommentSpecification.from(filter, null),
                        pagination.toPageable()
                )
                .map(commentMapper::toResponse);

        log.debug(
                "Found {} comments (total: {})",
                comments.getNumberOfElements(),
                comments.getTotalElements()
        );

        return PageResponse.from(comments);
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

        // Update relationships
        Post getPost = postRepository.findById(request.postId())
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", request.postId());
                    return new ResourceNotFoundException("Post not found: " + request.postId());
                });
        User getUser = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", request.authorId());
                    return new ResourceNotFoundException("Author not found: " + request.authorId());
                });

        comment.setPost(getPost);
        comment.setAuthor(getUser);


        CommentDto.Response response = commentMapper.toResponse(commentRepository.save(comment));
        log.info("Comment created successfully with id: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public CommentDto.Response updateComment(
            Long id,
            CommentDto.UpdateRequest request
    ) {
        log.debug("Updating comment with id: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - comment not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Comment not found: " + id
                    );
                });

        // Update simple fields
        commentMapper.updateFromRequest(request, comment);

        // Update relationships
        Post getPost = postRepository.findById(request.postId())
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", request.postId());
                    return new ResourceNotFoundException("Post not found: " + request.postId());
                });
        User getUser = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", request.authorId());
                    return new ResourceNotFoundException("Author not found: " + request.authorId());
                });

        comment.setPost(getPost);
        comment.setAuthor(getUser);

        CommentDto.Response response =
                commentMapper.toResponse(commentRepository.save(comment));

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

}
