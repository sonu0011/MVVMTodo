package com.sonu.mvvmtodo.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sonu.mvvmtodo.data.TaskDao
import com.sonu.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    /*
        here we don't want to use vieModel scope to launch coroutine
        because after clicking confirm button viewModel gets removed
        from memory since fragment is not visible in that case delete operation
        might take time . so we want to launch coroutine in global scope
     */


    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}