package org.example.post.service.impl;

import org.example.post.dto.PostRequest;
import org.example.post.dto.PostResponse;
import org.example.post.model.Post;
import org.example.post.repository.PostRepository;
import org.example.post.repository.CommentRepository;
import org.example.post.service.PostService;
import org.example.post.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.post.dto.CommentResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public PostResponse createPost(PostRequest request) {
        var userInfo = SecurityUtils.UserInfo.fromSecurityContext();
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(userInfo.id())
                .authorUsername(userInfo.username())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    @Override
    public PostResponse editPost(String postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        var userInfo = SecurityUtils.UserInfo.fromSecurityContext();
        if (userInfo == null || !post.getAuthorId().equals(userInfo.id())) {
            throw new RuntimeException("Unauthorized: You are not the author of this post");
        }
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(OffsetDateTime.now());
        postRepository.save(post);
        return toResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(String id) {
        return postRepository.findById(id).map(this::toResponse).orElse(null);
    }

    private PostResponse toResponse(Post post) {
        // Fetch comments from CommentRepository instead of using embedded comments
        List<CommentResponse> commentResponses = commentRepository.findByPostId(post.getId()).stream()
            .map(comment -> CommentResponse.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .authorUsername(comment.getAuthorUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .authorUsername(post.getAuthorUsername())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(commentResponses)
                .build();
    }
}
