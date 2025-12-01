package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.dtos.create.CommentCreateDTO;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.PostService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @PreAuthorize("permitAll()")
    @GetMapping("/latest")
    public List<PostResponseDTO> getLatest() {
        PostFilterOptions filterOptions = new PostFilterOptions(
                null, null, null, null, false,
                "createdAt", "desc", 0, 10
        );

        return postService.getAll(filterOptions).stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/top-commented")
    public List<PostResponseDTO> getTopCommented() {
        return postService.getMostCommented().stream()
                .limit(10)
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<PostResponseDTO> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PostFilterOptions filterOptions = new PostFilterOptions(
                title, keyword, authorId, tagName, isDeleted,
                sortBy, sortOrder, page, size);

        return postService.getAll(filterOptions).stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetPostId}")
    public PostResponseDTO getById(@PathVariable Long targetPostId) {
        return postMapper.toResponse(postService.getById(targetPostId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetPostId}/comments")
    public List<CommentResponseDTO> getComments(
            @PathVariable Long targetPostId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        CommentFilterOptions filterOptions = new CommentFilterOptions(
                targetPostId, null, null,
                null, null, null, page, size);

        return commentService.getAll(filterOptions).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDTO create(@Valid @RequestBody PostCreateDTO dto) {
        Post post = postService.create(dto);

        return postMapper.toResponse(post);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{targetPostId}")
    public PostResponseDTO update(
            @PathVariable Long targetPostId,
            @Valid @RequestBody PostUpdateDTO dto
    ) {
        postService.update(targetPostId, dto);
        return postMapper.toResponse(postService.getById(targetPostId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetPostId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long targetPostId) {
        postService.delete(targetPostId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{targetPostId}/likes")
    public void like(@PathVariable Long targetPostId) {
        postService.likePost(targetPostId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetPostId}/likes")
    public void unlike(@PathVariable Long targetPostId) {
        postService.unlikePost(targetPostId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{targetPostId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDTO comment(
            @PathVariable Long targetPostId,
            @Valid @RequestBody CommentCreateDTO dto
    ) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        commentService.create(comment, targetPostId);

        return commentMapper.toResponse(comment);
    }
}