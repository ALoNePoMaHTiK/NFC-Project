package com.example.nfcproject

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Parcelable
import android.util.Log

class NFCHandler {
     fun processIntent(checkIntent: Intent):String {
        var result = ""
        if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null) {
                var t = checkIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID)?.joinToString("") { "%02x".format(it) }?.uppercase()
                Log.d("NFCProjectTestDebug","Серийный номер "+t)
                val messages = arrayOfNulls<NdefMessage?>(rawMessages.size)
                for (i in rawMessages.indices) {
                    messages[i] = rawMessages[i] as NdefMessage;
                }
                result = processNdefMessages(messages)
            }
        }
        return result
    }
     fun processNdefMessages(ndefMessages: Array<NdefMessage?>):String{
         var result = ""
         for (curMsg in ndefMessages) {
            if (curMsg != null) {
                for (curRecord in curMsg.records) {
                    result = String(curRecord.payload).substring(3)
                }
            }
        }
         return result
    }
}