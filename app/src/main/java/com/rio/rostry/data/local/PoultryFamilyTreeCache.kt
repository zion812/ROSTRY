package com.rio.rostry.data.local

import androidx.room.RoomDatabase
import com.rio.rostry.data.model.Poultry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoultryFamilyTreeCache @Inject constructor() {
    private val cache = mutableMapOf<String, Poultry>()
    private val familyTreeCache = mutableMapOf<String, com.rio.rostry.domain.model.FamilyTreeNode>()
    
    fun getPoultry(id: String): Poultry? = cache[id]
    
    fun putPoultry(poultry: Poultry) {
        cache[poultry.id] = poultry
    }
    
    fun getFamilyTree(rootId: String): com.rio.rostry.domain.model.FamilyTreeNode? = familyTreeCache[rootId]
    
    fun putFamilyTree(rootId: String, familyTree: com.rio.rostry.domain.model.FamilyTreeNode) {
        familyTreeCache[rootId] = familyTree
    }
    
    fun invalidatePoultry(id: String) {
        cache.remove(id)
    }
    
    fun invalidateFamilyTree(rootId: String) {
        familyTreeCache.remove(rootId)
    }
    
    fun clear() {
        cache.clear()
        familyTreeCache.clear()
    }
}