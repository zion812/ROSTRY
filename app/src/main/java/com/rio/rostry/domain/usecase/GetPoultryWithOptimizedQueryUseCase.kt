package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.OptimizedPoultryQueries
import com.rio.rostry.data.model.Poultry
import javax.inject.Inject

class GetPoultryWithOptimizedQueryUseCase @Inject constructor(
    private val optimizedQueries: OptimizedPoultryQueries
) {
    suspend operator fun invoke(id: String): Poultry? {
        return optimizedQueries.getPoultryById(id)
    }
}