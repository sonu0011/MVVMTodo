package com.sonu.mvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sonu.mvvmtodo.data.Task
import com.sonu.mvvmtodo.databinding.ItemTaskBinding

/*
    ListAdapter has DiffUtil CallBack which compare two list and find the updated item.It calculate
    difference asynchronously
*/


class TaskAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<Task, TaskAdapter.ViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    onItemClickListener.onItemClick(getItem(position))
                }

                checkboxCompleted.setOnClickListener {
                    val position = adapterPosition
                    onItemClickListener.onCheckBoxClick(
                        getItem(position),
                        checkboxCompleted.isChecked
                    )
                }
            }

        }

        fun bind(task: Task) {
            binding.apply {
                textViewName.text = task.name
                checkboxCompleted.isChecked = task.completed
                imagePriority.isVisible = task.important
                textViewName.paint.isStrikeThruText = task.completed
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallBack : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }

}