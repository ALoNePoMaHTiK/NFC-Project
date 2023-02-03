package com.example.nfcproject

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Parcelable
import android.util.Log

class NFCHandler {
     fun processIntent(checkIntent: Intent) {
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
     fun processNdefMessages(ndefMessages: Array<NdefMessage?>) {
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
    }
}