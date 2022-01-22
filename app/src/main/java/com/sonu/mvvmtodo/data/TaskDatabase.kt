package com.sonu.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sonu.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun noteDao(): TaskDao


    /*
        create callback instance for initially inserting the data into the database
        so that recyclerview will not be empty when app starts
     */

    /*
        here we are lazily creating the instance of taskDatabase that means we will get the instance
        of taskDatabase until we call get on that if we no do that there would be cyclic dependency
        it means database requires callback and callback requires database to avoid we have to lazily gets
        the database reference
    */
    class CallBack @Inject constructor(
        @ApplicationScope private val applicationScope: CoroutineScope,
        private val taskDatabase: Provider<TaskDatabase>

    ) : RoomDatabase.Callback() {

        /*
            onCrete method gets called after we call build method on Room for building the  database
            and gets call only first time after building the database we take instance of taskdatabase
            using get() method of provider

        */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //database operations
            val dao = taskDatabase.get().noteDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Do the laundry"))
                dao.insert(Task("Buy groceries", important = true))
                dao.insert(Task("Prepare food", completed = true))
                dao.insert(Task("Call mom"))
                dao.insert(Task("Visit grandma", completed = true))
                dao.insert(Task("Repair my bike"))
                dao.insert(Task("Call Elon Musk"))
            }
        }
    }


}