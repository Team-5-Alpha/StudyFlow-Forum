package telerik.project.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.models.Comment;
import telerik.project.models.dtos.create.CommentCreateDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.update.CommentUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.services.contracts.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetCommentId}")
    public CommentResponseDTO getById(@PathVariable Long targetCommentId) {
        return commentMapper.toResponse(commentService.getById(targetCommentId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{targetCommentId}/replies")
    public List<CommentResponseDTO> getReplies(@PathVariable Long targetCommentId) {
        return commentService.getReplies(targetCommentId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{targetCommentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDTO create(
            @PathVariable Long targetCommentId,
            @Valid @RequestBody CommentCreateDTO dto
    ) {
        Comment parent = commentService.getById(targetCommentId);

        Comment reply = new Comment();
        reply.setContent(dto.getContent());
        reply.setParentComment(parent);

        commentService.create(reply, parent.getPost().getId());

        return commentMapper.toResponse(reply);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{targetCommentId}")
    public CommentResponseDTO update(
            @PathVariable Long targetCommentId,
            @Valid @RequestBody CommentUpdateDTO dto
    ) {
        Comment updatedComment = commentService.getById(targetCommentId);

        commentMapper.updateComment(updatedComment, dto);
        commentService.update(targetCommentId, updatedComment);

        return commentMapper.toResponse(commentService.getById(targetCommentId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetCommentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long targetCommentId) {
        commentService.delete(targetCommentId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{targetCommentId}/likes")
    public void like(@PathVariable Long targetCommentId) {
        commentService.likeComment(targetCommentId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{targetCommentId}/likes")
    public void unlike(@PathVariable Long targetCommentId) {
        commentService.unlikeComment(targetCommentId);
    }
}