package com.example.nfcproject
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nfcproject.databinding.ActivityMainBinding

var studNumber:String = ""
var a:Int = 0

class MainActivity : AppCompatActivity() {

    lateinit var mainbtn:Button
    lateinit var studNumberEditText:EditText

    private var nfcTag:String = ""

    //TODO Добавить проверку на выданные разрешения
    // При их отсутствии запрашивать через ActivityCompat.shouldShowRequestPermissionRationale()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        permissionLauncherSingle.launch(permission)
        getMac(applicationContext)
    }
    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")
        if (intent != null && a == 10) {
            NFCHandler().processIntent(intent)
        }
    }

    private val permissionLauncherSingle = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        //here we will check if permission was now (from permission request dialog) or already granted or not. the param isGranted contains value true/false
        Log.d("NFCProjectTestDebug", "onActivityResult: isGranted: $isGranted")

        if (isGranted) {
            //Permission granted now do the required task here or call the function for that
            Log.d("NFCProjectTestDebug", "Granted permission")
        } else {
            //Permission was denied so can't do the task that requires that permission
            Log.d("NFCProjectTestDebug", "onActivityResult: Permission denied...")
            Toast.makeText(this@MainActivity, "Permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    fun readNFC(){
        if (intent != null) {
            sendUserInputData()
            nfcTag = NFCHandler().processIntent(intent)
            Log.d("NFCProjectTestDebug",nfcTag+studNumber)
            if (nfcTag != "" && studNumber != "") {
                sendUserInputData()
            }
        }
    }

    private fun getMac(context: Context){
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d("NFCProjectTestDebug", manager.scanResults.toString())
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            Log.d("NFCProjectTestDebug", manager.scanResults.toString())
    }

    private fun sendUserInputData(){

        Toast.makeText(applicationContext,"Данные отправлены",Toast.LENGTH_LONG).show()
    }

    private fun saveUserInputData(inputData:String){
        if(inputValidation(inputData)){
            studNumber = inputData.uppercase()
            Log.d("NFCProjectTestDebug","Номер студенческого: "+studNumber)
            /// запись в файл!!!!
            Toast.makeText(applicationContext,"Данные сохранены",Toast.LENGTH_LONG).show()
        }
    }
    private fun inputValidation(inputData:String): Boolean{
        val countСharacter = 7
        return if(countСharacter == inputData.length) {
            true
        }
        else {
            Toast.makeText(applicationContext,"Номер студенческого билета введен некоректно",Toast.LENGTH_LONG).show()
            false
        }
    }
}