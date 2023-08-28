package com.example.nfcproject.Handlers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters



class WaitingAcceptWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams)  {
    override fun doWork(): Result {
        while (true){
            Log.d("NFCProjectTestDebug","A")
        }
        Log.d("NFCProjectTestDebug","WaitingAcceptWorker is complete")
        return Result.failure()
    }
}