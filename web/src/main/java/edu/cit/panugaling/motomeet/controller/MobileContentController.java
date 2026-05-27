package edu.cit.panugaling.motomeet.controller;

import edu.cit.panugaling.motomeet.dto.MobileContentItem;
import edu.cit.panugaling.motomeet.model.ChatMessage;
import edu.cit.panugaling.motomeet.model.ChatThread;
import edu.cit.panugaling.motomeet.model.Comment;
import edu.cit.panugaling.motomeet.model.FeedPost;
import edu.cit.panugaling.motomeet.model.Meetup;
import edu.cit.panugaling.motomeet.model.MarketplaceItem;
import edu.cit.panugaling.motomeet.model.Notification;
import edu.cit.panugaling.motomeet.model.RideLog;
import edu.cit.panugaling.motomeet.model.User;
import edu.cit.panugaling.motomeet.repository.ChatMessageRepository;
import edu.cit.panugaling.motomeet.repository.ChatThreadRepository;
import edu.cit.panugaling.motomeet.repository.CommentRepository;
import edu.cit.panugaling.motomeet.repository.FeedPostRepository;
import edu.cit.panugaling.motomeet.repository.MarketplaceItemRepository;
import edu.cit.panugaling.motomeet.repository.MeetupRepository;
import edu.cit.panugaling.motomeet.repository.NotificationRepository;
import edu.cit.panugaling.motomeet.repository.RideLogRepository;
import edu.cit.panugaling.motomeet.repository.UserRepository;
import edu.cit.panugaling.motomeet.dto.RideForm;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/mobile")
public class MobileContentController {

    private static final DateTimeFormatter FEED_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter RIDE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter MEETUP_TIME = DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a", Locale.ENGLISH);

