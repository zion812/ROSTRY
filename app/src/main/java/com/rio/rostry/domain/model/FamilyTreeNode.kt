package com.rio.rostry.domain.model

import com.rio.rostry.data.model.Poultry
import com.rio.rostry.data.model.BreedingRecord

data class FamilyTreeNode(
    val poultry: Poultry,
    val parents: List<FamilyTreeNode> = emptyList(),
    val children: List<FamilyTreeNode> = emptyList(),
    val breedingRecords: List<BreedingRecord> = emptyList()
)