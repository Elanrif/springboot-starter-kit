package com.elanrif.springbootstarterkit.controller.demo;

import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.repository.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts-demo")
@RequiredArgsConstructor
@Tag(
        name = "Posts Demo",
        description = "Educational endpoints demonstrating Spring Data pagination, sorting, " +
                "and Specification-based filtering. Not part of the real API surface."
)
public class FilterController {
    private final PostRepository postRepository;

    // ============================================================================
    // Spring Boot Pagination & Sorting — Demonstration
    // This section provides a simple example of how pagination and sorting
    // work with Spring Data using Pageable, PageRequest, and Sort.
    // ============================================================================
    @GetMapping
    @Operation(
            summary = "List posts (basic pagination)",
            description = "Demonstrates plain pagination using PageRequest, with a 1-based page parameter."
    )
    public ResponseEntity<Page<Post>> getPosts(
            @Parameter(description = "Page number, starting at 1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of results per page") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        log.debug("GET /api/v1/posts-demo - pageable: {}", pageable);

        Page<Post> posts = postRepository.findAll(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/sort")
    @Operation(
            summary = "List posts with sorting",
            description = "Demonstrates pagination combined with dynamic sorting, " +
                    "e.g. sort=createdAt,desc or sort=title,asc."
    )
    public ResponseEntity<Page<Post>> getPosts(
            @Parameter(description = "Page number, starting at 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of results per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Property and direction, e.g. 'createdAt,desc'")
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        // Split the sort parameter into the property and the direction.
        // Example: "createdAt,desc" → ["createdAt", "desc"]
        String[] parts = sort.split(",");

        // The first part is the entity property used for sorting.
        // Example: "createdAt"
        String property = parts[0];

        // The second part defines the sorting direction (ASC or DESC).
        // If no direction is provided, ASC is used by default.
        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1])
                : Sort.Direction.ASC;

        // Build the Spring Data Sort object from the property and direction.
        // Example: Sort.by(DESC, "createdAt")
        Sort sorting = Sort.by(direction, property);

        log.debug("GET /api/v1/posts-demo/sort - property: {}, direction: {}", property, direction);

        /* The same as above, but using PageRequest.of() */
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Post> posts = postRepository.findAll(pageable);

        return ResponseEntity.ok(posts);
    }

    // ============================================================================
    // Spring Data JPA Specification — Demonstration
    // This section provides a simple example of how to use Specification
    // to dynamically filter Posts based on a search parameter.
    // ============================================================================

    @GetMapping("/spec")
    @Operation(
            summary = "Filter posts by title (Specification)",
            description = "Demonstrates dynamic filtering with Spring Data Specification. " +
                    "Returns all posts if no search value is given."
    )
    public ResponseEntity<List<Post>> getPosts(
            @Parameter(description = "Case-insensitive substring to match against the post title")
            @RequestParam(required = false) String search
    ) {
        log.debug("GET /api/v1/posts-demo/spec - search: {}", search);

        // ------------------------------------------------------------------------
        // Build a Specification to filter posts by title.
        // If no search value is provided, no filtering is applied.
        // ------------------------------------------------------------------------
        Specification<Post> specification;

        if (search == null || search.isBlank()) {
            specification = Specification.unrestricted();
        } else {
            // ------------------------------------------------------------------------
            // Create a Specification<Post> to dynamically build the WHERE condition.
            //
            // root            → represents the Post entity.
            //                    Example: root.get("title") → Post.title
            //
            // query           → represents the JPA query being built.
            //                    It can be used for things such as DISTINCT,
            //                    ORDER BY, subqueries, etc.
            //
            // criteriaBuilder → provides methods to build query conditions.
            //                    Examples:
            //                    - equal(...)       → =
            //                    - like(...)        → LIKE
            //                    - greaterThan(...) → >
            //                    - lessThan(...)    →
            // ------------------------------------------------------------------------

            //    PostRepository equivalent : List<Post> findByTitleContainingIgnoreCase(String search);
            specification = (root, query, criteriaBuilder) ->
                    // expected : WHERE LOWER(title) LIKE '%spring%'
                    criteriaBuilder.like(
                            // Convert the search string to lowercase before comparing.
                            criteriaBuilder.lower(root.get("title")),
                            "%" + search.toLowerCase() + "%"
                    );
        }

        List<Post> posts = postRepository.findAll(specification);

        return ResponseEntity.ok(posts);
    }

    // ============================================================================
    // Spring Data JPA Specification + Pagination — Demonstration
    // This section demonstrates how to combine dynamic filtering with
    // pagination and sorting using Specification and Pageable.
    // ============================================================================

    @GetMapping("/spec-page")
    @Operation(
            summary = "Filter, sort and paginate posts (Specification + Pageable)",
            description = "Combines dynamic filtering (Specification), sorting, and pagination " +
                    "in a single query. The most complete example of the three above."
    )
    public ResponseEntity<Page<Post>> getPostsWithFilterAndPagination(
            @Parameter(description = "Case-insensitive substring to match against the post title")
            @RequestParam(required = false) String search,
            @Parameter(description = "Page number, starting at 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of results per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Property and direction, e.g. 'createdAt,desc'")
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        log.debug("GET /api/v1/posts-demo/spec-page - search: {}, page: {}, size: {}, sort: {}",
                search, page, size, sort);

        // ------------------------------------------------------------------------
        // Build a Specification to dynamically filter posts by title.
        // If no search value is provided, no filtering is applied.
        // ------------------------------------------------------------------------
        Specification<Post> specification;

        if (search == null || search.isBlank()) {
            specification = Specification.unrestricted();
        } else {

            // root            → represents the Post entity.
            //                   root.get("title") → Post.title
            //
            // query           → represents the JPA query being built.
            //
            // criteriaBuilder → provides methods to build query conditions.
            //                   Example: like(...), equal(...), greaterThan(...)

            specification = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            // Convert Post.title to lowercase.
                            criteriaBuilder.lower(root.get("title")),

                            // Convert the search value to lowercase.
                            // Example: search = "spring"
                            //          → "%spring%"
                            //
                            // Expected SQL:
                            // WHERE LOWER(title) LIKE '%spring%'
                            "%" + search.toLowerCase() + "%"
                    );
        }

        // ------------------------------------------------------------------------
        // Build the Sort object.
        // Example: "createdAt,desc"
        //          → property = "createdAt"
        //          → direction = DESC
        // ------------------------------------------------------------------------
        String[] parts = sort.split(",");

        String property = parts[0];

        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1])
                : Sort.Direction.ASC;

        Sort sorting = Sort.by(direction, property);

        // ------------------------------------------------------------------------
        // Create the Pageable.
        //
        // page → page number (starts at 0)
        // size → number of posts per page
        // sorting → sorting property and direction
        // ------------------------------------------------------------------------
        Pageable pageable = PageRequest.of(page, size, sorting);

        // ------------------------------------------------------------------------
        // Execute the query using both Specification and Pageable.
        //
        // Specification → filtering (WHERE)
        // Pageable      → pagination + sorting
        // ------------------------------------------------------------------------
        Page<Post> posts = postRepository.findAll(
                specification,
                pageable
        );

        return ResponseEntity.ok(posts);
    }
}