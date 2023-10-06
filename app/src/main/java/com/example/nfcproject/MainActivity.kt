package com.example.nfcproject
import com.example.nfcproject.Handlers.RetrofitHelper
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcproject.Handlers.NFCHandler
import com.example.nfcproject.databinding.ActivityMainBinding
import com.example.nfcproject.model.APIModels.DBAPI.Checkout
import com.example.nfcproject.model.APIModels.DBAPI.Lesson
import com.example.nfcproject.model.APIModels.DBAPI.Tag
import com.example.nfcproject.model.APIModels.VisitingAPI.Visiting
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.MainViewModel
import com.example.nfcproject.model.StudentViewModel
import com.example.nfcproject.Services.DBAPI
import com.example.nfcproject.Services.VisitingAPI
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MainViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        // прячем drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        setSupportActionBar(binding.appBarMain.toolbar)
        // у toolbar в свойствах выставлено - invisible

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profile, R.id.schedule, R.id.permissions, R.id.nfc_scanning
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerLayout = navView.getHeaderView(0)
        val HeaderFNameTextView = headerLayout.findViewById<TextView>(R.id.HeaderFName)
        val HeaderLNameTextView = headerLayout.findViewById<TextView>(R.id.HeaderLName)
        studentViewModel.userFirestName.observe(this) {
            HeaderFNameTextView.text = it.toString()
        }
        studentViewModel.userSecondName.observe(this) {
            HeaderLNameTextView.text = it.toString()
        }
        Auth()
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onResume() {
        super.onResume()
    }

     override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        showLog("New Intent")
         if (intent != null && sharedViewModel.stateNFC.value == true && (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED || intent.action == NfcAdapter.ACTION_TAG_DISCOVERED)) {
            readNFC(intent)
        }
    }

    private fun Auth(){
        val api = Retrofit.Builder().baseUrl("https://rtu-attends.rtu-tc.ru")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(VisitingAPI::class.java)
        api.Auth().enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showLog("Проблема с подключением к API")
                    showLog("Запрос : " + call.request())
                }
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showLog("Success")
                        showLog(response.headers()["set-cookie"].toString())
                        journalViewModel.setCookie(response.headers()["set-cookie"].toString())
                    }
                    else{
                        showLog("Код ответа : " + response?.code().toString())
                        showLog("Код ответа : " + response.raw())
                        showLog("Код ответа : " + response.headers().toString())
                    }
                }
            })
    }

    fun readNFC(intent: Intent){
         val nfcData = NFCHandler().writeNewNote(intent)
         if (nfcData == null){
             showError("Не удалось считать метку")
             return
         }
         showLog("Серийный номер: ${nfcData.serialNumber}")
         showLog("Старая запись: ${nfcData.oldNote}")
         showLog("Новая запись: ${nfcData.newNote}")
         getTag(nfcData.serialNumber)
         if(sharedViewModel.tag.value == null){
             showLog("Данная метка (${nfcData.serialNumber}) не зарегистрированна!")
         }
         else{
             if (sharedViewModel.tag.value!!.note == nfcData.oldNote){
                 sharedViewModel.setTag(
                     Tag(
                     sharedViewModel.tag.value!!.tagId,
                     sharedViewModel.tag.value!!.placementDateTime,
                     sharedViewModel.tag.value!!.roomId,
                     nfcData.newNote,
                     sharedViewModel.tag.value!!.isActive)
                 )
                 setNewNote()
                 addCheckout()
             }
             else{
                 showLog("Данная метка (${nfcData.serialNumber}) не актуальна!")
             }
         }
    }

    private fun setNewNote(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        api.updateTag(sharedViewModel.tag.value!!)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showLog("Проблема с подключением к API")
                    showLog(call.request().toString())
                }
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful)
                        showLog("Метка успешно актуализирована!")
                    if (response.code() == 404)
                        showLog("Ошибка при попытке актуализации метки!")
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
                    showLog("Проблема с подключением к API")
                    showLog(call.request().toString())
                }
                override fun onResponse(call: Call<Checkout>, response: Response<Checkout>) {
                    if (response.isSuccessful) {
                        showLog("Checkout успешно добавлен!")
                    }
                    if (response.code() == 404) {
                        showLog("Неверные данные об отметке")
                    }
                }
            })
    }

    private fun getTag(tagId: String){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        runBlocking {
            launch {
                val response = api.GetTagByIdSuspend(tagId)
                if (response != null){
                    showLog("Данные о зарегистрированной метке успешно получены!")
                }
                sharedViewModel.setTag(response.body())
            }.join()
        }
    }

    private fun showMessage(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
    private fun showError(msg: String) = Log.e("NFCProjectTestDebug", msg)
}