package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.helpers.mappers.PostMapper;
import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.response.PostResponseDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.PostService;
import telerik.project.services.contracts.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public List<PostResponseDTO> getAllPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PostFilterOptions filterOptions = new PostFilterOptions(
                title, keyword, authorId, tagName, isDeleted, sortBy, sortOrder, page, size);

        return postService.getAll(filterOptions).stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PostResponseDTO getPostById(@PathVariable Long id) {
        return postMapper.toResponse(postService.getById(id));
    }

    @PostMapping
    public PostResponseDTO createPost(@RequestParam Long actingUserId,
                                      @Valid @RequestBody PostCreateDTO dto) {
        User actingUser = userService.getById(actingUserId); // взимаме текущия потребител
        Post post = postMapper.fromCreateDTO(dto, actingUser);
        postService.create(post, actingUser);
        return postMapper.toResponse(post);
    }

    @PutMapping("/{id}")
    public PostResponseDTO updatePost(@RequestParam Long actingUserId,
                                      @PathVariable Long id,
                                      @Valid @RequestBody PostUpdateDTO dto) {
        User actingUser = userService.getById(actingUserId);
        Post post = postService.getById(id);
        postMapper.updatePost(post, dto);
        postService.update(id, post, actingUser);
        return postMapper.toResponse(postService.getById(id));
    }

    @DeleteMapping("/{id}")
    public void deletePost(@RequestParam Long actingUserId,
                           @PathVariable Long id) {
        User actingUser = userService.getById(actingUserId);
        postService.delete(id, actingUser);
    }

    @PostMapping("/{id}/like")
    public void likePost(@RequestParam Long actingUserId,
                         @PathVariable Long id) {
        User actingUser = userService.getById(actingUserId);
        postService.likePost(id, actingUser);
    }

    @DeleteMapping("/{id}/unlike")
    public void unlikePost(@RequestParam Long actingUserId,
                           @PathVariable Long id) {
        User actingUser = userService.getById(actingUserId);
        postService.unlikePost(id, actingUser);
    }

    @GetMapping("/latest")
    public List<PostResponseDTO> getLatestPosts(@RequestParam(defaultValue = "10") int limit) {
        return postService.getMostRecent().stream()
                .limit(limit)
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{postId}/comments")
    public List<CommentResponseDTO> getCommentsForPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        CommentFilterOptions filterOptions = new CommentFilterOptions(
                postId, null, null, null, null, null, page, size);

        return commentService.getAll(filterOptions).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/top-commented")
    public List<PostResponseDTO> getTopCommentedPosts(@RequestParam(defaultValue = "10") int limit) {
        return postService.getMostCommented().stream()
                .limit(limit)
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }
}