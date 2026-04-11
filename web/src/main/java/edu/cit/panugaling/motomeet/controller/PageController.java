package edu.cit.panugaling.motomeet.controller;

import edu.cit.panugaling.motomeet.dto.BikeForm;
import edu.cit.panugaling.motomeet.dto.FeedPostForm;
import edu.cit.panugaling.motomeet.dto.MeetupForm;
import edu.cit.panugaling.motomeet.dto.RideForm;
import edu.cit.panugaling.motomeet.model.Bike;
import edu.cit.panugaling.motomeet.model.FeedPost;
import edu.cit.panugaling.motomeet.model.Meetup;
import edu.cit.panugaling.motomeet.model.RideLog;
import edu.cit.panugaling.motomeet.model.User;
import edu.cit.panugaling.motomeet.repository.BikeRepository;
import edu.cit.panugaling.motomeet.repository.FeedPostRepository;
import edu.cit.panugaling.motomeet.repository.MeetupRepository;
import edu.cit.panugaling.motomeet.repository.RideLogRepository;
import edu.cit.panugaling.motomeet.service.AuthenticatedUserMissingException;
import edu.cit.panugaling.motomeet.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class PageController {

    private static final int MAX_IMAGE_URL_LENGTH = 255;

    private final CurrentUserService currentUserService;
    private final BikeRepository bikeRepository;
    private final FeedPostRepository feedPostRepository;
    private final RideLogRepository rideLogRepository;
    private final MeetupRepository meetupRepository;

    public PageController(
            CurrentUserService currentUserService,
            BikeRepository bikeRepository,
            FeedPostRepository feedPostRepository,
            RideLogRepository rideLogRepository,
            MeetupRepository meetupRepository
    ) {
        this.currentUserService = currentUserService;
        this.bikeRepository = bikeRepository;
        this.feedPostRepository = feedPostRepository;
        this.rideLogRepository = rideLogRepository;
        this.meetupRepository = meetupRepository;
    }

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())
                && currentUserService.findUser(auth).isPresent()) {
            return "redirect:/feed";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())
                && currentUserService.findUser(auth).isPresent()) {
            return "redirect:/feed";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/feed")
    public String feedPage(Authentication authentication, Model model) {
        User user = currentUserService.getRequiredUser(authentication);
        setupBaseModel(model, user, "feed");
        model.addAttribute("feedPostForm", new FeedPostForm());
        model.addAttribute("posts", feedPostRepository.findByOwnerOrderByCreatedAtDesc(user));
        return "feed";
    }

    @PostMapping("/feed")
    public String createFeedPost(
            Authentication authentication,
            @Valid @ModelAttribute FeedPostForm feedPostForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = currentUserService.getRequiredUser(authentication);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Story is required and must be under 1000 characters.");
            return "redirect:/feed";
        }

        Bike activeBike = bikeRepository.findByOwnerAndActiveTrue(user).orElse(null);
        FeedPost post = new FeedPost();
        post.setOwner(user);
        post.setStory(feedPostForm.getStory().trim());
        post.setBikeName(resolveText(feedPostForm.getBikeName(), activeBike != null ? activeBike.getDisplayName() : "My Bike"));
        post.setImageLeftUrl(normalizeImageUrl(feedPostForm.getImageLeftUrl(), "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=900&q=80"));
        post.setImageRightUrl(normalizeImageUrl(feedPostForm.getImageRightUrl(), "https://images.unsplash.com/photo-1591291621164-2c6367723315?auto=format&fit=crop&w=900&q=80"));
        post.setLikes(0);
        post.setCheers(0);
        post.setComments(0);
        post.setCreatedAt(LocalDateTime.now());

        feedPostRepository.save(post);
        redirectAttributes.addFlashAttribute("successMessage", "Ride story posted successfully.");
        return "redirect:/feed";
    }

    @GetMapping("/rides")
    public String ridesPage(Authentication authentication, Model model) {
        User user = currentUserService.getRequiredUser(authentication);
        List<RideLog> rides = rideLogRepository.findByOwnerOrderByRideDateDesc(user);

        setupBaseModel(model, user, "rides");
        model.addAttribute("rideForm", new RideForm());
        model.addAttribute("rides", rides);

        int totalMiles = rides.stream().mapToInt(RideLog::getDistanceMiles).sum();
        int totalMinutes = rides.stream().mapToInt(RideLog::getDurationMinutes).sum();
        int totalElevationFeet = totalMiles * 30;

        model.addAttribute("totalMiles", totalMiles);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("totalElevationFeet", totalElevationFeet);
        return "rides";
    }

    @PostMapping("/rides")
    public String logRide(
            Authentication authentication,
            @Valid @ModelAttribute RideForm rideForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = currentUserService.getRequiredUser(authentication);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid ride details.");
            return "redirect:/rides";
        }

        RideLog rideLog = new RideLog();
        rideLog.setOwner(user);
        rideLog.setTitle(rideForm.getTitle().trim());
        rideLog.setRoute(rideForm.getRoute().trim());
        rideLog.setDistanceMiles(rideForm.getDistanceMiles());
        rideLog.setDurationMinutes(rideForm.getDurationMinutes());
        rideLog.setAvgSpeedMph(rideForm.getAvgSpeedMph());
        rideLog.setRideDate(rideForm.getRideDate());
        String normalizedRideImage = normalizeImageUrl(rideForm.getImageUrl(), "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=900&q=80");
        if (rideForm.getImageUrl() != null && !rideForm.getImageUrl().trim().isEmpty() && !rideForm.getImageUrl().trim().equals(normalizedRideImage)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image URL was too long or unsupported, so a default image was used.");
        }
        rideLog.setImageUrl(normalizedRideImage);

        rideLogRepository.save(rideLog);
        redirectAttributes.addFlashAttribute("successMessage", "Ride logged successfully.");
        return "redirect:/rides";
    }

    @GetMapping("/meetups")
    public String meetupsPage(Authentication authentication, Model model) {
        User user = currentUserService.getRequiredUser(authentication);
        setupBaseModel(model, user, "meetups");
        model.addAttribute("meetupForm", new MeetupForm());
        model.addAttribute("meetups", meetupRepository.findByOwnerOrderByMeetupDateAscMeetupTimeAsc(user));
        return "meetups";
    }

    @PostMapping("/meetups")
    public String createMeetup(
            Authentication authentication,
            @Valid @ModelAttribute MeetupForm meetupForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = currentUserService.getRequiredUser(authentication);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid meetup details.");
            return "redirect:/meetups";
        }

        Meetup meetup = new Meetup();
        meetup.setOwner(user);
        meetup.setTitle(meetupForm.getTitle().trim());
        meetup.setLocation(meetupForm.getLocation().trim());
        meetup.setMeetupDate(meetupForm.getMeetupDate());
        meetup.setMeetupTime(meetupForm.getMeetupTime());
        meetup.setDistanceMiles(meetupForm.getDistanceMiles());
        meetup.setImageUrl(normalizeImageUrl(meetupForm.getImageUrl(), "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80"));
        meetup.setGoingCount(1);

        meetupRepository.save(meetup);
        redirectAttributes.addFlashAttribute("successMessage", "Meetup created successfully.");
        return "redirect:/meetups";
    }

    @GetMapping("/garage")
    public String garagePage(Authentication authentication, Model model) {
        User user = currentUserService.getRequiredUser(authentication);
        setupBaseModel(model, user, "garage");
        model.addAttribute("bikeForm", new BikeForm());
        return "garage";
    }

    @PostMapping("/garage")
    public String addBike(
            Authentication authentication,
            @Valid @ModelAttribute BikeForm bikeForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = currentUserService.getRequiredUser(authentication);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid bike specifications.");
            return "redirect:/garage";
        }

        Bike bike = new Bike();
        bike.setOwner(user);
        bike.setDisplayName(bikeForm.getDisplayName().trim());
        bike.setModelYear(bikeForm.getModelYear());
        bike.setEngineCc(bikeForm.getEngineCc());
        bike.setPowerHp(bikeForm.getPowerHp());
        bike.setWeightKg(bikeForm.getWeightKg());
        bike.setTopSpeedKph(bikeForm.getTopSpeedKph());
        bike.setActive(bikeRepository.findByOwnerAndActiveTrue(user).isEmpty());

        bikeRepository.save(bike);
        redirectAttributes.addFlashAttribute("successMessage", "Bike added to your garage.");
        return "redirect:/garage";
    }

    @PostMapping("/garage/{id}/activate")
    public String activateBike(
            Authentication authentication,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        User user = currentUserService.getRequiredUser(authentication);
        List<Bike> bikes = bikeRepository.findByOwnerOrderByActiveDescDisplayNameAsc(user);

        Bike selected = bikes.stream()
                .filter(bike -> bike.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bike not found.");
            return "redirect:/garage";
        }

        for (Bike bike : bikes) {
            bike.setActive(bike.getId().equals(id));
        }
        bikeRepository.saveAll(bikes);

        redirectAttributes.addFlashAttribute("successMessage", "Active bike updated.");
        return "redirect:/garage";
    }

    private void setupBaseModel(Model model, User user, String activeTab) {
        ensureDemoData(user);

        List<Bike> bikes = bikeRepository.findByOwnerOrderByActiveDescDisplayNameAsc(user);
        Bike activeBike = bikes.stream().filter(Bike::isActive).findFirst().orElse(null);

        if (activeBike == null && !bikes.isEmpty()) {
            activeBike = bikes.get(0);
            activeBike.setActive(true);
            bikeRepository.save(activeBike);
        }

        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastname() == null ? "" : user.getLastname().trim();
        String initials = ((firstName.isEmpty() ? "R" : String.valueOf(firstName.charAt(0)))
                + (lastName.isEmpty() ? "D" : String.valueOf(lastName.charAt(0)))).toUpperCase();

        int followers = 1200 + rideLogRepository.findByOwnerOrderByRideDateDesc(user).size() * 24;
        int following = 300 + meetupRepository.findByOwnerOrderByMeetupDateAscMeetupTimeAsc(user).size() * 7;

        model.addAttribute("activeTab", activeTab);
        model.addAttribute("fullName", user.getFirstname() + " " + user.getLastname());
        model.addAttribute("username", "@" + user.getFirstname().toLowerCase() + "." + user.getLastname().toLowerCase());
        model.addAttribute("avatarInitials", initials);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        model.addAttribute("bikes", bikes);
        model.addAttribute("activeBike", activeBike);
    }

    private void ensureDemoData(User user) {
        if (bikeRepository.findByOwnerOrderByActiveDescDisplayNameAsc(user).isEmpty()) {
            Bike bikeOne = new Bike();
            bikeOne.setOwner(user);
            bikeOne.setDisplayName("Ducati Monster 937");
            bikeOne.setModelYear(2022);
            bikeOne.setEngineCc(937);
            bikeOne.setPowerHp(111);
            bikeOne.setWeightKg(188);
            bikeOne.setTopSpeedKph(220);
            bikeOne.setActive(true);

            Bike bikeTwo = new Bike();
            bikeTwo.setOwner(user);
            bikeTwo.setDisplayName("Kawasaki Z900");
            bikeTwo.setModelYear(2021);
            bikeTwo.setEngineCc(948);
            bikeTwo.setPowerHp(125);
            bikeTwo.setWeightKg(212);
            bikeTwo.setTopSpeedKph(235);
            bikeTwo.setActive(false);

            bikeRepository.saveAll(List.of(bikeOne, bikeTwo));
        }

        if (rideLogRepository.findByOwnerOrderByRideDateDesc(user).isEmpty()) {
            RideLog rideOne = new RideLog();
            rideOne.setOwner(user);
            rideOne.setTitle("Mulholland Sunday Morning");
            rideOne.setRoute("Calabasas -> Mulholland -> Malibu -> PCH");
            rideOne.setDistanceMiles(87);
            rideOne.setDurationMinutes(135);
            rideOne.setAvgSpeedMph(42);
            rideOne.setRideDate(LocalDate.now().minusDays(9));
            rideOne.setImageUrl("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80");

            RideLog rideTwo = new RideLog();
            rideTwo.setOwner(user);
            rideTwo.setTitle("Angeles Crest Run");
            rideTwo.setRoute("La Canada -> ACH -> Wrightwood -> Big Pines");
            rideTwo.setDistanceMiles(124);
            rideTwo.setDurationMinutes(220);
            rideTwo.setAvgSpeedMph(38);
            rideTwo.setRideDate(LocalDate.now().minusDays(17));
            rideTwo.setImageUrl("https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=1200&q=80");

            rideLogRepository.saveAll(List.of(rideOne, rideTwo));
        }

        if (meetupRepository.findByOwnerOrderByMeetupDateAscMeetupTimeAsc(user).isEmpty()) {
            Meetup meetupOne = new Meetup();
            meetupOne.setOwner(user);
            meetupOne.setTitle("La Cafe Racer Sunday Roll");
            meetupOne.setLocation("Griffith Park, Los Angeles");
            meetupOne.setMeetupDate(LocalDate.now().plusDays(4));
            meetupOne.setMeetupTime(LocalTime.of(8, 0));
            meetupOne.setDistanceMiles(4.2);
            meetupOne.setImageUrl("https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=1200&q=80");
            meetupOne.setGoingCount(3);

            Meetup meetupTwo = new Meetup();
            meetupTwo.setOwner(user);
            meetupTwo.setTitle("Pacific Coast Sunset Ride");
            meetupTwo.setLocation("Santa Monica Pier");
            meetupTwo.setMeetupDate(LocalDate.now().plusDays(11));
            meetupTwo.setMeetupTime(LocalTime.of(17, 30));
            meetupTwo.setDistanceMiles(8.7);
            meetupTwo.setImageUrl("https://images.unsplash.com/photo-1511882150382-421056c89033?auto=format&fit=crop&w=1200&q=80");
            meetupTwo.setGoingCount(7);

            meetupRepository.saveAll(List.of(meetupOne, meetupTwo));
        }

        if (feedPostRepository.findByOwnerOrderByCreatedAtDesc(user).isEmpty()) {
            FeedPost postOne = new FeedPost();
            postOne.setOwner(user);
            postOne.setStory("Perfect morning at Mulholland Drive. Zero traffic, golden hour light, and endless corners. This is why we ride.");
            postOne.setBikeName("Honda CBR 600RR");
            postOne.setImageLeftUrl("https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=1200&q=80");
            postOne.setImageRightUrl("https://images.unsplash.com/photo-1558981359-219d6364c9c8?auto=format&fit=crop&w=1200&q=80");
            postOne.setLikes(124);
            postOne.setCheers(58);
            postOne.setComments(23);
            postOne.setCreatedAt(LocalDateTime.now().minusHours(2));

            FeedPost postTwo = new FeedPost();
            postTwo.setOwner(user);
            postTwo.setStory("Just got the new bike home and already planning the next canyon run. Engine note is unreal.");
            postTwo.setBikeName("Harley Sportster S");
            postTwo.setImageLeftUrl("https://images.unsplash.com/photo-1558618666-fcd25c85cd64?auto=format&fit=crop&w=1200&q=80");
            postTwo.setImageRightUrl("https://images.unsplash.com/photo-1471478331149-c72f17e33c73?auto=format&fit=crop&w=1200&q=80");
            postTwo.setLikes(96);
            postTwo.setCheers(37);
            postTwo.setComments(12);
            postTwo.setCreatedAt(LocalDateTime.now().minusHours(5));

            feedPostRepository.saveAll(List.of(postOne, postTwo));
        }
    }

    private String resolveText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private String normalizeImageUrl(String candidate, String fallback) {
        if (candidate == null || candidate.trim().isEmpty()) {
            return fallback;
        }

        String value = candidate.trim();

        // Prevent oversized data URLs from causing SQL truncation errors.
        if (value.startsWith("data:") || value.length() > MAX_IMAGE_URL_LENGTH) {
            return fallback;
        }

        return value;
    }

    @ExceptionHandler(AuthenticatedUserMissingException.class)
    public String handleMissingAuthenticatedUser(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return "redirect:/login?expired=true";
    }
}
