package com.rio.rostry.data.repo

import com.rio.rostry.data.models.Fowl
import android.net.Uri
import androidx.paging.PagingData
import com.rio.rostry.data.models.FowlRecord
import kotlinx.coroutines.flow.Flow

interface FowlRepository {
    suspend fun addFowl(fowl: Fowl, photoUri: Uri?)
    fun getFowlById(fowlId: String): Flow<Fowl?>
    fun getFowls(): Flow<PagingData<Fowl>>
    fun getFowlRecords(fowlId: String): Flow<List<FowlRecord>>
    suspend fun addFowlRecord(fowlRecord: FowlRecord)
    fun getOffspring(fowlId: String): Flow<List<Fowl>>
    suspend fun updateFowlOwner(fowlId: String, newOwnerId: String)
}
