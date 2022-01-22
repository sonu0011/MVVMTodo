package com.sonu.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /*
        Flow is just coroutine which emit multiple values  over the time. It is asynchronous  flow of data.
        Here we don't need to mark this function as suspend because from viewModel when we collect
        the flow this is suspend function which required coroutine or another suspend function
        at that time we have to crete coroutine the function we have added suspend keyword room will
        takes care of switching the function execution to child thread and later point will resume the
        function. this way it will not block the other code. Coroutine make asynchronous programing
        easier like just it is executing sequentially and it removed all the  callback .Internally
        suspend keyword gets converted into Continuation parameter to that function and managers callback.

        || is operator used to concatenate two string in sql
     */

    fun getTasks(query: String, hideCompleted: Boolean, sortOrder: SortOrder): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> {
                getTasksSortedByName(query, hideCompleted)
            }
            SortOrder.BY_DATE -> {
                getTasksSortedByDateCreated(query, hideCompleted)
            }
        }


    @Query("SELECT * FROM tasks_table WHERE( completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER by important desc , name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>


    @Query("SELECT * FROM tasks_table WHERE( completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER by important desc ,timeStamp")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)


    @Query("DELETE FROM tasks_table WHERE completed = 1")
    suspend fun deleteCompletedTasks()

}