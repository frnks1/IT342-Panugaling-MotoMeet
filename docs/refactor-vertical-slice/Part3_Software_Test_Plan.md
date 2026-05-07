# Part 3 Software Test Plan - MotoMeet Application

## Document Information
- **Project:** MotoMeet - Motorcycle Community Platform
- **Version:** 1.0.0
- **Date:** [Current Date]
- **Prepared By:** Development Team
- **Reviewed By:** QA Team
- **Approved By:** Project Manager

## 1. Introduction

### 1.1 Purpose
This Software Test Plan (STP) outlines the testing approach, scope, resources, and schedule for the MotoMeet application. The plan ensures comprehensive coverage of all functional requirements and validates the quality of the vertical slice architecture implementation.

### 1.2 Scope
The testing scope includes all features implemented in the MotoMeet application:
- User Authentication and Registration
- Social Feed with Posts and Comments
- Bike Garage Management
- Ride Logging and Tracking
- Meetup Organization
- Marketplace for Item Trading
- Notification System

### 1.3 Test Objectives
- Verify all functional requirements are implemented correctly
- Ensure the vertical slice architecture maintains system integrity
- Validate cross-feature interactions and data consistency
- Confirm performance and usability standards
- Identify and document any defects or issues

### 1.4 Assumptions and Constraints
- Testing environment matches production specifications
- Test data is available and realistic
- Application is deployed and accessible
- Stakeholders are available for clarification and approval

## 2. Test Approach

### 2.1 Testing Levels
1. **Unit Testing:** Individual components and methods
2. **Integration Testing:** Feature interactions and API endpoints
3. **System Testing:** End-to-end user workflows
4. **Acceptance Testing:** Business requirement validation

### 2.2 Testing Types
- **Functional Testing:** Feature functionality validation
- **Non-Functional Testing:** Performance, security, usability
- **Regression Testing:** Existing functionality preservation
- **Exploratory Testing:** Unscripted testing for edge cases

### 2.3 Test Environment
- **Operating System:** Windows 10/11
- **Browser:** Chrome 120+, Firefox 115+, Edge 120+
- **Database:** H2 Database (development), PostgreSQL/MySQL (production)
- **Java Version:** 17+
- **Framework:** Spring Boot 3.x

## 3. Functional Requirements Coverage

### Important Note: Scope Limitation
This test plan covers **only implemented features**. The following advanced features mentioned in some documentation are **NOT implemented** and have been excluded:
- Password reset functionality
- GPS route tracking for rides
- Real-time notification updates
- Price negotiation in marketplace
- Like/unlike posts in feed

Only basic CRUD operations and core functionality are tested to ensure accurate representation of the actual application capabilities.

### 3.1 Authentication Feature
**Requirements:**
- REQ-AUTH-001: User registration with email and password
- REQ-AUTH-002: User login with valid credentials
- REQ-AUTH-003: Password validation (minimum 8 characters)
- REQ-AUTH-004: Email format validation
- REQ-AUTH-005: Session management and logout

### 3.2 Feed Feature
**Requirements:**
- REQ-FEED-001: Create and publish feed posts
- REQ-FEED-002: Display posts in chronological order
- REQ-FEED-003: Add comments to posts
- REQ-FEED-004: View comment threads
- REQ-FEED-005: Edit/delete own posts and comments

### 3.3 Garage Feature
**Requirements:**
- REQ-GARAGE-001: Add bikes to personal garage
- REQ-GARAGE-002: View bike collection
- REQ-GARAGE-003: Update bike information
- REQ-GARAGE-004: Remove bikes from garage
- REQ-GARAGE-005: Bike photo upload
- REQ-GARAGE-006: Bike specifications tracking

### 3.4 Rides Feature
**Requirements:**
- REQ-RIDES-001: Log new rides with details
- REQ-RIDES-002: View ride history
- REQ-RIDES-003: Calculate ride statistics
- REQ-RIDES-004: Filter rides by date/location
- REQ-RIDES-006: Ride duration and distance logging

