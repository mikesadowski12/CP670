package com.example.androidassignments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolBar extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "TestToolBar";
    static String optionText = "You selected item 1.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is a string from Mike Sadowski", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.toolbar_menu, m);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        switch(mi.getItemId()) {
            case R.id.action_one:
                Log.d("Toolbar", "Option 1 selected");
                Snackbar.make(findViewById(R.id.action_one), optionText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;

            case R.id.action_two:
                Log.d("Toolbar", "Option 2 selected");
                AlertDialog.Builder builder = new AlertDialog.Builder(TestToolBar.this);
                builder.setTitle(R.string.tb_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                Intent resultIntent = new Intent(  );
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .show();
                break;

            case R.id.action_three:
                Log.d("Toolbar", "Option 3 selected");
                AlertDialog.Builder builder2 = new AlertDialog.Builder(TestToolBar.this);
                // Get the layout inflater
                LayoutInflater inflater = this.getLayoutInflater();
                final View customDialogLayout = inflater.inflate(R.layout.custom_dialog, null);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder2.setView(customDialogLayout)
                        // Add action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                optionText = ((EditText) customDialogLayout.findViewById(R.id.new_message)).getText().toString();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
                break;

            case R.id.action_about:
                Log.d("Toolbar", "Option 4 selected");
                CharSequence text = "Version 1.0, by Mike Sadowski";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext() ,text ,duration);
                toast.show();
                break;
        }

        return true;
    }
}