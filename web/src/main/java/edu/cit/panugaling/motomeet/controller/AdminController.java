package edu.cit.panugaling.motomeet.controller;

import edu.cit.panugaling.motomeet.model.Comment;
import edu.cit.panugaling.motomeet.model.FeedPost;
import edu.cit.panugaling.motomeet.model.MarketplaceItem;
import edu.cit.panugaling.motomeet.model.Meetup;
import edu.cit.panugaling.motomeet.model.RideLog;
import edu.cit.panugaling.motomeet.model.User;
import edu.cit.panugaling.motomeet.repository.CommentRepository;
import edu.cit.panugaling.motomeet.repository.FeedPostRepository;
import edu.cit.panugaling.motomeet.repository.MarketplaceItemRepository;
import edu.cit.panugaling.motomeet.repository.MeetupRepository;
import edu.cit.panugaling.motomeet.repository.RideLogRepository;
import edu.cit.panugaling.motomeet.repository.UserRepository;
import edu.cit.panugaling.motomeet.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final FeedPostRepository feedPostRepository;
    private final RideLogRepository rideLogRepository;
    private final MeetupRepository meetupRepository;
    private final MarketplaceItemRepository marketplaceItemRepository;
    private final CommentRepository commentRepository;

    public AdminController(
            CurrentUserService currentUserService,
            UserRepository userRepository,
            FeedPostRepository feedPostRepository,
            RideLogRepository rideLogRepository,
            MeetupRepository meetupRepository,
            MarketplaceItemRepository marketplaceItemRepository,
            CommentRepository commentRepository
    ) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.feedPostRepository = feedPostRepository;
        this.rideLogRepository = rideLogRepository;
        this.meetupRepository = meetupRepository;
        this.marketplaceItemRepository = marketplaceItemRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Authentication authentication, Model model) {
        User admin = currentUserService.getRequiredUser(authentication);
        addCommonAdminModel(model, admin);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("feedPosts", feedPostRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("rides", rideLogRepository.findAll());
        model.addAttribute("meetups", meetupRepository.findAll());
        model.addAttribute("marketplaceItems", marketplaceItemRepository.findAll());
        model.addAttribute("commentCount", commentRepository.count());
        return "admin";
    }

    @PostMapping("/admin/users/{id}/role/{role}")
    public String updateUserRole(
            Authentication authentication,
            @PathVariable Long id,
            @PathVariable String role,
            RedirectAttributes redirectAttributes
    ) {
        currentUserService.getRequiredUser(authentication);
        userRepository.findById(id).ifPresent(user -> {
            user.setRole("ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "USER");
            userRepository.save(user);
        });
        redirectAttributes.addFlashAttribute("successMessage", "User role updated.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/feed/{id}/delete")
    public String deleteFeedPost(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        currentUserService.getRequiredUser(authentication);
        feedPostRepository.findById(id).ifPresent(post -> {
            List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
            commentRepository.deleteAll(comments);
            feedPostRepository.delete(post);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Feed post removed.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/rides/{id}/delete")
    public String deleteRide(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        currentUserService.getRequiredUser(authentication);
        rideLogRepository.findById(id).ifPresent(rideLogRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Ride log removed.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/marketplace/{id}/delete")
    public String deleteMarketplaceItem(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        currentUserService.getRequiredUser(authentication);
        marketplaceItemRepository.findById(id).ifPresent(marketplaceItemRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Marketplace item removed.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/meetups/{id}/delete")
    public String deleteMeetup(Authentication authentication, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        currentUserService.getRequiredUser(authentication);
        meetupRepository.findById(id).ifPresent(meetupRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Meetup removed.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/cleanup-images")
    public String cleanupPlaceholderImages(Authentication authentication, RedirectAttributes redirectAttributes) {
        currentUserService.getRequiredUser(authentication);
        List<FeedPost> posts = feedPostRepository.findAll();
        int changed = 0;
        for (FeedPost p : posts) {
            boolean updated = false;
            if (p.getImageLeftUrl() != null && p.getImageLeftUrl().startsWith("https://images.unsplash.com")) {
                p.setImageLeftUrl(null);
                updated = true;
            }
            if (p.getImageRightUrl() != null && p.getImageRightUrl().startsWith("https://images.unsplash.com")) {
                p.setImageRightUrl(null);
                updated = true;
            }
            if (updated) {
                feedPostRepository.save(p);
                changed++;
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", changed + " posts cleaned of placeholder images.");
        return "redirect:/admin";
    }

    private void addCommonAdminModel(Model model, User admin) {
        String firstName = admin.getFirstname() == null ? "" : admin.getFirstname().trim();
        String lastName = admin.getLastname() == null ? "" : admin.getLastname().trim();
        String initials = ((firstName.isEmpty() ? "A" : String.valueOf(firstName.charAt(0)))
                + (lastName.isEmpty() ? "D" : String.valueOf(lastName.charAt(0)))).toUpperCase();

        model.addAttribute("adminName", admin.getFirstname() + " " + admin.getLastname());
        model.addAttribute("adminEmail", admin.getEmail());
        model.addAttribute("adminInitials", initials);
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("postCount", feedPostRepository.count());
        model.addAttribute("rideCount", rideLogRepository.count());
        model.addAttribute("meetupCount", meetupRepository.count());
        model.addAttribute("marketplaceCount", marketplaceItemRepository.count());
    }
}
