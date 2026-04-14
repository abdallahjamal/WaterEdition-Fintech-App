package com.abood.wateredition.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.abood.wateredition.data.local.converter.BigDecimalConverter
import com.abood.wateredition.data.local.converter.TransactionTypeConverter
import com.abood.wateredition.data.local.dao.CustomerDao
import com.abood.wateredition.data.local.dao.WaterTransactionDao
import com.abood.wateredition.data.local.entity.CustomerEntity
import com.abood.wateredition.data.local.entity.WaterTransactionEntity

@Database(
    entities = [CustomerEntity::class, WaterTransactionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(BigDecimalConverter::class, TransactionTypeConverter::class)
abstract class WaterDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun waterTransactionDao(): WaterTransactionDao

    companion object {
        /**
         * Seed callback — inserts mock data on first creation only.
         * Timestamps are milliseconds (Unix epoch).
         */
        val SEED_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // ── Customers ─────────────────────────────────────────────
                // Balance stored as plain-string BigDecimal
                db.execSQL(
                    """
                    INSERT INTO customers (name, phone, currentBalance, createdAt) VALUES
                    ('Ahmed', NULL, '30', 1712000000000),
                    ('Yasser', NULL, '25', 1712000100000),
                    ('Muath',  NULL, '-5', 1712000200000)
                    """.trimIndent()
                )

                // ── Transactions for Ahmed (id=1) ──────────────────────────
                // FILL 50V @ ₪12 = ₪60  →  PAYMENT ₪30 → balance = ₪30
                db.execSQL(
                    """
                    INSERT INTO transactions
                        (customerId, type, voltsUsed, kwhPriceAtTime, finalAmount, note, timestamp) VALUES
                    (1, 'FILL',    '50', '12', '60', 'Water fill 50V',  1712001000000),
                    (1, 'PAYMENT', '0',  '0',  '30', 'Payment received', 1712002000000)
                    """.trimIndent()
                )

                // ── Transactions for Yasser (id=2) ─────────────────────────
                // FILL 20V @ ₪15 = ₪30, FILL 10V @ ₪15 = ₪15, PAYMENT ₪20 → balance = ₪25
                db.execSQL(
                    """
                    INSERT INTO transactions
                        (customerId, type, voltsUsed, kwhPriceAtTime, finalAmount, note, timestamp) VALUES
                    (2, 'FILL',    '20', '15', '30', 'Water fill 20V',  1712001000000),
                    (2, 'FILL',    '10', '15', '15', 'Water fill 10V',  1712001500000),
                    (2, 'PAYMENT', '0',  '0',  '20', 'Payment received', 1712002000000)
                    """.trimIndent()
                )

                // ── Transactions for Muath (id=3) ──────────────────────────
                // FILL 30V @ ₪10 = ₪30, PAYMENT ₪35 → balance = -₪5 (credit)
                db.execSQL(
                    """
                    INSERT INTO transactions
                        (customerId, type, voltsUsed, kwhPriceAtTime, finalAmount, note, timestamp) VALUES
                    (3, 'FILL',    '30', '10', '30', 'Water fill 30V',  1712001000000),
                    (3, 'PAYMENT', '0',  '0',  '35', 'Payment received', 1712002000000)
                    """.trimIndent()
                )
            }
        }
    }
}
