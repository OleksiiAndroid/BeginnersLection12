package ua.com.webacademy.beginnerslection12;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.widget.Toast;

public class MyJobIntentService extends JobIntentService {

    public static final String EXTRA_TEXT = "ua.com.webacademy.beginnerslection12.extra.TEXT";
    static final int JOB_ID = 1000;

    public static void doSomething(Context context, String text) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TEXT, text);

        enqueueWork(context, MyJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String text = intent.getStringExtra(EXTRA_TEXT);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