    private final FeedPostRepository feedPostRepository;
    private final MarketplaceItemRepository marketplaceItemRepository;
    private final RideLogRepository rideLogRepository;
    private final MeetupRepository meetupRepository;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    public MobileContentController(
            FeedPostRepository feedPostRepository,
            MarketplaceItemRepository marketplaceItemRepository,
            RideLogRepository rideLogRepository,
            MeetupRepository meetupRepository,
            NotificationRepository notificationRepository,
            CommentRepository commentRepository,
            UserRepository userRepository,
            ChatThreadRepository chatThreadRepository,
            ChatMessageRepository chatMessageRepository
                , PasswordEncoder passwordEncoder
    ) {
        this.feedPostRepository = feedPostRepository;
        this.marketplaceItemRepository = marketplaceItemRepository;
        this.rideLogRepository = rideLogRepository;
        this.meetupRepository = meetupRepository;
        this.notificationRepository = notificationRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.chatThreadRepository = chatThreadRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/feed")
    public List<MobileContentItem> feed() {
        List<MobileContentItem> items = new ArrayList<>();
        for (FeedPost post : feedPostRepository.findAllByOrderByCreatedAtDesc()) {
            items.add(mapFeedPost(post));
        }
        return items;
    }

    @PostMapping("/feed")
    public ResponseEntity<MobileContentItem> createPost(@RequestBody MobileContentItem item) {
        User actor = resolveMobileActor();

        FeedPost post = new FeedPost();
        post.setOwner(actor);
        post.setStory(normalizeText(item.getDescription(), item.getTitle(), "Shared from MotoMeet mobile"));
        post.setBikeName(normalizeText(item.getTitle(), item.getSubtitle(), "MotoMeet Ride"));
        post.setImageLeftUrl(normalizeOptionalImageUrl(item.getImageUrl()));
        post.setImageRightUrl(normalizeOptionalImageUrl(item.getSecondaryImageUrl()));
        post.setLikes(0);
        post.setCheers(0);
        post.setComments(0);
        post.setCreatedAt(LocalDateTime.now());

        FeedPost saved = feedPostRepository.save(post);
        return ResponseEntity.ok(mapFeedPost(saved));
    }

    @PostMapping("/rides")
    public ResponseEntity<MobileContentItem> createRide(@Valid @RequestBody RideForm rideForm) {
        User actor = resolveMobileActor();

        RideLog rideLog = new RideLog();
        rideLog.setOwner(actor);
        rideLog.setTitle(normalizeText(rideForm.getTitle(), "MotoMeet Ride", "MotoMeet Ride"));
        rideLog.setRoute(normalizeText(rideForm.getRoute(), "Local route", "Local route"));
        rideLog.setDistanceMiles(rideForm.getDistanceMiles());
        rideLog.setDurationMinutes(rideForm.getDurationMinutes());
        rideLog.setAvgSpeedMph(rideForm.getAvgSpeedMph());
        rideLog.setRideDate(rideForm.getRideDate());
        rideLog.setImageUrl(normalizeOptionalImageUrl(rideForm.getImageUrl()));

        RideLog saved = rideLogRepository.save(rideLog);
        return ResponseEntity.ok(mapRideLog(saved));
    }

    @PostMapping("/feed/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        User actor = resolveMobileActor();
        FeedPost post = feedPostRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        post.setLikes((post.getLikes() == null ? 0 : post.getLikes()) + 1);
        feedPostRepository.save(post);

        if (post.getOwner() != null && !post.getOwner().getId().equals(actor.getId())) {
            notificationRepository.save(new Notification("post_like", actor.getFirstname() + " liked your post.", post.getOwner()));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feed/{id}/comment")
    public ResponseEntity<Void> commentPost(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User actor = resolveMobileActor();
        FeedPost post = feedPostRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        String message = normalizeText(body.get("message"), "", "");
        if (message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Comment comment = new Comment();
        comment.setAuthor(actor);
        comment.setPost(post);
        comment.setMessage(message);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        post.setComments((post.getComments() == null ? 0 : post.getComments()) + 1);
        feedPostRepository.save(post);

        if (post.getOwner() != null && !post.getOwner().getId().equals(actor.getId())) {
            notificationRepository.save(new Notification("post_comment", actor.getFirstname() + " commented on your post.", post.getOwner()));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feed/{id}/share")
    public ResponseEntity<Void> sharePost(@PathVariable Long id) {
        User actor = resolveMobileActor();
        FeedPost post = feedPostRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        post.setCheers((post.getCheers() == null ? 0 : post.getCheers()) + 1);
        feedPostRepository.save(post);

        if (post.getOwner() != null && !post.getOwner().getId().equals(actor.getId())) {
            notificationRepository.save(new Notification("post_share", actor.getFirstname() + " shared your post.", post.getOwner()));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/marketplace")
    public List<MobileContentItem> marketplace() {
        List<MarketplaceItem> items = marketplaceItemRepository.findByStatusWithSellerOrderByCreatedAtDesc("available");
        return items.stream()
                .map(this::mapMarketplaceItem)
                .toList();
    }

    @GetMapping("/marketplace/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<MarketplaceItem> getMarketplaceItem(@PathVariable Long id) {
        return marketplaceItemRepository.findByIdWithSeller(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rides")
    public List<MobileContentItem> rides() {
        return rideLogRepository.findAll().stream()
                .sorted(Comparator.comparing(RideLog::getRideDate).reversed())
                .map(this::mapRideLog)
                .toList();
    }

    @GetMapping("/meetups")
    public List<MobileContentItem> meetups() {
        return meetupRepository.findAll().stream()
                .sorted(Comparator.comparing(Meetup::getMeetupDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Meetup::getMeetupTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::mapMeetup)
                .toList();
    }

    @GetMapping("/notifications")
    public List<MobileContentItem> notifications() {
        return notificationRepository.findAll().stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::mapNotification)
                .toList();
    }

    @GetMapping("/user/me")
    public User currentUser() {
        return resolveMobileActor();
    }

    @GetMapping("/profile")
    public List<MobileContentItem> profile() {
        List<MobileContentItem> items = new ArrayList<>();
        MobileContentItem overview = new MobileContentItem();
        overview.setId(0L);
        overview.setSection("profile");
        overview.setBadge("Community Overview");
        overview.setTitle("MotoMeet Community");
        overview.setSubtitle("Live data from the same backend powering the web app");
        overview.setDescription("Members: " + userRepository.count()
                + " | Posts: " + feedPostRepository.count()
                + " | Rides: " + rideLogRepository.count()
                + " | Meetups: " + meetupRepository.count()
                + " | Listings: " + marketplaceItemRepository.count());
        overview.setMetaLeft("Supabase-backed");
        overview.setMetaRight("Updated live");
        items.add(overview);

        feedPostRepository.findAllByOrderByCreatedAtDesc().stream().limit(3).forEach(post -> items.add(mapFeedPost(post)));
        rideLogRepository.findAll().stream()
                .sorted(Comparator.comparing(RideLog::getRideDate).reversed())
                .limit(2)
                .forEach(ride -> items.add(mapRideLog(ride)));
        meetupRepository.findAll().stream()
                .sorted(Comparator.comparing(Meetup::getMeetupDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Meetup::getMeetupTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(2)
                .forEach(meetup -> items.add(mapMeetup(meetup)));
        return items;
    }

    private MobileContentItem mapFeedPost(FeedPost post) {
        MobileContentItem item = new MobileContentItem();
        item.setId(post.getId());
        item.setSection("feed");
        item.setBadge("Feed");
        item.setTitle(post.getBikeName());
        item.setSubtitle(post.getOwner() == null ? "MotoMeet rider" : safeName(post.getOwner().getFirstname(), post.getOwner().getLastname()));
        item.setDescription(post.getStory());
        item.setImageUrl(post.getImageLeftUrl());
        item.setSecondaryImageUrl(post.getImageRightUrl());
        item.setMetaLeft((post.getLikes() == null ? 0 : post.getLikes()) + " likes");
        item.setMetaRight((post.getComments() == null ? 0 : post.getComments()) + " comments");
        item.setTimestamp(post.getCreatedAt() == null ? "" : FEED_TIME.format(post.getCreatedAt()));
        return item;
    }

    private MobileContentItem mapMarketplaceItem(MarketplaceItem marketplaceItem) {
        MobileContentItem item = new MobileContentItem();
        item.setId(marketplaceItem.getId());
        item.setSection("marketplace");
        item.setBadge(marketplaceItem.getCategory());
        item.setTitle(marketplaceItem.getTitle());
        item.setSubtitle(marketplaceItem.getSeller() == null ? "MotoMeet seller" : safeName(marketplaceItem.getSeller().getFirstname(), marketplaceItem.getSeller().getLastname()));
        item.setDescription(marketplaceItem.getDescription());
        item.setImageUrl(marketplaceItem.getImageUrl());
        item.setMetaLeft(String.format(Locale.ENGLISH, "PHP %.0f", marketplaceItem.getPrice()));
        item.setMetaRight(marketplaceItem.getStatus());
        item.setTimestamp(marketplaceItem.getCreatedAt() == null ? "" : FEED_TIME.format(marketplaceItem.getCreatedAt()));
        return item;
    }

    private MobileContentItem mapRideLog(RideLog rideLog) {
        MobileContentItem item = new MobileContentItem();
        item.setId(rideLog.getId());
        item.setSection("rides");
        item.setBadge("Ride");
        item.setTitle(rideLog.getTitle());
        item.setSubtitle(rideLog.getRoute());
        item.setDescription(rideLog.getDistanceMiles() + " miles • " + rideLog.getDurationMinutes() + " min • " + rideLog.getAvgSpeedMph() + " mph");
        item.setImageUrl(rideLog.getImageUrl());
        item.setMetaLeft(rideLog.getRideDate() == null ? "" : RIDE_TIME.format(rideLog.getRideDate()));
        item.setMetaRight("Ride log");
        return item;
    }

    private MobileContentItem mapMeetup(Meetup meetup) {
        MobileContentItem item = new MobileContentItem();
        item.setId(meetup.getId());
        item.setSection("meetups");
        item.setBadge("Meetup");
        item.setTitle(meetup.getTitle());
        item.setSubtitle(meetup.getLocation());
        item.setDescription(meetup.getDescription());
        item.setImageUrl(meetup.getImageUrl());
        item.setMetaLeft(meetup.getMeetupDate() == null || meetup.getMeetupTime() == null ? "" : MEETUP_TIME.format(java.time.LocalDateTime.of(meetup.getMeetupDate(), meetup.getMeetupTime())));
        item.setMetaRight((meetup.getGoingCount() == null ? 0 : meetup.getGoingCount()) + " going");
        return item;
    }

    private MobileContentItem mapNotification(Notification notification) {
        MobileContentItem item = new MobileContentItem();
        item.setId(notification.getId());
        item.setSection("notifications");
        item.setBadge(notification.getType());
        item.setTitle(notification.getMessage());
        item.setSubtitle(notification.getIsRead() != null && notification.getIsRead() ? "Read" : "Unread");
        item.setDescription(notification.getCreatedAt() == null ? "" : FEED_TIME.format(notification.getCreatedAt()));
        item.setMetaLeft(notification.getType());
        item.setMetaRight(notification.getIsRead() != null && notification.getIsRead() ? "seen" : "new");
        return item;
    }

    private String safeName(String firstname, String lastname) {
        String first = firstname == null ? "" : firstname.trim();
        String last = lastname == null ? "" : lastname.trim();
        if (first.isEmpty() && last.isEmpty()) {
            return "MotoMeet rider";
        }
        return (first + " " + last).trim();
    }

    private User resolveMobileActor() {
        return userRepository.findAll().stream().findFirst().orElseGet(() -> {
            User created = new User();
            created.setEmail("mobile.user@motomeet.local");
            created.setFirstname("Moto");
            created.setLastname("Mobile");
            created.setPassword(passwordEncoder.encode("Password123!"));
            return userRepository.save(created);
        });
    }

    // Mobile chat endpoints
    @GetMapping("/chat/messages/{recipientId}/{itemId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getChatMessagesForItem(@PathVariable Long recipientId, @PathVariable Long itemId) {
        User current = resolveMobileActor();
        MarketplaceItem item = marketplaceItemRepository.findByIdWithSeller(itemId).orElse(null);
        if (item == null || item.getSeller() == null) {
            return ResponseEntity.notFound().build();
        }

        // Ensure recipient matches item seller
        if (!item.getSeller().getId().equals(recipientId)) {
            return ResponseEntity.badRequest().build();
        }

        if (item.getSeller().getId().equals(current.getId())) {
            ChatThread thread = chatThreadRepository.findTopByMarketplaceItemOrderByCreatedAtDesc(item).orElse(null);
            if (thread == null) {
                Map<String, Object> payload = new java.util.LinkedHashMap<>();
                payload.put("thread_id", null);
                payload.put("item_title", item.getTitle());
                payload.put("opponent_id", item.getSeller().getId());
                payload.put("opponent_name", safeName(item.getSeller().getFirstname(), item.getSeller().getLastname()));
                payload.put("messages", List.of());
                return ResponseEntity.ok(payload);
            }
            return ResponseEntity.ok(buildMobileChatPayload(thread, current));
        }

        ChatThread thread = chatThreadRepository.findByMarketplaceItemAndBuyerAndSeller(item, current, item.getSeller()).orElse(null);
        if (thread == null) {
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("thread_id", null);
            payload.put("item_title", item.getTitle());
            payload.put("opponent_id", item.getSeller().getId());
            payload.put("opponent_name", safeName(item.getSeller().getFirstname(), item.getSeller().getLastname()));
            payload.put("messages", List.of());
            return ResponseEntity.ok(payload);
        }

        return ResponseEntity.ok(buildMobileChatPayload(thread, current));
    }

    @PostMapping("/chat/messages")
    @Transactional
    public ResponseEntity<?> sendChatMessage(@RequestBody Map<String, Object> body) {
        User current = resolveMobileActor();
        String content = body.getOrDefault("content", "").toString().trim();
        Long threadId = body.get("thread_id") == null ? null : Long.valueOf(body.get("thread_id").toString());
        Long recipientId = body.get("recipient_id") == null ? null : Long.valueOf(body.get("recipient_id").toString());
        Long itemId = body.get("item_id") == null ? null : Long.valueOf(body.get("item_id").toString());

        if (content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ChatThread thread = null;
        if (threadId != null) {
            thread = chatThreadRepository.findById(threadId).orElse(null);
        } else if (itemId != null && recipientId != null) {
            MarketplaceItem item = marketplaceItemRepository.findByIdWithSeller(itemId).orElse(null);
            if (item == null || item.getSeller() == null || !item.getSeller().getId().equals(recipientId)) {
                return ResponseEntity.badRequest().build();
            }
            thread = chatThreadRepository.findByMarketplaceItemAndBuyerAndSeller(item, current, item.getSeller())
                    .orElseGet(() -> chatThreadRepository.save(new ChatThread(item, current, item.getSeller())));
        } else {
            return ResponseEntity.badRequest().build();
        }

        ChatMessage message = new ChatMessage();
        message.setThread(thread);
        message.setSender(current);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        ChatMessage saved = chatMessageRepository.save(message);

        Map<String, Object> payload = Map.of(
                "id", saved.getId(),
                "content", saved.getContent(),
                "sender_id", saved.getSender().getId(),
                "thread_id", saved.getThread().getId(),
                "created_at", saved.getCreatedAt().toString(),
                "sender", Map.of(
                        "id", saved.getSender().getId(),
                        "firstname", saved.getSender().getFirstname(),
                        "lastname", saved.getSender().getLastname(),
                        "email", saved.getSender().getEmail()
                )
        );

        User recipient = null;
        if (thread.getBuyer().getId().equals(current.getId())) recipient = thread.getSeller(); else recipient = thread.getBuyer();
        notificationRepository.save(new Notification(
                "chat_message",
                safeName(current.getFirstname(), current.getLastname()) + " sent you a message about " + thread.getMarketplaceItem().getTitle(),
                recipient
        ));

        return ResponseEntity.ok(payload);
    }

    private Map<String, Object> buildMobileChatPayload(ChatThread thread, User viewer) {
        List<Map<String, Object>> messages = chatMessageRepository.findByThreadOrderByCreatedAtAsc(thread).stream()
                .map(message -> Map.<String, Object>of(
                        "id", message.getId(),
                        "content", message.getContent(),
                        "sender_id", message.getSender().getId(),
                        "thread_id", thread.getId(),
                        "created_at", message.getCreatedAt().toString(),
                        "sender", Map.of(
                                "id", message.getSender().getId(),
                                "firstname", message.getSender().getFirstname(),
                                "lastname", message.getSender().getLastname(),
                                "email", message.getSender().getEmail()
                        )
                ))
                .toList();

        User opponent = thread.getBuyer().getId().equals(viewer.getId()) ? thread.getSeller() : thread.getBuyer();
        return Map.of(
                "thread_id", thread.getId(),
                "item_title", thread.getMarketplaceItem().getTitle(),
                "opponent_id", opponent.getId(),
                "opponent_name", safeName(opponent.getFirstname(), opponent.getLastname()),
                "messages", messages
        );
    }

    private String normalizeText(String preferred, String fallback, String defaultValue) {
        String value = preferred != null && !preferred.trim().isEmpty() ? preferred.trim() : null;
        if (value == null && fallback != null && !fallback.trim().isEmpty()) {
            value = fallback.trim();
        }
        return value == null ? defaultValue : value;
    }

    private static final int MAX_IMAGE_URL_LENGTH = 255;

    private String normalizeOptionalImageUrl(String candidate) {
        if (candidate == null || candidate.trim().isEmpty()) {
            return null;
        }

        String value = candidate.trim();
        if (value.startsWith("data:") || value.length() > MAX_IMAGE_URL_LENGTH) {
            return null;
        }

        return value;
    }
}
