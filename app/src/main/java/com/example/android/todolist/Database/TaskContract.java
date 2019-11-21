package com.example.android.todolist.Database;

import android.provider.BaseColumns;

public class TaskContract {

    public static final class TaskEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_TITLE = "tasktitle";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
