package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "fowl_transfers",
    foreignKeys = [ForeignKey(
        entity = Fowl::class,
        parentColumns = ["id"],
        childColumns = ["fowlId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["fowlId"])]
)
data class FowlTransfer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fowlId: String,
    val fromOwnerId: String,
    val toOwnerId: String,
    val date: Date
)
