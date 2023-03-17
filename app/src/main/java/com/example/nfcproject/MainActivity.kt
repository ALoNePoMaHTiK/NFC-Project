package com.example.nfcproject
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.ActivityMainBinding
import com.example.nfcproject.model.MainViewModel

var studNumber:String = ""


class MainActivity : AppCompatActivity() {

    lateinit var mainbtn:Button
    lateinit var studNumberEditText:EditText

    private var nfcTag:String = ""

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")
        if (intent != null && sharedViewModel.stateFNC.value == true) {
            NFCHandler().processIntent(intent)
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