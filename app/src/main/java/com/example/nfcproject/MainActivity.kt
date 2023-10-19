package com.example.nfcproject
import android.content.Intent
import android.graphics.Color
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcproject.Handlers.NFCHandler
import com.example.nfcproject.Handlers.RetrofitHelper
import com.example.nfcproject.Services.DBAPI
import com.example.nfcproject.databinding.ActivityMainBinding
import com.example.nfcproject.model.APIModels.DBAPI.Checkout
import com.example.nfcproject.model.APIModels.DBAPI.Tag
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.MainViewModel
import com.example.nfcproject.model.StudentViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


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
    fun readNFC(intent: Intent){
        if(journalViewModel.RoomId.value == 0.toShort()) {
            showMessage("Сейчас нет пар")
            return
        }
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
                if(sharedViewModel.tag.value!!.roomId == journalViewModel.RoomId.value) {
                    addCheckout()
                    showMessage("Вы отметились на паре!")
                }
                else {
                    showMessage("Это не Ваша аудитория !")
                }
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
    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
    private fun showError(msg: String) = Log.e("NFCProjectTestDebug", msg)

    private fun showMessage(message: String) {

        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        val paramView = snackBarView.layoutParams as FrameLayout.LayoutParams
        paramView.gravity = Gravity.CENTER
        snackBarView.setBackgroundColor(Color.parseColor("#8BBBF2"))
        snackbar.setTextColor(Color.parseColor("#1D283D"))
        snackbar.show()
    }
}