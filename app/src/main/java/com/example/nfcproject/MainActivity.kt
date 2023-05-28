package com.example.nfcproject
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcproject.databinding.ActivityMainBinding
import com.example.nfcproject.model.MainViewModel
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLessonData()
    }
    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")
        if (intent != null && sharedViewModel.stateNFC.value == true && intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            readNFC(intent)
        }
    }

    fun readNFC(intent: Intent){
        val NFCTagId = DBConnection().getNFCTagId(NFCHandler().getNFCSerialNumber(intent))
        if(NFCTagId==""){
            Toast.makeText(applicationContext,"Метка повреждена!",Toast.LENGTH_LONG).show()
        }
        else{
            val StudentId = DBConnection().getStudentId(sharedViewModel.studentCardId.value.toString())
            DBConnection().postStudentCheckout(StudentId,NFCTagId)
            Toast.makeText(applicationContext,"Данные отправлены!",Toast.LENGTH_LONG).show()
            sendToTimeTable()
        }
    }

    //TODO отображение результата отметки на паре
    fun getLessonData(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mejs.api.adev-team.ru/attendance/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val visitingApi = retrofit.create(VisitingAPI::class.java)
        //TODO Добавить корутину
        visitingApi.getLesson("9:00","10:30",SimpleDateFormat("yyyy-MM-dd").format(Date())).enqueue(object : Callback<Lesson> {
            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Log.e("NFCProjectTestDebug :", t.message.toString())
            }
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                if (response.isSuccessful) {
                    Log.d("NFCProjectTestDebug :", response.body()?.disciplineName.toString())
                    Log.d("NFCProjectTestDebug :", response.body()?.startAt.toString())
                    Log.d("NFCProjectTestDebug :", response.body()?.finishAt.toString())
                }
                Log.d("NFCProjectTestDebug :", response?.code().toString())
            }

        })
    }

    fun sendToTimeTable(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mejs.api.adev-team.ru/attendance/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val visitingApi = retrofit.create(VisitingAPI::class.java)

        val body = Visiting("9:00","10:30",SimpleDateFormat("yyyy-MM-dd").format(Date()),"НФЦГ-01-22")
        //TODO Добавить корутину
        visitingApi.setVisitingByStudentId(sharedViewModel.studentCardId.value.toString(),body).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("NFCProjectTestDebug :", t.message.toString())
            }
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("NFCProjectTestDebug :", "Success")
                }
                Log.d("NFCProjectTestDebug :", response?.code().toString())
            }

        })
    }
}