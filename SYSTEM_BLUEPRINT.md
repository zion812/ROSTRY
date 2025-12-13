# ROSTRY System Blueprint (SINF - System Information and Functionality)

## 1. Executive Summary

- **Project Overview:** ROSTRY is a comprehensive poultry management platform integrating social networking, marketplace commerce, and farm management features to connect farmers, enthusiasts, and consumers.
- **Stakeholders:** Farmers (producers), Enthusiasts (hobbyists/breeders), General Users (consumers), Experts (consultants).
- **Technology Stack:** Android (Kotlin, Jetpack Compose, Material 3), Firebase (Auth, Firestore, Storage, Functions, App Check), Room with SQLCipher, Hilt DI, WorkManager, Google Maps Platform.
- **Current Version/Status:** Version 1.0 (Production Ready). See `CHANGELOG.md` for release history.
- **Quick Navigation:** Markdown links to key sections like `[System Architecture](#2-system-architecture)`, `[Codebase Structure](#3-codebase-structure)`, `[Feature Catalog](#4-feature-catalog)`, `[Data Flow Diagrams](#5-data-flow-diagrams)`, `[API Documentation](#6-api-documentation)`

## 2. System Architecture

ROSTRY follows clean architecture with clear layer separation.

graph TB
    subgraph "Presentation Layer"
        UI[Jetpack Compose UI]
        VM[ViewModels]
        NAV[Navigation Graph]
    end
    
    subgraph "Domain Layer"
        UC[Use Cases]
        REPO[Repositories]
    end
    
    subgraph "Data Layer"
        ROOM[(Room Database<br/>SQLCipher)]
        FIRE[(Firebase Services)]
        API[(REST APIs)]
    end
    
    subgraph "Infrastructure"
        HILT[Hilt DI]
        WM[WorkManager]
        SEC[Security Utils]
    end
    
    UI --> VM
    VM --> UC
    UC --> REPO
    REPO --> ROOM
    REPO --> FIRE
    REPO --> API
    VM --> NAV
    HILT --> VM
    HILT --> REPO
    WM --> REPO
    SEC --> REPO
