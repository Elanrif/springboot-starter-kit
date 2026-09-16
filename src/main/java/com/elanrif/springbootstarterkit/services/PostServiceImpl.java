package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.config.SecurityUtils;
import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.mapper.CommentMapper;
import com.elanrif.springbootstarterkit.mapper.PostMapper;
import com.elanrif.springbootstarterkit.repository.CommentRepository;
import com.elanrif.springbootstarterkit.repository.PostLikeRepository;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.specification.PostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PostDto.Response> getPosts(
            PostDto.Filter filter,
            PaginationDto.Pagination pagination
    ) {
        log.debug(
                "Fetching posts - page: {}, size: {}, search: {}, authorId: {}",
                pagination.page(),
                pagination.size(),
                filter.search(),
                filter.authorId()
        );

        Page<PostDto.Response> posts = postRepository
                .findAll(
                        PostSpecification.from(filter),
                        pagination.toPageable()
                )
                .map(post -> enrichWithLikeState(post, postMapper.toDto(post)));

        log.debug(
                "Found {} posts (total: {})",
                posts.getNumberOfElements(),
                posts.getTotalElements()
        );

        return PageResponse.from(posts);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto.Response getPostById(Long id) {
        log.debug("Fetching post detail with id: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Post not found with id: {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Post not found: " + id
                    );
                });
        return enrichWithLikeState(post, postMapper.toDto(post));
    }

    @Override
    @Transactional
    public PostDto.Response createPost(PostDto.CreateRequest request) {
        log.debug("Creating post with title: {}", request.title());

        if (request.authorId() == null) {
            log.warn("Post creation failed - authorId is missing");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorId is required");
        }

        Post post = postMapper.toEntity(request);
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", request.authorId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found: " + request.authorId());
                });
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);
        PostDto.Response response = enrichWithLikeState(savedPost, postMapper.toDto(savedPost));
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
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Post not found: " + id
                    );
                });
        postMapper.updateEntity(request, post);

        Post postSaved = postRepository.save(post);
        PostDto.Response response = enrichWithLikeState(postSaved, postMapper.toDto(postSaved));
        log.info("Post updated successfully with id: {}", id);
        return response;
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        log.debug("Deleting post with id: {}", id);
        if (!postRepository.existsById(id)) {
            log.warn("Delete failed - post not found with id: {}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Post not found: " + id
            );
        }
        postRepository.deleteById(id);
        log.info("Post deleted successfully with id: {}", id);
    }

    private PostDto.Response enrichWithLikeState(Post post, PostDto.Response baseResponse) {
        long totalLikes = postLikeRepository.countByPostId(post.getId());
        boolean liked = false;

        try {
            Long currentUserId = securityUtils.getCurrentUserId();
            liked = postLikeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                liked = false;
            } else {
                throw ex;
            }
        }

        return new PostDto.Response(
                baseResponse.id(),
                baseResponse.title(),
                baseResponse.imageUrl(),
                baseResponse.description(),
                totalLikes,
                liked,
                baseResponse.author(),
                baseResponse.numberOfComments(),
                baseResponse.createdAt(),
                baseResponse.updatedAt()
        );
    }
}
