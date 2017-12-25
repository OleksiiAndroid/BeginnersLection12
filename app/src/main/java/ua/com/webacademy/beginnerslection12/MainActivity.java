package ua.com.webacademy.beginnerslection12;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClick(View v) {
        final Student student;

        switch (v.getId()) {
            case R.id.button:
                mDialog = new ProgressDialog(this);
                mDialog.setMessage("Wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                student = new Student("Ivan", "Ivanov", 22);
                StudentIntentService.insertStudent(this, student);
                break;
            case R.id.button2:
                mDialog = new ProgressDialog(this);
                mDialog.setMessage("Wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                StudentIntentService.getStudent(this, 1);
                break;
            case R.id.button3: {
                ServiceConnection connection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        StudentService studentService = ((StudentService.ServiceBinder) iBinder).getService();

                        long id = studentService.saveStudent(new Student("Ivan", "Ivanov", 22));
                        Toast.makeText(MainActivity.this, "id:" + id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                };

                Intent intent = new Intent(this, StudentService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);
            }
            break;
            case R.id.button4: {
                ServiceConnection connection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        StudentService studentService = ((StudentService.ServiceBinder) iBinder).getService();

                        Student student = studentService.getStudent(1);
                        Toast.makeText(MainActivity.this, student.FirstName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                };

                Intent intent = new Intent(this, StudentService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);
            }
            break;
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == StudentIntentService.REQUEST_CODE_SAVE_STUDENT) {
                long id = data.getLongExtra(StudentIntentService.EXTRA_ID, 0);
                Toast.makeText(this, "id:" + id, Toast.LENGTH_SHORT).show();
            } else if (requestCode == StudentIntentService.REQUEST_CODE_GET_STUDENT) {
                Student student = data.getParcelableExtra(StudentIntentService.EXTRA_STUDENT);
                Toast.makeText(this, student.FirstName, Toast.LENGTH_SHORT).show();
            }
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
