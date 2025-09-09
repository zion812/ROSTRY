package com.rio.rostry.data.di;

import com.rio.rostry.data.local.db.AppDatabase;
import com.rio.rostry.data.local.db.OutboxDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DataModule_ProvideOutboxDaoFactory implements Factory<OutboxDao> {
  private final Provider<AppDatabase> dbProvider;

  public DataModule_ProvideOutboxDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public OutboxDao get() {
    return provideOutboxDao(dbProvider.get());
  }

  public static DataModule_ProvideOutboxDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DataModule_ProvideOutboxDaoFactory(dbProvider);
  }

  public static OutboxDao provideOutboxDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideOutboxDao(db));
  }
}
