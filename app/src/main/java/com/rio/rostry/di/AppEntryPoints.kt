package com.rio.rostry.di

import coil.ImageLoader
import com.rio.rostry.data.demo.DemoProductSeeder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoints {
    fun imageLoader(): ImageLoader
    fun demoProductSeeder(): DemoProductSeeder
}
