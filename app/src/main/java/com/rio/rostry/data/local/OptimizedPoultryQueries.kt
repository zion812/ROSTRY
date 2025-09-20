package com.rio.rostry.data.local

import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptimizedPoultryQueries @Inject constructor(
    private val poultryDao: PoultryDao,
    private val cache: PoultryFamilyTreeCache
) {
    fun getPoultryWithCachedFallback(id: String): Flow<Poultry?> = flow {
        // First check cache
        val cached = cache.getPoultry(id)
        if (cached != null) {
            emit(cached)
            return@flow
        }
        
        // If not in cache, fetch from database and cache it
        val poultry = poultryDao.getPoultryById(id)
        poultry?.let {
            cache.putPoultry(it)
        }
        emit(poultry)
    }
    
    suspend fun getPoultryById(id: String): Poultry? {
        // First check cache
        val cached = cache.getPoultry(id)
        if (cached != null) {
            return cached
        }
        
        // If not in cache, fetch from database and cache it
        val poultry = poultryDao.getPoultryById(id)
        poultry?.let {
            cache.putPoultry(it)
        }
        return poultry
    }
    
    fun getChildrenWithCache(parentId: String): Flow<List<Poultry>> {
        // For children, we'll always fetch from database to ensure freshness
        // but cache the results for future use
        return poultryDao.getChildrenByParentId(parentId).map { children ->
            children.forEach { child ->
                cache.putPoultry(child)
            }
            children
        }
    }
}