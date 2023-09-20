package com.example.nfcproject.Handlers.Workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class WaitingAcceptWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams)  {
    override fun doWork(): Result {
        try{
            Log.d("NFCProjectTestDebug","1")
            Thread.sleep(1000)
            Log.d("NFCProjectTestDebug","WaitingAcceptWorker is complete")
            workDataOf()
            return Result.success()
        }
        catch(e:Exception){
            return Result.failure()
        }
    }
}