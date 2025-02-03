package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.Post;
import com.thegoalgrid.goalgrid.entity.PostReaction;
import com.thegoalgrid.goalgrid.entity.ReactionType;
import com.thegoalgrid.goalgrid.entity.User;
import com.thegoalgrid.goalgrid.mapper.PostMapper;
import com.thegoalgrid.goalgrid.mapper.ReactionMapper;
import com.thegoalgrid.goalgrid.repository.PostReactionRepository;
import com.thegoalgrid.goalgrid.repository.PostRepository;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostReactionRepository postReactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private ReactionMapper reactionMapper;

    private PostDTO postDTO;
    private Post post;
    private User user;
    private UserDetailsImpl userDetails;
    private ReactionDTO reactionDTO;
    private PostReaction reaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");

        postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setContent("Sample Content");
        postDTO.setReferencedGoalId(100L);
        postDTO.setProgressUpdate(false);

        post = new Post();
        post.setId(1L);
        post.setContent("Sample Content");
        post.setReferencedGoal(null);
        post.setProgressUpdate(false);
        post.setAuthor(user);
        post.setPostReactions(new ArrayList<>());
        post.setComments(new ArrayList<>());

        userDetails = new UserDetailsImpl(1L, "testuser", "password", "Test", "User");

        reactionDTO = new ReactionDTO();
        reactionDTO.setType(ReactionType.LIKE);

        reaction = new PostReaction();
        reaction.setId(1L);
        reaction.setType(ReactionType.LIKE);
        reaction.setUser(user);
        reaction.setPost(post);
    }

    // Existing tests for createPost, getPostById, updatePost, deletePost

    @Test
    void createReactionForPost_ShouldReturnReactionDTO_WhenValid() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userService.getUserById(1L)).thenReturn(user);
        when(postReactionRepository.findByUserAndTypeAndPost(user, ReactionType.LIKE, post)).thenReturn(Optional.empty());
        when(reactionMapper.toPostReactionEntity(reactionDTO)).thenReturn(reaction);
        when(postReactionRepository.save(reaction)).thenReturn(reaction);
        when(reactionMapper.toDTO(reaction)).thenReturn(new ReactionDTO());

        // Act
        ReactionDTO savedReactionDTO = postService.createReactionForPost(1L, reactionDTO, userDetails);

        // Assert
        assertNotNull(savedReactionDTO);
        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(postReactionRepository, times(1)).findByUserAndTypeAndPost(user, ReactionType.LIKE, post);
        verify(reactionMapper, times(1)).toPostReactionEntity(reactionDTO);
        verify(postReactionRepository, times(1)).save(reaction);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void createReactionForPost_ShouldThrowException_WhenReactionAlreadyExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userService.getUserById(1L)).thenReturn(user);
        when(postReactionRepository.findByUserAndTypeAndPost(user, ReactionType.LIKE, post)).thenReturn(Optional.of(reaction));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createReactionForPost(1L, reactionDTO, userDetails);
        });

        assertEquals("You have already reacted with this type to this post.", exception.getMessage());

        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(postReactionRepository, times(1)).findByUserAndTypeAndPost(user, ReactionType.LIKE, post);
        verify(postReactionRepository, times(0)).save(any(PostReaction.class));
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    void createReactionForPost_ShouldThrowException_WhenPostDoesNotExist() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createReactionForPost(1L, reactionDTO, userDetails);
        });

        assertEquals("Post not found with ID: 1", exception.getMessage());

        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(0)).getUserById(anyLong());
        verify(postReactionRepository, times(0)).findByUserAndTypeAndPost(any(User.class), any(ReactionType.class), any(Post.class));
        verify(postReactionRepository, times(0)).save(any(PostReaction.class));
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    void createReactionForPost_ShouldHandleNullReferencedGoal() {
        // Arrange
        post.setReferencedGoal(null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userService.getUserById(1L)).thenReturn(user);
        when(postReactionRepository.findByUserAndTypeAndPost(user, ReactionType.LIKE, post)).thenReturn(Optional.empty());
        when(reactionMapper.toPostReactionEntity(reactionDTO)).thenReturn(reaction);
        when(postReactionRepository.save(reaction)).thenReturn(reaction);
        when(reactionMapper.toDTO(reaction)).thenReturn(new ReactionDTO());

        // Act
        ReactionDTO savedReactionDTO = postService.createReactionForPost(1L, reactionDTO, userDetails);

        // Assert
        assertNotNull(savedReactionDTO);
        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(postReactionRepository, times(1)).findByUserAndTypeAndPost(user, ReactionType.LIKE, post);
        verify(reactionMapper, times(1)).toPostReactionEntity(reactionDTO);
        verify(postReactionRepository, times(1)).save(reaction);
        verify(postRepository, times(1)).save(post);
    }
}
