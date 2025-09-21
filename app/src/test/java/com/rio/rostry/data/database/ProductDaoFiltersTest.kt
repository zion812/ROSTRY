package com.rio.rostry.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.entity.ProductEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductDaoFiltersTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun filterByPriceBreed_basic() = runBlocking {
        val dao = db.productDao()
        val now = System.currentTimeMillis()
        val p1 = ProductEntity("p1","s1","A","","cat",100.0,1.0,"pc","loc",null,null, emptyList(), updatedAt = now, lastModifiedAt = now, breed = "Cobb")
        val p2 = ProductEntity("p2","s1","B","","cat",300.0,1.0,"pc","loc",null,null, emptyList(), updatedAt = now, lastModifiedAt = now, breed = "Ross")
        dao.insertProducts(listOf(p1,p2))
        val res = dao.filterByPriceBreed(50.0, 200.0, "Cobb")
        assertEquals(1, res.size)
        assertEquals("p1", res.first().productId)
    }

    @Test
    fun autocomplete_prefix() = runBlocking {
        val dao = db.productDao()
        val now = System.currentTimeMillis()
        val p1 = ProductEntity("p1","s1","Alpha","","cat",100.0,1.0,"pc","loc",null,null, emptyList(), updatedAt = now, lastModifiedAt = now, breed = "Cobb")
        val p2 = ProductEntity("p2","s1","Beta","","cat",100.0,1.0,"pc","loc",null,null, emptyList(), updatedAt = now, lastModifiedAt = now, breed = "Ross")
        dao.insertProducts(listOf(p1,p2))
        val res = dao.autocomplete("Al", 10)
        assertEquals(1, res.size)
        assertEquals("p1", res.first().productId)
    }
}
