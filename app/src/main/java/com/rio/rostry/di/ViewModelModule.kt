package com.rio.rostry.di

import com.rio.rostry.presentation.viewmodel.UserViewModel
import com.rio.rostry.presentation.viewmodel.ProductViewModel
import com.rio.rostry.presentation.viewmodel.OrderViewModel
import com.rio.rostry.presentation.viewmodel.TransferViewModel
import com.rio.rostry.presentation.viewmodel.CoinViewModel
import com.rio.rostry.presentation.viewmodel.NotificationViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    // ViewModel dependencies can be provided here if needed
}