package ua.com.webacademy.beginnerslection12;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.widget.Toast;

public class MyJobService extends JobService {

    public static final String KEY_TEXT = "ua.com.webacademy.beginnerslection12.keys.TEXT";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        PersistableBundle bundle = jobParameters.getExtras();
        String text = bundle.getString(KEY_TEXT);

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
