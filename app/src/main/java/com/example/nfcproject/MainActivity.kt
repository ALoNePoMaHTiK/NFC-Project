package com.example.nfcproject

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.sql.ResultSet
import java.sql.Statement


class MainActivity : AppCompatActivity() {

    lateinit var Label:TextView
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Label = findViewById<TextView>(R.id.label)
        Log.d("NFCProjectTestDebug","Created")

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.let { it }
        if (intent != null) {
            processIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?){
        super.onNewIntent(intent)
        Log.d("NFCProjectTestDebug","New Intent")

        if (intent != null) {
            processIntent(intent)
        }
    }

    private fun processIntent(checkIntent: Intent) {
        Log.d("NFCProjectTestDebug","processIntent")
        if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null) {
                Log.d("NFCProjectTestDebug",rawMessages.size.toString())
                val messages = arrayOfNulls<NdefMessage?>(rawMessages.size)
                for (i in rawMessages.indices) {
                    messages[i] = rawMessages[i] as NdefMessage;
                }
                processNdefMessages(messages)
            }
        }

    }

    private fun processNdefMessages(ndefMessages: Array<NdefMessage?>) {
        for (curMsg in ndefMessages) {
            if (curMsg != null) {
                Log.d("NFCProjectTestDebug", curMsg.toString())
                Log.d("NFCProjectTestDebug", curMsg.records.size.toString())

                for (curRecord in curMsg.records) {
                    //Log.d("NFCProjectTestDebug", curRecord.payload.contentToString())
                    Log.d("NFCProjectTestDebug", String(curRecord.payload).substring(3))
                }
            }
        }
    }

    private fun readNFC(intent:Intent){
        Log.d("NFCProjectTestDebug","Start reading NFC")
        var messages:Array<Parcelable>?  = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        if(messages != null){
            for (message in messages){
                var ndefMessage:NdefMessage = message as NdefMessage
                for (record in ndefMessage.records){
                    Log.d("NFCProjectTestDebug",record.payload.toString())
                }

            }
        }


//        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
//            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
//            IsoDep.get(tag)?.let { isoDepTag ->
//                Log.d("NFCProjectTestDebug","NFC connected")
//                isoDepTag.connect()
//                Log.d("NFCProjectTestDebug",isoDepTag.toString())
//                Log.d("NFCProjectTestDebug",isoDepTag.tag.toString())
//                Log.d("NFCProjectTestDebug",isoDepTag.tag.id.toString())
//                Log.d("NFCProjectTestDebug",isoDepTag.tag.techList.toString())
//            }
//        }
//        val nfc = NfcA.get(tagFromIntent)
//
//        val atqa: ByteArray = nfc.getAtqa()
//        val sak: Short = nfc.getSak()
//        nfc.connect()
//
//        if(nfc.isConnected())
//        {
//            Log.d("test","NFC connected")
//            val receivedData:ByteArray= nfc.transceive(
//                byteArrayOf(
//                    0x30,  /* CMD = READ */
//                    0x10   /* PAGE = 16  */
//                )
//            )
//            Log.d("test",receivedData.toString())
//        } else{
//            Log.d("test", "NFC Not connected")
//        }
    }

    private fun readDB(){
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            val rs: ResultSet = s.executeQuery("SELECT * FROM Test")
            while(rs.next()) {
                Log.d("test",rs.getString(1))
                Log.d("test",rs.getString(2))
                Log.d("test",rs.getString(3))
            }
        }
        catch (ex: Exception){
            Log.e("Error : ", ex.message.toString())
        }
    }
}