package ua.com.webacademy.beginnerslection12;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mDialog;

    private SaveTask mSaveTask;
    private GetTask mGetTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        if (mGetTask != null) {
            mGetTask.cancel(true);
        }
    }

    public void OnClick(View v) {
        Student student;

        switch (v.getId()) {
            case R.id.button:
                mDialog = new ProgressDialog(this);
                mDialog.setMessage("Wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                student = new Student("Ivan", "Ivanov", 22);
                StudentService.insertStudent(this, student);
                break;
            case R.id.button2:
                mDialog = new ProgressDialog(this);
                mDialog.setMessage("Wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                StudentService.getStudent(this, 1);
                break;
            case R.id.button3:
                student = new Student("Ivan", "Ivanov", 22);

                mSaveTask = new SaveTask();
                mSaveTask.execute(student);
                break;
            case R.id.button4:
                mGetTask = new GetTask();
                mGetTask.execute(2l);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == StudentService.REQUEST_CODE_SAVE_STUDENT) {
                long id = data.getLongExtra(StudentService.EXTRA_ID, 0);
                Toast.makeText(this, "id:" + id, Toast.LENGTH_SHORT).show();
            } else if (requestCode == StudentService.REQUEST_CODE_GET_STUDENT) {
                Student student = data.getParcelableExtra(StudentService.EXTRA_STUDENT);
                Toast.makeText(this, student.FirstName, Toast.LENGTH_SHORT).show();
            }
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    class SaveTask extends AsyncTask<Student, Void, Long> {

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Wait...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Long doInBackground(Student... params) {
            long id = 0;

            try {
                Student student = params[0];

                DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
                id = helper.insertStudent(student);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return id;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            Toast.makeText(MainActivity.this, "id:" + result, Toast.LENGTH_SHORT).show();
        }
    }

    class GetTask extends AsyncTask<Long, Void, Student> {

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Wait...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Student doInBackground(Long... params) {
            Student student = null;

            try {
                long id = params[0];

                DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
                student = helper.getStudent(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return student;
        }

        @Override
        protected void onPostExecute(Student result) {
            super.onPostExecute(result);

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Student not found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, result.FirstName, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
