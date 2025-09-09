package com.rio.rostry.data.di;

import com.rio.rostry.data.local.db.OutboxDao;
import com.rio.rostry.domain.repository.OutboxRepository;
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
public final class DataModule_ProvideOutboxRepositoryFactory implements Factory<OutboxRepository> {
  private final Provider<OutboxDao> daoProvider;

  public DataModule_ProvideOutboxRepositoryFactory(Provider<OutboxDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public OutboxRepository get() {
    return provideOutboxRepository(daoProvider.get());
  }

  public static DataModule_ProvideOutboxRepositoryFactory create(Provider<OutboxDao> daoProvider) {
    return new DataModule_ProvideOutboxRepositoryFactory(daoProvider);
  }

  public static OutboxRepository provideOutboxRepository(OutboxDao dao) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideOutboxRepository(dao));
  }
}
