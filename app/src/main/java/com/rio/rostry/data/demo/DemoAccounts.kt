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
    val statusNote: String?
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
            statusNote = "Prefers doorstep delivery on Saturdays"
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
            statusNote = "Available for pickup after 6 PM"
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
            statusNote = "Testing new cold-chain alerts beta"
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
            statusNote = "Open for farm visits every Thursday"
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
            statusNote = "Upcoming feed workshop in October"
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
            statusNote = "Pilot export route to Singapore in testing"
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
            statusNote = "Running beta tests for AI recommendations"
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
            statusNote = "Curating verified breeder leaderboard"
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
            statusNote = "Piloting AR-based training analytics"
        )
    )

    val byUsername: Map<String, DemoUserProfile> = all.associateBy { it.credential.username }
    val byId: Map<String, DemoUserProfile> = all.associateBy { it.id }

    fun profilesByRole(role: UserType): List<DemoUserProfile> = all.filter { it.role == role }
    fun credentials(): Map<String, String> = all.associate { it.credential.username to it.credential.password }
}
