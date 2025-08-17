package org.example.post.dto;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private String id;
    private String postId;
    private String authorId;
    private String authorUsername;
    private String content;
    private OffsetDateTime createdAt;
}
