package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.*;
import com.thegoalgrid.goalgrid.mapper.CommentMapper;
import com.thegoalgrid.goalgrid.mapper.ReactionMapper;
import com.thegoalgrid.goalgrid.repository.CommentReactionRepository;
import com.thegoalgrid.goalgrid.repository.CommentRepository;
import com.thegoalgrid.goalgrid.repository.PostReactionRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentReactionRepository reactionRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ReactionMapper reactionMapper;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    private CommentDTO commentDTO;
    private Comment comment;
    private User user1;
    private User user2;
    private Post post;
    private UserDetailsImpl userDetails1;
    private UserDetailsImpl userDetails2;
    private ReactionDTO reactionDTO;
    private CommentReaction reaction1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Users
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setPassword("password");
        user1.setFirstName("Test");
        user1.setLastName("User");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("anotherUser");
        user2.setPassword("password2");
        user2.setFirstName("Another");
        user2.setLastName("User");

        // Initialize Post
        post = new Post();
        post.setId(10L);
        post.setContent("Sample Post");
        post.setAuthor(user1);
        post.setPostReactions(new ArrayList<>());
        post.setComments(new ArrayList<>());

        // Initialize CommentDTO
        commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent("Sample Comment");
        commentDTO.setPostId(10L);
        commentDTO.setAuthorId(null); // To be set via mapping

        // Initialize Comment
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Sample Comment");
        comment.setPost(post);
        comment.setAuthor(user1);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setCommentReactions(new ArrayList<>());

        // Initialize UserDetailsImpl for user1
        userDetails1 = new UserDetailsImpl(1L, "testuser", "password", "Test", "User");

        // Initialize UserDetailsImpl for user2
        userDetails2 = new UserDetailsImpl(2L, "anotherUser", "password2", "Another", "User");

        // Initialize ReactionDTO
        reactionDTO = new ReactionDTO();
        reactionDTO.setType(ReactionType.LIKE);

        // Initialize Reaction
        reaction1 = new CommentReaction();
        reaction1.setId(1L);
        reaction1.setType(ReactionType.LIKE);
        reaction1.setUser(user1);
        reaction1.setComment(comment);
    }

}
