package org.example.post.service;

import org.example.post.dto.PostRequest;
import org.example.post.dto.PostResponse;
import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request);
    List<PostResponse> getAllPosts();
    PostResponse getPostById(String id);
    PostResponse editPost(String postId, PostRequest request);
}
