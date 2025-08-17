package org.example.post.repository;

import org.example.post.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    // Add custom query methods if needed
}

