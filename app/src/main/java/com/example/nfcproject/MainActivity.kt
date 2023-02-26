package com.example.nfcproject
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcproject.databinding.ActivityMainBinding

var studNumber:String = ""

class MainActivity : AppCompatActivity() {

    lateinit var mainbtn:Button
    lateinit var studNumberEditText:EditText

    private var nfcAdapter: NfcAdapter? = null

    private var nfcTag:String = ""

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")
        if (intent != null) {
            NFCHandler().processIntent(intent)
        }
    }

    private fun readNFC(){
        if (intent != null) {
            nfcTag = NFCHandler().processIntent(intent)
            Log.d("NFCProjectTestDebug",nfcTag+studNumber)
            if (nfcTag != "" && studNumber != "") {
                sendUserInputData()
            }
        }
    }

    private fun sendUserInputData(){

        Toast.makeText(applicationContext,"Данные отправлены",Toast.LENGTH_LONG).show()
    }

    private fun saveUserInputData(inputData:String){
        if(inputValidation(inputData)){
            studNumber = inputData.uppercase()
            Log.d("NFCProjectTestDebug","Номер студенческого: "+studNumber)
            /// запись в файл!!!!
            setContentView(R.layout.activity_main)
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