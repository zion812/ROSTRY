package com.rio.rostry.di

import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.repository.EnthusiastBreedingRepositoryImpl
import com.rio.rostry.data.repository.FeedbackRepository
import com.rio.rostry.data.repository.FeedbackRepositoryImpl
import com.rio.rostry.data.repository.LikesRepository
import com.rio.rostry.data.repository.LikesRepositoryImpl
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.repository.social.MessagingRepositoryImpl
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.social.SocialRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocialRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: SocialRepositoryImpl): SocialRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(impl: MessagingRepositoryImpl): MessagingRepository

    @Binds
    @Singleton
    abstract fun bindEnthusiastBreedingRepository(impl: EnthusiastBreedingRepositoryImpl): EnthusiastBreedingRepository

    @Binds
    @Singleton
    abstract fun bindLikesRepository(impl: LikesRepositoryImpl): LikesRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository
}
