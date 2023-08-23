package com.example.nfcproject.Hendlers

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import com.example.nfcproject.model.IOData_NFC
import java.util.UUID

class NFCHandler {

    fun processIntent(checkIntent: Intent): IOData_NFC {
        var note = ""
        var serialNumber: String = ""
        val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (rawMessages != null) {
            serialNumber = checkIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                ?.joinToString("") { "%02x".format(it) }?.uppercase().toString()
            val messages = arrayOfNulls<NdefMessage?>(rawMessages.size)
            for (i in rawMessages.indices) {
                messages[i] = rawMessages[i] as NdefMessage;
            }
            for (curMsg in messages) {
                if (curMsg != null) {
                    for (curRecord in curMsg.records) {
                        note = String(curRecord.payload).substring(3)
                    }
                }
            }
        }
        showLog("Перезапись Note")
        val tag = checkIntent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val ndef = Ndef.get(tag)
        val newNote = UUID.randomUUID().toString()
        if (ndef.isWritable) {
            var message = NdefMessage(
                arrayOf(
                    NdefRecord.createTextRecord("ru", newNote),
                )
            )
            ndef.connect()
            ndef.writeNdefMessage(message)
            ndef.close()
        }
        return IOData_NFC(note, newNote, serialNumber)
    }

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}