package com.example.stepcounter.Data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class HalfwayWorker(val context: Context, val params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result{
        WorkRequest(context).createNotification(
            inputData.getString("title").toString(),
            inputData.getString("message").toString())
        return Result.success()
    }

}