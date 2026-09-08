package com.blitzlabx.zcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "generate" or "translate"
    val originalText: String,
    val zCode: String,
    val codeType: String,
    val hash: String,
    val zLanguage: String,
    val mode: String,
    val hasPassword: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false
)
