package com.rio.rostry.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.rio.rostry.domain.repository.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SyncManagerTest {
    
    private lateinit var syncManager: SyncManager
    private val context = mockk<Context>()
    private val connectivityManager = mockk<ConnectivityManager>()
    private val userRepository = mockk<UserRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val orderRepository = mockk<OrderRepository>()
    private val transferRepository = mockk<TransferRepository>()
    private val coinRepository = mockk<CoinRepository>()
    private val notificationRepository = mockk<NotificationRepository>()
    private val productTrackingRepository = mockk<ProductTrackingRepository>()
    private val familyTreeRepository = mockk<FamilyTreeRepository>()
    private val chatMessageRepository = mockk<ChatMessageRepository>()
    
    @Before
    fun setup() {
        mockkObject(SyncManager::class)
        
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        
        syncManager = SyncManager(
            context,
            userRepository,
            productRepository,
            orderRepository,
            transferRepository,
            coinRepository,
            notificationRepository,
            productTrackingRepository,
            familyTreeRepository,
            chatMessageRepository
        )
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `isNetworkAvailable returns true when network is available`() {
        val network = mockk<Network>()
        every { connectivityManager.activeNetwork } returns network
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        
        val result = syncManager.isNetworkAvailable()
        assertTrue("Network should be available", result)
    }
    
    @Test
    fun `isNetworkAvailable returns false when no network`() {
        every { connectivityManager.activeNetwork } returns null
        
        val result = syncManager.isNetworkAvailable()
        assertFalse("Network should not be available", result)
    }
    
    @Test
    fun `syncAll returns false when no network`() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns null
        
        runBlocking {
            val result = syncManager.syncAll()
            assertFalse("Sync should fail when no network", result)
        }
    }
    
    @Test
    fun `resolveConflicts returns remote entity for user conflict`() {
        val localUser = com.rio.rostry.domain.model.User(
            id = "user1",
            phone = "123",
            userType = com.rio.rostry.domain.model.UserType.FARMER,
            verificationStatus = com.rio.rostry.domain.model.VerificationStatus.VERIFIED,
            kycStatus = com.rio.rostry.domain.model.KycStatus.VERIFIED,
            coins = 100,
            createdAt = java.util.Date(System.currentTimeMillis() - 3600000),
            updatedAt = java.util.Date(System.currentTimeMillis() - 1800000)
        )
        
        val remoteUser = com.rio.rostry.domain.model.User(
            id = "user1",
            phone = "123",
            userType = com.rio.rostry.domain.model.UserType.FARMER,
            verificationStatus = com.rio.rostry.domain.model.VerificationStatus.VERIFIED,
            kycStatus = com.rio.rostry.domain.model.KycStatus.VERIFIED,
            coins = 150,
            createdAt = java.util.Date(System.currentTimeMillis() - 3600000),
            updatedAt = java.util.Date()
        )
        
        val result = syncManager.resolveConflicts("users", localUser, remoteUser)
        assertEquals("Should return remote user", remoteUser, result)
    }
}