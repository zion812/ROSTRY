package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.PoultryDao
import javax.inject.Inject

class ValidateParentChildRelationshipUseCase @Inject constructor(
    private val poultryDao: PoultryDao
) {
    suspend operator fun invoke(parentId: String, childId: String): Result<Boolean> {
        return try {
            val parent = poultryDao.getPoultryById(parentId)
            val child = poultryDao.getPoultryById(childId)
            
            if (parent == null || child == null) {
                return Result.failure(Exception("Parent or child not found"))
            }
            
            // Check if child's parentId1 or parentId2 matches the parent
            val isParent = child.parentId1 == parentId || child.parentId2 == parentId
            
            Result.success(isParent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}