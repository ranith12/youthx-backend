package com.youthx.backend.controller;


import com.youthx.backend.dto.CommentResponse;
import com.youthx.backend.dto.CreateCommentRequest;
import com.youthx.backend.dto.CreatePostRequest;
import com.youthx.backend.dto.PageResponse;
import com.youthx.backend.dto.PostResponse;
import com.youthx.backend.dto.UpdatePostRequest;
import com.youthx.backend.entity.Comment;
import com.youthx.backend.entity.Post;
import com.youthx.backend.entity.PostPhoto;
import com.youthx.backend.entity.User;
import com.youthx.backend.repository.CommentRepository;
import com.youthx.backend.repository.LikeRepository;
import com.youthx.backend.repository.PostPhotoRepository;
import com.youthx.backend.repository.UserRepository;
import com.youthx.backend.service.CommentService;
import com.youthx.backend.service.LikeService;
import com.youthx.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final PostPhotoRepository postPhotoRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Post post = postService.createPost(currentUserId, request.getContent(), request.getPhotoUrls());
        return ResponseEntity.status(HttpStatus.CREATED).body(toPostResponse(post, currentUserId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> feed(Pageable pageable) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Page<Post> page = postService.getFeed(pageable);
        return ResponseEntity.ok(toPageResponse(page, currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(toPostResponse(postService.getPost(id), currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdatePostRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Post post = postService.updatePost(currentUserId, id, request.getContent());
        return ResponseEntity.ok(toPostResponse(post, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        postService.deletePost(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                      @Valid @RequestBody CreateCommentRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Comment comment = commentService.addComment(currentUserId, postId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentResponse(comment));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> listComments(@PathVariable Long postId,
                                                                      Pageable pageable) {
        Page<Comment> page = commentService.listComments(postId, pageable);
        List<CommentResponse> content = page.getContent().stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        commentService.deleteComment(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId) {
        UUID currentUserId = currentUserProvider.currentUserId();
        likeService.toggleLike(currentUserId, postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{postId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> uploadPhoto(@PathVariable Long postId,
                                                    @RequestParam("file") MultipartFile file) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Post post = postService.addPhoto(currentUserId, postId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(toPostResponse(post, currentUserId));
    }

    private PostResponse toPostResponse(Post post, UUID currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setContent(post.getContent());
        response.setPhotoUrls(loadPhotoUrls(post.getId()));
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setAuthorFullName(loadAuthorFullName(post.getUserId()));
        response.setLikeCount(likeRepository.countByPostId(post.getId()));
        response.setCommentCount(commentRepository.countByPostId(post.getId()));
        response.setLikedByMe(likeRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
        return response;
    }

    private String loadAuthorFullName(UUID userId) {
        return userRepository.findById(userId)
                .map(User::getFullName)
                .orElse(null);
    }

    private List<String> loadPhotoUrls(Long postId) {
        return postPhotoRepository.findByPostIdOrderByDisplayOrderAsc(postId).stream()
                .map(PostPhoto::getPhotoUrl)
                .collect(Collectors.toList());
    }

    private CommentResponse toCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setUserId(comment.getUserId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setAuthorFullName(loadAuthorFullName(comment.getUserId()));
        return response;
    }

    private PageResponse<PostResponse> toPageResponse(Page<Post> page, UUID currentUserId) {
        List<PostResponse> content = page.getContent().stream()
                .map(post -> toPostResponse(post, currentUserId))
                .collect(Collectors.toList());
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
