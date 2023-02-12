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


class MainActivity : AppCompatActivity() {

    lateinit var mainbtn:Button
    lateinit var studNumber:EditText

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

        mainbtn = findViewById(R.id.fragmMainBTN)
        studNumber = findViewById(R.id.editTextStudentNumber)

        mainbtn.setOnClickListener {
            saveUserInputData(studNumber.text.toString())
        }
        Log.d("NFCProjectTestDebug","Created")

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.let { it }
        if (intent != null) {
            NFCHandler().processIntent(intent)
        }
    }
    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")
        if (intent != null) {
            NFCHandler().processIntent(intent)
        }
    }
    private fun saveUserInputData(inputData:String){
        if(inputValidation(inputData)){
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