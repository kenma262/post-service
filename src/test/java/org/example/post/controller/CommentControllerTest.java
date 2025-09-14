package org.example.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.post.config.SecurityConfig;
import org.example.post.dto.CommentRequest;
import org.example.post.dto.CommentResponse;
import org.example.post.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    // Mock JwtDecoder so resource-server config doesn't try to wire the real bean
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("POST /api/comments - success (ROLE_USER)")
    @WithMockUser(username = "john", roles = {"USER"})
    void createComment_success() throws Exception {
        CommentRequest req = CommentRequest.builder()
                .postId("post-1")
                .content("Nice post!")
                .build();

        CommentResponse resp = CommentResponse.builder()
                .id("c1")
                .postId("post-1")
                .authorId("u1")
                .authorUsername("john")
                .content("Nice post!")
                .createdAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"))
                .build();

        when(commentService.createComment(any(CommentRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.postId").value("post-1"))
                .andExpect(jsonPath("$.authorUsername").value("john"))
                .andExpect(jsonPath("$.content").value("Nice post!"));
    }

    @Test
    @DisplayName("POST /api/comments - validation error (400)")
    @WithMockUser(username = "john", roles = {"USER"})
    void createComment_validationError() throws Exception {
        // Missing content and postId blank
        String badJson = "{" +
                "\"postId\":\"\"," +
                "\"content\":\"\"}";

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/comments - unauthorized without auth (401)")
    void createComment_unauthorized() throws Exception {
        CommentRequest req = CommentRequest.builder()
                .postId("post-1")
                .content("Hi")
                .build();

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/comments/post/{postId} - success (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCommentsByPost_success() throws Exception {
        CommentResponse c1 = CommentResponse.builder()
                .id("c1").postId("post-1").authorId("u1").authorUsername("john")
                .content("Nice post!")
                .createdAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"))
                .build();
        CommentResponse c2 = CommentResponse.builder()
                .id("c2").postId("post-1").authorId("u2").authorUsername("jane")
                .content("Thanks for sharing")
                .createdAt(OffsetDateTime.parse("2024-01-01T00:01:00Z"))
                .build();

        when(commentService.getCommentsByPost(eq("post-1"))).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/comments/post/{postId}", "post-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[1].id").value("c2"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id} - success (ROLE_USER)")
    @WithMockUser(username = "john", roles = {"USER"})
    void editComment_success() throws Exception {
        CommentResponse updated = CommentResponse.builder()
                .id("c1")
                .postId("post-1")
                .authorId("u1")
                .authorUsername("john")
                .content("Edited content")
                .createdAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"))
                .build();

        when(commentService.editComment(eq("c1"), eq("Edited content"))).thenReturn(updated);

        mockMvc.perform(put("/api/comments/{commentId}", "c1")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Edited content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Edited content"));
    }
}