```

### Layer-by-Layer Breakdown

#### UI Layer (Presentation)
- **Framework**: Jetpack Compose with Material 3
- **Structure**: Feature-based packages under `ui/` (auth, marketplace, monitoring, etc.)
- **Components**: Reusable components in `ui/components/`, theme in `ui/theme/`
- **Navigation**: Centralized in `ui/navigation/` with role-aware routing
- **State Management**: StateFlow for reactive UI updates

#### ViewModel Layer
- **Purpose**: Business logic coordination and state management
- **Pattern**: MVVM with SavedStateHandle for process death survival
- **Injection**: Hilt-assisted injection
- **Communication**: StateFlow for UI state, SharedFlow for events

#### Repository Layer
- **Purpose**: Data orchestration and business logic encapsulation
- **Pattern**: Interface + Implementation for testability
- **Responsibilities**: Sync coordination, validation, error handling
- **Integration**: Room DAOs, Firebase services, Retrofit APIs

#### Data Layer
- **Local**: Room database with SQLCipher encryption
- **Remote**: Firebase Firestore, Storage, Auth, Functions
- **Sync**: Offline-first with conflict resolution
- **APIs**: Retrofit for external services

### Dependency Injection Structure
- **Framework**: Hilt with SingletonComponent, ViewModelScoped, ActivityRetainedScoped
- **Modules**: AppModule, NetworkModule, DatabaseModule, RepositoryModule, WorkerModule
- **Qualifiers**: @LocalDataSource, @RemoteDataSource for multi-implementation binding
- **Testing**: TestInstallIn for mock injection

### Navigation Architecture
- **Graph**: Single activity with Compose Navigation
- **Routes**: Centralized in Routes.kt with deep link support
- **Guards**: Role-based access control for destinations
- **State**: Navigation state preserved across configuration changes

### Background Processing Architecture
- **Framework**: WorkManager for reliable deferred execution
- **Workers**: 15+ specialized workers (SyncWorker, FarmMonitoringWorker, etc.)
- **Scheduling**: Periodic and one-time work with constraints
- **Integration**: Hilt injection, progress reporting, chaining

### Security Architecture
- **Authentication**: Firebase Auth with phone OTP, multi-provider support
- **Authorization**: Role-based access control (RBAC) with Permission enums
- **Data Protection**: SQLCipher encryption, certificate pinning
- **Device Security**: Root detection, secure key storage
- **Network**: HTTPS-only, Firebase security rules

---

## 3. Codebase Structure

**Scope:** This section provides high-level codebase organization and architectural patterns. For detailed package contents, file locations, and navigation guidance, refer to `docs/CODEBASE_STRUCTURE.md`.

The ROSTRY codebase follows a clean architecture with feature-based organization in the UI layer and layer-based organization in the data layer.

#### Root Structure
```
ROSTRY/
├── app/src/main/java/com/rio/rostry/     # Main application code
├── app/src/main/res/                     # Android resources
├── app/src/test/                         # Unit tests
├── app/src/androidTest/                  # Integration tests
├── docs/                                 # Documentation
├── firebase/                             # Firebase configuration
├── gradle/                               # Build configuration
└── [config files]
```

The overall architectural organization is feature-based in the UI layer and layer-based in the data layer. Key architectural patterns include MVVM, Repository pattern, and offline-first design.

Top-level package categories:

- `ui/`: UI layer with Compose screens and ViewModels, organized by features
- `data/`: Data layer including repositories, database, and synchronization
- `di/`: Hilt dependency injection modules
- `domain/`: Domain layer with use cases and business logic
- `workers/`: WorkManager background jobs

For detailed package-by-package breakdown with file descriptions and navigation guidance, see `docs/CODEBASE_STRUCTURE.md`

### 3.2 Database Schema

#### Complete Entity List (60+ Entities)
**User Management:**
- UserEntity, UserProfileEntity, UserPreferencesEntity

**Products & Marketplace:**
- ProductEntity, ProductImageEntity, ProductTrackingEntity
- OrderEntity, OrderItemEntity, PaymentEntity, RefundEntity
- AuctionEntity, BidEntity, CartEntity, WishlistEntity

**Social Platform:**
- PostEntity, CommentEntity, LikeEntity, ShareEntity
- MessageEntity, ThreadEntity, GroupEntity, EventEntity
- ExpertProfileEntity, BookingEntity

**Farm Monitoring:**
- FarmEntity, FlockEntity, VaccinationRecordEntity
- GrowthRecordEntity, MortalityRecordEntity, HatchingRecordEntity
- AlertEntity, QuarantineEntity

**Transfers & Traceability:**
- TransferEntity, TransferVerificationEntity, DisputeEntity
- AuditLogEntity, FamilyTreeEntity, CertificationEntity

**Analytics & Gamification:**
- AnalyticsDailyEntity, ReportEntity, DashboardSnapshotEntity
- AchievementEntity, BadgeEntity, ReputationEntity

**System:**
- SessionEntity, NotificationEntity, FeedbackEntity
- ModerationReportEntity, RateLimitEntity

#### Entity Relationships and Foreign Keys
- Users → Products (seller relationship)
- Products → Orders (many-to-many via OrderItem)
- Posts → Comments (parent-child hierarchy)
- Transfers → Products (ownership changes)
- FamilyTree → Products (lineage tracking)
- Users → Achievements (gamification)

#### Migration History (v1 to v40)
- **v1-v5**: Core entities (User, Product, Order)
- **v6-v10**: Social features (Post, Comment, Group)
- **v11-v15**: Farm monitoring (Vaccination, Growth tracking)
- **v16-v20**: Community features (ThreadMetadata, Recommendations)
- **v21-v25**: Transfer system (TransferEntity, Dispute)
- **v26-v30**: Analytics (AnalyticsDaily, Reports)
- **v31-v35**: Gamification (Achievements, Badges)
- **v36-v40**: Performance optimizations and indexes

#### Indexes and Performance Optimizations
- Composite indexes on (user_id, created_at) for feed queries
- Full-text search indexes on product descriptions
- Spatial indexes for location-based searches
- Foreign key indexes for join performance

#### SQLCipher Encryption Setup
- Passphrase derived from Android Keystore
- AES-256 encryption for all data at rest
- Re-encryption capability on passphrase change
- Secure key storage with biometric fallback

### 3.3 Firebase Structure

#### Firestore Collections and Documents
- **users/{userId}**: User profiles with role and verification status
- **products/{productId}**: Product listings with media URLs and metadata
- **transfers/{transferId}**: Transfer records with verification data
- **social/posts/{postId}**: Posts with subcollections for comments
- **analytics/daily/{date}**: Aggregated analytics data
- **notifications/{userId}/inbox**: User notification queue

#### Storage Bucket Organization
- **products/**: Product images and videos
- **profiles/**: User profile pictures
- **transfers/**: Transfer verification documents
- **exports/**: Generated reports and exports

#### Cloud Functions
- **verifyTransfer**: Secure transfer validation
- **processPayment**: Payment processing and verification
- **sendNotification**: FCM notification dispatching
- **moderateContent**: Automated content moderation

#### Security Rules
```javascript
match /users/{userId} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
}

