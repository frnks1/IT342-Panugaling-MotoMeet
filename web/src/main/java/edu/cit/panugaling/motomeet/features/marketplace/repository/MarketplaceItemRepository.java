package edu.cit.panugaling.motomeet.features.marketplace.repository;

import edu.cit.panugaling.motomeet.features.marketplace.model.MarketplaceItem;
import edu.cit.panugaling.motomeet.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceItemRepository extends JpaRepository<MarketplaceItem, Long> {
    List<MarketplaceItem> findBySellerOrderByCreatedAtDesc(User seller);
    List<MarketplaceItem> findByStatusOrderByCreatedAtDesc(String status);
    List<MarketplaceItem> findByCategory(String category);
}
