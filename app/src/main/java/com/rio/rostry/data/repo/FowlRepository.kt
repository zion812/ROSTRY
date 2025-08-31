package com.rio.rostry.data.repo

import com.rio.rostry.data.models.Fowl
import android.net.Uri
import com.rio.rostry.data.models.FowlRecord
import kotlinx.coroutines.flow.Flow

interface FowlRepository {
    suspend fun addFowl(fowl: Fowl, photoUri: Uri?)
    fun getFowlById(fowlId: String): Flow<Fowl?>
    fun getFowls(): Flow<List<Fowl>>
    fun getFowlRecords(fowlId: String): Flow<List<FowlRecord>>
    suspend fun addFowlRecord(fowlRecord: FowlRecord)
    suspend fun sync()
    fun getOffspring(fowlId: String): Flow<List<Fowl>>
}
