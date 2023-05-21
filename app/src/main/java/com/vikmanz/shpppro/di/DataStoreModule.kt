package com.vikmanz.shpppro.di

import com.vikmanz.shpppro.data.datastore.DataStoreManager
import com.vikmanz.shpppro.data.datastore.interfaces.MyPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    @Singleton
    abstract fun bindDataStore(
        navigator: DataStoreManager
    ): MyPreferences

}