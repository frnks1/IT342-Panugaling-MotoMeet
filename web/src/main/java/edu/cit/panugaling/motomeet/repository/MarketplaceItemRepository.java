package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.MarketplaceItem;
import edu.cit.panugaling.motomeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceItemRepository extends JpaRepository<MarketplaceItem, Long> {
    List<MarketplaceItem> findBySellerOrderByCreatedAtDesc(User seller);
    List<MarketplaceItem> findByStatusOrderByCreatedAtDesc(String status);
    List<MarketplaceItem> findByCategory(String category);
    
    @Query("SELECT m FROM MarketplaceItem m LEFT JOIN FETCH m.seller WHERE m.id = :id")
    Optional<MarketplaceItem> findByIdWithSeller(@Param("id") Long id);
    
    @Query("SELECT m FROM MarketplaceItem m LEFT JOIN FETCH m.seller WHERE m.status = :status ORDER BY m.createdAt DESC")
    List<MarketplaceItem> findByStatusWithSellerOrderByCreatedAtDesc(@Param("status") String status);
}
