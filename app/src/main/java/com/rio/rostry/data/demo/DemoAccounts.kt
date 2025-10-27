package com.rio.rostry.data.demo

import com.rio.rostry.domain.model.UserType

data class DemoCredential(
    val username: String,
    val password: String
)

data class DemoProduct(
    val name: String,
    val category: String,
    val priceInr: Int,
    val description: String
)

data class DemoTransaction(
    val reference: String,
    val counterpart: String,
    val amountInr: Int,
    val status: String,
    val summary: String
)

data class DemoSocialConnection(
    val name: String,
    val relation: String,
    val note: String
)

data class DemoFarmDetails(
    val location: String,
    val acreage: Double,
    val breeds: List<String>,
    val highlights: List<String>
)

// Marketplace commerce data classes
data class DemoOrder(
    val orderId: String,
    val status: String,
    val totalAmount: Int,
    val items: List<DemoOrderItem>,
    val trackingEvents: List<DemoTrackingEvent>
)

data class DemoOrderItem(
    val productId: String,
    val quantity: Int,
    val price: Int
)

data class DemoTrackingEvent(
    val status: String,
    val timestamp: Long,
    val location: String
)

data class DemoCartItem(
    val productId: String,
    val quantity: Int
)

data class DemoWishlistItem(
    val productId: String,
    val addedAt: Long
)

data class DemoAuction(
    val productId: String,
    val startTime: Long,
    val endTime: Long,
    val minPrice: Int,
    val bids: List<DemoBid>
)

data class DemoBid(
    val amount: Int,
    val timestamp: Long
)

data class DemoInvoice(
    val orderId: String,
    val subtotal: Int,
    val gst: Int,
    val total: Int
)

data class DemoPayment(
    val orderId: String,
    val method: String,
    val amount: Int,
    val status: String
)

// Farm monitoring data classes
data class DemoDailyLog(
    val productId: String,
    val date: Long,
    val weight: Double,
    val feed: String,
    val notes: String,
    val temperature: Double,
    val humidity: Double
)

data class DemoTask(
    val type: String,
    val title: String,
    val dueDate: Long,
    val priority: String,
    val status: String,
    val productId: String?
)

data class DemoGrowthRecord(
    val productId: String,
    val week: Int,
    val weight: Double,
    val height: Double,
    val healthStatus: String
)

data class DemoQuarantineRecord(
    val productId: String,
    val reason: String,
    val status: String,
    val startDate: Long,
    val endDate: Long?
)

data class DemoMortalityRecord(
    val productId: String,
    val cause: String,
    val ageWeeks: Int,
    val date: Long
)

data class DemoVaccinationRecord(
    val productId: String,
    val vaccineType: String,
    val scheduledDate: Long,
    val administeredDate: Long?
)

data class DemoHatchingBatch(
    val name: String,
    val eggsCount: Int,
    val expectedHatchDate: Long,
    val status: String
)

data class DemoBreedingPair(
    val maleProductId: String,
    val femaleProductId: String,
    val status: String,
    val eggsCollected: Int
)

// Gamification data classes
data class DemoAchievement(
    val achievementId: String,
    val name: String,
    val description: String,
    val points: Int,
    val unlockedAt: Long?
)

data class DemoCoinTransaction(
    val type: String,
    val amount: Int,
    val description: String,
    val date: Long
)

data class DemoLeaderboardEntry(
    val rank: Int,
    val score: Int,
    val period: String
)

// Community/social data classes
data class DemoComment(
    val postId: String,
    val text: String,
    val timestamp: Long
)

data class DemoLike(
    val postId: String,
    val timestamp: Long
)

data class DemoFollow(
    val followedUserId: String,
    val timestamp: Long
)

data class DemoGroup(
    val name: String,
    val description: String,
    val category: String
)

data class DemoEvent(
    val title: String,
    val description: String,
    val location: String,
    val startTime: Long,
    val rsvpStatus: String
)

data class DemoChatMessage(
    val receiverId: String,
    val body: String,
    val timestamp: Long
)

data class DemoExpertBooking(
    val expertId: String,
    val topic: String,
    val startTime: Long,
    val status: String
)

// Traceability data classes
data class DemoFamilyTreeNode(
    val productId: String,
    val parentProductId: String,
    val relationType: String
)

data class DemoLifecycleEvent(
    val productId: String,
    val week: Int,
    val stage: String,
    val type: String,
    val notes: String
)

// Breeding data classes
data class DemoMatingLog(
    val pairId: String,
    val matedAt: Long,
    val observedBehavior: String,
    val notes: String
)

data class DemoEggCollection(
    val pairId: String,
    val eggsCollected: Int,
    val qualityGrade: String,
    val date: Long
)

// Notification data class
data class DemoNotification(
    val type: String,
    val title: String,
    val message: String,
    val domain: String,
    val timestamp: Long
)

data class DemoUserProfile(
    val id: String,
    val credential: DemoCredential,
    val fullName: String,
    val role: UserType,
    val phoneNumber: String,
    val email: String,
    val location: String,
    val headline: String,
    val tags: List<String>,
    val farmDetails: DemoFarmDetails?,
    val productListings: List<DemoProduct>,
    val transactions: List<DemoTransaction>,
    val socialConnections: List<DemoSocialConnection>,
    val statusNote: String?,
    val orders: List<DemoOrder>,
    val cartItems: List<DemoCartItem>,
    val wishlistItems: List<DemoWishlistItem>,
    val auctions: List<DemoAuction>,
    val dailyLogs: List<DemoDailyLog>,
    val tasks: List<DemoTask>,
    val growthRecords: List<DemoGrowthRecord>,
    val quarantineRecords: List<DemoQuarantineRecord>,
    val mortalityRecords: List<DemoMortalityRecord>,
    val vaccinationRecords: List<DemoVaccinationRecord>,
    val hatchingBatches: List<DemoHatchingBatch>,
    val breedingPairs: List<DemoBreedingPair>,
    val achievements: List<DemoAchievement>,
    val coinTransactions: List<DemoCoinTransaction>,
    val leaderboardEntries: List<DemoLeaderboardEntry>,
    val comments: List<DemoComment>,
    val likes: List<DemoLike>,
    val follows: List<DemoFollow>,
    val groups: List<DemoGroup>,
    val events: List<DemoEvent>,
    val chatMessages: List<DemoChatMessage>,
    val expertBookings: List<DemoExpertBooking>,
    val familyTreeNodes: List<DemoFamilyTreeNode>,
    val lifecycleEvents: List<DemoLifecycleEvent>,
    val matingLogs: List<DemoMatingLog>,
    val eggCollections: List<DemoEggCollection>,
    val notifications: List<DemoNotification>
)

