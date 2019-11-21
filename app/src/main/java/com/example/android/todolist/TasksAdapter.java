package com.example.android.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.todolist.Database.TaskContract;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {


    private Context context;
    private Cursor cursor;


    public TasksAdapter(Context context, Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item , parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position)
    {
        if (!cursor.moveToPosition(position))
        {
            return;
        }

        String name = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_TITLE));
        long id = cursor.getLong(cursor.getColumnIndex(TaskContract.TaskEntry._ID));

        holder.taskTitle.setText(name);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount()
    {
        return cursor.getCount();
    }

    public void refreshCursor (Cursor newcursor)
    {
        if (cursor != null)
        {
            cursor.close();
        }

        cursor = newcursor;

        if (newcursor != null)
        {
            notifyDataSetChanged();
        }

    }

    public class TaskViewHolder extends RecyclerView.ViewHolder
    {
        TextView taskTitle;

        public TaskViewHolder(View itemView)
        {
            super(itemView);

            taskTitle = itemView.findViewById(R.id.task_title);



        }
    }




}
