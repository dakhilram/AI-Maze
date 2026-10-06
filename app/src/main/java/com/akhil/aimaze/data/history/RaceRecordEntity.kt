package com.akhil.aimaze.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "race_records")
data class RaceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val rows: Int,
    val columns: Int,
    val mazeSeed: Long,
    val winner: String,
    val playerMoves: Int,
    val botSteps: Int,
)
