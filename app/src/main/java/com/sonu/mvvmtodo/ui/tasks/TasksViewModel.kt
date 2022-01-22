package com.sonu.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sonu.mvvmtodo.data.PreferencesManager
import com.sonu.mvvmtodo.data.SortOrder
import com.sonu.mvvmtodo.data.Task
import com.sonu.mvvmtodo.data.TaskDao
import com.sonu.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.sonu.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle,
) : ViewModel() {
    /*
        MutableStateFlow is just Like MutableLiveDAta . it keeps only single value but it is a flow
    */

    /*
      we can't save flow in savedStateHandler because it's language feature we can store liveDAta inside it
        getLiveDAta takes care of setting the searchQuery key value when we set from ui
     */

    val searchQuery = state.getLiveData("searchQuery", "")
    private val preferencesFlow = preferencesManager.preferencesFlow

    /*
         channel is used to communicate between two coroutine we can send event over channel and
         another coroutine which might be running in the ui will consume that event
         we can also use livedata for this task .but the problem with livedata when ui rotate it
         gives the last value . we don't want this . that's why we use channel to send event , consume

    */

    private val taskEventChannel = Channel<TasksEvent>()
    val tasksEvent: Flow<TasksEvent> = taskEventChannel.receiveAsFlow()

    /*
        FlatMatLatest operator is just like switchMatch operator of liveDAta
         Returns a flow that switches to a new flow produced by transform function every time the
          original flow emits a value.
          combine function combines the multiple flow and returns the latest value of each flow
          Here wer are returning three values in form of Triple and this triple gets passed to flatMapLatest operator
          where we have used kotlin destruction syntax to get each value
     */
    private val taskFlow =
        combine(searchQuery.asFlow(), preferencesFlow) { searchQuery, filterPreferences ->
            Pair(searchQuery, filterPreferences)
        }.flatMapLatest { (searchQuery, filterPreferences) ->
            taskDao.getTasks(
                searchQuery,
                filterPreferences.hideCompleted,
                filterPreferences.sortOrder
            )
        }

    val task = taskFlow.asLiveData()


    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch { preferencesManager.updateSorOrder(sortOrder) }

    fun onHideCompletedClick(hideCompleted: Boolean) =
        viewModelScope.launch { preferencesManager.updateHideCompleted(hideCompleted) }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToEditTasksScreen(task))

    }

    fun onTaskCheckChanged(task: Task, isChecked: Boolean) =
        viewModelScope.launch { taskDao.update(task.copy(completed = isChecked)) }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTasksMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch { taskDao.insert(task) }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToAddTasksScreen)
    }
    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }


    sealed class TasksEvent {
        object NavigateToAddTasksScreen : TasksEvent()
        data class NavigateToEditTasksScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTasksMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()

    }

}