### 3.5 Meetups Feature
**Requirements:**
- REQ-MEETUPS-001: Create meetup events
- REQ-MEETUPS-002: Join/leave meetups
- REQ-MEETUPS-003: View upcoming meetups
- REQ-MEETUPS-004: Meetup attendee management
- REQ-MEETUPS-005: Meetup location and time setting
- REQ-MEETUPS-006: Meetup description and details

### 3.6 Marketplace Feature
**Requirements:**
- REQ-MARKET-001: List items for sale
- REQ-MARKET-002: Browse marketplace listings
- REQ-MARKET-003: View item details
- REQ-MARKET-004: Contact sellers
- REQ-MARKET-006: Item categories and search

### 3.7 Notifications Feature
**Requirements:**
- REQ-NOTIF-001: Receive notifications for activities
- REQ-NOTIF-002: Mark notifications as read/unread
- REQ-NOTIF-003: Notification history
- REQ-NOTIF-004: Notification preferences
- REQ-NOTIF-006: Notification types (ride joins, meetup invites, etc.)

## 4. Test Cases

### 4.1 Authentication Test Cases

#### TC-AUTH-001: User Registration - Valid Data
**Objective:** Verify successful user registration with valid data
**Preconditions:**
- User is on registration page
- Email is not already registered
**Test Steps:**
1. Navigate to registration page
2. Enter valid email address
3. Enter valid password (8+ characters)
4. Enter matching confirm password
5. Enter username
6. Click "Register" button
**Expected Results:**
- User account created successfully
- Redirect to login page
- Success message displayed

#### TC-AUTH-002: User Login - Valid Credentials
**Objective:** Verify successful login with correct credentials
**Preconditions:**
- Valid user account exists
**Test Steps:**
1. Navigate to login page
2. Enter registered email
3. Enter correct password
4. Click "Login" button
**Expected Results:**
- User logged in successfully
- Redirect to dashboard/feed
- Session created
- User menu shows logged-in state

#### TC-AUTH-003: User Login - Invalid Credentials
**Objective:** Verify login rejection with invalid credentials
**Preconditions:**
- Valid user account exists
**Test Steps:**
1. Navigate to login page
2. Enter registered email
3. Enter incorrect password
4. Click "Login" button
**Expected Results:**
- Login rejected
- Error message displayed
- User remains on login page
- No session created

#### TC-AUTH-004: Password Validation
**Objective:** Verify password requirements enforcement
**Preconditions:**
- User is on registration page
**Test Steps:**
1. Attempt registration with password < 8 characters
2. Attempt registration with password >= 8 characters
**Expected Results:**
- Short password rejected with validation message
- Valid password accepted

#### TC-AUTH-005: Session Management
**Objective:** Verify logout functionality and session handling
**Preconditions:**
- User is logged in
**Test Steps:**
1. Click logout button
2. Attempt to access protected page
**Expected Results:**
- User logged out successfully
- Redirect to login page
- Session terminated

### 4.2 Feed Test Cases

#### TC-FEED-001: Create Feed Post
**Objective:** Verify post creation functionality
**Preconditions:**
- User is logged in
**Test Steps:**
1. Navigate to feed page
2. Click "Create Post" button
3. Enter post content
4. Click "Post" button
**Expected Results:**
- Post appears in feed
- Post shows correct author and timestamp
- Post content displays correctly

#### TC-FEED-002: Add Comment to Post
**Objective:** Verify comment functionality
**Preconditions:**
- User is logged in
- Feed post exists
**Test Steps:**
1. View a feed post
2. Click "Comment" button
3. Enter comment text
4. Click "Submit Comment"
**Expected Results:**
- Comment appears under post
- Comment shows correct author and timestamp
- Comment count updates

#### TC-FEED-003: View Comment Thread
**Objective:** Verify comment display and threading
**Preconditions:**
- Post with comments exists
**Test Steps:**
1. View a post with multiple comments
2. Scroll through comments
3. Verify comment order (chronological)
**Expected Results:**
- All comments display correctly
- Comments in correct order
- Author information visible

### 4.3 Garage Test Cases

