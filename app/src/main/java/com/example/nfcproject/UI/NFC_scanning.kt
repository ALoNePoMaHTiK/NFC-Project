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
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.StudentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    }

    override fun onResume() {
        super.onResume()
        getCurrentLesson()
    }
    private fun getCurrentLesson(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        runBlocking {
            launch {
                val response = api.GetLesson(studentViewModel.groupId.value.toString(),SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()))
                if (response.body() != null)
                {
                    var startDateTimeFull = response.body()?.startDateTime.toString().split('T')[1].split(':')
                    var finishDateTimeFull = response.body()?.finishDateTime.toString().split('T')[1].split(':')
                    journalViewModel.set(
                        response.body()?.lessonName.toString(),
                        startDateTimeFull[0] + ":" + startDateTimeFull[1],
                        finishDateTimeFull[0] + ":" + finishDateTimeFull[1],
                        response.body()?.roomId!!.toShort()

                    )
                }
                else
                    journalViewModel.reset()
            }.join()
        }
    }
    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}