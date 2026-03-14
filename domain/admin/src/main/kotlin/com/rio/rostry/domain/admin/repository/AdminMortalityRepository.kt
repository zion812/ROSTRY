package com.rio.rostry.domain.admin.repository

import com.rio.rostry.domain.admin.model.OutbreakAlert
import com.rio.rostry.domain.admin.model.RegionalMortality
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AdminMortalityRepository {
    fun getRegionalMortalityStats(): Flow<Resource<List<RegionalMortality>>>
    fun getPotentialOutbreaks(): Flow<Resource<List<OutbreakAlert>>>
}
