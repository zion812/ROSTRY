package com.rio.rostry.data.repository;

import com.rio.rostry.data.local.db.OutboxDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class OutboxRepositoryImpl_Factory implements Factory<OutboxRepositoryImpl> {
  private final Provider<OutboxDao> daoProvider;

  public OutboxRepositoryImpl_Factory(Provider<OutboxDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public OutboxRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static OutboxRepositoryImpl_Factory create(Provider<OutboxDao> daoProvider) {
    return new OutboxRepositoryImpl_Factory(daoProvider);
  }

  public static OutboxRepositoryImpl newInstance(OutboxDao dao) {
    return new OutboxRepositoryImpl(dao);
  }
}
