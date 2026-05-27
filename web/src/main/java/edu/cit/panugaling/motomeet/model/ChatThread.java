package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_threads", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"marketplace_item_id", "buyer_id", "seller_id"})
})
public class ChatThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_item_id", nullable = false)
    private MarketplaceItem marketplaceItem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ChatThread() {
    }

    public ChatThread(MarketplaceItem marketplaceItem, User buyer, User seller) {
        this.marketplaceItem = marketplaceItem;
        this.buyer = buyer;
        this.seller = seller;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MarketplaceItem getMarketplaceItem() {
        return marketplaceItem;
    }

    public void setMarketplaceItem(MarketplaceItem marketplaceItem) {
        this.marketplaceItem = marketplaceItem;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}