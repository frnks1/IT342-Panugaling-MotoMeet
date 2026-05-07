package edu.cit.panugaling.motomeet.shared.repository;

import edu.cit.panugaling.motomeet.shared.model.Comment;
import edu.cit.panugaling.motomeet.features.feed.model.FeedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(FeedPost post);
}