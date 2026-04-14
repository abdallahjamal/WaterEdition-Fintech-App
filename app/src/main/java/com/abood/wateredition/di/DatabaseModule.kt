package com.abood.wateredition.di

import android.content.Context
import androidx.room.Room
import com.abood.wateredition.data.local.WaterDatabase
import com.abood.wateredition.data.local.dao.CustomerDao
import com.abood.wateredition.data.local.dao.WaterTransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWaterDatabase(
        @ApplicationContext context: Context
    ): WaterDatabase = Room.databaseBuilder(
        context,
        WaterDatabase::class.java,
        "water_edition.db"
    )
        .addCallback(WaterDatabase.SEED_CALLBACK)
        .build()

    @Provides
    @Singleton
    fun provideCustomerDao(db: WaterDatabase): CustomerDao = db.customerDao()

    @Provides
    @Singleton
    fun provideWaterTransactionDao(db: WaterDatabase): WaterTransactionDao =
        db.waterTransactionDao()
}
