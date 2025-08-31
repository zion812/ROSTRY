package com.rio.rostry.data.location

import android.location.Location
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow

interface LocationService {
    fun requestLocationUpdates(): Flow<Result<Location>>
}
