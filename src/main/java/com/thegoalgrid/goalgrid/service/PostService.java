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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (postDTO.getReferencedGoalId() != null) {
            Goal goal = goalRepository.findById(postDTO.getReferencedGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found with id: " + postDTO.getReferencedGoalId()));
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
        if (postDTO.getReferencedGoalId() != null) {
            Goal goal = goalRepository.findById(postDTO.getReferencedGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found with id: " + postDTO.getReferencedGoalId()));
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
     * Retrieve all comments for a given post.
     */
    public List<CommentDTO> getAllCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPost_Id(postId);
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
     * Retrieve the main feed.
     *
     * This implementation gathers all groups in which the current user is a member,
     * then collects all user IDs (including the current user) that belong to those groups.
     * Finally, it retrieves posts authored by any of those users.
     *
     * @param userDetails The current logged-in user.
     * @return A list of PostDTOs for the main feed.
     */
    public List<PostDTO> getMainFeed(UserDetailsImpl userDetails) {
        User currentUser = userService.getUserById(userDetails.getId());
        // Get the groups the current user belongs to
        Set<Group> userGroups = currentUser.getGroups();

        // Collect all user IDs from these groups
        Set<Long> userIds = userGroups.stream()
                .flatMap(group -> group.getUsers().stream())
                .map(User::getId)
                .collect(Collectors.toSet());
        // Ensure the current user's own posts are included
        userIds.add(currentUser.getId());

        // Retrieve posts for the collected user IDs
        List<Post> posts = postRepository.findByAuthor_IdIn(new ArrayList<>(userIds));
        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve posts for a specific group.
     *
     * This implementation finds the group by its unique URL, collects its members'
     * user IDs, and then retrieves posts authored by these users.
     *
     * @param groupUrl The unique URL of the group.
     * @return A list of PostDTOs for the group feed.
     */
    public List<PostDTO> getGroupFeed(String groupUrl) {
        Group group = groupRepository.findByUniqueUrl(groupUrl)
                .orElseThrow(() -> new RuntimeException("Group not found with URL: " + groupUrl));

        // Collect user IDs from the group
        Set<Long> userIds = group.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<Post> posts = postRepository.findByAuthor_IdIn(new ArrayList<>(userIds));
        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve posts for a goal.
     * Only returns posts that reference the given goal.
     *
     * @param goalId The ID of the goal.
     * @return A list of PostDTOs for the goal feed.
     */
    public List<PostDTO> getGoalFeed(Long goalId) {
        List<Post> posts = postRepository.findByReferencedGoal_Id(goalId);
        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }
}
