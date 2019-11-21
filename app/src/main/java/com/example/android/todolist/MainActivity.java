package com.example.android.todolist;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.Database.TaskContract;
import com.example.android.todolist.Database.TaskDBHelper;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addNewTaskBtn;
    private ImageView image;
    private TasksAdapter tasksAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private TaskDBHelper taskDBHelper;
    private Cursor cursor;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addNewTaskBtn = findViewById(R.id.add_task);
        recyclerView = findViewById(R.id.recyclerview);
        image = findViewById(R.id.task_image);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


        taskDBHelper = new TaskDBHelper(getApplicationContext());

        sqLiteDatabase = taskDBHelper.getWritableDatabase();

        cursor = getAllTasks();

        tasksAdapter = new TasksAdapter(getApplicationContext(), cursor);

        recyclerView.setAdapter(tasksAdapter);

        if (cursor.getCount() != 0) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
        }

        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                removeTask(id);
                //update the list
                tasksAdapter.refreshCursor(getAllTasks());

                if (getAllTasks().getCount() == 0) {
                    image.setVisibility(View.VISIBLE);
                }
            }

            // attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(recyclerView);
    }

    private long addNewTask(String taskName) {
        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COLUMN_TASK_TITLE, taskName);
        return sqLiteDatabase.insert(TaskContract.TaskEntry.TABLE_NAME, null, cv);
    }



    private boolean removeTask(long id) {
        return sqLiteDatabase.delete(TaskContract.TaskEntry.TABLE_NAME, TaskContract.TaskEntry._ID + "=" + id, null) > 0;
    }


    private Cursor getAllTasks() {
        return sqLiteDatabase.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TaskContract.TaskEntry.COLUMN_TIMESTAMP
        );
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_task_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Button addTask = dialog.findViewById(R.id.add_task_btn);
        final Button back = dialog.findViewById(R.id.back_btn);

        final EditText taskTitle = dialog.findViewById(R.id.task_title_et);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast));
        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setTextColor(Color.WHITE);
        toastText.setText("Please Enter Valid Data");
        CardView cardView = layout.findViewById(R.id.cardview);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = taskTitle.getText().toString();

                if (taskName.length() == 0) {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                } else {
                    addNewTask(taskName);

                    tasksAdapter.refreshCursor(getAllTasks());

                    image.setVisibility(View.GONE);

                    dialog.dismiss();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}

