package com.example.srtodolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class Home extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseAuth srAuth;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SR Todo List App");

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        String onlineUserID = srAuth.getUid();
        assert onlineUserID != null;
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> addTask());
    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText task =myView.findViewById(R.id.task);
        final EditText description =myView.findViewById(R.id.description);
        Button save = myView.findViewById(R.id.saveButton);
        Button cancel = myView.findViewById(R.id.cancelButton);

        cancel.setOnClickListener((v)-> dialog.dismiss());

        save.setOnClickListener((v)-> {
            String mTask = task.getText().toString().trim();
            String mDescription = description.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());

            if (TextUtils.isEmpty(mTask)) {
                task.setError("Task Required");
                return;
            }
            if (TextUtils.isEmpty(mDescription)) {
                description.setError("Description Required");
                return;
            } else {
                loader.setMessage("Adding Your Data");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                Model model = new Model(mTask, mDescription, id, date);
                assert id != null;
                reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(Home.this, "Task has been inserted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = Objects.requireNonNull(task1.getException()).toString();
                        Toast.makeText(Home.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });
            }
            dialog.dismiss();
        });
        dialog.show();
    }
}