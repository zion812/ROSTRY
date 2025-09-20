package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.PoultryDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetectFamilyTreeCyclesUseCase @Inject constructor(
    private val poultryDao: PoultryDao
) {
    suspend operator fun invoke(rootPoultryId: String): Result<Boolean> {
        return try {
            val allPoultry = poultryDao.getAllPoultry().first()
            val visited = mutableSetOf<String>()
            val recursionStack = mutableSetOf<String>()
            
            // Build adjacency list for parent-child relationships
            val adjacencyList = mutableMapOf<String, MutableList<String>>()
            
            allPoultry.forEach { poultry ->
                // Add parent1 relationship
                poultry.parentId1?.let { parentId ->
                    if (!adjacencyList.containsKey(parentId)) {
                        adjacencyList[parentId] = mutableListOf()
                    }
                    adjacencyList[parentId]?.add(poultry.id)
                }
                
                // Add parent2 relationship
                poultry.parentId2?.let { parentId ->
                    if (!adjacencyList.containsKey(parentId)) {
                        adjacencyList[parentId] = mutableListOf()
                    }
                    adjacencyList[parentId]?.add(poultry.id)
                }
            }
            
            // Check for cycles using DFS
            val hasCycle = hasCycleUtil(rootPoultryId, visited, recursionStack, adjacencyList)
            
            Result.success(hasCycle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun hasCycleUtil(
        nodeId: String,
        visited: MutableSet<String>,
        recursionStack: MutableSet<String>,
        adjacencyList: Map<String, List<String>>
    ): Boolean {
        if (!visited.contains(nodeId)) {
            visited.add(nodeId)
            recursionStack.add(nodeId)
            
            val children = adjacencyList[nodeId] ?: emptyList()
            
            for (childId in children) {
                if (!visited.contains(childId) && hasCycleUtil(childId, visited, recursionStack, adjacencyList)) {
                    return true
                } else if (recursionStack.contains(childId)) {
                    return true
                }
            }
        }
        
        recursionStack.remove(nodeId)
        return false
    }
}