match /products/{productId} {
  allow read: if request.auth != null;
  allow create, update, delete: if request.auth != null && 
    resource.data.sellerId == request.auth.uid;
}
```

#### FCM Notification Structure
- **Data Messages**: Background updates with custom data
- **Notification Messages**: User-visible alerts with actions
- **Topics**: Role-based notification channels
- **Deep Links**: Direct navigation to app sections

---

## 4. Feature Catalog

### 4.1 Authentication & User Management
- **Phone OTP Authentication**: Firebase Auth with SMS verification
- **Multi-Provider Auth**: Google, Email/Password, Phone support
- **Role-Based Access Control**: General, Farmer, Enthusiast roles with permissions
- **User Profiles**: Detailed profiles with verification status and trust scores
- **Session Management**: Persistent sessions with automatic refresh

### 4.2 Social Platform
- **Posts, Comments, Likes, Shares**: Full social media functionality
- **Direct Messaging and Group Chats**: Context-aware messaging system
- **Community Groups and Events**: Organized community engagement
- **Follow System and Feed Personalization**: AI-powered content recommendations
- **Context-Aware Messaging**: Intelligent conversation threading
- **Expert Profiles and Consultation Booking**: Professional services marketplace
- **Moderation System**: Automated and manual content moderation

### 4.3 Marketplace
- **Product Listings with Rich Media**: Image/video support with metadata
- **Auction System with Bidding**: Real-time bidding with notifications
- **Shopping Cart and Wishlist**: E-commerce functionality
- **Filter Presets**: Nearby, Traceable, Budget, Premium filters
- **Payment Integration Framework**: UPI, cards, wallets support
- **Order Management**: Complete order lifecycle tracking

### 4.4 Secure Transfer System
- **Ownership Transfer with Verification**: Guided workflow with checks
- **QR Code Scanning**: Easy transfer initiation
- **Cloud Functions-Based Security**: Server-side validation
- **Rate Limiting and Fraud Detection**: Anti-abuse measures
- **Complete Audit Trail**: Immutable transfer history
- **Dispute Resolution**: Mediation and resolution system

### 4.5 Farm Monitoring
- **Growth Tracking and Analytics**: Performance metrics and trends
- **Breeding Management**: Lineage and breeding records
- **Quarantine Management**: Isolation tracking and protocols
- **Mortality Tracking**: Loss recording and analysis
- **Vaccination Scheduling**: Automated reminders and tracking
- **Hatching Process Monitoring**: Incubation and hatchery management
- **Alert System with Urgency Levels**: Critical health notifications
- **Daily Logging**: Comprehensive farm activity recording

### 4.6 Traceability
- **Lineage Tracking and Family Trees**: Genetic history visualization
- **Product Tracking Throughout Lifecycle**: End-to-end traceability
- **Certification and Verification**: Authenticity guarantees
- **QR Code Generation for Products**: Easy verification access

### 4.7 Analytics & Reporting
- **Performance Dashboards**: Role-specific analytics views
- **Financial Tracking and ROI**: Revenue and profitability metrics
- **Breeding Insights**: Genetic performance analysis
- **AI-Powered Recommendations**: Predictive insights
- **Export Functionality**: CSV, PDF report generation
- **Data Visualization**: Charts and graphs for trends

### 4.8 Gamification
- **Achievement System**: Goal-based rewards
- **Badges and Rewards**: Visual recognition system
- **Reputation Scoring**: Community trust metrics
- **Leaderboards**: Competitive rankings

### 4.9 AI Personalization
- **Content Recommendations**: Personalized feed algorithm
- **User Interest Tracking**: Behavior analysis
- **Personalized Feed**: Customized content delivery
- **Expert Matching**: AI-powered consultant recommendations

---

## 5. Data Flow Diagrams

### Authentication Flow
```mermaid
sequenceDiagram
    participant User
    participant UI
    participant AuthVM as AuthViewModel
    participant Repo as AuthRepository
    participant Firebase

    User->>UI: Enter phone number
    UI->>AuthVM: initiatePhoneAuth(phone)
    AuthVM->>Repo: sendVerificationCode(phone)
    Repo->>Firebase: Phone Auth API
    Firebase-->>Repo: Verification ID
    Repo-->>AuthVM: Result.Success(verificationId)
    AuthVM-->>UI: Navigate to OTP screen
    
    User->>UI: Enter OTP
    UI->>AuthVM: verifyOtp(code)
    AuthVM->>Repo: signInWithCredential(code)
    Repo->>Firebase: FirebaseUser
    Repo->>Firebase: Link phone number
    Firebase-->>Repo: Updated user
    Repo-->>AuthVM: Result.Success(user)
    AuthVM-->>UI: Navigate to main screen
