package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.ChatMessage;
import edu.cit.panugaling.motomeet.model.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByThreadOrderByCreatedAtAsc(ChatThread thread);
}