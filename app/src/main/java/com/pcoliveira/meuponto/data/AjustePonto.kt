package com.pcoliveira.meuponto.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AjustePonto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registroId: Int,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val motivo: String
)
