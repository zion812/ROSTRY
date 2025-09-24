package com.rio.rostry.data.demo

import com.rio.rostry.domain.model.UserType

data class DemoUser(
    val id: String,
    val name: String,
    val role: UserType,
    val description: String,
    val highlights: List<String>
)

object DemoUsers {
    val general = DemoUser(
        id = "demo-general-001",
        name = "Asha Rao",
        role = UserType.GENERAL,
        description = "Urban buyer exploring sustainable roosters",
        highlights = listOf(
            "Follows 8 farms",
            "Average monthly spend ₹3,200",
            "Participates in weekend community auctions"
        )
    )

    val farmer = DemoUser(
        id = "demo-farmer-001",
        name = "Manoj Patel",
        role = UserType.FARMER,
        description = "Heritage breeder managing a co-op farm",
        highlights = listOf(
            "Active listings: 24",
            "Verified geolocation & documents",
            "Ships to 6 nearby districts"
        )
    )

    val enthusiast = DemoUser(
        id = "demo-enthusiast-001",
        name = "Leela Fernandes",
        role = UserType.ENTHUSIAST,
        description = "Competitive handler tracking bloodlines",
        highlights = listOf(
            "Keeps 32 performance metrics",
            "Maintains coin wallet balance ₹18,500",
            "Participated in 12 transfers this season"
        )
    )

    val all: List<DemoUser> = listOf(general, farmer, enthusiast)
}