#### TC-GARAGE-001: Add Bike to Garage
**Objective:** Verify bike addition functionality
**Preconditions:**
- User is logged in
**Test Steps:**
1. Navigate to garage page
2. Click "Add Bike" button
3. Enter bike details (make, model, year)
4. Upload bike photo (optional)
5. Click "Save Bike"
**Expected Results:**
- Bike added to garage
- Bike appears in garage list
- Bike details display correctly

#### TC-GARAGE-002: Update Bike Information
**Objective:** Verify bike editing functionality
**Preconditions:**
- User has bikes in garage
**Test Steps:**
1. View garage
2. Click "Edit" on a bike
3. Modify bike details
4. Click "Save Changes"
**Expected Results:**
- Bike information updated
- Changes reflected in garage view

#### TC-GARAGE-003: Remove Bike from Garage
**Objective:** Verify bike deletion functionality
**Preconditions:**
- User has bikes in garage
**Test Steps:**
1. View garage
2. Click "Delete" on a bike
3. Confirm deletion
**Expected Results:**
- Bike removed from garage
- Bike no longer appears in list

### 4.4 Rides Test Cases

#### TC-RIDES-001: Log New Ride
**Objective:** Verify ride logging functionality
**Preconditions:**
- User is logged in
**Test Steps:**
1. Navigate to rides page
2. Click "Log Ride" button
3. Enter ride details (date, distance, duration)
4. Select bike from garage
5. Click "Save Ride"
**Expected Results:**
- Ride logged successfully
- Ride appears in ride history
- Statistics update

#### TC-RIDES-002: View Ride History
**Objective:** Verify ride history display
**Preconditions:**
- User has logged rides
**Test Steps:**
1. Navigate to rides page
2. View ride list
3. Check ride details and statistics
**Expected Results:**
- All rides display correctly
- Statistics calculated accurately
- Sorting and filtering work

### 4.5 Meetups Test Cases

#### TC-MEETUPS-001: Create Meetup
**Objective:** Verify meetup creation functionality
**Preconditions:**
- User is logged in
**Test Steps:**
1. Navigate to meetups page
2. Click "Create Meetup" button
3. Enter meetup details (title, date, location, description)
4. Click "Create Meetup"
**Expected Results:**
- Meetup created successfully
- Meetup appears in meetups list
- User is listed as organizer

#### TC-MEETUPS-002: Join Meetup
**Objective:** Verify meetup joining functionality
**Preconditions:**
- Meetup exists
- User is logged in
**Test Steps:**
1. View meetup details
2. Click "Join Meetup" button
**Expected Results:**
- User added to attendee list
- Attendee count updates
- Confirmation message displayed

### 4.6 Marketplace Test Cases

#### TC-MARKET-001: List Item for Sale
**Objective:** Verify item listing functionality
**Preconditions:**
- User is logged in
**Test Steps:**
1. Navigate to marketplace
2. Click "Sell Item" button
3. Enter item details (title, description, price, category)
4. Upload item photos
5. Click "List Item"
**Expected Results:**
- Item listed successfully
- Item appears in marketplace
- Item details display correctly

#### TC-MARKET-002: Browse Marketplace
**Objective:** Verify marketplace browsing
**Preconditions:**
- Items are listed for sale
**Test Steps:**
1. Navigate to marketplace
2. Browse item listings
3. Use search and filter options
4. View item details
**Expected Results:**
- Items display correctly
- Search and filtering work
- Item details accessible

### 4.7 Notifications Test Cases

#### TC-NOTIF-001: Receive Notifications
**Objective:** Verify notification generation and display
**Preconditions:**
- User is logged in
- Activities that generate notifications occur
**Test Steps:**
1. Perform actions that trigger notifications (join meetup, comment on post)
2. Check notifications area
**Expected Results:**
- Notifications appear
- Correct notification types and messages
- Timestamps accurate

#### TC-NOTIF-002: Mark Notification as Read
**Objective:** Verify notification read status management
**Preconditions:**
- User has unread notifications
**Test Steps:**
1. View notifications
2. Click on a notification
3. Mark as read
**Expected Results:**
- Notification marked as read
- Read status persists
- Unread count updates

