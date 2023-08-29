package com.example.nfcproject.Handlers.Workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nfcproject.Handlers.RetrofitHelper
import com.example.nfcproject.Services.DBAPI
import com.example.nfcproject.model.APIModels.DBAPI.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserByIdWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams)  {
    override fun doWork(): Result {
        try{
            showLog("Hello")
            return Result.success()
        }
        catch(e:Exception){
            return Result.failure()
        }
    }
    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}

