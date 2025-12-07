package telerik.project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.create.CommentCreateDTO;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.PostService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @GetMapping("/public/posts/latest")
    public ResponseDTO<List<PostResponseDTO>> getLatest() {
        PostFilterOptions filterOptions = new PostFilterOptions(
                null, null, null, null, false,
                "createdAt", "desc", 0, 10
        );

        List<PostResponseDTO> data = postService.getAll(filterOptions).stream()
                .map(postMapper::toResponse)
                .toList();
        return ResponseDTO.success(data);
    }

    @GetMapping("/public/posts/top-commented")
    public ResponseDTO<List<PostResponseDTO>> getTopCommented() {
        List<PostResponseDTO> data = postService.getMostCommented().stream()
                .limit(10)
                .map(postMapper::toResponse)
                .toList();
        return ResponseDTO.success(data);
    }

    @GetMapping("/private/posts")
    public ResponseDTO<List<PostResponseDTO>> getAll(
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

        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        List<PostResponseDTO> data = postService.getAll(filterOptions).stream()
                .map(p -> postMapper.toResponse(p, actingUserId))
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/posts/{targetPostId}")
    public ResponseDTO<PostResponseDTO> getById(@PathVariable Long targetPostId) {
        Post post = postService.getById(targetPostId);
        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        PostResponseDTO dto = postMapper.toResponse(post, actingUserId);
        return ResponseDTO.success(dto);
    }

    @GetMapping("/private/posts/{targetPostId}/comments")
    public ResponseDTO<List<CommentResponseDTO>> getComments(
            @PathVariable Long targetPostId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        CommentFilterOptions filterOptions = new CommentFilterOptions(
                targetPostId, null, null,
                null, null, null, page, size);

        List<CommentResponseDTO> data = commentService.getAll(filterOptions).stream()
                .map(commentMapper::toResponse)
                .toList();

        return ResponseDTO.success(data);
    }

    @PostMapping("/private/posts")
    public ResponseDTO<PostResponseDTO> create(@Valid @RequestBody PostCreateDTO dto) {
        Post post = postService.create(dto);
        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        PostResponseDTO response = postMapper.toResponse(post, actingUserId);
        return ResponseDTO.success("Post created.", response);
    }

    @PutMapping("/private/posts/{targetPostId}")
    public ResponseDTO<PostResponseDTO> update(
            @PathVariable Long targetPostId,
            @Valid @RequestBody PostUpdateDTO dto
    ) {
        postService.update(targetPostId, dto);
        Post updated = postService.getById(targetPostId);

        Long actingUserId = SecurityContextUtil.getCurrentUserId();
        PostResponseDTO response = postMapper.toResponse(updated, actingUserId);

        return ResponseDTO.success("Post updated.", response);
    }

    @DeleteMapping("/private/posts/{targetPostId}")
    public ResponseDTO<?> delete(@PathVariable Long targetPostId) {
        postService.delete(targetPostId);
        return ResponseDTO.success("Post deleted.");
    }

    @PostMapping("/private/posts/{targetPostId}/likes")
    public ResponseDTO<?> like(@PathVariable Long targetPostId) {
        postService.likePost(targetPostId);
        return ResponseDTO.success("Post liked.");
    }

    @DeleteMapping("/private/posts/{targetPostId}/likes")
    public ResponseDTO<?> unlike(@PathVariable Long targetPostId) {
        postService.unlikePost(targetPostId);
        return ResponseDTO.success("Post unliked.");
    }

    @PostMapping("/private/posts/{postId}/comments")
    public ResponseDTO<CommentResponseDTO> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateDTO dto
    ) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        if (dto.getParentCommentId() != null) {
            Comment parent = commentService.getById(dto.getParentCommentId());
            comment.setParentComment(parent);
        }

        commentService.create(comment, postId);
        CommentResponseDTO response = commentMapper.toResponse(comment);

        return ResponseDTO.success("Comment created.", response);
    }
}
