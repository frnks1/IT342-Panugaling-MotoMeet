package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.ChatThread;
import edu.cit.panugaling.motomeet.model.MarketplaceItem;
import edu.cit.panugaling.motomeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    Optional<ChatThread> findByMarketplaceItemAndBuyerAndSeller(MarketplaceItem marketplaceItem, User buyer, User seller);
    Optional<ChatThread> findTopByMarketplaceItemOrderByCreatedAtDesc(MarketplaceItem marketplaceItem);
}