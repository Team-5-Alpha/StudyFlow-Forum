package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public List<CommentResponseDTO> getAll(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long parentCommentId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        CommentFilterOptions filterOptions = new CommentFilterOptions(
                postId, authorId, parentCommentId,
                isDeleted, sortBy, sortOrder, page, size);

        return commentService.getAll(filterOptions).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CommentResponseDTO getById(@PathVariable Long id) {
        return commentMapper.toResponse(commentService.getById(id));
    }

    @GetMapping("/{id}/replies")
    public List<CommentResponseDTO> getReplies(@PathVariable Long id) {
        return commentService.getReplies(id).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDTO create(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO dto
    ) {
        User actingUser = userService.getById(actingUserId);
        Comment parent = commentService.getById(id);

        Comment reply = new Comment();
        reply.setContent(dto.getContent());
        reply.setParentComment(parent);

        commentService.create(reply, parent.getPost().getId(), actingUser);

        return commentMapper.toResponse(reply);
    }

    @PutMapping("/{id}")
    public CommentResponseDTO update(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateDTO dto
    ) {
        User actingUser = userService.getById(actingUserId);
        Comment target = commentService.getById(id);

        commentMapper.updateComment(target, dto);
        commentService.update(id, target, actingUser);

        return commentMapper.toResponse(commentService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        commentService.delete(id, actingUser);
    }

    @PostMapping("/{id}/likes")
    public void like(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        commentService.likeComment(id, actingUser);
    }

    @DeleteMapping("/{id}/likes")
    public void unlike(
            @RequestHeader("X-User-Id") Long actingUserId,
            @PathVariable Long id
    ) {
        User actingUser = userService.getById(actingUserId);
        commentService.unlikeComment(id, actingUser);
    }
}