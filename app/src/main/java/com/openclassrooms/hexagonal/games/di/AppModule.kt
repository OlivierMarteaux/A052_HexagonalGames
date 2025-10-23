package com.openclassrooms.hexagonal.games.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.oliviermarteaux.localShared.utils.AndroidLogger
import com.oliviermarteaux.localShared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.data.service.PostFirebaseApi
import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.data.service.UserFirebaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
  /**
   * Provides a Singleton instance of PostApi using a PostFakeApi implementation for testing purposes.
   * This means that whenever a dependency on PostApi is requested, the same instance of PostFakeApi will be used
   * throughout the application, ensuring consistent data for testing scenarios.
   *
   * @return A Singleton instance of PostFakeApi.
   */
  @Provides
  @Singleton
  fun providePostApi(): PostApi {
    return PostFirebaseApi() // PostFakeApi() // to be replaced for test
  }
  @Singleton
  @Provides
  fun provideUserApi(): UserApi {
    return UserFirebaseApi()
  }
  @Provides
  fun provideNotificationManager(app: Application): NotificationManager =
    app.getSystemService(NotificationManager::class.java)

  @Provides
  @Singleton
  fun providePreferencesDataStore(
    @ApplicationContext context: Context
  ): DataStore<Preferences> =
    PreferenceDataStoreFactory.create {
      context.preferencesDataStoreFile("user_preferences")
    }

  @Provides
  @Singleton
  fun provideLogger(): Logger = AndroidLogger

  @Provides
  fun provideIsOnlineFlow(
    @ApplicationContext context: Context
  ): Flow<Boolean> = checkInternetConnection(context)

  @Provides
  @Singleton
  fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider {
    return CoroutineDispatcherProvider()
  }
}
