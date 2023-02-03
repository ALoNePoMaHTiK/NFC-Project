package com.example.nfcproject
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


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
}