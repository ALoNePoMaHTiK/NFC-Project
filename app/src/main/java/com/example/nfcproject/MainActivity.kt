package com.example.nfcproject
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcproject.databinding.ActivityMainBinding

var studNumber:String = ""

class MainActivity : AppCompatActivity() {

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
        if (intent != null) NFCHandler().processIntent(intent)
    }
     fun readNFC(){
        if (intent != null) {
            nfcTag = NFCHandler().processIntent(intent)
            Log.d("NFCProjectTestDebug",nfcTag+studNumber)
            if (nfcTag != "" && studNumber != "") sendUserInputData()
        }
    }
    private fun sendUserInputData(){
        Toast.makeText(applicationContext,"Данные отправлены",Toast.LENGTH_LONG).show()
    }
}