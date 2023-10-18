package com.example.nfcproject.UI

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.Handlers.RetrofitHelper
import com.example.nfcproject.Services.DBAPI
import com.example.nfcproject.databinding.FragmentMainBinding
import com.example.nfcproject.model.APIModels.DBAPI.AuthData
import com.example.nfcproject.model.APIModels.DBAPI.Lesson
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.MainViewModel
import com.example.nfcproject.model.StudentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class NFC_scanning : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val journalViewModel: JournalViewModel by activityViewModels()
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            sViewModel = studentViewModel
            jViewModel = journalViewModel
        }
        showLog("Created")
        //test()
    }

    private fun getCurrentLesson(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        runBlocking {
            launch {
                val response = api.GetLesson(studentViewModel.groupId.value.toString(),SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()))
                if (response.body() != null)
                {
                    journalViewModel.set(
                        response.body()?.lessonName.toString(),
                        response.body()?.startDateTime.toString(),
                        response.body()?.finishDateTime.toString())
                }
                else{
                    journalViewModel.reset()
                }
            }.join()
        }
    }

    private fun test(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        showLog("Запрос")
        api.GetLessonSync(studentViewModel.groupId.value.toString(),SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()))
            .enqueue(object : Callback<Lesson> {
                override fun onFailure(call: Call<Lesson>, t: Throwable) {
                    showLog("Проблема с подключением к API")
                    showLog(call.request().toString())
                }
                override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                    showLog(response.body().toString())
                    if (response.isSuccessful)
                        journalViewModel.set(
                            response.body()?.lessonName.toString(),
                            response.body()?.startDateTime.toString(),
                            response.body()?.finishDateTime.toString())
                    if (response.code() == 204)
                        journalViewModel.reset()
                }
            })
    }

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}