package com.rio.rostry.core.di;

import com.rio.rostry.core.AppDispatchers;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class CoreModule_ProvideDispatchersFactory implements Factory<AppDispatchers> {
  @Override
  public AppDispatchers get() {
    return provideDispatchers();
  }

  public static CoreModule_ProvideDispatchersFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppDispatchers provideDispatchers() {
    return Preconditions.checkNotNullFromProvides(CoreModule.INSTANCE.provideDispatchers());
  }

  private static final class InstanceHolder {
    private static final CoreModule_ProvideDispatchersFactory INSTANCE = new CoreModule_ProvideDispatchersFactory();
  }
}
