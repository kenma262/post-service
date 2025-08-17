package org.example.post.service.impl;

import org.example.post.dto.CommentRequest;
import org.example.post.dto.CommentResponse;
import org.example.post.model.Comment;
import org.example.post.repository.CommentRepository;
import org.example.post.service.CommentService;
import org.example.post.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentResponse createComment(CommentRequest request) {
        var userInfo = SecurityUtils.UserInfo.fromSecurityContext();
        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .content(request.getContent())
                .authorId(userInfo.id())
                .authorUsername(userInfo.username())
                .createdAt(OffsetDateTime.now())
                .build();
        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    @Override
    public List<CommentResponse> getCommentsByPost(String postId) {
        return commentRepository.findByPostId(postId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CommentResponse editComment(String commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        var userInfo = SecurityUtils.UserInfo.fromSecurityContext();
        if (userInfo == null || !comment.getAuthorId().equals(userInfo.id())) {
            throw new RuntimeException("Unauthorized: You are not the author of this comment");
        }
        comment.setContent(newContent);
        commentRepository.save(comment);
        return toResponse(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .authorId(comment.getAuthorId())
                .authorUsername(comment.getAuthorUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
