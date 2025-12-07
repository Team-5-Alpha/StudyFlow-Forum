package telerik.project.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telerik.project.helpers.mappers.CommentMapper;
import telerik.project.models.Comment;
import telerik.project.models.dtos.response.ResponseDTO;
import telerik.project.models.dtos.response.CommentResponseDTO;
import telerik.project.models.dtos.update.CommentUpdateDTO;
import telerik.project.models.filters.CommentFilterOptions;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/private/comments")
    public ResponseDTO<List<CommentResponseDTO>> getAll(
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

        Long actingUserId = SecurityContextUtil.getCurrentUserId();

        List<CommentResponseDTO> data = commentService
                .getAll(filterOptions).stream()
                .map(c -> commentMapper.toResponse(c, actingUserId))
                .toList();

        return ResponseDTO.success(data);
    }

    @GetMapping("/private/comments/{targetCommentId}")
    public ResponseDTO<CommentResponseDTO> getById(@PathVariable Long targetCommentId) {
        Long actingUserId = SecurityContextUtil.getCurrentUserId();
        CommentResponseDTO dto = commentMapper.toResponse(
                commentService.getById(targetCommentId),
                actingUserId
        );
        return ResponseDTO.success(dto);
    }

    @GetMapping("/private/comments/{targetCommentId}/replies")
    public ResponseDTO<List<CommentResponseDTO>> getReplies(@PathVariable Long targetCommentId) {
        Long actingUserId = SecurityContextUtil.getCurrentUserId();
        List<CommentResponseDTO> data = commentService.getReplies(targetCommentId)
                .stream()
                .map(c -> commentMapper.toResponse(c, actingUserId))
                .toList();

        return ResponseDTO.success(data);
    }

    @PutMapping("/private/comments/{targetCommentId}")
    public ResponseDTO<CommentResponseDTO> update(
            @PathVariable Long targetCommentId,
            @Valid @RequestBody CommentUpdateDTO dto
    ) {
        Comment updatedComment = commentService.getById(targetCommentId);
        commentMapper.updateComment(updatedComment, dto);
        commentService.update(targetCommentId, updatedComment);

        CommentResponseDTO response = commentMapper.toResponse(commentService.getById(targetCommentId),
                SecurityContextUtil.getCurrentUserId());

        return ResponseDTO.success("Comment updated.", response);
    }

    @DeleteMapping("/private/comments/{targetCommentId}")
    public ResponseDTO<?> delete(@PathVariable Long targetCommentId) {
        commentService.delete(targetCommentId);
        return ResponseDTO.success("Comment deleted.");
    }

    @PostMapping("/private/comments/{targetCommentId}/likes")
    public ResponseDTO<?> like(@PathVariable Long targetCommentId) {
        commentService.likeComment(targetCommentId);
        return ResponseDTO.success("Comment liked.");
    }

    @DeleteMapping("/private/comments/{targetCommentId}/likes")
    public ResponseDTO<?> unlike(@PathVariable Long targetCommentId) {
        commentService.unlikeComment(targetCommentId);
        return ResponseDTO.success("Comment unliked.");
    }
}