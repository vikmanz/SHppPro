package com.vikmanz.shpppro.di

import com.vikmanz.shpppro.common.Constants.BASE_URL
import com.vikmanz.shpppro.data.api.ShPPApi
import com.vikmanz.shpppro.data.utils.MyResponseInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun providesHttpStringInterceptor(): MyResponseInterceptor = MyResponseInterceptor()

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        myResponseInterceptor: MyResponseInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(myResponseInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshiConvertorFactory(): MoshiConverterFactory {
        return MoshiConverterFactory
            .create()
    }

    @Singleton
    @Provides
    fun provideConvertorFactory(): GsonConverterFactory {
        return GsonConverterFactory
            .create()
    }


    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        convertorFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(convertorFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideShPPServerApi (
        retrofitClient: Retrofit
    ): ShPPApi {
        return retrofitClient
            .create(ShPPApi::class.java)
    }

}