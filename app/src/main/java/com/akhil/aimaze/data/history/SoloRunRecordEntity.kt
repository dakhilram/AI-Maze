package com.akhil.aimaze.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solo_run_records")
data class SoloRunRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val mode: String,
    val rows: Int,
    val columns: Int,
    val mazeSeed: Long,
    val moves: Int,
    val elapsedMs: Long,
    val stars: Int,
)
