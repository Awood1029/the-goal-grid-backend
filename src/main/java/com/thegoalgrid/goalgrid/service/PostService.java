package com.thegoalgrid.goalgrid.service;

import com.thegoalgrid.goalgrid.dto.social.CommentDTO;
import com.thegoalgrid.goalgrid.dto.social.PostDTO;
import com.thegoalgrid.goalgrid.dto.social.ReactionDTO;
import com.thegoalgrid.goalgrid.entity.*;
import com.thegoalgrid.goalgrid.mapper.CommentMapper;
import com.thegoalgrid.goalgrid.mapper.PostMapper;
import com.thegoalgrid.goalgrid.mapper.ReactionMapper;
import com.thegoalgrid.goalgrid.repository.*;
import com.thegoalgrid.goalgrid.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final CommentRepository commentRepository;
    private final GoalRepository goalRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ReactionMapper reactionMapper;
    private final UserService userService;

    /**
     * Create a new post.
     */
    @Transactional
    public PostDTO createPost(PostDTO postDTO, UserDetailsImpl userDetails) {
        Post post = postMapper.toEntity(postDTO);
        // Set the post author from the authenticated user.
        User author = userService.getUserById(userDetails.getId());
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        // If a referencedGoalId is provided, load the Goal and assign it.
        if (postDTO.getReferencedGoal() != null) {
            Goal goal = goalRepository.findById(postDTO.getReferencedGoal().getId())
                    .orElseThrow(() -> new RuntimeException("Goal not found with id: " + postDTO.getReferencedGoal().getId()));
            post.setReferencedGoal(goal);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    /**
     * Retrieve a post by its ID.
     */
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        return postMapper.toDTO(post);
    }

    /**
     * Update a post.
     * Only the post author can update.
     */
    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Check if the authenticated user is the author
        if (!post.getAuthor().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Unauthorized to update this post.");
        }

        // Update allowed fields
        if (postDTO.getContent() != null) {
            post.setContent(postDTO.getContent());
        }

        if (postDTO.getReferencedGoal().getId() != null) {
            Goal goal = goalRepository.findById(postDTO.getReferencedGoal().getId())
                    .orElseThrow(() -> new RuntimeException("Goal not found with id: " + postDTO.getReferencedGoal().getId()));
            post.setReferencedGoal(goal);
        }
        postRepository.save(post);
        return postMapper.toDTO(post);
    }

    /**
     * Delete a post.
     * Only the post author can delete.
     */
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Check if the authenticated user is the author
        if (!post.getAuthor().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Unauthorized to delete this post.");
        }

        postRepository.delete(post);
    }

    /**
     * Retrieve all comments for a specific post with sorting.
     */
    public List<CommentDTO> getAllCommentsForPost(Long postId, String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, sortBy);

        List<Comment> comments = commentRepository.findByPost_Id(postId, sort);
        return comments.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new comment on a post.
     * The author is set from the authenticated user.
     */
    @Transactional
    public CommentDTO createCommentForPost(Long postId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        Comment comment = commentMapper.toEntity(commentDTO);
        User author = userService.getUserById(userDetails.getId());
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    /**
     * Create a reaction for a specific post.
     * This method will be updated in the next section to include reaction restrictions.
     */
    @Transactional
    public ReactionDTO createReactionForPost(Long postId, ReactionDTO reactionDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        User user = userService.getUserById(userDetails.getId());

        // Check if the user has already reacted with this type to this post
        Optional<PostReaction> existingReaction = postReactionRepository.findByUserAndTypeAndPost(user, reactionDTO.getType(), post);
        if (existingReaction.isPresent()) {
            throw new RuntimeException("You have already reacted with this type to this post.");
        }

        // Create and save the new reaction
        PostReaction reaction = reactionMapper.toPostReactionEntity(reactionDTO);
        reaction.setUser(user);
        reaction.setPost(post);
        PostReaction savedReaction = postReactionRepository.save(reaction);

        // Add the reaction to the post's reactions
        post.getPostReactions().add(savedReaction);
        postRepository.save(post);

        return reactionMapper.toDTO(savedReaction);
    }

    /**
     * Remove a reaction for a specific post.
     */
    @Transactional
    public void removeReactionForPost(Long postId, ReactionType type, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        User user = userService.getUserById(userDetails.getId());

        // Find the existing reaction
        PostReaction reaction = postReactionRepository.findByUserAndTypeAndPost(user, type, post)
                .orElseThrow(() -> new RuntimeException("Reaction not found for type: " + type));

        // Remove the reaction
        post.getPostReactions().remove(reaction);
        postReactionRepository.delete(reaction);
    }

    /**
     * Retrieve the main feed with dynamic sorting.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getMainFeed(UserDetailsImpl userDetails, int page, int size, String sortBy, String sortDir) {
        User currentUser = userService.getUserById(userDetails.getId());
        // Fetch posts only from friends (using the User.friends set)
        List<Long> userIds = currentUser.getFriends().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Add own users post
        userIds.add(userDetails.getId());

        // Determine sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Fetch posts where the author is one of the friends
        Page<Post> postsPage = postRepository.findByAuthor_IdIn(userIds, pageable);
        return postsPage.map(postMapper::toDTO);
    }

    /**
     * Retrieve feed for a specific group with dynamic sorting.
     */
    public Page<PostDTO> getGroupFeed(String groupUniqueCode, int page, int size, String sortBy, String sortDir) {
        Group group = groupRepository.findByUniqueUrl(groupUniqueCode)
                .orElseThrow(() -> new RuntimeException("Group not found with unique URL: " + groupUniqueCode));

        List<Long> memberIds = group.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Determine Sort direction
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        // Create Pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Fetch posts with sorting
        Page<Post> postsPage = postRepository.findByAuthor_IdIn(memberIds, pageable);

        // Map to DTOs
        return postsPage.map(postMapper::toDTO);
    }

    /**
     * Retrieve feed for a goal with dynamic sorting.
     */
    public Page<PostDTO> getGoalFeed(Long goalId, int page, int size, String sortBy, String sortDir) {
        // Determine Sort direction
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        // Create Pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Fetch posts with sorting
        Page<Post> postsPage = postRepository.findByReferencedGoal_Id(goalId, pageable);

        // Map to DTOs
        return postsPage.map(postMapper::toDTO);
    }

    /**
     * NEW: Retrieve the 3 most recent posts by a specific user.
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getRecentPostsByUser(Long userId) {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> page = postRepository.findByAuthor_Id(userId, pageable);
        return page.stream().map(postMapper::toDTO).collect(Collectors.toList());
    }
}
