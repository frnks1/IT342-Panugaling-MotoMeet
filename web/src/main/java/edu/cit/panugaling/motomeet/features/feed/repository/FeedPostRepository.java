package edu.cit.panugaling.motomeet.features.feed.repository;

import edu.cit.panugaling.motomeet.features.feed.model.FeedPost;
import edu.cit.panugaling.motomeet.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedPostRepository extends JpaRepository<FeedPost, Long> {
    List<FeedPost> findAllByOrderByCreatedAtDesc();
    List<FeedPost> findByOwnerOrderByCreatedAtDesc(User owner);
}
