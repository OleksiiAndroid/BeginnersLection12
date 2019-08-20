package ua.com.webacademy.beginnerslection12;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class MyWorker2 extends Worker {

    public MyWorker2(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("workmng", "MyWorker2 doWork: start");
        Log.d("workmng", "Input data: " + getInputData().getString("InputData"));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("workmng", "MyWorker2 doWork: end");

        String input = getInputData().getString("InputData");
        Data out = new Data.Builder().putString("OutData", input + " out data").build();

        return Result.success(out);
    }
}
