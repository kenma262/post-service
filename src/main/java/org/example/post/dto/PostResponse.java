package org.example.post.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorUsername;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<CommentResponse> comments;
}