## 5. Test Scripts and Procedures

### 5.1 Manual Test Scripts

#### Script 1: User Registration and Login
```bash
# Prerequisites: Clean database, application running on localhost:8080

# Step 1: Access registration page
open_browser "http://localhost:8080/register"

# Step 2: Fill registration form
fill_field "email" "test@example.com"
fill_field "password" "password123"
fill_field "confirmPassword" "password123"
fill_field "username" "testuser"

# Step 3: Submit form
click_button "register-button"

# Step 4: Verify registration success
verify_text_present "Registration successful"
verify_url "http://localhost:8080/login"

# Step 5: Login with new account
fill_field "email" "test@example.com"
fill_field "password" "password123"
click_button "login-button"

# Step 6: Verify login success
verify_url "http://localhost:8080/feed"
verify_text_present "Welcome, testuser"
```

#### Script 2: Feed Post Creation
```bash
# Prerequisites: User logged in

# Step 1: Navigate to feed
navigate_to "/feed"

# Step 2: Create new post
click_button "create-post-btn"
fill_field "post-content" "This is a test post about motorcycle riding!"
click_button "submit-post"

# Step 3: Verify post creation
verify_element_present ".post-content"
verify_text_present "This is a test post about motorcycle riding!"
verify_text_present "testuser"
```

### 5.2 Automated Test Cases

#### Automated Test Case 1: User Authentication API
```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUserRegistration() throws Exception {
        // Test data
        String registrationJson = """
            {
                "email": "test@example.com",
                "password": "password123",
                "username": "testuser"
            }
            """;

        // Perform registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    public void testUserLogin() throws Exception {
        // Test data
        String loginJson = """
            {
                "email": "test@example.com",
                "password": "password123"
            }
            """;

        // Perform login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
```

#### Automated Test Case 2: Feed API
```java
@SpringBootTest
@AutoConfigureMockMvc
public class FeedApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    public void setup() {
        // Create test user and get auth token
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        userRepository.save(user);

        authToken = getAuthTokenForUser(user);
    }

    @Test
    public void testCreateFeedPost() throws Exception {
        String postJson = """
            {
                "content": "Test post content"
            }
            """;

        mockMvc.perform(post("/api/feed/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test post content"))
                .andExpect(jsonPath("$.author.username").value("testuser"));
    }

    @Test
    public void testGetFeedPosts() throws Exception {
        mockMvc.perform(get("/api/feed/posts")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
```

#### Automated Test Case 3: Garage Management
```java
@SpringBootTest
@AutoConfigureMockMvc
public class GarageApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        userRepository.save(user);
        authToken = getAuthTokenForUser(user);
    }

    @Test
    public void testAddBikeToGarage() throws Exception {
        String bikeJson = """
            {
                "make": "Honda",
                "model": "CBR600RR",
                "year": 2020,
                "color": "Red"
            }
            """;

        mockMvc.perform(post("/api/garage/bikes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bikeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.model").value("CBR600RR"));
    }

    @Test
    public void testGetUserGarage() throws Exception {
        mockMvc.perform(get("/api/garage/bikes")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
```

#### Automated Test Case 4: Meetups Management
```java
@SpringBootTest
@AutoConfigureMockMvc
public class MeetupsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        userRepository.save(user);
        authToken = getAuthTokenForUser(user);
    }

    @Test
    public void testCreateMeetup() throws Exception {
        String meetupJson = """
            {
                "title": "Monthly Ride Meetup",
                "description": "Join us for our monthly group ride",
                "location": "City Park",
                "meetupDate": "2024-06-15T10:00:00",
                "maxAttendees": 20
            }
            """;

        mockMvc.perform(post("/api/meetups")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(meetupJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Monthly Ride Meetup"))
                .andExpect(jsonPath("$.organizer.username").value("testuser"));
    }

    @Test
    public void testJoinMeetup() throws Exception {
        // First create a meetup
        Meetup meetup = createTestMeetup();

        mockMvc.perform(post("/api/meetups/" + meetup.getId() + "/join")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendees[*].username").value(hasItem("testuser")));
    }
}
```

