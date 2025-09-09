package com.rio.rostry.core.di;

import com.rio.rostry.core.NetworkMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class CoreModule_ProvideNetworkMonitorFactory implements Factory<NetworkMonitor> {
  private final Provider<NetworkMonitor> monitorProvider;

  public CoreModule_ProvideNetworkMonitorFactory(Provider<NetworkMonitor> monitorProvider) {
    this.monitorProvider = monitorProvider;
  }

  @Override
  public NetworkMonitor get() {
    return provideNetworkMonitor(monitorProvider.get());
  }

  public static CoreModule_ProvideNetworkMonitorFactory create(
      Provider<NetworkMonitor> monitorProvider) {
    return new CoreModule_ProvideNetworkMonitorFactory(monitorProvider);
  }

  public static NetworkMonitor provideNetworkMonitor(NetworkMonitor monitor) {
    return Preconditions.checkNotNullFromProvides(CoreModule.INSTANCE.provideNetworkMonitor(monitor));
  }
}
