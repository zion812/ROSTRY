package com.rio.rostry.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class) // ViewModelComponent is typically used for ViewModel-specific bindings
object ViewModelModule {

    // Provide ViewModel-specific dependencies here if needed
    // Or bind ViewModel interfaces to implementations if you're not solely relying on @HiltViewModel and @Inject constructor

}
