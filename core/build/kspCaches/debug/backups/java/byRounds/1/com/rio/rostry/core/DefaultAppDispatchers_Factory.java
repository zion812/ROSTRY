package com.rio.rostry.core;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DefaultAppDispatchers_Factory implements Factory<DefaultAppDispatchers> {
  @Override
  public DefaultAppDispatchers get() {
    return newInstance();
  }

  public static DefaultAppDispatchers_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DefaultAppDispatchers newInstance() {
    return new DefaultAppDispatchers();
  }

  private static final class InstanceHolder {
    private static final DefaultAppDispatchers_Factory INSTANCE = new DefaultAppDispatchers_Factory();
  }
}
