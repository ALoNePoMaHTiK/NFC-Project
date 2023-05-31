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

    val LessonTimeTable = listOf(
        Pair("9:00","10:30"),
        Pair("10:40","12:10"),
        Pair("12:40","14:10"),
        Pair("14:20","15:50"),
        Pair("16:20","17:50"),
        Pair("18:00","19:30"),
        Pair("19:40","21:00")
    )

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentLessonTime()
        //getLessonData()
    }

    //Возвращяется пара (Начало текущей пары, Конец текущей пары)
    //Если текущее время не соотвествует ни одному времени пары, то возращается пустое значение Pair("","")
    fun getCurrentLessonTime():Pair<String,String>{
        var result:Pair<String,String> = Pair("","")
        for(i in LessonTimeTable){
            val firstTime = SimpleDateFormat("HH:mm").parse(i.first).time
            val secondTime = SimpleDateFormat("HH:mm").parse(i.second).time
            val currentTime = SimpleDateFormat("HH:mm").parse(SimpleDateFormat("HH:mm").format(Date())).time
            if( firstTime < currentTime && currentTime < secondTime){
                result = i
            }
        }
        return result
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
    //Возвращает информацию о паре
    //Если время отметки неподходящее
    //Или пара отсутствует возвращяет пустой оъект Lesson
    fun getCurrentLessonData():Lesson{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mejs.api.adev-team.ru/attendance/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val visitingApi = retrofit.create(VisitingAPI::class.java)
        //TODO Добавить корутину
        var result = Lesson()
        val lessonTime = getCurrentLessonTime()
        if (lessonTime != Pair("","")){
            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            visitingApi.getLesson(lessonTime.first,lessonTime.second,currentDate).enqueue(object : Callback<Lesson> {
                override fun onFailure(call: Call<Lesson>, t: Throwable) {
                    Log.e("NFCProjectTestDebug :", t.message.toString())
                }
                override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                    if (response.isSuccessful) {
                        result = response.body() as Lesson
                    }
                }
            })
        }
        return result
    }

    fun sendToTimeTable(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mejs.api.adev-team.ru/attendance/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val visitingApi = retrofit.create(VisitingAPI::class.java)

        val lessonTime = getCurrentLessonTime()
        if (lessonTime != Pair("","")) {
            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            val body = Visiting(
                lessonTime.first,
                lessonTime.second,
                currentDate,
                "НФЦГ-01-22"
            )
            //TODO Добавить корутину
            visitingApi.setVisitingByStudentId(sharedViewModel.studentCardId.value.toString(), body)
                .enqueue(object : Callback<String> {
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
}