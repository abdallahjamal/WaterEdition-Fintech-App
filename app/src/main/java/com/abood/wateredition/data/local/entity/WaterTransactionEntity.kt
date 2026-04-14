package com.abood.wateredition.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.model.WaterTransaction
import java.math.BigDecimal

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class WaterTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val type: TransactionType,       // stored as String via TypeConverter
    val voltsUsed: BigDecimal,       // stored as String via TypeConverter
    val kwhPriceAtTime: BigDecimal,  // stored as String via TypeConverter
    val finalAmount: BigDecimal,     // stored as String via TypeConverter
    val note: String? = null,
    val timestamp: Long
) {
    fun toDomain(): WaterTransaction = WaterTransaction(
        id = id,
        customerId = customerId,
        type = type,
        voltsUsed = voltsUsed,
        kwhPriceAtTime = kwhPriceAtTime,
        finalAmount = finalAmount,
        note = note,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(tx: WaterTransaction) = WaterTransactionEntity(
            id = tx.id,
            customerId = tx.customerId,
            type = tx.type,
            voltsUsed = tx.voltsUsed,
            kwhPriceAtTime = tx.kwhPriceAtTime,
            finalAmount = tx.finalAmount,
            note = tx.note,
            timestamp = tx.timestamp
        )
    }
}
