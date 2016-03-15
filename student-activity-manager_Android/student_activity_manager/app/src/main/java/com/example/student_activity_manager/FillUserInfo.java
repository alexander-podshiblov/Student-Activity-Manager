package com.example.student_activity_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.List;

public class FillUserInfo extends Activity {

    private final FillUserInfo mThis = this;
    private AutoCompleteTextView eiTV;
    private AutoCompleteTextView faqTV;

    private EdInstItem edInstItem;
    private FacultyItem facultyItem;

    private boolean isEdInst_New = false;
    private boolean isFaculty_New = false;

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private void createAndShowDialogFromTask(final String message, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(message, title);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_user_info);
        ToDoActivity.mClient.withFilter(new ProgressFilter(this,
                (ProgressBar) findViewById(R.id.loadingProgressBar2)));

        prepareEITextView();
    }

    private void prepareEITextView() {
        eiTV = (AutoCompleteTextView) findViewById(R.id.EdInstTV);
        eiTV.setThreshold(0);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    final List<EdInstItem> edInstItems = ToDoActivity.mClient.getTable(getString(R.string.EdInst_table_name),
                                                                                       EdInstItem.class).execute().get();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eiTV.setAdapter(new ArrayAdapter(mThis, R.layout.dropdown_item, edInstItems));
                            eiTV.setEnabled(true);

                            eiTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                    EdInstItem selection = (EdInstItem) parent.getItemAtPosition(position);
                                    edInstItem = selection;
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    createAndShowDialogFromTask(e.getMessage(), "Error");
                }
                return null;
            }
        };
        task.execute();
    }

    public void submitEdInst(View view)
    {
        eiTV.setEnabled(false);
        findViewById(R.id.submitEdInst).setEnabled(false);

        if (edInstItem == null || eiTV.getText().toString() != edInstItem.getmName())
        {
            edInstItem = new EdInstItem(eiTV.getText().toString());
            isEdInst_New = true;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (isEdInst_New) {
                        //to get id from server
                        edInstItem = ToDoActivity.mClient.getTable(getString(R.string.EdInst_table_name),
                                EdInstItem.class).insert(edInstItem).get();
                    }
                    else
                    {
                        ToDoActivity.mClient.getTable(getString(R.string.EdInst_table_name),
                                EdInstItem.class).update(edInstItem).get();
                    }

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e.getMessage(), "Error");
                }

            return null;
            }
        };
        task.execute();

        findViewById(R.id.submitFaculty).setVisibility(View.VISIBLE);
        prepareFacultyTextView();
    }

    private void prepareFacultyTextView() {
        faqTV = (AutoCompleteTextView) findViewById(R.id.FacultyTV);
        faqTV.setVisibility(View.VISIBLE);
        faqTV.setThreshold(0);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    if (!isEdInst_New)
                    {
                        final List<FacultyItem> facultyItems = ToDoActivity.mClient.getTable(getString(R.string.Faculties_table_name),
                                FacultyItem.class).where().field("EdInstId").eq(edInstItem.getmId()).execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                faqTV.setAdapter(new ArrayAdapter(mThis, R.layout.dropdown_item, facultyItems));
                                faqTV.setVisibility(View.VISIBLE);

                                faqTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                        FacultyItem selection = (FacultyItem) parent.getItemAtPosition(position);
                                        facultyItem = selection;
                                    }
                                });
                            }
                        });
                    }

                } catch (Exception e) {
                    createAndShowDialogFromTask(e.getMessage(), "Error");
                }
                return null;
            }
        };
        task.execute();
    }

    public void submitFaculty(View view)
    {
        faqTV.setEnabled(false);
        findViewById(R.id.submitFaculty).setEnabled(false);

        if (facultyItem == null || faqTV.getText().toString() != facultyItem.getmName())
        {
            facultyItem = new FacultyItem(faqTV.getText().toString(), edInstItem.getmId());
            isFaculty_New = true;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (isFaculty_New) {
                        facultyItem = ToDoActivity.mClient.getTable(getString(R.string.Faculties_table_name),
                                FacultyItem.class).insert(facultyItem).get();
                    }
                    else
                    {
                        ToDoActivity.mClient.getTable(getString(R.string.Faculties_table_name),
                                FacultyItem.class).update(facultyItem).get();
                    }

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e.getMessage(), "Error");
                }

                return null;
            }
        };
        task.execute();
    }
}