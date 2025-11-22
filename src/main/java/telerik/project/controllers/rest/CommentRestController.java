package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.models.Comment;
import telerik.project.models.User;
import telerik.project.models.dtos.create.CommentCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.update.CommentUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.services.contracts.CommentService;
import telerik.project.services.contracts.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;
    
    @GetMapping
    public List<CommentResponseDTO> getComments(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long parentCommentId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        CommentFilterOptions filterOptions = new CommentFilterOptions(
                postId, authorId, parentCommentId, isDeleted, sortBy, sortOrder, page, size);

        return commentService.getAll(filterOptions).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CommentResponseDTO getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return commentMapper.toResponse(comment);
    }

    @PostMapping
    public CommentResponseDTO createComment(@RequestParam Long actingUserId,
                                            @Valid @RequestBody CommentCreateDTO dto) {
        User user = userService.getById(actingUserId);

        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        if (dto.getParentCommentId() != null) {
            Comment parent = commentService.getById(dto.getParentCommentId());
            comment.setParentComment(parent);

            commentService.create(comment, parent.getPost().getId(), user);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Parent comment ID is required for this endpoint. Use /posts/{postId}/comments for top-level comments."
            );
        }

        return commentMapper.toResponse(comment);
    }

    @PutMapping("/{id}")
    public CommentResponseDTO updateComment(@RequestParam Long actingUserId,
                                            @PathVariable Long id,
                                            @Valid @RequestBody CommentUpdateDTO dto) {
        User user = userService.getById(actingUserId);

        Comment existing = commentService.getById(id);
        commentMapper.updateComment(existing, dto);
        commentService.update(id, existing, user);

        return commentMapper.toResponse(commentService.getById(id));
    }
    @DeleteMapping("/comments/{id}")
    public void deleteComment(@RequestParam Long actingUserId,
                              @PathVariable Long id) {
        User user = userService.getById(actingUserId);
        commentService.delete(id, user);
    }

    @PostMapping("/{id}/like")
    public void likeComment(@RequestParam Long actingUserId,
                            @PathVariable Long id) {
        User user = userService.getById(actingUserId);
        commentService.likeComment(id, user);
    }

    @DeleteMapping("/{id}/unlike")
    public void unlikeComment(@RequestParam Long actingUserId,
                              @PathVariable Long id) {
        User user = userService.getById(actingUserId);
        commentService.unlikeComment(id, user);
    }

    @GetMapping("/{id}/replies")
    public List<CommentResponseDTO> getReplies(@PathVariable Long id) {
        return commentService.getReplies(id).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
}