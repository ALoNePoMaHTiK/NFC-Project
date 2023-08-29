package com.example.nfcproject.Handlers.Workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class GetStudentWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams)  {
    override fun doWork(): Result {
        try{
            Log.d("NFCProjectTestDebug","1")
            Thread.sleep(1000)
            Log.d("NFCProjectTestDebug","WaitingAcceptWorker is complete")
            return Result.success()
        }
        catch(e:Exception){
            return Result.failure()
        }
    }
}
