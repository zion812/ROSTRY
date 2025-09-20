package com.rio.rostry.data.repository

import com.rio.rostry.data.local.ProductTrackingDao
import com.rio.rostry.domain.model.ProductTracking
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class ProductTrackingRepositoryImplTest {
    
    private lateinit var repository: ProductTrackingRepositoryImpl
    private val dao = mockk<ProductTrackingDao>()
    
    @Before
    fun setup() {
        repository = ProductTrackingRepositoryImpl(dao)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `insertProductTracking should call dao insert`() = runBlocking {
        val productTracking = ProductTracking(
            id = "tracking123",
            productId = "product123",
            userId = "user123",
            latitude = 40.7128,
            longitude = -74.0060,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        coEvery { dao.insertProductTracking(any()) } just runs
        
        repository.insertProductTracking(productTracking)
        
        coVerify(exactly = 1) { dao.insertProductTracking(any()) }
    }
    
    @Test
    fun `updateProductTracking should update timestamp`() = runBlocking {
        val originalTracking = ProductTracking(
            id = "tracking123",
            productId = "product123",
            userId = "user123",
            latitude = 40.7128,
            longitude = -74.0060,
            createdAt = Date(System.currentTimeMillis() - 3600000),
            updatedAt = Date(System.currentTimeMillis() - 1800000)
        )
        
        coEvery { dao.updateProductTracking(any()) } just runs
        
        repository.updateProductTracking(originalTracking)
        
        coVerify(exactly = 1) { dao.updateProductTracking(match { 
            it.updatedAt.after(originalTracking.updatedAt)
        }) }
    }
}