package com.example.nfcproject

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter

class NFCHandler {

    fun getNFCSerialNumber(intent: Intent):String{
        var serialNumber:String = ""
        serialNumber = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)?.joinToString("") { "%02x".format(it) }?.uppercase().toString()
        return serialNumber
    }
    fun processIntent(checkIntent: Intent):String {
        var result = ""
        var serialNumber:String = ""
        val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMessages != null) {
            serialNumber = checkIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID)?.joinToString("") { "%02x".format(it) }?.uppercase().toString()
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
        return result
    }
}