```

### Product Creation and Listing Flow
```mermaid
flowchart TD
    A[User Creates Product] --> B[Validate Input]
    B --> C{Valid?}
    C -->|No| D[Show Errors]
    C -->|Yes| E[Save to Room]
    E --> F[Upload Images to Firebase Storage]
    F --> G[Create Firestore Document]
    G --> H[Queue for Sync]
    H --> I{Sync Success?}
    I -->|Yes| J[Update Local Status]
    I -->|No| K[Retry Later]
    J --> L[Show Success]
    D --> A
```

### Transfer Workflow
```mermaid
flowchart TD
    A[Initiate Transfer] --> B[Scan QR Code]
    B --> C[Validate Product Ownership]
    C --> D{Valid Owner?}
    D -->|No| E[Show Error]
    D -->|Yes| F[Create Transfer Record]
    F --> G[Upload Verification Docs]
    G --> H[Call Cloud Function]
    H --> I{Verification Success?}
    I -->|No| J[Handle Rejection]
    I -->|Yes| K[Update Ownership]
    K --> L[Send Notifications]
    L --> M[Complete Transfer]
    J --> E
```

### Sync and Offline-First Flow
```mermaid
flowchart TD
    A[App Launch] --> B{Network Available?}
    B -->|Yes| C[SyncWorker Triggered]
    B -->|No| D[Load from Room Cache]
    
    C --> E[Fetch Remote Changes]
    E --> F{Conflicts?}
    F -->|Yes| G[Resolve Conflicts]
    F -->|No| H[Apply Changes]
    
    G --> I[User Resolution UI]
    I --> J[Apply Resolution]
    J --> H
    
    H --> K[Update Room DB]
    K --> L[Notify UI via Flow]
    D --> L
    
    L --> M[UI Shows Latest Data]
    
    N[User Makes Changes] --> O[Save to Room]
    O --> P[Mark as Pending Sync]
    P --> Q{Network Available?}
    Q -->|Yes| R[Upload Changes]
    Q -->|No| S[Queue for Later]
    R --> T[Mark as Synced]
```

### Payment Flow
```mermaid
sequenceDiagram
    participant User
    participant UI
    participant PaymentVM as PaymentViewModel
    participant PaymentRepo as PaymentRepository
    participant Gateway as Payment Gateway
    participant Firebase

    User->>UI: Initiate Payment
    UI->>PaymentVM: processPayment(order)
    PaymentVM->>PaymentRepo: createPaymentIntent(order)
    PaymentRepo->>Gateway: Create payment intent
    Gateway-->>PaymentRepo: Payment intent
    PaymentRepo-->>PaymentVM: Payment data
    PaymentVM-->>UI: Show payment UI
    
    User->>UI: Complete payment
    UI->>PaymentVM: confirmPayment(result)
    PaymentVM->>PaymentRepo: verifyPayment(result)
    PaymentRepo->>Gateway: Verify payment
    Gateway-->>PaymentRepo: Confirmation
    PaymentRepo->>Firebase: Update order status
    Firebase-->>PaymentRepo: Success
    PaymentRepo-->>PaymentVM: Result.Success
    PaymentVM-->>UI: Show success
```

### Notification Flow
```mermaid
flowchart TD
    A[Event Occurs] --> B{Notification Type}
    B -->|Local| C[Create Local Notification]
    B -->|Push| D[Send to FCM]
    
    C --> E[Schedule via WorkManager]
    D --> F[Cloud Function Processes]
    F --> G[Send to Device]
    
    E --> H[Android Notification Manager]
    G --> H
    
    H --> I[User Taps Notification]
    I --> J[Deep Link Handling]
    J --> K[Navigate to Destination]
    
    L[User Opens App] --> M[Check Pending Notifications]
    M --> N[Show In-App Notifications]
```

---

## 6. API Documentation

### Firebase API Usage
- **Authentication**: Firebase Auth for phone OTP and multi-provider login
- **Firestore**: Real-time database for products, users, transfers, social data
- **Storage**: File uploads for images, videos, documents
- **Functions**: Server-side logic for transfers, payments, moderation
- **FCM**: Push notifications with data payloads

### Retrofit REST APIs
- **Marketplace API**: Product search, pricing, logistics
- **Analytics API**: External data for AI recommendations
- **Compliance API**: KYC validation (framework ready)

### Internal API Contracts
- **Repository Interfaces**: Standardized data access patterns
- **Use Cases**: Business logic encapsulation
- **DTOs**: Data transfer objects for API communication

### Data Transfer Objects (DTOs)
```kotlin
data class ProductDto(
    val id: String,
    val name: String,
    val price: Double,
    val images: List<String>,
    val sellerId: String,
    val createdAt: Long
)

data class TransferDto(
    val id: String,
    val productId: String,
    val fromUserId: String,
    val toUserId: String,
    val status: TransferStatus,
    val verificationDocuments: List<String>
)
