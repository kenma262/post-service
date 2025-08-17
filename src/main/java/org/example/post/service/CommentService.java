package org.example.post.service;

import org.example.post.dto.CommentRequest;
import org.example.post.dto.CommentResponse;
import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
    List<CommentResponse> getCommentsByPost(String postId);
    CommentResponse editComment(String commentId, String newContent);
}
