package ua.com.webacademy.beginnerslection12;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.button5:
                PersistableBundle bundle = new PersistableBundle();
                bundle.putString(MyJobService.KEY_TEXT, "Some text");

                ComponentName serviceName = new ComponentName(this, MyJobService.class);
                JobInfo jobInfo = new JobInfo.Builder(1, serviceName)
                        .setMinimumLatency(10)
                        .setOverrideDeadline(20)
                        .setExtras(bundle)
                        .build();

                JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                scheduler.schedule(jobInfo);
                break;
            case R.id.button6:
                MyJobIntentService.doSomething(this, "Some text");
                break;
            case R.id.button7:
                if (hasPermission(Manifest.permission.READ_CONTACTS)) {
                    ArrayList<String> contacts = getContacts();
                    Toast.makeText(this, String.format("Contacts count:%s", contacts.size()), Toast.LENGTH_LONG).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] permissions = {
                                Manifest.permission.READ_CONTACTS
                        };

                        requestPermissions(permissions, 0);
                    }
                }
                break;
            case R.id.button8:
                ArrayList<Student> students = getStudents();
                Toast.makeText(this, String.format("Students count:%s", students.size()), Toast.LENGTH_LONG).show();
                break;
            case R.id.button9: {
                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWorker.class).build();
                WorkManager.getInstance().enqueue(request);

                LiveData<WorkInfo> info = WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId());
                info.observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        Log.d("workmng", "MyWorker onChanged " + Thread.currentThread().getName() + " " + workInfo.getState());
                    }
                });
            }
            break;
            case R.id.button10: {
                Data input = new Data.Builder()
                        .putString("InputData", "input data")
                        .build();

                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWorker2.class)
                        .setInputData(input)
                        .build();

                WorkManager.getInstance().enqueue(request);

                LiveData<WorkInfo> info = WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId());
                info.observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        Log.d("workmng", "MyWorker2 onChanged " + Thread.currentThread().getName() + " " + workInfo.getState());
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED))
                            Log.d("workmng", "Out data: " + workInfo.getOutputData().getString("OutData"));
                    }
                });
            }
            break;
        }
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<>();
        Cursor cursor = null;

        try {
            Uri uri = Uri.parse("content://ua.com.webacademy.beginnerslection12/students");

            cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Student student = new Student();

                    student.id = cursor.getLong(cursor.getColumnIndex("_id"));
                    student.FirstName = cursor.getString(cursor.getColumnIndex("FirstName"));
                    student.LastName = cursor.getString(cursor.getColumnIndex("LastName"));
                    student.Age = cursor.getLong(cursor.getColumnIndex("Age"));

                    students.add(student);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return students;
    }

    private ArrayList<String> getContacts() {
        ArrayList<String> names = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    names.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return names;
    }
}
