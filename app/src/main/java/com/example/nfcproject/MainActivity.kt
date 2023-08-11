package com.example.nfcproject
import RetrofitHelper
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcproject.databinding.ActivityMainBinding
import com.example.nfcproject.model.APIModels.Checkout
import com.example.nfcproject.model.APIModels.Lesson
import com.example.nfcproject.model.APIModels.Tag
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.MainViewModel
import com.example.nfcproject.model.StudentViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
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

    val Statuses = mapOf(
        "Success" to "Вы успешно отмечены",
        "Wait" to "Отсканируйте метку",
        "None" to "Дождитесь начала пары"
    )

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MainViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (getCurrentLessonTime().first != journalViewModel.timeStartLesson.value)
            setCurrentLessonData()
    }

    //Возвращяется пара (Начало текущей пары, Конец текущей пары)
    //Если текущее время не соотвествует ни одному времени пары, то возращается пустое значение Pair("","")
    fun getCurrentLessonTime():Pair<String,String>{
        var result:Pair<String,String> = Pair("","")
        for(i in LessonTimeTable) {
            val firstTime = SimpleDateFormat("HH:mm").parse(i.first).time
            val secondTime = SimpleDateFormat("HH:mm").parse(i.second).time
            val currentTime =
                SimpleDateFormat("HH:mm").parse(SimpleDateFormat("HH:mm").format(Date())).time
            if (firstTime < currentTime && currentTime < secondTime) {
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
         val nfcData = NFCHandler().processIntent(intent)
         showLog("NFCProjectTestDebug","Серийный номер: ${nfcData.serialNumber}")
         showLog("NFCProjectTestDebug","Старая запись: ${nfcData.oldNote}")
         showLog("NFCProjectTestDebug","Новая запись: ${nfcData.newNote}")
         getTag(nfcData.serialNumber)
         if(sharedViewModel.tag.value == null){
             showLog("NFCProjectTestDebug","Данная метка (${nfcData.serialNumber}) не зарегистрированна!")
         }
         else{
             if (sharedViewModel.tag.value!!.note == nfcData.oldNote){
                 sharedViewModel.setTag(Tag(
                     sharedViewModel.tag.value!!.tagId,
                     sharedViewModel.tag.value!!.placementDateTime,
                     sharedViewModel.tag.value!!.roomId,
                     nfcData.newNote,
                     sharedViewModel.tag.value!!.isActive))
                 setNewNote()
                 addCheckout()
             }
             else{
                 showLog("NFCProjectTestDebug","Данная метка (${nfcData.serialNumber}) не актуальна!")
             }
         }
    }

    private fun setNewNote(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        api.updateTag(sharedViewModel.tag.value!!)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showLog("NFCProjectTestDebug","Проблема с подключением к API")
                    showLog("NFCProjectTestDebug",call.request().toString())
                }
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful)
                        showLog("NFCProjectTestDebug","Метка успешно актуализирована!")
                    if (response.code() == 404)
                        showLog("NFCProjectTestDebug","Ошибка при попытке актуализации метки!")
                }
            })
    }

    private fun addCheckout(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        var checkout = Checkout(UUID.randomUUID(),
            null,
            sharedViewModel.tag.value?.tagId.toString(),
            studentViewModel.studentId.value.toString(),
            null
        )

        api.CreateCheckout(checkout)
            .enqueue(object : Callback<Checkout> {
                override fun onFailure(call: Call<Checkout>, t: Throwable) {
                    showLog("NFCProjectTestDebug","Проблема с подключением к API")
                    showLog("NFCProjectTestDebug",call.request().toString())
                    showLog("NFCProjectTestDebug",t.message.toString())
                    showLog("NFCProjectTestDebug",t.localizedMessage)
                }
                override fun onResponse(call: Call<Checkout>, response: Response<Checkout>) {
                    if (response.isSuccessful) {
                        showLog("NFCProjectTestDebug","Checkout успешно добавлен!")
                    }
                    if (response.code() == 404) {
                        showLog("NFCProjectTestDebug","Неверные данные об отметке")
                    }
                }
            })
    }

    private fun getTagByIdAndNote(tagId: String, note: String){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        runBlocking {
            launch {
                val response = api.GetTagByIdAndNote(tagId, note)
                if (response != null){
                    showLog("NFCProjectTestDebug", response.body().toString())
                    showLog("NFCProjectTestDebug","Данные отправлены")
                    sharedViewModel.setTag(response.body())
                }
            }.join()
        }
    }

    private fun getTag(tagId: String){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        runBlocking {
            launch {
                val response = api.GetTagById1(tagId)
                if (response != null){
                    showLog("NFCProjectTestDebug", "Данные о зарегистрированной метке успешно получены!")
                }
                sharedViewModel.setTag(response.body())
            }.join()
        }


//        api.GetTagById(tagId)
//            .enqueue(object : Callback<Tag> {
//                override fun onFailure(call: Call<Tag>, t: Throwable) {
//                    showLog("NFCProjectTestDebug","Проблема с подключением к API")
//                    showLog("NFCProjectTestDebug",call.request().toString())
//                }
//                override fun onResponse(call: Call<Tag>, response: Response<Tag>) {
//                    if (response.isSuccessful) {
//                        showLog("NFCProjectTestDebug","Success")
//                        sharedViewModel.setTag(response.body()!!)
//                    }
//                    if (response.code() == 404) {
//                        showLog("NFCProjectTestDebug","Метка не найдена")
//                        sharedViewModel.setTag(null)
//                    }
//                    showLog("NFCProjectTestDebug","Код ответа : " + response?.code().toString())
//                }
//            })
    }

    //Возвращает информацию о паре
    //Если время отметки неподходящее
    //Или пара отсутствует возвращяет пустой оъект Lesson
    fun setCurrentLessonData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mejs.api.adev-team.ru/attendance/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val visitingApi = retrofit.create(VisitingAPI::class.java)
        //TODO Добавить корутину
        val lessonTime = getCurrentLessonTime()
        if (lessonTime != Pair("","")){
            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            visitingApi.getLesson(lessonTime.first,lessonTime.second,currentDate).enqueue(object : Callback<Lesson> {
                override fun onFailure(call: Call<Lesson>, t: Throwable) {
                    showError("NFCProjectTestDebug","Не получилось получить данные о паре!")
                }
                override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                    if (response.isSuccessful) {
                        journalViewModel.setLesson(response.body()?.disciplineName.toString(),response.body()?.startAt.toString(),response.body()?.finishAt.toString())
                        journalViewModel.setTextMessage(Statuses["Wait"].toString())
                    }
                    if (response.code()==404){
                        journalViewModel.resetData()
                        journalViewModel.setTextMessage(Statuses["None"].toString())
                    }
                    showLog("NFCProjectTestDebug","Код ответа : " + response.code().toString())
                }
            })
        }
    }

     fun sendToTimeTable() = runBlocking {
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

            showLog("NFCProjectTestDebug",sharedViewModel.studentCardId.value.toString())
            showLog("NFCProjectTestDebug",lessonTime.first)
            showLog("NFCProjectTestDebug",lessonTime.second)
            showLog("NFCProjectTestDebug",currentDate)
            showLog("NFCProjectTestDebug","НФЦГ-01-22")
            //TODO Добавить корутину
            visitingApi.setVisitingByStudentId(sharedViewModel.studentCardId.value.toString(), body)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        showMessage("Проблема с подключением к API")
                        showError("NFCProjectTestDebug",t.message.toString())
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            journalViewModel.setTextMessage(Statuses["Success"].toString())
                            showLog("NFCProjectTestDebug :","Success")
                        }
                        if (response.code() == 404) {
                            showMessage("Вы уже были отмечены!")
                        }
                        showLog("NFCProjectTestDebug","Код ответа : " + response?.code().toString())
                    }
                })
        }
    }

    private fun showMessage(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    private fun showLog(tag: String, msg: String) = Log.d(tag, msg)
    private fun showError(tag: String, msg: String) = Log.e(tag, msg)
}