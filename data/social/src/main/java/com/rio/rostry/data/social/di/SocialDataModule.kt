package com.rio.rostry.data.social.di

import com.rio.rostry.data.social.repository.ChatRepositoryImpl
import com.rio.rostry.data.social.repository.CommunityRepositoryImpl
import com.rio.rostry.data.social.repository.LikesRepositoryImpl
import com.rio.rostry.data.social.repository.SocialFeedRepositoryImpl
import com.rio.rostry.data.social.repository.MessagingRepositoryImpl
import com.rio.rostry.domain.social.repository.ChatRepository
import com.rio.rostry.domain.social.repository.CommunityRepository
import com.rio.rostry.domain.social.repository.LikesRepository
import com.rio.rostry.domain.social.repository.SocialFeedRepository
import com.rio.rostry.domain.social.repository.MessagingRepository
import com.rio.rostry.domain.social.repository.MediaGalleryRepository
import com.rio.rostry.data.social.repository.MediaGalleryRepositoryImpl
import com.rio.rostry.domain.social.manager.MediaCacheManager
import com.rio.rostry.data.social.manager.MediaCacheManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding social data implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SocialDataModule {

    @Binds
    @Singleton
    abstract fun bindSocialFeedRepository(
        impl: SocialFeedRepositoryImpl
    ): SocialFeedRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(
        impl: MessagingRepositoryImpl
    ): MessagingRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindLikesRepository(
        impl: LikesRepositoryImpl
    ): LikesRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        impl: CommunityRepositoryImpl
    ): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindMediaGalleryRepository(
        impl: MediaGalleryRepositoryImpl
    ): MediaGalleryRepository

    @Binds
    @Singleton
    abstract fun bindMediaCacheManager(
        impl: MediaCacheManagerImpl
    ): MediaCacheManager
}
