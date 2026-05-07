# Architecture Inventory

## Current Architecture (Post-Refactoring)

### Vertical Slice Architecture Overview
The MotoMeet application has been refactored from a traditional layered architecture to a vertical slice architecture. Code is now organized by business features rather than technical concerns.

## Feature Slices

### 1. Authentication Feature (`features/auth/`)
**Purpose:** User registration, login, and authentication management

**Components:**
- `AuthController.java` - REST endpoints for auth operations
- `RegisterRequest.java` - DTO for user registration

**Dependencies:**
- Shared User model
- Spring Security configuration

### 2. Feed Feature (`features/feed/`)
**Purpose:** Social feed functionality, posts, and comments

**Components:**
- `FeedPost.java` - JPA entity for feed posts
- `FeedPostForm.java` - DTO for post creation/editing
- `FeedPostRepository.java` - Data access layer

**Dependencies:**
- Shared Comment model
- Shared User model

### 3. Garage Feature (`features/garage/`)
**Purpose:** Bike inventory and management

**Components:**
- `Bike.java` - JPA entity for bike information
- `BikeForm.java` - DTO for bike data
- `BikeRepository.java` - Data access layer

**Dependencies:**
- Shared User model

### 4. Rides Feature (`features/rides/`)
**Purpose:** Ride logging and history tracking

**Components:**
- `RideLog.java` - JPA entity for ride records
- `RideForm.java` - DTO for ride data
- `RideLogRepository.java` - Data access layer

**Dependencies:**
- Shared User model

### 5. Meetups Feature (`features/meetups/`)
**Purpose:** Motorcycle meetup organization and management

**Components:**
- `Meetup.java` - JPA entity for meetup events
- `MeetupForm.java` - DTO for meetup data
- `MeetupRepository.java` - Data access layer

**Dependencies:**
- Shared User model

### 6. Marketplace Feature (`features/marketplace/`)
**Purpose:** Buying and selling motorcycle-related items

**Components:**
- `MarketplaceItem.java` - JPA entity for marketplace listings
- `MarketplaceItemForm.java` - DTO for item data
- `MarketplaceItemRepository.java` - Data access layer

**Dependencies:**
- Shared User model

### 7. Notifications Feature (`features/notifications/`)
**Purpose:** User notification management

**Components:**
- `Notification.java` - JPA entity for notifications
- `NotificationRepository.java` - Data access layer

**Dependencies:**
- Shared User model

## Shared Components (`shared/`)

### Models
- `User.java` - Core user entity
- `Comment.java` - Cross-feature comment functionality

### Repositories
- `UserRepository.java` - User data access
- `CommentRepository.java` - Comment data access

### Services
- `CurrentUserService.java` - Authentication context service

### Configuration
- `SecurityConfig.java` - Spring Security configuration

### Exceptions
- `AuthenticatedUserMissingException.java` - Authentication error handling

## Web Layer

### Controllers
- `PageController.java` - Main web controller for all features
- `AuthController.java` - Authentication REST endpoints

### Templates (`templates/features/`)
- Organized by feature: `auth/`, `feed/`, `garage/`, `rides/`, `meetups/`, `marketplace/`, `notifications/`

### Static Assets (`static/`)
- CSS stylesheets
- JavaScript files
- Images and other assets

## Database Schema

### Core Tables
- `users` - User accounts and profiles
- `feed_posts` - Social feed content
- `bikes` - User's bike inventory
- `ride_logs` - Ride history records
- `meetups` - Meetup events
- `marketplace_items` - Items for sale
- `notifications` - User notifications
- `comments` - Comments on various entities

### Relationships
- All feature entities reference `users` table
- `comments` can reference multiple entity types (polymorphic relationship)
- Standard foreign key relationships for ownership and references

## Technology Stack

### Backend
- **Java:** 17
- **Framework:** Spring Boot 3.x
- **Database:** H2 (development), configurable for production
- **ORM:** Spring Data JPA/Hibernate
- **Security:** Spring Security
- **Build:** Maven

### Frontend
- **Templates:** Thymeleaf
- **Styling:** CSS
- **JavaScript:** Vanilla JS (no frameworks)

### Mobile
- **Platform:** Android
- **Build:** Gradle
- **Language:** Kotlin/Java

## Architecture Benefits

### Maintainability
- Feature-specific code is co-located
- Clear boundaries between business concerns
- Easier to modify and extend individual features

### Scalability
- Independent feature development
- Reduced coupling between features
- Easier to add new features

### Testability
- Feature-level testing isolation
- Clear component boundaries
- Easier mocking and stubbing

### Developer Experience
- Intuitive code organization
- Faster feature development
- Reduced cognitive load when working on specific features</content>
<parameter name="filePath">c:\Users\jhaja\OneDrive\Desktop\IT342-Panugaling-MotoMeet\docs\refactor-vertical-slice\Architecture_Inventory.md