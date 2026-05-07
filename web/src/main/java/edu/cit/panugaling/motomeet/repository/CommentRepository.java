package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.Comment;
import edu.cit.panugaling.motomeet.model.FeedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(FeedPost post);
}