object DemoAccounts {
    val all: List<DemoUserProfile> = listOf(
        DemoUserProfile(
            id = "demo-general-asharao",
            credential = DemoCredential("demo_buyer1", "password123"),
            fullName = "Asha Rao",
            role = UserType.GENERAL,
            phoneNumber = "+91 90000 11111",
            email = "asha.rao@demo.rostry.app",
            location = "Vijayawada, Andhra Pradesh",
            headline = "Urban buyer exploring heritage roosters",
            tags = listOf("Weekend auctions", "Sustainable farms", "Andhra breeds"),
            farmDetails = null,
            productListings = listOf(
                DemoProduct("Kadaknath Eggs", "Eggs", 220, "Weekly subscription from certified farms"),
                DemoProduct("Country Broiler", "Meat", 380, "Fresh dressed bird from Guntur collective")
            ),
            transactions = listOf(
                DemoTransaction("TXN-A101", "Guntur Co-op", 2600, "COMPLETED", "Quarterly community auction purchase"),
                DemoTransaction("TXN-A155", "Amaravati Farms", 1800, "COMPLETED", "Organic feed and accessories bundle")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Manoj Patel", "Following", "Subscribed to farm updates"),
                DemoSocialConnection("Leela Fernandes", "Group Member", "Participates in weekend auction circle")
            ),
            statusNote = "Prefers doorstep delivery on Saturdays",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-A001",
                    status = "DELIVERED",
                    totalAmount = 600,
                    items = listOf(DemoOrderItem("Kadaknath Eggs", 2, 220), DemoOrderItem("Country Broiler", 1, 380)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 7, "Vijayawada"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 5, "Guntur"),
                        DemoTrackingEvent("DELIVERED", System.currentTimeMillis() - 86400000 * 2, "Vijayawada")
                    )
                ),
                DemoOrder(
                    orderId = "ORD-A002",
                    status = "IN_TRANSIT",
                    totalAmount = 440,
                    items = listOf(DemoOrderItem("Kadaknath Eggs", 2, 220)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 3, "Vijayawada"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 1, "Amaravati")
                    )
                )
            ),
            cartItems = listOf(
                DemoCartItem("Country Broiler", 1),
                DemoCartItem("Kadaknath Eggs", 3)
            ),
            wishlistItems = listOf(
                DemoWishlistItem("Kadaknath Eggs", System.currentTimeMillis() - 86400000 * 10),
                DemoWishlistItem("Country Broiler", System.currentTimeMillis() - 86400000 * 5),
                DemoWishlistItem("Heritage Country Chicken", System.currentTimeMillis() - 86400000 * 2),
                DemoWishlistItem("Farm Fresh Eggs", System.currentTimeMillis() - 86400000 * 1),
                DemoWishlistItem("Herbal Feed Kit", System.currentTimeMillis() - 86400000 * 7),
                DemoWishlistItem("Traceability Badge", System.currentTimeMillis() - 86400000 * 3),
                DemoWishlistItem("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 4),
                DemoWishlistItem("Starter Chicks", System.currentTimeMillis() - 86400000 * 6)
            ),
            auctions = listOf(
                DemoAuction(
                    productId = "Aseel Game Bird",
                    startTime = System.currentTimeMillis() - 86400000 * 2,
                    endTime = System.currentTimeMillis() + 86400000 * 1,
                    minPrice = 5000,
                    bids = listOf(
                        DemoBid(5100, System.currentTimeMillis() - 86400000 * 1),
                        DemoBid(5300, System.currentTimeMillis() - 3600000 * 12)
                    )
                )
            ),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = emptyList(),
            achievements = emptyList(),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 500, "Auction participation bonus", System.currentTimeMillis() - 86400000 * 1),
                DemoCoinTransaction("SPENT", -200, "Order discount", System.currentTimeMillis() - 86400000 * 3)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(15, 1200, "weekly"),
                DemoLeaderboardEntry(45, 850, "monthly")
            ),
            comments = listOf(
                DemoComment("POST-001", "Great auction experience!", System.currentTimeMillis() - 86400000 * 2),
                DemoComment("POST-002", "Looking forward to the next batch.", System.currentTimeMillis() - 86400000 * 1)
            ),
            likes = listOf(
                DemoLike("POST-001", System.currentTimeMillis() - 86400000 * 2),
                DemoLike("POST-003", System.currentTimeMillis() - 86400000 * 1),
                DemoLike("POST-004", System.currentTimeMillis() - 3600000 * 6),
                DemoLike("POST-005", System.currentTimeMillis() - 3600000 * 12),
                DemoLike("POST-006", System.currentTimeMillis() - 86400000 * 3)
            ),
            follows = listOf(
                DemoFollow("demo-farmer-vijayawada", System.currentTimeMillis() - 86400000 * 10),
                DemoFollow("demo-farmer-guntur", System.currentTimeMillis() - 86400000 * 7),
                DemoFollow("demo-enthusiast-premium", System.currentTimeMillis() - 86400000 * 5),
                DemoFollow("demo-enthusiast-verified", System.currentTimeMillis() - 86400000 * 3),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 1)
            ),
            groups = listOf(
                DemoGroup("Weekend Auction Circle", "Weekly auctions for heritage breeds", "Auctions"),
                DemoGroup("Sustainable Farms Network", "Discussing eco-friendly farming", "Community")
            ),
            events = listOf(
                DemoEvent("Heritage Rooster Meetup", "Monthly gathering for enthusiasts", "Vijayawada Community Hall", System.currentTimeMillis() + 86400000 * 7, "ATTENDING"),
                DemoEvent("Farm-to-Table Workshop", "Learn about sustainable sourcing", "Amaravati Agri Center", System.currentTimeMillis() + 86400000 * 14, "INTERESTED")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-farmer-vijayawada", "Hi Raghav, interested in your Aseel birds?", System.currentTimeMillis() - 86400000 * 1),
                DemoChatMessage("demo-enthusiast-verified", "Thanks for the recipe share!", System.currentTimeMillis() - 3600000 * 12)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-expert", "Breeding tips for Kadaknath", System.currentTimeMillis() + 86400000 * 3, "CONFIRMED")
            ),
            familyTreeNodes = emptyList(),
            lifecycleEvents = emptyList(),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("ORDER", "Order Delivered", "Your order ORD-A001 has been delivered.", "ORDER", System.currentTimeMillis() - 86400000 * 2),
                DemoNotification("AUCTION", "Bid Placed", "You placed a bid on Aseel Game Bird.", "AUCTION", System.currentTimeMillis() - 3600000 * 12),
                DemoNotification("SOCIAL", "New Follower", "demo-farmer-guntur started following you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 7),
                DemoNotification("PAYMENT", "Payment Processed", "Payment for ORD-A002 successful.", "PAYMENT", System.currentTimeMillis() - 86400000 * 3),
                DemoNotification("EVENT", "Event Reminder", "Heritage Rooster Meetup starts soon.", "EVENT", System.currentTimeMillis() - 3600000 * 2),
                DemoNotification("COIN", "Coins Earned", "Earned 500 coins from auction.", "COIN", System.currentTimeMillis() - 86400000 * 1)
            )
        ),
        DemoUserProfile(
            id = "demo-general-sameer",
            credential = DemoCredential("demo_consumer2", "password123"),
            fullName = "Sameer Kulkarni",
            role = UserType.GENERAL,
            phoneNumber = "+91 90200 22222",
            email = "sameer.k@demo.rostry.app",
            location = "Guntur, Andhra Pradesh",
            headline = "Local foodie tracking farm-fresh offerings",
            tags = listOf("Farm visits", "Live auctions", "Gourmet cooking"),
            farmDetails = null,
            productListings = listOf(
                DemoProduct("Heritage Country Chicken", "Meat", 420, "Live bird purchase with on-site cleaning"),
                DemoProduct("Farm Fresh Eggs", "Eggs", 190, "Daily delivery from Urban Greens Farm")
            ),
            transactions = listOf(
                DemoTransaction("TXN-B210", "Urban Greens", 3200, "COMPLETED", "Monthly egg subscription"),
                DemoTransaction("TXN-B245", "Coastal Breeders", 5400, "COMPLETED", "Bulk rooster purchase for community event")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Vikas Rao", "Direct Message", "Shared recipes for heritage birds"),
                DemoSocialConnection("Coastal Breeders", "Following", "Alerts for flash sales")
            ),
            statusNote = "Available for pickup after 6 PM",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-B001",
                    status = "DELIVERED",
                    totalAmount = 610,
                    items = listOf(DemoOrderItem("Heritage Country Chicken", 1, 420), DemoOrderItem("Farm Fresh Eggs", 1, 190)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 10, "Guntur"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 8, "Urban Greens"),
                        DemoTrackingEvent("DELIVERED", System.currentTimeMillis() - 86400000 * 5, "Guntur")
                    )
                ),
                DemoOrder(
                    orderId = "ORD-B002",
                    status = "PENDING",
                    totalAmount = 380,
                    items = listOf(DemoOrderItem("Farm Fresh Eggs", 2, 190)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 1, "Guntur")
                    )
                )
            ),
            cartItems = listOf(
                DemoCartItem("Heritage Country Chicken", 2)
            ),
            wishlistItems = listOf(
                DemoWishlistItem("Heritage Country Chicken", System.currentTimeMillis() - 86400000 * 8),
                DemoWishlistItem("Farm Fresh Eggs", System.currentTimeMillis() - 86400000 * 4),
                DemoWishlistItem("Kadaknath Eggs", System.currentTimeMillis() - 86400000 * 6),
                DemoWishlistItem("Country Broiler", System.currentTimeMillis() - 86400000 * 2),
                DemoWishlistItem("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 5),
                DemoWishlistItem("Starter Chicks", System.currentTimeMillis() - 86400000 * 3)
            ),
            auctions = listOf(
                DemoAuction(
                    productId = "Kadaknath Female",
                    startTime = System.currentTimeMillis() - 86400000 * 3,
                    endTime = System.currentTimeMillis() + 86400000 * 2,
                    minPrice = 3000,
                    bids = listOf(
                        DemoBid(3100, System.currentTimeMillis() - 86400000 * 2),
                        DemoBid(3300, System.currentTimeMillis() - 3600000 * 18)
                    )
                )
            ),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = emptyList(),
            achievements = emptyList(),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 300, "Referral bonus", System.currentTimeMillis() - 86400000 * 5),
                DemoCoinTransaction("SPENT", -150, "Cart discount", System.currentTimeMillis() - 86400000 * 2)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(22, 950, "weekly"),
                DemoLeaderboardEntry(38, 720, "monthly")
            ),
            comments = listOf(
                DemoComment("POST-007", "Delicious recipes using farm eggs!", System.currentTimeMillis() - 86400000 * 3),
                DemoComment("POST-008", "Visited Urban Greens last week.", System.currentTimeMillis() - 86400000 * 1)
            ),
            likes = listOf(
                DemoLike("POST-007", System.currentTimeMillis() - 86400000 * 3),
                DemoLike("POST-009", System.currentTimeMillis() - 86400000 * 2),
                DemoLike("POST-010", System.currentTimeMillis() - 3600000 * 8),
                DemoLike("POST-011", System.currentTimeMillis() - 3600000 * 16),
                DemoLike("POST-012", System.currentTimeMillis() - 86400000 * 4)
            ),
            follows = listOf(
                DemoFollow("demo-farmer-guntur", System.currentTimeMillis() - 86400000 * 9),
                DemoFollow("demo-farmer-vizag", System.currentTimeMillis() - 86400000 * 6),
                DemoFollow("demo-enthusiast-verified", System.currentTimeMillis() - 86400000 * 4),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 2)
            ),
            groups = listOf(
                DemoGroup("Gourmet Cooking Club", "Sharing recipes with heritage ingredients", "Cooking"),
                DemoGroup("Farm Visit Enthusiasts", "Planning visits to local farms", "Community")
            ),
            events = listOf(
                DemoEvent("Live Auction Night", "Weekly auctions with gourmet pairings", "Guntur Agri Hall", System.currentTimeMillis() + 86400000 * 5, "ATTENDING"),
                DemoEvent("Recipe Workshop", "Cooking with farm-fresh produce", "Urban Greens Farm", System.currentTimeMillis() + 86400000 * 10, "ATTENDING")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-farmer-guntur", "Hi Bhavani, interested in your feed pack?", System.currentTimeMillis() - 86400000 * 2),
                DemoChatMessage("demo-enthusiast-verified", "Great tips on egg quality!", System.currentTimeMillis() - 3600000 * 10)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-verified", "Egg quality grading", System.currentTimeMillis() + 86400000 * 4, "PENDING")
            ),
            familyTreeNodes = emptyList(),
            lifecycleEvents = emptyList(),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("ORDER", "Order Shipped", "Your order ORD-B001 is on the way.", "ORDER", System.currentTimeMillis() - 86400000 * 8),
                DemoNotification("AUCTION", "Auction Won", "You won the bid for Kadaknath Female.", "AUCTION", System.currentTimeMillis() - 3600000 * 18),
                DemoNotification("SOCIAL", "New Comment", "Someone commented on your post.", "SOCIAL", System.currentTimeMillis() - 86400000 * 3),
                DemoNotification("PAYMENT", "Refund Processed", "Refund for TXN-B245 issued.", "PAYMENT", System.currentTimeMillis() - 86400000 * 6),
                DemoNotification("EVENT", "RSVP Confirmed", "You're attending Live Auction Night.", "EVENT", System.currentTimeMillis() - 3600000 * 4),
                DemoNotification("COIN", "Coins Spent", "Spent 150 coins on discount.", "COIN", System.currentTimeMillis() - 86400000 * 2)
            )
        ),
        DemoUserProfile(
            id = "demo-general-urban",
            credential = DemoCredential("demo_urban3", "password123"),
            fullName = "Pavani Devi",
            role = UserType.GENERAL,
            phoneNumber = "+91 90400 33333",
            email = "pavani.devi@demo.rostry.app",
            location = "Visakhapatnam, Andhra Pradesh",
            headline = "Smart city buyer using farm-to-table trackers",
            tags = listOf("Traceability", "Cold storage", "Community auctions"),
            farmDetails = null,
            productListings = listOf(
                DemoProduct("Herbal Feed Kit", "Supplies", 750, "Partnered with Vizag agri university"),
                DemoProduct("Traceability Badge", "Digital", 120, "Verified lineage badge unlock")
            ),
            transactions = listOf(
                DemoTransaction("TXN-C130", "Vizag Collective", 2100, "COMPLETED", "Live auction purchase with delivery"),
                DemoTransaction("TXN-C178", "Harbor Farms", 980, "REFUNDED", "Returned due to schedule conflict")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Harbor Farms", "Following", "Monitors weekly catches"),
                DemoSocialConnection("Andhra Rooster Club", "Group Admin", "Hosts quarterly meetups")
            ),
            statusNote = "Testing new cold-chain alerts beta",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-C001",
                    status = "DELIVERED",
                    totalAmount = 870,
                    items = listOf(DemoOrderItem("Herbal Feed Kit", 1, 750), DemoOrderItem("Traceability Badge", 1, 120)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 12, "Visakhapatnam"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 10, "Vizag Collective"),
                        DemoTrackingEvent("DELIVERED", System.currentTimeMillis() - 86400000 * 7, "Visakhapatnam")
                    )
                )
            ),
            cartItems = listOf(
                DemoCartItem("Traceability Badge", 2)
            ),
            wishlistItems = listOf(
                DemoWishlistItem("Herbal Feed Kit", System.currentTimeMillis() - 86400000 * 9),
                DemoWishlistItem("Traceability Badge", System.currentTimeMillis() - 86400000 * 5),
                DemoWishlistItem("Kadaknath Eggs", System.currentTimeMillis() - 86400000 * 7),
                DemoWishlistItem("Country Broiler", System.currentTimeMillis() - 86400000 * 3),
                DemoWishlistItem("Heritage Country Chicken", System.currentTimeMillis() - 86400000 * 4),
                DemoWishlistItem("Farm Fresh Eggs", System.currentTimeMillis() - 86400000 * 6)
            ),
            auctions = listOf(
                DemoAuction(
                    productId = "Premium Kadaknath Trio",
                    startTime = System.currentTimeMillis() - 86400000 * 4,
                    endTime = System.currentTimeMillis() + 86400000 * 3,
                    minPrice = 8500,
                    bids = listOf(
                        DemoBid(8600, System.currentTimeMillis() - 86400000 * 3),
                        DemoBid(8800, System.currentTimeMillis() - 3600000 * 20)
                    )
                )
            ),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = emptyList(),
            achievements = emptyList(),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 400, "Beta tester reward", System.currentTimeMillis() - 86400000 * 7),
                DemoCoinTransaction("SPENT", -100, "Badge purchase", System.currentTimeMillis() - 86400000 * 4)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(8, 1450, "weekly"),
                DemoLeaderboardEntry(25, 1100, "monthly")
            ),
            comments = listOf(
                DemoComment("POST-013", "Cold-chain tracking is amazing!", System.currentTimeMillis() - 86400000 * 4),
                DemoComment("POST-014", "Beta testing the new badges.", System.currentTimeMillis() - 86400000 * 2)
            ),
            likes = listOf(
                DemoLike("POST-013", System.currentTimeMillis() - 86400000 * 4),
                DemoLike("POST-015", System.currentTimeMillis() - 86400000 * 3),
                DemoLike("POST-016", System.currentTimeMillis() - 3600000 * 10),
                DemoLike("POST-017", System.currentTimeMillis() - 3600000 * 20),
                DemoLike("POST-018", System.currentTimeMillis() - 86400000 * 5)
            ),
            follows = listOf(
                DemoFollow("demo-farmer-vizag", System.currentTimeMillis() - 86400000 * 8),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 5),
                DemoFollow("demo-enthusiast-premium", System.currentTimeMillis() - 86400000 * 3)
            ),
            groups = listOf(
                DemoGroup("Andhra Rooster Club", "Quarterly meetups for breeders", "Breeding"),
                DemoGroup("Traceability Advocates", "Discussing farm-to-table tech", "Technology")
            ),
            events = listOf(
                DemoEvent("Cold-Chain Demo", "New cold storage solutions", "Vizag Tech Park", System.currentTimeMillis() + 86400000 * 6, "ATTENDING"),
                DemoEvent("Community Auction", "Monthly auction with traceability", "Harbor Farms", System.currentTimeMillis() + 86400000 * 12, "INTERESTED")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-farmer-vizag", "Hi Savitri, interested in traceability kits?", System.currentTimeMillis() - 86400000 * 3),
                DemoChatMessage("demo-enthusiast-expert", "Thanks for the beta invite!", System.currentTimeMillis() - 3600000 * 8)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-expert", "Traceability setup", System.currentTimeMillis() + 86400000 * 5, "CONFIRMED")
            ),
            familyTreeNodes = emptyList(),
            lifecycleEvents = emptyList(),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("ORDER", "Order Delivered", "Your order ORD-C001 arrived.", "ORDER", System.currentTimeMillis() - 86400000 * 7),
                DemoNotification("AUCTION", "Bid Update", "New bid on Premium Kadaknath Trio.", "AUCTION", System.currentTimeMillis() - 3600000 * 20),
                DemoNotification("SOCIAL", "Group Invite", "Invited to Traceability Advocates.", "SOCIAL", System.currentTimeMillis() - 86400000 * 4),
                DemoNotification("PAYMENT", "Refund Issued", "Refund for TXN-C178 processed.", "PAYMENT", System.currentTimeMillis() - 86400000 * 8),
                DemoNotification("EVENT", "Event Update", "Cold-Chain Demo details changed.", "EVENT", System.currentTimeMillis() - 3600000 * 6),
                DemoNotification("COIN", "Coins Earned", "Earned 400 coins for beta testing.", "COIN", System.currentTimeMillis() - 86400000 * 7)
            )
        ),
        DemoUserProfile(
            id = "demo-farmer-vijayawada",
            credential = DemoCredential("demo_farmer1", "password123"),
            fullName = "Raghav Reddy",
            role = UserType.FARMER,
            phoneNumber = "+91 90550 44444",
            email = "raghav.reddy@demo.rostry.app",
            location = "Vijayawada Rural, Andhra Pradesh",
            headline = "Co-op farmer shipping verified heritage roosters",
            tags = listOf("Logistics ready", "Co-op lead", "FSSAI certified"),
            farmDetails = DemoFarmDetails(
                location = "Penamaluru Mandal",
                acreage = 8.5,
                breeds = listOf("Aseel", "Kadaknath", "Telangana Palla"),
                highlights = listOf("Solar brooder housing", "On-site vet station", "Cold chain ready trucks")
            ),
            productListings = listOf(
                DemoProduct("Aseel Game Bird", "Live Bird", 5200, "Champion lineage with vaccination records"),
                DemoProduct("Kadaknath Female", "Breeding", 3400, "2.1kg average, 98% hatch rate")
            ),
            transactions = listOf(
                DemoTransaction("TXN-F101", "Hyderabad Elite", 15600, "COMPLETED", "Bulk sale to premium buyer"),
                DemoTransaction("TXN-F133", "Vizag Resort", 9800, "PENDING", "Awaiting transport scheduling")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Amaravati Logistics", "Partner", "Handles cold chain deliveries"),
                DemoSocialConnection("Vijayawada Co-op", "Moderator", "Leads pricing discussions")
            ),
            statusNote = "Open for farm visits every Thursday",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-F001",
                    status = "SHIPPED",
                    totalAmount = 8600,
                    items = listOf(DemoOrderItem("Aseel Game Bird", 1, 5200), DemoOrderItem("Kadaknath Female", 1, 3400)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 5, "Hyderabad"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 2, "Vijayawada")
                    )
                )
            ),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = listOf(
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 30, 2.5, "Corn and greens", "Healthy activity", 28.0, 65.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 29, 2.1, "Rice bran mix", "Egg laid", 27.5, 70.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 28, 2.6, "Protein supplement", "Weight gain noted", 29.0, 68.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 27, 2.2, "Fermented feed", "Good appetite", 28.5, 72.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 26, 2.7, "Mixed grains", "Active behavior", 30.0, 66.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 25, 2.3, "Herbal mix", "Feathers clean", 27.0, 69.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 24, 2.8, "Corn pellets", "Strong comb", 28.5, 67.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 23, 2.4, "Balanced diet", "Consistent output", 29.5, 71.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 22, 2.9, "High protein", "Muscle development", 31.0, 65.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 21, 2.5, "Vitamin enriched", "Egg quality improved", 28.0, 73.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 20, 3.0, "Performance feed", "Peak condition", 29.5, 68.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 19, 2.6, "Organic blend", "Healthy droppings", 27.5, 70.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 18, 3.1, "Champ feed", "Ready for show", 30.5, 66.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 17, 2.7, "Layer pellets", "Regular laying", 28.5, 72.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 16, 3.2, "Premium mix", "Excellent health", 29.0, 69.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 15, 2.8, "Heritage grains", "Strong bones", 27.0, 71.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 14, 3.3, "Game bird formula", "Fighting fit", 31.5, 67.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 13, 2.9, "Balanced nutrition", "Good fertility", 28.0, 73.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 12, 3.4, "High energy", "Peak performance", 30.0, 68.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 11, 3.0, "Breeding feed", "Prime condition", 29.5, 70.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 10, 3.5, "Show quality", "Champion ready", 29.5, 66.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 9, 3.1, "Layer premium", "High output", 28.5, 72.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 8, 3.6, "Competition diet", "Top form", 30.5, 69.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 7, 3.2, "Organic layers", "Consistent eggs", 27.5, 71.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 6, 3.7, "Elite feed", "Show winner", 31.0, 67.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 5, 3.3, "Heritage blend", "Excellent health", 28.0, 73.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 4, 3.8, "Premium pellets", "Prime weight", 29.0, 68.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 3, 3.4, "Breeding formula", "Fertile period", 29.5, 70.0),
                DemoDailyLog("Aseel Game Bird", System.currentTimeMillis() - 86400000 * 2, 3.9, "Champ mix", "Competition ready", 30.5, 66.0),
                DemoDailyLog("Kadaknath Female", System.currentTimeMillis() - 86400000 * 1, 3.5, "Layer gold", "Peak laying", 28.5, 72.0)
            ),
            tasks = listOf(
                DemoTask("FEEDING", "Morning feed distribution", System.currentTimeMillis() - 3600000 * 2, "HIGH", "COMPLETED", null),
                DemoTask("HEALTH_CHECK", "Weekly health inspection", System.currentTimeMillis() + 3600000 * 4, "MEDIUM", "PENDING", null),
                DemoTask("CLEANING", "Coop deep clean", System.currentTimeMillis() + 86400000 * 1, "LOW", "PENDING", null),
                DemoTask("VACCINATION", "Booster shots due", System.currentTimeMillis() - 86400000 * 3, "HIGH", "OVERDUE", null),
                DemoTask("WEIGHING", "Monthly weight check", System.currentTimeMillis() + 86400000 * 2, "MEDIUM", "PENDING", null),
                DemoTask("RECORDING", "Update breeding logs", System.currentTimeMillis() + 3600000 * 6, "LOW", "PENDING", null),
                DemoTask("SUPPLY_ORDER", "Order new feed bags", System.currentTimeMillis() - 86400000 * 1, "MEDIUM", "COMPLETED", null),
                DemoTask("VET_VISIT", "Schedule vet checkup", System.currentTimeMillis() + 86400000 * 3, "HIGH", "PENDING", null)
            ),
            growthRecords = listOf(
                DemoGrowthRecord("Aseel Game Bird", 1, 0.8, 15.0, "Healthy start"),
                DemoGrowthRecord("Kadaknath Female", 1, 0.7, 14.5, "Good development"),
                DemoGrowthRecord("Aseel Game Bird", 2, 1.2, 18.0, "Rapid growth"),
                DemoGrowthRecord("Kadaknath Female", 2, 1.1, 17.5, "On track"),
                DemoGrowthRecord("Aseel Game Bird", 3, 1.6, 21.0, "Strong build"),
                DemoGrowthRecord("Kadaknath Female", 3, 1.5, 20.5, "Healthy weight"),
                DemoGrowthRecord("Aseel Game Bird", 4, 2.0, 24.0, "Muscle development"),
                DemoGrowthRecord("Kadaknath Female", 4, 1.9, 23.5, "Good appetite"),
                DemoGrowthRecord("Aseel Game Bird", 5, 2.4, 27.0, "Peak growth"),
                DemoGrowthRecord("Kadaknath Female", 5, 2.3, 26.5, "Excellent health"),
                DemoGrowthRecord("Aseel Game Bird", 6, 2.8, 30.0, "Show condition"),
                DemoGrowthRecord("Kadaknath Female", 6, 2.7, 29.5, "Prime weight"),
                DemoGrowthRecord("Aseel Game Bird", 7, 3.2, 33.0, "Champion ready"),
                DemoGrowthRecord("Kadaknath Female", 7, 3.1, 32.5, "Breeding age"),
                DemoGrowthRecord("Aseel Game Bird", 8, 3.6, 36.0, "Elite status"),
                DemoGrowthRecord("Kadaknath Female", 8, 3.5, 35.5, "Full maturity")
            ),
            quarantineRecords = listOf(
                DemoQuarantineRecord("Aseel Game Bird", "Respiratory infection", "COMPLETED", System.currentTimeMillis() - 86400000 * 20, System.currentTimeMillis() - 86400000 * 10),
                DemoQuarantineRecord("Kadaknath Female", "Contact precaution", "ACTIVE", System.currentTimeMillis() - 86400000 * 5, null)
            ),
            mortalityRecords = listOf(
                DemoMortalityRecord("Aseel Game Bird", "Natural causes", 52, System.currentTimeMillis() - 86400000 * 15),
                DemoMortalityRecord("Kadaknath Female", "Predator attack", 24, System.currentTimeMillis() - 86400000 * 8),
                DemoMortalityRecord("Aseel Game Bird", "Disease", 18, System.currentTimeMillis() - 86400000 * 12),
                DemoMortalityRecord("Kadaknath Female", "Injury", 36, System.currentTimeMillis() - 86400000 * 6),
                DemoMortalityRecord("Aseel Game Bird", "Old age", 60, System.currentTimeMillis() - 86400000 * 10)
            ),
            vaccinationRecords = listOf(
                DemoVaccinationRecord("Aseel Game Bird", "Newcastle", System.currentTimeMillis() - 86400000 * 14, System.currentTimeMillis() - 86400000 * 14),
                DemoVaccinationRecord("Kadaknath Female", "Marek's", System.currentTimeMillis() - 86400000 * 10, System.currentTimeMillis() - 86400000 * 10),
                DemoVaccinationRecord("Aseel Game Bird", "IBD", System.currentTimeMillis() - 86400000 * 7, null),
                DemoVaccinationRecord("Kadaknath Female", "Coryza", System.currentTimeMillis() - 86400000 * 3, System.currentTimeMillis() - 86400000 * 3),
                DemoVaccinationRecord("Aseel Game Bird", "Fowl Pox", System.currentTimeMillis() + 86400000 * 2, null),
                DemoVaccinationRecord("Kadaknath Female", "Booster", System.currentTimeMillis() + 86400000 * 5, null)
            ),
            hatchingBatches = listOf(
                DemoHatchingBatch("Spring Hatch 2024", 50, System.currentTimeMillis() + 86400000 * 14, "INCUBATING"),
                DemoHatchingBatch("Winter Reserve", 30, System.currentTimeMillis() - 86400000 * 10, "HATCHED")
            ),
            breedingPairs = listOf(
                DemoBreedingPair("Aseel Game Bird", "Kadaknath Female", "ACTIVE", 12),
                DemoBreedingPair("Aseel Game Bird", "Kadaknath Female", "RESTING", 8),
                DemoBreedingPair("Aseel Game Bird", "Kadaknath Female", "ACTIVE", 15),
                DemoBreedingPair("Aseel Game Bird", "Kadaknath Female", "INACTIVE", 5)
            ),
            achievements = emptyList(),
            coinTransactions = emptyList(),
            leaderboardEntries = emptyList(),
            comments = listOf(
                DemoComment("POST-019", "Farm visit was excellent!", System.currentTimeMillis() - 86400000 * 2),
                DemoComment("POST-020", "Co-op meeting notes shared.", System.currentTimeMillis() - 86400000 * 1)
            ),
            likes = listOf(
                DemoLike("POST-021", System.currentTimeMillis() - 86400000 * 3),
                DemoLike("POST-022", System.currentTimeMillis() - 86400000 * 2)
            ),
            follows = listOf(
                DemoFollow("demo-general-asharao", System.currentTimeMillis() - 86400000 * 5),
                DemoFollow("demo-general-sameer", System.currentTimeMillis() - 86400000 * 3)
            ),
            groups = listOf(
                DemoGroup("Vijayawada Co-op", "Regional farmer discussions", "Farming"),
                DemoGroup("Heritage Breeders Alliance", "Preserving native breeds", "Breeding")
            ),
            events = listOf(
                DemoEvent("Co-op Meeting", "Monthly pricing discussion", "Vijayawada Co-op Hall", System.currentTimeMillis() + 86400000 * 8, "ORGANIZING"),
                DemoEvent("Farm Open Day", "Public farm visit", "Penamaluru Farm", System.currentTimeMillis() + 86400000 * 15, "ATTENDING")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-general-asharao", "Farm visit confirmed for Thursday.", System.currentTimeMillis() - 3600000 * 4),
                DemoChatMessage("demo-general-sameer", "Thanks for the feed advice!", System.currentTimeMillis() - 3600000 * 8)
            ),
            expertBookings = emptyList(),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Aseel Game Bird", "Champion Line A", "PARENT"),
                DemoFamilyTreeNode("Kadaknath Female", "Pure Kadaknath", "PARENT"),
                DemoFamilyTreeNode("Aseel Game Bird", "Kadaknath Female", "MATING"),
                DemoFamilyTreeNode("Kadaknath Female", "Aseel Game Bird", "MATING")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Aseel Game Bird", 4, "GROWTH", "WEIGHT_CHECK", "2.0kg milestone"),
                DemoLifecycleEvent("Kadaknath Female", 6, "BREEDING", "FIRST_EGG", "Started laying"),
                DemoLifecycleEvent("Aseel Game Bird", 8, "HEALTH", "VACCINATION", "Full schedule complete"),
                DemoLifecycleEvent("Kadaknath Female", 12, "PRODUCTION", "PEAK_LAYING", "High egg output")
            ),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("FARM", "Task Overdue", "Vaccination booster due.", "FARM", System.currentTimeMillis() - 86400000 * 3),
                DemoNotification("ORDER", "Order Shipped", "ORD-F001 is on the way.", "ORDER", System.currentTimeMillis() - 86400000 * 2),
                DemoNotification("SOCIAL", "New Follower", "demo-general-asharao followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 5),
                DemoNotification("TRANSFER", "Transfer Completed", "TXN-F101 settled.", "TRANSFER", System.currentTimeMillis() - 86400000 * 10),
                DemoNotification("EVENT", "Event Reminder", "Co-op Meeting tomorrow.", "EVENT", System.currentTimeMillis() - 3600000 * 12),
                DemoNotification("FARM", "Quarantine Alert", "Kadaknath Female in quarantine.", "FARM", System.currentTimeMillis() - 86400000 * 5)
            )
        ),
        DemoUserProfile(
            id = "demo-farmer-guntur",
            credential = DemoCredential("demo_farmer2", "password123"),
            fullName = "Bhavani Prasad",
            role = UserType.FARMER,
            phoneNumber = "+91 90660 55555",
            email = "bhavani.prasad@demo.rostry.app",
            location = "Guntur, Andhra Pradesh",
            headline = "Organic farmer specializing in native feed blends",
            tags = listOf("Organic", "Feed innovator", "District winner"),
            farmDetails = DemoFarmDetails(
                location = "Tenali Delta",
                acreage = 6.0,
                breeds = listOf("Country Broiler", "Aseel", "Giriraja"),
                highlights = listOf("Fermented feed program", "Biogas powered incubators", "District best farm 2023")
            ),
            productListings = listOf(
                DemoProduct("Organic Feed Pack", "Supplies", 1250, "Fermented feed with neem & moringa"),
                DemoProduct("Starter Chicks", "Chicks", 420, "Day-old vaccinated chicks, min order 10")
            ),
            transactions = listOf(
                DemoTransaction("TXN-F202", "Urban Greens", 7200, "COMPLETED", "Monthly feed subscription"),
                DemoTransaction("TXN-F233", "FarmRight", 5400, "COMPLETED", "Starter chicks consignment")
            ),
            socialConnections = listOf(
                DemoSocialConnection("FarmRight Coop", "Partner", "Collaborates on regional fairs"),
                DemoSocialConnection("Agri University Guntur", "Advisor", "Participates in feed trials")
            ),
            statusNote = "Upcoming feed workshop in October",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-F002",
                    status = "DELIVERED",
                    totalAmount = 1670,
                    items = listOf(DemoOrderItem("Organic Feed Pack", 1, 1250), DemoOrderItem("Starter Chicks", 1, 420)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 8, "Urban Greens"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 6, "Guntur"),
                        DemoTrackingEvent("DELIVERED", System.currentTimeMillis() - 86400000 * 4, "Urban Greens")
                    )
                )
            ),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = listOf(
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 25, 1.8, "Fermented grains", "Good fermentation", 26.0, 75.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 24, 0.1, "Starter crumble", "Chicks active", 32.0, 60.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 23, 1.9, "Neem supplement", "Healthy droppings", 27.0, 72.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 22, 0.15, "Medicated feed", "No issues", 31.5, 62.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 21, 2.0, "Moringa leaves", "Strong immunity", 28.0, 70.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 20, 0.2, "Grower feed", "Feathers emerging", 30.0, 65.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 19, 2.1, "Herbal mix", "Excellent health", 26.5, 73.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 18, 0.25, "Balanced diet", "Good growth", 31.0, 63.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 17, 2.2, "Organic grains", "Natural behavior", 27.5, 71.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 16, 0.3, "Vitamin boost", "Active chicks", 29.5, 66.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 15, 2.3, "Fermentation peak", "Prime quality", 28.5, 69.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 14, 0.35, "Prebiotic feed", "Strong immunity", 30.5, 64.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 13, 2.4, "Neem infusion", "Pest resistant", 27.0, 72.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 12, 0.4, "Grower pellets", "Healthy weight", 31.5, 62.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 11, 2.5, "Moringa powder", "High nutrition", 28.0, 70.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 10, 0.45, "Layer starter", "Transitioning", 30.0, 65.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 9, 2.6, "Complete organic", "Market ready", 26.5, 73.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 8, 0.5, "Finisher feed", "Ready for sale", 31.0, 63.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 7, 2.7, "Premium blend", "Award quality", 27.5, 71.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 6, 0.55, "Organic transition", "Natural growth", 29.5, 66.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 5, 2.8, "District winner", "Certified organic", 28.5, 69.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 4, 0.6, "Final growth", "Prime condition", 30.5, 64.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 3, 2.9, "Workshop sample", "Educational", 27.0, 72.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis() - 86400000 * 2, 0.65, "Market feed", "Sale ready", 31.5, 62.0),
                DemoDailyLog("Organic Feed Pack", System.currentTimeMillis() - 86400000 * 1, 3.0, "Fresh batch", "New production", 28.0, 70.0),
                DemoDailyLog("Starter Chicks", System.currentTimeMillis(), 0.7, "Premium organic", "Top quality", 30.0, 65.0)
            ),
            tasks = listOf(
                DemoTask("FEEDING", "Fermentation monitoring", System.currentTimeMillis() - 3600000 * 3, "HIGH", "COMPLETED", null),
                DemoTask("RESEARCH", "Feed trial analysis", System.currentTimeMillis() + 3600000 * 2, "MEDIUM", "PENDING", null),
                DemoTask("CLEANING", "Incubator maintenance", System.currentTimeMillis() + 86400000 * 1, "LOW", "PENDING", null),
                DemoTask("VACCINATION", "Chick vaccination", System.currentTimeMillis() - 86400000 * 2, "HIGH", "COMPLETED", null),
                DemoTask("HARVESTING", "Feed ingredient harvest", System.currentTimeMillis() + 86400000 * 2, "MEDIUM", "PENDING", null),
                DemoTask("RECORDING", "Organic certification docs", System.currentTimeMillis() + 3600000 * 8, "LOW", "PENDING", null),
                DemoTask("SUPPLY_ORDER", "Order neem leaves", System.currentTimeMillis() - 86400000 * 1, "MEDIUM", "COMPLETED", null),
                DemoTask("WORKSHOP", "Prepare feed workshop", System.currentTimeMillis() + 86400000 * 5, "HIGH", "PENDING", null)
            ),
            growthRecords = listOf(
                DemoGrowthRecord("Starter Chicks", 1, 0.05, 8.0, "Healthy hatch"),
                DemoGrowthRecord("Starter Chicks", 2, 0.15, 12.0, "Rapid growth"),
                DemoGrowthRecord("Starter Chicks", 3, 0.25, 16.0, "Feather development"),
                DemoGrowthRecord("Starter Chicks", 4, 0.35, 20.0, "Strong legs"),
                DemoGrowthRecord("Starter Chicks", 5, 0.45, 24.0, "Active behavior"),
                DemoGrowthRecord("Starter Chicks", 6, 0.55, 28.0, "Juvenile stage"),
                DemoGrowthRecord("Starter Chicks", 7, 0.65, 32.0, "Adolescent growth"),
                DemoGrowthRecord("Starter Chicks", 8, 0.75, 36.0, "Market ready")
            ),
            quarantineRecords = listOf(
                DemoQuarantineRecord("Starter Chicks", "Hatchery protocol", "COMPLETED", System.currentTimeMillis() - 86400000 * 15, System.currentTimeMillis() - 86400000 * 8)
            ),
            mortalityRecords = listOf(
                DemoMortalityRecord("Starter Chicks", "Hatch failure", 1, System.currentTimeMillis() - 86400000 * 20),
                DemoMortalityRecord("Starter Chicks", "Weak chick", 3, System.currentTimeMillis() - 86400000 * 18),
                DemoMortalityRecord("Starter Chicks", "Disease", 5, System.currentTimeMillis() - 86400000 * 12)
            ),
            vaccinationRecords = listOf(
                DemoVaccinationRecord("Starter Chicks", "Marek's", System.currentTimeMillis() - 86400000 * 20, System.currentTimeMillis() - 86400000 * 20),
                DemoVaccinationRecord("Starter Chicks", "Newcastle", System.currentTimeMillis() - 86400000 * 15, System.currentTimeMillis() - 86400000 * 15),
                DemoVaccinationRecord("Starter Chicks", "IBD", System.currentTimeMillis() - 86400000 * 10, System.currentTimeMillis() - 86400000 * 10),
                DemoVaccinationRecord("Starter Chicks", "Coryza", System.currentTimeMillis() - 86400000 * 5, System.currentTimeMillis() - 86400000 * 5)
            ),
            hatchingBatches = listOf(
                DemoHatchingBatch("Organic Hatch 2024", 100, System.currentTimeMillis() + 86400000 * 10, "INCUBATING")
            ),
            breedingPairs = listOf(
                DemoBreedingPair("Country Broiler", "Aseel", "ACTIVE", 20),
                DemoBreedingPair("Giriraja", "Country Broiler", "RESTING", 10)
            ),
            achievements = emptyList(),
            coinTransactions = emptyList(),
            leaderboardEntries = emptyList(),
            comments = listOf(
                DemoComment("POST-023", "Organic feed workshop was great!", System.currentTimeMillis() - 86400000 * 3),
                DemoComment("POST-024", "Sharing fermentation recipe.", System.currentTimeMillis() - 86400000 * 1)
            ),
            likes = listOf(
                DemoLike("POST-025", System.currentTimeMillis() - 86400000 * 4),
                DemoLike("POST-026", System.currentTimeMillis() - 86400000 * 2)
            ),
            follows = listOf(
                DemoFollow("demo-general-sameer", System.currentTimeMillis() - 86400000 * 6),
                DemoFollow("demo-general-urban", System.currentTimeMillis() - 86400000 * 4)
            ),
            groups = listOf(
                DemoGroup("Organic Farmers Network", "Sustainable farming practices", "Farming"),
                DemoGroup("Feed Innovation Hub", "Developing better feeds", "Research")
            ),
            events = listOf(
                DemoEvent("Feed Workshop", "Organic feed preparation", "Tenali Farm", System.currentTimeMillis() + 86400000 * 12, "ORGANIZING"),
                DemoEvent("District Fair", "Showcase innovations", "Guntur Agri Expo", System.currentTimeMillis() + 86400000 * 20, "ATTENDING")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-general-sameer", "Interested in your organic feed?", System.currentTimeMillis() - 3600000 * 6),
                DemoChatMessage("demo-general-urban", "Great workshop last time!", System.currentTimeMillis() - 3600000 * 10)
            ),
            expertBookings = emptyList(),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Country Broiler", "Local Heritage", "PARENT"),
                DemoFamilyTreeNode("Aseel", "Game Breed Pure", "PARENT"),
                DemoFamilyTreeNode("Giriraja", "Native Andhra", "PARENT")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Starter Chicks", 1, "HATCHING", "DAY_OLD", "Vaccinated at hatch"),
                DemoLifecycleEvent("Starter Chicks", 4, "GROWTH", "FEATHERING", "Complete coverage"),
                DemoLifecycleEvent("Starter Chicks", 8, "HEALTH", "VACCINATION", "Full schedule"),
                DemoLifecycleEvent("Organic Feed Pack", 1, "PRODUCTION", "FERMENTATION", "Started process")
            ),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("FARM", "Workshop Reminder", "Feed workshop in 5 days.", "FARM", System.currentTimeMillis() - 3600000 * 8),
                DemoNotification("ORDER", "Order Delivered", "ORD-F002 received.", "ORDER", System.currentTimeMillis() - 86400000 * 4),
                DemoNotification("SOCIAL", "New Follower", "demo-general-sameer followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 6),
                DemoNotification("TRANSFER", "Transfer Completed", "TXN-F202 settled.", "TRANSFER", System.currentTimeMillis() - 86400000 * 12),
                DemoNotification("EVENT", "Event Update", "District Fair confirmed.", "EVENT", System.currentTimeMillis() - 3600000 * 16),
                DemoNotification("FARM", "Task Completed", "Vaccination done.", "FARM", System.currentTimeMillis() - 86400000 * 2)
            )
        ),
        DemoUserProfile(
            id = "demo-farmer-vizag",
            credential = DemoCredential("demo_farmer3", "password123"),
            fullName = "Savitri Naidu",
            role = UserType.FARMER,
            phoneNumber = "+91 90770 66666",
            email = "savitri.naidu@demo.rostry.app",
            location = "Visakhapatnam Coast, Andhra Pradesh",
            headline = "Coastal farmer exporting premium birds",
            tags = listOf("Export ready", "Traceability champion", "Women entrepreneur"),
            farmDetails = DemoFarmDetails(
                location = "Bheemili Mandal",
                acreage = 11.2,
                breeds = listOf("Kadaknath", "White Leghorn", "Aseel"),
                highlights = listOf("Seawater cooled sheds", "Export compliant packaging", "Traceability QR codes")
            ),
            productListings = listOf(
                DemoProduct("Premium Kadaknath Trio", "Breeding", 8900, "Breeding trio with DNA certificate"),
                DemoProduct("Traceability Kit", "Digital", 1400, "QR stickers with blockchain logs")
            ),
            transactions = listOf(
                DemoTransaction("TXN-F330", "Dubai Gourmet", 25600, "COMPLETED", "Export order via special permit"),
                DemoTransaction("TXN-F345", "Bengaluru Bistro", 11200, "COMPLETED", "Weekly supply contract")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Women Agri Collective", "Member", "Mentors coastal entrepreneurs"),
                DemoSocialConnection("Vizag Port Authority", "Partner", "Supports cold chain logistics")
            ),
            statusNote = "Pilot export route to Singapore in testing",
            orders = listOf(
                DemoOrder(
                    orderId = "ORD-F003",
                    status = "SHIPPED",
                    totalAmount = 10300,
                    items = listOf(DemoOrderItem("Premium Kadaknath Trio", 1, 8900), DemoOrderItem("Traceability Kit", 1, 1400)),
                    trackingEvents = listOf(
                        DemoTrackingEvent("ORDERED", System.currentTimeMillis() - 86400000 * 6, "Dubai Gourmet"),
                        DemoTrackingEvent("SHIPPED", System.currentTimeMillis() - 86400000 * 3, "Vizag Port")
                    )
                )
            ),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = listOf(
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 28, 3.2, "Export quality feed", "Health check passed", 25.0, 78.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 27, 1.5, "QR verification", "Codes working", 24.5, 80.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 26, 3.3, "Premium pellets", "Export ready", 26.0, 76.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 25, 1.6, "Blockchain sync", "Logs verified", 25.5, 79.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 24, 3.4, "DNA certified", "Certificate attached", 24.0, 81.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 23, 1.7, "Cold chain test", "Temperature stable", 26.5, 77.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 22, 3.5, "Breeding feed", "Fertility high", 25.0, 79.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 21, 1.8, "Export packaging", "Compliance checked", 24.5, 80.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 20, 3.6, "Show quality", "Champion potential", 26.0, 76.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 19, 1.9, "QR scanning", "All working", 25.5, 78.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 18, 3.7, "Elite nutrition", "Top condition", 24.0, 81.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 17, 2.0, "Blockchain update", "Latest tech", 26.5, 77.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 16, 3.8, "Export standard", "Permit ready", 25.0, 79.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 15, 2.1, "Cold storage", "Temperature logged", 24.5, 80.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 14, 3.9, "Premium care", "Health optimal", 26.0, 76.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 13, 2.2, "Digital logs", "Fully traceable", 25.5, 78.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 12, 4.0, "Breeding prime", "Ready for trio", 24.0, 81.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 11, 2.3, "Export docs", "Paperwork complete", 26.5, 77.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 10, 4.1, "DNA verified", "Certificate valid", 25.0, 79.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 9, 2.4, "Blockchain secure", "Immutable logs", 24.5, 80.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 8, 4.2, "Export quality", "Dubai standard", 26.0, 76.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 7, 2.5, "QR stickers", "Applied successfully", 25.5, 78.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 6, 4.3, "Trio complete", "Male and females", 24.0, 81.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 5, 2.6, "Cold chain", "Maintained", 26.5, 77.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 4, 4.4, "Premium package", "Luxury export", 25.0, 79.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 3, 2.7, "Digital certificate", "Blockchain verified", 24.5, 80.0),
                DemoDailyLog("Premium Kadaknath Trio", System.currentTimeMillis() - 86400000 * 2, 4.5, "Final check", "Export ready", 26.0, 76.0),
                DemoDailyLog("Traceability Kit", System.currentTimeMillis() - 86400000 * 1, 2.8, "Shipping prep", "All set", 25.5, 78.0)
            ),
            tasks = listOf(
                DemoTask("EXPORT", "Documentation check", System.currentTimeMillis() - 3600000 * 4, "HIGH", "COMPLETED", null),
                DemoTask("TRACEABILITY", "QR code verification", System.currentTimeMillis() + 3600000 * 1, "MEDIUM", "PENDING", null),
                DemoTask("PACKAGING", "Export box preparation", System.currentTimeMillis() + 86400000 * 1, "LOW", "PENDING", null),
                DemoTask("VACCINATION", "Export health cert", System.currentTimeMillis() - 86400000 * 4, "HIGH", "COMPLETED", null),
                DemoTask("LOGISTICS", "Port coordination", System.currentTimeMillis() + 86400000 * 2, "MEDIUM", "PENDING", null),
                DemoTask("RECORDING", "Blockchain updates", System.currentTimeMillis() + 3600000 * 6, "LOW", "PENDING", null),
                DemoTask("SUPPLY_ORDER", "Order export materials", System.currentTimeMillis() - 86400000 * 2, "MEDIUM", "COMPLETED", null),
                DemoTask("CERTIFICATION", "DNA verification", System.currentTimeMillis() + 86400000 * 4, "HIGH", "PENDING", null)
            ),
            growthRecords = listOf(
                DemoGrowthRecord("Premium Kadaknath Trio", 12, 3.5, 45.0, "Breeding age"),
                DemoGrowthRecord("Premium Kadaknath Trio", 16, 4.0, 50.0, "Prime weight"),
                DemoGrowthRecord("Premium Kadaknath Trio", 20, 4.5, 55.0, "Export quality"),
                DemoGrowthRecord("Premium Kadaknath Trio", 24, 5.0, 60.0, "Champion size")
            ),
            quarantineRecords = listOf(
                DemoQuarantineRecord("Premium Kadaknath Trio", "Export protocol", "COMPLETED", System.currentTimeMillis() - 86400000 * 10, System.currentTimeMillis() - 86400000 * 5)
            ),
            mortalityRecords = listOf(
                DemoMortalityRecord("White Leghorn", "Heat stress", 48, System.currentTimeMillis() - 86400000 * 18),
                DemoMortalityRecord("Aseel", "Shipping injury", 36, System.currentTimeMillis() - 86400000 * 14)
            ),
            vaccinationRecords = listOf(
                DemoVaccinationRecord("Premium Kadaknath Trio", "Export vaccines", System.currentTimeMillis() - 86400000 * 12, System.currentTimeMillis() - 86400000 * 12),
                DemoVaccinationRecord("Premium Kadaknath Trio", "Health certificate", System.currentTimeMillis() - 86400000 * 8, System.currentTimeMillis() - 86400000 * 8),
                DemoVaccinationRecord("White Leghorn", "Layer vaccines", System.currentTimeMillis() - 86400000 * 6, System.currentTimeMillis() - 86400000 * 6),
                DemoVaccinationRecord("Aseel", "Game bird shots", System.currentTimeMillis() - 86400000 * 4, System.currentTimeMillis() - 86400000 * 4)
            ),
            hatchingBatches = listOf(
                DemoHatchingBatch("Export Quality 2024", 75, System.currentTimeMillis() + 86400000 * 7, "INCUBATING"),
                DemoHatchingBatch("Singapore Pilot", 25, System.currentTimeMillis() + 86400000 * 21, "PLANNED")
            ),
            breedingPairs = listOf(
                DemoBreedingPair("Kadaknath", "White Leghorn", "ACTIVE", 18),
                DemoBreedingPair("Aseel", "Kadaknath", "ACTIVE", 22),
                DemoBreedingPair("White Leghorn", "Aseel", "RESTING", 12)
            ),
            achievements = emptyList(),
            coinTransactions = emptyList(),
            leaderboardEntries = emptyList(),
            comments = listOf(
                DemoComment("POST-027", "Export process going smoothly!", System.currentTimeMillis() - 86400000 * 4),
                DemoComment("POST-028", "Traceability tech is amazing.", System.currentTimeMillis() - 86400000 * 2)
            ),
            likes = listOf(
                DemoLike("POST-029", System.currentTimeMillis() - 86400000 * 5),
                DemoLike("POST-030", System.currentTimeMillis() - 86400000 * 3)
            ),
            follows = listOf(
                DemoFollow("demo-general-urban", System.currentTimeMillis() - 86400000 * 7),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 5)
            ),
            groups = listOf(
                DemoGroup("Women Agri Collective", "Empowering women farmers", "Community"),
                DemoGroup("Export Farmers Network", "International trade support", "Business")
            ),
            events = listOf(
                DemoEvent("Export Workshop", "Documentation and logistics", "Vizag Port", System.currentTimeMillis() + 86400000 * 9, "ORGANIZING"),
                DemoEvent("Singapore Pilot Launch", "New export route", "Vizag Airport", System.currentTimeMillis() + 86400000 * 25, "ATTENDING")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-general-urban", "Interested in traceability kits?", System.currentTimeMillis() - 3600000 * 5),
                DemoChatMessage("demo-enthusiast-expert", "Great export work!", System.currentTimeMillis() - 3600000 * 9)
            ),
            expertBookings = emptyList(),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Premium Kadaknath Trio", "DNA Certified Line", "PARENT"),
                DemoFamilyTreeNode("White Leghorn", "Export White", "PARENT"),
                DemoFamilyTreeNode("Aseel", "Coastal Game", "PARENT"),
                DemoFamilyTreeNode("Kadaknath", "Black Gold", "PARENT")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Premium Kadaknath Trio", 12, "BREEDING", "DNA_CERT", "Certificate issued"),
                DemoLifecycleEvent("Premium Kadaknath Trio", 16, "EXPORT", "HEALTH_CHECK", "Vet approved"),
                DemoLifecycleEvent("White Leghorn", 20, "PRODUCTION", "EGG_LAYING", "High output"),
                DemoLifecycleEvent("Aseel", 24, "COMPETITION", "SHOW_PREP", "Ready for events")
            ),
            matingLogs = emptyList(),
            eggCollections = emptyList(),
            notifications = listOf(
                DemoNotification("FARM", "Export Reminder", "Documentation due soon.", "FARM", System.currentTimeMillis() - 3600000 * 6),
                DemoNotification("ORDER", "Order Shipped", "ORD-F003 exported.", "ORDER", System.currentTimeMillis() - 86400000 * 3),
                DemoNotification("SOCIAL", "New Follower", "demo-general-urban followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 7),
                DemoNotification("TRANSFER", "Transfer Completed", "TXN-F330 settled.", "TRANSFER", System.currentTimeMillis() - 86400000 * 14),
                DemoNotification("EVENT", "Event Update", "Singapore Pilot confirmed.", "EVENT", System.currentTimeMillis() - 3600000 * 18),
                DemoNotification("FARM", "Task Completed", "Vaccination certified.", "FARM", System.currentTimeMillis() - 86400000 * 4)
            )
        ),
        DemoUserProfile(
            id = "demo-enthusiast-premium",
            credential = DemoCredential("demo_breeder1", "password123"),
            fullName = "Arjun Menon",
            role = UserType.ENTHUSIAST,
            phoneNumber = "+91 90880 77777",
            email = "arjun.menon@demo.rostry.app",
            location = "Vijayawada, Andhra Pradesh",
            headline = "Premium breeder tracking elite bloodlines",
            tags = listOf("AI analytics", "Champion birds", "Premium auctions"),
            farmDetails = DemoFarmDetails(
                location = "Kanuru",
                acreage = 3.5,
                breeds = listOf("Aseel", "Malay", "American Game"),
                highlights = listOf("AI weight tracking", "24 metrics per bird", "Encrypted lineage vault")
            ),
            productListings = listOf(
                DemoProduct("Grand Champion Package", "Breeding", 15800, "Includes mating pair with lineage dossier"),
                DemoProduct("Telemetry Harness", "Accessories", 950, "Real-time activity tracker for game birds")
            ),
            transactions = listOf(
                DemoTransaction("TXN-E110", "Vizag Royals", 18600, "COMPLETED", "Champion pair sale"),
                DemoTransaction("TXN-E145", "Auction House", 22000, "SETTLED", "Championship winnings")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Vizag Royals", "Alliance", "Co-manages premium bloodline"),
                DemoSocialConnection("Heritage Auctions", "Host", "Regular panel expert")
            ),
            statusNote = "Running beta tests for AI recommendations",
            orders = emptyList(),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = listOf(
                DemoBreedingPair("Aseel", "Malay", "ACTIVE", 8),
                DemoBreedingPair("American Game", "Aseel", "ACTIVE", 12),
                DemoBreedingPair("Malay", "American Game", "RESTING", 6)
            ),
            achievements = listOf(
                DemoAchievement("ACH-001", "First Champion", "Won first championship", 1000, System.currentTimeMillis() - 86400000 * 30),
                DemoAchievement("ACH-002", "Bloodline Master", "Tracked 50+ generations", 2500, System.currentTimeMillis() - 86400000 * 20),
                DemoAchievement("ACH-003", "AI Pioneer", "Beta tester for AI analytics", 1500, System.currentTimeMillis() - 86400000 * 15),
                DemoAchievement("ACH-004", "Auction Expert", "100 successful auctions", 2000, System.currentTimeMillis() - 86400000 * 10),
                DemoAchievement("ACH-005", "Telemetry Innovator", "Pioneered real-time tracking", 3000, null)
            ),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 5000, "Championship winnings", System.currentTimeMillis() - 86400000 * 30),
                DemoCoinTransaction("EARNED", 2500, "Bloodline achievement", System.currentTimeMillis() - 86400000 * 20),
                DemoCoinTransaction("EARNED", 1500, "AI beta reward", System.currentTimeMillis() - 86400000 * 15),
                DemoCoinTransaction("EARNED", 2000, "Auction milestone", System.currentTimeMillis() - 86400000 * 10),
                DemoCoinTransaction("SPENT", -800, "Telemetry harness", System.currentTimeMillis() - 86400000 * 5),
                DemoCoinTransaction("EARNED", 1200, "Referral bonus", System.currentTimeMillis() - 86400000 * 3),
                DemoCoinTransaction("SPENT", -500, "Premium feed", System.currentTimeMillis() - 86400000 * 1),
                DemoCoinTransaction("EARNED", 800, "Social engagement", System.currentTimeMillis() - 3600000 * 12),
                DemoCoinTransaction("SPENT", -300, "Auction fee", System.currentTimeMillis() - 3600000 * 6),
                DemoCoinTransaction("EARNED", 600, "Weekly activity", System.currentTimeMillis() - 3600000 * 1)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(1, 25000, "weekly"),
                DemoLeaderboardEntry(2, 18500, "monthly"),
                DemoLeaderboardEntry(5, 12000, "yearly")
            ),
            comments = listOf(
                DemoComment("POST-031", "AI analytics are revolutionary!", System.currentTimeMillis() - 86400000 * 5),
                DemoComment("POST-032", "Sharing champion bloodline tips.", System.currentTimeMillis() - 86400000 * 3)
            ),
            likes = listOf(
                DemoLike("POST-033", System.currentTimeMillis() - 86400000 * 6),
                DemoLike("POST-034", System.currentTimeMillis() - 86400000 * 4),
                DemoLike("POST-035", System.currentTimeMillis() - 3600000 * 10)
            ),
            follows = listOf(
                DemoFollow("demo-farmer-vijayawada", System.currentTimeMillis() - 86400000 * 8),
                DemoFollow("demo-enthusiast-verified", System.currentTimeMillis() - 86400000 * 6),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 4)
            ),
            groups = listOf(
                DemoGroup("Elite Breeders Club", "Premium bloodline discussions", "Breeding"),
                DemoGroup("AI Analytics Users", "Sharing AI insights", "Technology"),
                DemoGroup("Championship Circuit", "Competition preparation", "Sports")
            ),
            events = listOf(
                DemoEvent("AI Demo Day", "Showcase analytics features", "Kanuru Tech Hub", System.currentTimeMillis() + 86400000 * 4, "ORGANIZING"),
                DemoEvent("Championship Finals", "Annual game bird competition", "Vizag Arena", System.currentTimeMillis() + 86400000 * 18, "ATTENDING"),
                DemoEvent("Bloodline Conference", "Elite breeder networking", "Hyderabad Convention", System.currentTimeMillis() + 86400000 * 30, "INTERESTED")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-enthusiast-verified", "Interested in your AI analytics?", System.currentTimeMillis() - 3600000 * 7),
                DemoChatMessage("demo-enthusiast-expert", "Great championship results!", System.currentTimeMillis() - 3600000 * 11),
                DemoChatMessage("demo-farmer-vijayawada", "Want to discuss Aseel bloodlines?", System.currentTimeMillis() - 3600000 * 15)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-expert", "AI implementation", System.currentTimeMillis() + 86400000 * 2, "CONFIRMED"),
                DemoExpertBooking("demo-enthusiast-verified", "Bloodline verification", System.currentTimeMillis() + 86400000 * 8, "PENDING")
            ),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Aseel", "Champion Line Elite", "PARENT"),
                DemoFamilyTreeNode("Malay", "Malay Pure Blood", "PARENT"),
                DemoFamilyTreeNode("American Game", "American Import", "PARENT"),
                DemoFamilyTreeNode("Aseel", "Malay", "MATING"),
                DemoFamilyTreeNode("American Game", "Aseel", "MATING"),
                DemoFamilyTreeNode("Malay", "American Game", "MATING"),
                DemoFamilyTreeNode("Grand Champion Package", "Aseel", "OFFSPRING"),
                DemoFamilyTreeNode("Grand Champion Package", "Malay", "OFFSPRING")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Aseel", 24, "COMPETITION", "CHAMPION", "Won elite title"),
                DemoLifecycleEvent("Malay", 18, "BREEDING", "MATING", "Paired with Aseel"),
                DemoLifecycleEvent("American Game", 20, "HEALTH", "TELEMETRY", "Tracker attached"),
                DemoLifecycleEvent("Grand Champion Package", 1, "BIRTH", "DNA_TEST", "Bloodline verified")
            ),
            matingLogs = listOf(
                DemoMatingLog("PAIR-001", System.currentTimeMillis() - 86400000 * 25, "Aggressive courtship", "Successful mating observed"),
                DemoMatingLog("PAIR-002", System.currentTimeMillis() - 86400000 * 20, "Multiple attempts", "Pair bonding strong"),
                DemoMatingLog("PAIR-003", System.currentTimeMillis() - 86400000 * 15, "AI monitored", "Optimal timing detected"),
                DemoMatingLog("PAIR-004", System.currentTimeMillis() - 86400000 * 10, "Telemetry data", "Activity peaks noted"),
                DemoMatingLog("PAIR-005", System.currentTimeMillis() - 86400000 * 5, "Bloodline match", "Genetic compatibility high"),
                DemoMatingLog("PAIR-006", System.currentTimeMillis() - 3600000 * 48, "Premium pair", "Elite breeding event")
            ),
            eggCollections = listOf(
                DemoEggCollection("PAIR-001", 3, "A", System.currentTimeMillis() - 86400000 * 22),
                DemoEggCollection("PAIR-002", 4, "A+", System.currentTimeMillis() - 86400000 * 17),
                DemoEggCollection("PAIR-003", 2, "B", System.currentTimeMillis() - 86400000 * 12),
                DemoEggCollection("PAIR-004", 5, "A", System.currentTimeMillis() - 86400000 * 7),
                DemoEggCollection("PAIR-005", 3, "A+", System.currentTimeMillis() - 86400000 * 2),
                DemoEggCollection("PAIR-006", 4, "A", System.currentTimeMillis() - 3600000 * 24),
                DemoEggCollection("PAIR-001", 2, "B", System.currentTimeMillis() - 3600000 * 12),
                DemoEggCollection("PAIR-002", 3, "A", System.currentTimeMillis() - 3600000 * 6)
            ),
            notifications = listOf(
                DemoNotification("SOCIAL", "Achievement Unlocked", "Bloodline Master achieved!", "SOCIAL", System.currentTimeMillis() - 86400000 * 20),
                DemoNotification("COIN", "Coins Earned", "2500 coins from achievement.", "COIN", System.currentTimeMillis() - 86400000 * 20),
                DemoNotification("EVENT", "Event Reminder", "AI Demo Day tomorrow.", "EVENT", System.currentTimeMillis() - 3600000 * 20),
                DemoNotification("SOCIAL", "New Follower", "demo-farmer-vijayawada followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 8),
                DemoNotification("BREEDING", "Mating Success", "PAIR-006 mated successfully.", "BREEDING", System.currentTimeMillis() - 3600000 * 48),
                DemoNotification("COIN", "Leaderboard Bonus", "Top spot bonus: 1000 coins.", "COIN", System.currentTimeMillis() - 86400000 * 7)
            )
        ),
        DemoUserProfile(
            id = "demo-enthusiast-verified",
            credential = DemoCredential("demo_enthusiast2", "password123"),
            fullName = "Meera Chaitanya",
            role = UserType.ENTHUSIAST,
            phoneNumber = "+91 90990 88888",
            email = "meera.chaitanya@demo.rostry.app",
            location = "Guntur, Andhra Pradesh",
            headline = "Verified breeder mentoring upcoming talent",
            tags = listOf("Mentor", "Verified breeder", "Social community"),
            farmDetails = DemoFarmDetails(
                location = "Amaravati",
                acreage = 2.8,
                breeds = listOf("Kadaknath", "Kalamashi", "Aseel"),
                highlights = listOf("Mentor program", "Breeding logs", "Hatchery tie-ups")
            ),
            productListings = listOf(
                DemoProduct("Mentor Session", "Service", 1500, "1:1 video guidance for breeding setup"),
                DemoProduct("Hatchling Pack", "Chicks", 5800, "Verified genetic lineage, includes feed starter kit")
            ),
            transactions = listOf(
                DemoTransaction("TXN-E220", "FarmRight", 7800, "COMPLETED", "Mentorship subscription"),
                DemoTransaction("TXN-E255", "Future Breeders", 9400, "PENDING", "Awaiting verification documents")
            ),
            socialConnections = listOf(
                DemoSocialConnection("Future Breeders", "Community Lead", "Hosts monthly webinars"),
                DemoSocialConnection("FarmRight", "Mentor", "Leads training cohort")
            ),
            statusNote = "Curating verified breeder leaderboard",
            orders = emptyList(),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = listOf(
                DemoBreedingPair("Kadaknath", "Kalamashi", "ACTIVE", 15),
                DemoBreedingPair("Aseel", "Kadaknath", "ACTIVE", 10),
                DemoBreedingPair("Kalamashi", "Aseel", "RESTING", 8)
            ),
            achievements = listOf(
                DemoAchievement("ACH-006", "Mentor Extraordinaire", "Mentored 50+ breeders", 1800, System.currentTimeMillis() - 86400000 * 25),
                DemoAchievement("ACH-007", "Verified Breeder", "Passed elite verification", 2200, System.currentTimeMillis() - 86400000 * 18),
                DemoAchievement("ACH-008", "Community Builder", "Built largest breeding group", 1600, System.currentTimeMillis() - 86400000 * 12),
                DemoAchievement("ACH-009", "Hatchery Partner", "Established 5 hatchery ties", 1900, System.currentTimeMillis() - 86400000 * 8),
                DemoAchievement("ACH-010", "Leaderboard Curator", "Maintained top breeder rankings", 1400, null)
            ),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 1800, "Mentor achievement", System.currentTimeMillis() - 86400000 * 25),
                DemoCoinTransaction("EARNED", 2200, "Verification bonus", System.currentTimeMillis() - 86400000 * 18),
                DemoCoinTransaction("EARNED", 1600, "Community building", System.currentTimeMillis() - 86400000 * 12),
                DemoCoinTransaction("EARNED", 1900, "Hatchery partnership", System.currentTimeMillis() - 86400000 * 8),
                DemoCoinTransaction("SPENT", -600, "Mentor session materials", System.currentTimeMillis() - 86400000 * 6),
                DemoCoinTransaction("EARNED", 1000, "Webinar hosting", System.currentTimeMillis() - 86400000 * 4),
                DemoCoinTransaction("SPENT", -400, "Group event", System.currentTimeMillis() - 86400000 * 2),
                DemoCoinTransaction("EARNED", 700, "Mentorship completion", System.currentTimeMillis() - 3600000 * 24),
                DemoCoinTransaction("SPENT", -200, "Verification fee", System.currentTimeMillis() - 3600000 * 12),
                DemoCoinTransaction("EARNED", 500, "Monthly activity", System.currentTimeMillis() - 3600000 * 1)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(3, 21800, "weekly"),
                DemoLeaderboardEntry(4, 15200, "monthly"),
                DemoLeaderboardEntry(7, 9800, "yearly")
            ),
            comments = listOf(
                DemoComment("POST-036", "Mentoring is so rewarding!", System.currentTimeMillis() - 86400000 * 6),
                DemoComment("POST-037", "Sharing verified breeding tips.", System.currentTimeMillis() - 86400000 * 4)
            ),
            likes = listOf(
                DemoLike("POST-038", System.currentTimeMillis() - 86400000 * 7),
                DemoLike("POST-039", System.currentTimeMillis() - 86400000 * 5),
                DemoLike("POST-040", System.currentTimeMillis() - 3600000 * 8)
            ),
            follows = listOf(
                DemoFollow("demo-enthusiast-premium", System.currentTimeMillis() - 86400000 * 9),
                DemoFollow("demo-enthusiast-expert", System.currentTimeMillis() - 86400000 * 7),
                DemoFollow("demo-farmer-guntur", System.currentTimeMillis() - 86400000 * 5)
            ),
            groups = listOf(
                DemoGroup("Future Breeders", "Mentoring new talent", "Education"),
                DemoGroup("Verified Breeders Network", "Elite breeder discussions", "Breeding"),
                DemoGroup("Mentor Community", "Sharing knowledge", "Community")
            ),
            events = listOf(
                DemoEvent("Monthly Webinar", "Breeding best practices", "Amaravati Center", System.currentTimeMillis() + 86400000 * 6, "ORGANIZING"),
                DemoEvent("Verification Workshop", "Getting verified as breeder", "Guntur Agri University", System.currentTimeMillis() + 86400000 * 15, "ATTENDING"),
                DemoEvent("Mentor Meetup", "Networking for mentors", "Hyderabad Hub", System.currentTimeMillis() + 86400000 * 25, "INTERESTED")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-enthusiast-premium", "Want to collaborate on mentoring?", System.currentTimeMillis() - 3600000 * 8),
                DemoChatMessage("demo-enthusiast-expert", "Great webinar series!", System.currentTimeMillis() - 3600000 * 12),
                DemoChatMessage("demo-farmer-guntur", "Thanks for the feed advice!", System.currentTimeMillis() - 3600000 * 16)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-expert", "Verification process", System.currentTimeMillis() + 86400000 * 3, "CONFIRMED"),
                DemoExpertBooking("demo-enthusiast-premium", "Mentoring techniques", System.currentTimeMillis() + 86400000 * 10, "PENDING")
            ),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Kadaknath", "Verified Black", "PARENT"),
                DemoFamilyTreeNode("Kalamashi", "Kalamashi Heritage", "PARENT"),
                DemoFamilyTreeNode("Aseel", "Aseel Champion", "PARENT"),
                DemoFamilyTreeNode("Kadaknath", "Kalamashi", "MATING"),
                DemoFamilyTreeNode("Aseel", "Kadaknath", "MATING"),
                DemoFamilyTreeNode("Kalamashi", "Aseel", "MATING"),
                DemoFamilyTreeNode("Hatchling Pack", "Kadaknath", "OFFSPRING"),
                DemoFamilyTreeNode("Hatchling Pack", "Kalamashi", "OFFSPRING")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Kadaknath", 16, "VERIFICATION", "BREEDER_CERT", "Verified elite breeder"),
                DemoLifecycleEvent("Kalamashi", 14, "MENTORING", "TRAINING", "Mentored 10 breeders"),
                DemoLifecycleEvent("Aseel", 18, "COMMUNITY", "GROUP_LEAD", "Founded Future Breeders"),
                DemoLifecycleEvent("Hatchling Pack", 1, "BIRTH", "VERIFIED", "Genetic verification complete")
            ),
            matingLogs = listOf(
                DemoMatingLog("PAIR-007", System.currentTimeMillis() - 86400000 * 28, "Gentle introduction", "Pair bonded quickly"),
                DemoMatingLog("PAIR-008", System.currentTimeMillis() - 86400000 * 23, "Mentored pairing", "Observed by trainees"),
                DemoMatingLog("PAIR-009", System.currentTimeMillis() - 86400000 * 18, "Verified genetics", "DNA matched pairs"),
                DemoMatingLog("PAIR-010", System.currentTimeMillis() - 86400000 * 13, "Community event", "Public demonstration"),
                DemoMatingLog("PAIR-011", System.currentTimeMillis() - 86400000 * 8, "Training session", "Educational mating"),
                DemoMatingLog("PAIR-012", System.currentTimeMillis() - 86400000 * 3, "Elite pairing", "Champion bloodlines")
            ),
            eggCollections = listOf(
                DemoEggCollection("PAIR-007", 4, "A", System.currentTimeMillis() - 86400000 * 25),
                DemoEggCollection("PAIR-008", 3, "A+", System.currentTimeMillis() - 86400000 * 20),
                DemoEggCollection("PAIR-009", 5, "A", System.currentTimeMillis() - 86400000 * 15),
                DemoEggCollection("PAIR-010", 2, "B", System.currentTimeMillis() - 86400000 * 10),
                DemoEggCollection("PAIR-011", 4, "A+", System.currentTimeMillis() - 86400000 * 5),
                DemoEggCollection("PAIR-012", 3, "A", System.currentTimeMillis() - 3600000 * 48),
                DemoEggCollection("PAIR-007", 3, "A", System.currentTimeMillis() - 3600000 * 24),
                DemoEggCollection("PAIR-008", 4, "A+", System.currentTimeMillis() - 3600000 * 12)
            ),
            notifications = listOf(
                DemoNotification("SOCIAL", "Achievement Unlocked", "Verified Breeder achieved!", "SOCIAL", System.currentTimeMillis() - 86400000 * 18),
                DemoNotification("COIN", "Coins Earned", "2200 coins from verification.", "COIN", System.currentTimeMillis() - 86400000 * 18),
                DemoNotification("EVENT", "Event Reminder", "Monthly Webinar tomorrow.", "EVENT", System.currentTimeMillis() - 3600000 * 18),
                DemoNotification("SOCIAL", "New Follower", "demo-enthusiast-premium followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 9),
                DemoNotification("BREEDING", "Mating Success", "PAIR-012 mated successfully.", "BREEDING", System.currentTimeMillis() - 86400000 * 3),
                DemoNotification("COIN", "Mentor Bonus", "500 coins for mentorship.", "COIN", System.currentTimeMillis() - 3600000 * 1)
            )
        ),
        DemoUserProfile(
            id = "demo-enthusiast-expert",
            credential = DemoCredential("demo_expert3", "password123"),
            fullName = "Leela Fernandes",
            role = UserType.ENTHUSIAST,
            phoneNumber = "+91 91010 99999",
            email = "leela.fernandes@demo.rostry.app",
            location = "Visakhapatnam, Andhra Pradesh",
            headline = "High-level handler with global network",
            tags = listOf("Global network", "Transfer specialist", "Competition coach"),
            farmDetails = DemoFarmDetails(
                location = "Duvvada",
                acreage = 4.2,
                breeds = listOf("Aseel", "Old English", "Shamo"),
                highlights = listOf("International exports", "Virtual reality training", "Bloodline analytics")
            ),
            productListings = listOf(
                DemoProduct("Elite Handler Program", "Service", 18800, "8-week remote coaching with VR sessions"),
                DemoProduct("Transfer Assurance", "Service", 4200, "End-to-end compliance for transfers")
            ),
            transactions = listOf(
                DemoTransaction("TXN-E330", "International Arena", 32800, "COMPLETED", "Coaching retainer"),
                DemoTransaction("TXN-E355", "Vizag Exports", 16400, "COMPLETED", "Transfer assurance package")
            ),
            socialConnections = listOf(
                DemoSocialConnection("International Arena", "Partner", "Coaches competition teams"),
                DemoSocialConnection("Vizag Exports", "Advisor", "Supports export clearances")
            ),
            statusNote = "Piloting AR-based training analytics",
            orders = emptyList(),
            cartItems = emptyList(),
            wishlistItems = emptyList(),
            auctions = emptyList(),
            dailyLogs = emptyList(),
            tasks = emptyList(),
            growthRecords = emptyList(),
            quarantineRecords = emptyList(),
            mortalityRecords = emptyList(),
            vaccinationRecords = emptyList(),
            hatchingBatches = emptyList(),
            breedingPairs = listOf(
                DemoBreedingPair("Aseel", "Old English", "ACTIVE", 14),
                DemoBreedingPair("Shamo", "Aseel", "ACTIVE", 9),
                DemoBreedingPair("Old English", "Shamo", "RESTING", 11)
            ),
            achievements = listOf(
                DemoAchievement("ACH-011", "Global Networker", "Connected 200+ breeders worldwide", 2500, System.currentTimeMillis() - 86400000 * 28),
                DemoAchievement("ACH-012", "Transfer Specialist", "Handled 100+ transfers", 2100, System.currentTimeMillis() - 86400000 * 21),
                DemoAchievement("ACH-013", "Competition Coach", "Coached 50+ champions", 2300, System.currentTimeMillis() - 86400000 * 14),
                DemoAchievement("ACH-014", "VR Innovator", "Pioneered VR training", 2700, System.currentTimeMillis() - 86400000 * 9),
                DemoAchievement("ACH-015", "Export Expert", "Facilitated international exports", 2000, null)
            ),
            coinTransactions = listOf(
                DemoCoinTransaction("EARNED", 2500, "Global networking", System.currentTimeMillis() - 86400000 * 28),
                DemoCoinTransaction("EARNED", 2100, "Transfer expertise", System.currentTimeMillis() - 86400000 * 21),
                DemoCoinTransaction("EARNED", 2300, "Coaching success", System.currentTimeMillis() - 86400000 * 14),
                DemoCoinTransaction("EARNED", 2700, "VR innovation", System.currentTimeMillis() - 86400000 * 9),
                DemoCoinTransaction("SPENT", -1200, "VR equipment", System.currentTimeMillis() - 86400000 * 7),
                DemoCoinTransaction("EARNED", 1500, "International coaching", System.currentTimeMillis() - 86400000 * 5),
                DemoCoinTransaction("SPENT", -800, "Transfer materials", System.currentTimeMillis() - 86400000 * 3),
                DemoCoinTransaction("EARNED", 900, "Competition winnings", System.currentTimeMillis() - 3600000 * 48),
                DemoCoinTransaction("SPENT", -400, "Export fees", System.currentTimeMillis() - 3600000 * 24),
                DemoCoinTransaction("EARNED", 600, "Network growth", System.currentTimeMillis() - 3600000 * 1)
            ),
            leaderboardEntries = listOf(
                DemoLeaderboardEntry(2, 22800, "weekly"),
                DemoLeaderboardEntry(3, 16800, "monthly"),
                DemoLeaderboardEntry(6, 11200, "yearly")
            ),
            comments = listOf(
                DemoComment("POST-041", "Global networking is key!", System.currentTimeMillis() - 86400000 * 7),
                DemoComment("POST-042", "Sharing transfer expertise.", System.currentTimeMillis() - 86400000 * 5)
            ),
            likes = listOf(
                DemoLike("POST-043", System.currentTimeMillis() - 86400000 * 8),
                DemoLike("POST-044", System.currentTimeMillis() - 86400000 * 6),
                DemoLike("POST-045", System.currentTimeMillis() - 3600000 * 12)
            ),
            follows = listOf(
                DemoFollow("demo-enthusiast-premium", System.currentTimeMillis() - 86400000 * 10),
                DemoFollow("demo-enthusiast-verified", System.currentTimeMillis() - 86400000 * 8),
                DemoFollow("demo-farmer-vizag", System.currentTimeMillis() - 86400000 * 6)
            ),
            groups = listOf(
                DemoGroup("International Breeders", "Global breeding network", "International"),
                DemoGroup("Competition Coaches", "Training champions", "Sports"),
                DemoGroup("Transfer Specialists", "Expert transfer services", "Services")
            ),
            events = listOf(
                DemoEvent("Global Summit", "International breeder conference", "Dubai Convention", System.currentTimeMillis() + 86400000 * 8, "ATTENDING"),
                DemoEvent("VR Training Demo", "Showcase VR coaching", "Vizag Tech Center", System.currentTimeMillis() + 86400000 * 20, "ORGANIZING"),
                DemoEvent("Transfer Workshop", "Advanced transfer techniques", "Hyderabad Institute", System.currentTimeMillis() + 86400000 * 32, "INTERESTED")
            ),
            chatMessages = listOf(
                DemoChatMessage("demo-enthusiast-premium", "Let's discuss global opportunities?", System.currentTimeMillis() - 3600000 * 9),
                DemoChatMessage("demo-enthusiast-verified", "Need help with transfers?", System.currentTimeMillis() - 3600000 * 13),
                DemoChatMessage("demo-farmer-vizag", "Great export work!", System.currentTimeMillis() - 3600000 * 17)
            ),
            expertBookings = listOf(
                DemoExpertBooking("demo-enthusiast-premium", "Global networking", System.currentTimeMillis() + 86400000 * 5, "CONFIRMED"),
                DemoExpertBooking("demo-enthusiast-verified", "Transfer training", System.currentTimeMillis() + 86400000 * 12, "PENDING")
            ),
            familyTreeNodes = listOf(
                DemoFamilyTreeNode("Aseel", "Indian Champion", "PARENT"),
                DemoFamilyTreeNode("Old English", "English Import", "PARENT"),
                DemoFamilyTreeNode("Shamo", "Japanese Game", "PARENT"),
                DemoFamilyTreeNode("Aseel", "Old English", "MATING"),
                DemoFamilyTreeNode("Shamo", "Aseel", "MATING"),
                DemoFamilyTreeNode("Old English", "Shamo", "MATING"),
                DemoFamilyTreeNode("Elite Handler Program", "Aseel", "OFFSPRING"),
                DemoFamilyTreeNode("Elite Handler Program", "Old English", "OFFSPRING")
            ),
            lifecycleEvents = listOf(
                DemoLifecycleEvent("Aseel", 20, "INTERNATIONAL", "EXPORT", "First international export"),
                DemoLifecycleEvent("Old English", 16, "TRAINING", "VR_COACHING", "VR training pioneer"),
                DemoLifecycleEvent("Shamo", 18, "COMPETITION", "CHAMPION", "Won international title"),
                DemoLifecycleEvent("Elite Handler Program", 1, "SERVICE", "LAUNCH", "Program launched globally")
            ),
            matingLogs = listOf(
                DemoMatingLog("PAIR-013", System.currentTimeMillis() - 86400000 * 30, "International pairing", "Cross-cultural breeding"),
                DemoMatingLog("PAIR-014", System.currentTimeMillis() - 86400000 * 25, "Competition prep", "Training for events"),
                DemoMatingLog("PAIR-015", System.currentTimeMillis() - 86400000 * 20, "VR monitored", "Virtual observation"),
                DemoMatingLog("PAIR-016", System.currentTimeMillis() - 86400000 * 15, "Transfer assisted", "Smooth relocation"),
                DemoMatingLog("PAIR-017", System.currentTimeMillis() - 86400000 * 10, "Global network", "International collaboration"),
                DemoMatingLog("PAIR-018", System.currentTimeMillis() - 86400000 * 5, "Expert handling", "Professional management")
            ),
            eggCollections = listOf(
                DemoEggCollection("PAIR-013", 3, "A+", System.currentTimeMillis() - 86400000 * 27),
                DemoEggCollection("PAIR-014", 4, "A", System.currentTimeMillis() - 86400000 * 22),
                DemoEggCollection("PAIR-015", 2, "A+", System.currentTimeMillis() - 86400000 * 17),
                DemoEggCollection("PAIR-016", 5, "A", System.currentTimeMillis() - 86400000 * 12),
                DemoEggCollection("PAIR-017", 3, "A+", System.currentTimeMillis() - 86400000 * 7),
                DemoEggCollection("PAIR-018", 4, "A", System.currentTimeMillis() - 3600000 * 48),
                DemoEggCollection("PAIR-013", 2, "A", System.currentTimeMillis() - 3600000 * 24),
                DemoEggCollection("PAIR-014", 3, "A+", System.currentTimeMillis() - 3600000 * 12)
            ),
            notifications = listOf(
                DemoNotification("SOCIAL", "Achievement Unlocked", "Global Networker achieved!", "SOCIAL", System.currentTimeMillis() - 86400000 * 28),
                DemoNotification("COIN", "Coins Earned", "2500 coins from networking.", "COIN", System.currentTimeMillis() - 86400000 * 28),
                DemoNotification("EVENT", "Event Reminder", "Global Summit in 8 days.", "EVENT", System.currentTimeMillis() - 3600000 * 16),
                DemoNotification("SOCIAL", "New Follower", "demo-enthusiast-premium followed you.", "SOCIAL", System.currentTimeMillis() - 86400000 * 10),
                DemoNotification("BREEDING", "Mating Success", "PAIR-018 mated successfully.", "BREEDING", System.currentTimeMillis() - 86400000 * 5),
                DemoNotification("COIN", "Expert Bonus", "600 coins for expertise.", "COIN", System.currentTimeMillis() - 3600000 * 1)
            )
        )
    )

    val byUsername: Map<String, DemoUserProfile> = all.associateBy { it.credential.username }
    val byId: Map<String, DemoUserProfile> = all.associateBy { it.id }

    fun profilesByRole(role: UserType): List<DemoUserProfile> = all.filter { it.role == role }
    fun credentials(): Map<String, String> = all.associate { it.credential.username to it.credential.password }
}