#### Automated Test Case 5: Marketplace Functionality
```java
@SpringBootTest
@AutoConfigureMockMvc
public class MarketplaceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        userRepository.save(user);
        authToken = getAuthTokenForUser(user);
    }

    @Test
    public void testListItemForSale() throws Exception {
        String itemJson = """
            {
                "title": "Vintage Motorcycle Helmet",
                "description": "Well maintained vintage helmet",
                "price": 150.00,
                "category": "Safety Gear",
                "condition": "Good"
            }
            """;

        mockMvc.perform(post("/api/marketplace/items")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Vintage Motorcycle Helmet"))
                .andExpect(jsonPath("$.seller.username").value("testuser"));
    }

    @Test
    public void testBrowseMarketplace() throws Exception {
        mockMvc.perform(get("/api/marketplace/items")
                .param("category", "Safety Gear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
```

## Test Execution and Reporting

### Test Case Summary
- **Total Functional Requirements:** 37 (across 7 features)
- **Total Test Cases:** 21 (covering all implemented functionality)
- **Automated Test Cases:** 5 (API-level integration tests)
- **Manual Test Cases:** 16 (UI and end-to-end scenarios)

### 6.1 Test Execution Schedule
- **Week 1:** Unit and Integration Testing
- **Week 2:** System Testing
- **Week 3:** User Acceptance Testing
- **Week 4:** Regression Testing and Bug Fixes

### 6.2 Test Data Management
- Use realistic test data that mimics production scenarios
- Maintain test data independence between test cases
- Clean up test data after execution

### 6.3 Defect Reporting
- Use JIRA for defect tracking
- Include steps to reproduce, expected vs actual results
- Assign severity levels (Critical, Major, Minor, Trivial)

### 6.4 Test Metrics
- Test Case Execution Rate
- Defect Density
- Test Coverage Percentage
- Pass/Fail Ratios

## 7. Roles and Responsibilities

### 7.1 Test Manager
- Overall test planning and execution oversight
- Resource allocation and scheduling
- Stakeholder communication

### 7.2 Test Engineers
- Test case execution
- Defect reporting and verification
- Test automation development

### 7.3 Developers
- Unit test creation
- Code fixes for identified defects
- Technical support for testing

### 7.4 Business Analysts
- Requirement clarification
- Acceptance criteria validation
- User acceptance testing coordination

## 8. Risks and Mitigations

### 8.1 Schedule Risks
- **Risk:** Testing phase overruns due to defect fixes
- **Mitigation:** Parallel development of fixes during testing

### 8.2 Resource Risks
- **Risk:** Limited testing resources
- **Mitigation:** Prioritize critical test cases, use automation

### 8.3 Technical Risks
- **Risk:** Environment instability
- **Mitigation:** Dedicated test environments, infrastructure monitoring

## 9. Acceptance Criteria

### 9.1 Functional Acceptance
- All critical and major test cases pass
- No open critical or major defects
- All functional requirements implemented

### 9.2 Non-Functional Acceptance
- Application performance meets requirements
- Security standards maintained
- Usability standards met

### 9.3 Business Acceptance
- Business requirements fulfilled
- User workflows function correctly
- Data integrity maintained

## 10. Appendices

### Appendix A: Test Case Traceability Matrix
[Mapping of requirements to test cases]

### Appendix B: Test Data Specifications
[Detailed test data requirements]

### Appendix C: Test Environment Setup
[Environment configuration details]

### Appendix D: Automated Test Framework
[Testing framework documentation]

### Appendix E: Glossary
[Test-specific terminology definitions]

---

**Approval Signatures:**

Test Plan Prepared By: ___________________________ Date: ____________

Test Plan Reviewed By: ___________________________ Date: ____________

Test Plan Approved By: ___________________________ Date: ____________</content>
<parameter name="filePath">c:\Users\jhaja\OneDrive\Desktop\IT342-Panugaling-MotoMeet\docs\refactor-vertical-slice\Part3_Software_Test_Plan.md