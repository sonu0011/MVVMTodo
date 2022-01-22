package com.sonu.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sonu.mvvmtodo.data.TaskDatabase
import com.sonu.mvvmtodo.data.TaskDatabase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        application: Application,
        callback: CallBack,
    ) = Room.databaseBuilder(application, TaskDatabase::class.java, "note_db")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()


    //since database is singleton  , dao is also  singleton no need to add singleton annotation
    @Provides
    fun provideNoteDao(
        database: TaskDatabase
    ) = database.noteDao()


    /*
        here optionally we are qualifying the CoroutineScope with application Scope without at later point of time
        we might want to crete another scope . At that point of time hilt will not able to identify which scope i
        should  return so we differentiate them using Qualifiers
    */
    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
