package com.example.nfcproject
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
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
import java.sql.ResultSet

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
        if (intent != null && sharedViewModel.stateNFC.value == true && intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            //NFCHandler().processIntent(intent)
            readNFC(intent)
        }
    }

//    fun readNFC(){
//        if (intent != null) {
//            sendUserInputData()
//            nfcTag = NFCHandler().processIntent(intent)
//            Log.d("NFCProjectTestDebug",nfcTag+sharedViewModel.studentCardId.value)
//            if (nfcTag != "" && sharedViewModel.studentCardId.value != "") {
//                sendUserInputData()
//            }
//        }
//    }

    fun readNFC(intent: Intent){
        var result = ""
        var serialNumber:String = ""
        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMessages != null) {
            serialNumber = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)?.joinToString("") { "%02x".format(it) }?.uppercase().toString()
            val messages = arrayOfNulls<NdefMessage?>(rawMessages.size)
            for (i in rawMessages.indices) {
                messages[i] = rawMessages[i] as NdefMessage;
            }
            for (curMsg in messages) {
                if (curMsg != null) {
                    for (curRecord in curMsg.records) {
                        result = String(curRecord.payload).substring(3)
                    }
                }
            }
        }
        val NFCTagId = DBConnection().getNFCTagId(serialNumber)
        if(NFCTagId==""){
            Toast.makeText(applicationContext,"Метка повреждена!",Toast.LENGTH_LONG).show()
        }
        else{
            val StudentId = DBConnection().getStudentId(sharedViewModel.studentCardId.value.toString())
            DBConnection().postStudentCheckout(StudentId,NFCTagId)
            Toast.makeText(applicationContext,"Данные отправлены!",Toast.LENGTH_LONG).show()
        }
    }
}