package com.example.nfcproject.Handlers

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.util.Log
import com.example.nfcproject.model.IOData_NFC
import java.io.IOException
import java.util.Arrays
import java.util.UUID
import kotlin.experimental.and

class NFCHandler {

    fun writeNewNote(intent: Intent): IOData_NFC? {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val needAuth = true
        var response = ByteArray(0)
        nfcA.connect()
        if (needAuth) {
            val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
            val packBytes = byteArrayOf(0x4F, 0x4B)                  // OK
            var response = sendPwdAuthData(nfcA, passwordBytes)
            if (response == null) {
                showError("ERROR while verifying password, aborted");
                return null
            }
            val packResponse = response.clone()
            if (Arrays.equals(packResponse, packBytes)) {
                showLog("The entered PACK is correct")
            } else {
                showError("entered PACK: " + bytesToHex(packBytes))
                showError("Respons PACK: " + bytesToHex(packResponse))
                showError("The entered PACK is NOT correct, abort")
                return null
            }
        }

        var oldNoteBytes = ByteArray(0)
        for (i in 10..20 step 4) {
            val response = getPageBytes(nfcA, i)
            if (response != null) {
                oldNoteBytes += response
            } else
                break
        }
        val oldNote = oldNoteBytes.sliceArray((0..35)).toString(Charsets.UTF_8)
        var serialNumber = bytesToHex(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)!!).uppercase()

        val newNote = UUID.randomUUID().toString()
        showLog("New note: $newNote")
        var dataByte = newNote.toByteArray(Charsets.UTF_8)
        val dataPages = dataByte.size / 4

        for (i in 0 until dataPages) {
            var commandW: ByteArray
            commandW = byteArrayOf(
                0xA2.toByte(), (10 + i).toByte(),  // page 4 is the first user memory page
                dataByte[0 + i * 4],
                dataByte[1 + i * 4],
                dataByte[2 + i * 4],
                dataByte[3 + i * 4]
            )
            response = nfcA.transceive(commandW)
            if (response == null) {
                showError("ERROR: null response")
                return null
            } else if (response.size === 1 && ((response[0] and 0x00A) !== 0x00A.toByte())) {
                // NACK response according to Digital Protocol/T2TOP
                // Log and return
                showError("ERROR: NACK response: " + bytesToHex(response))
                return null
            }
        }
        nfcA.close()
        showLog(IOData_NFC(oldNote, newNote, serialNumber).toString())
        return IOData_NFC(oldNote, newNote, serialNumber)
    }

    fun readNote(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val needAuth = false
        var result = ByteArray(0)
        nfcA.connect()
        if (needAuth) {
            val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
            val packBytes = byteArrayOf(0x4F, 0x4B)                  // OK
            var response = sendPwdAuthData(nfcA, passwordBytes)
            if (response == null) {
                showError("ERROR while verifying password, aborted");
                return;
            }
            val packResponse = response.clone()
            if (Arrays.equals(packResponse, packBytes)) {
                showLog("The entered PACK is correct")
            } else {
                showError("entered PACK: " + bytesToHex(packBytes))
                showError("Respons PACK: " + bytesToHex(packResponse))
                showError("The entered PACK is NOT correct, abort")
                return
            }
        }

        for (i in 10..20 step 4) {
            val response = getPageBytes(nfcA, i)
            if (response != null) {
                result += response
            } else
                break
        }
        result = result.sliceArray((0..35))
        showLog(bytesToHex(result))
        showLog(result.toString(Charsets.UTF_8))
        nfcA.close()
    }

    fun writeNote(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val needAuth = false
        var response = ByteArray(0)
        nfcA.connect()
        if (needAuth) {
            val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
            val packBytes = byteArrayOf(0x4F, 0x4B)                  // OK
            var response = sendPwdAuthData(nfcA, passwordBytes)
            if (response == null) {
                showError("ERROR while verifying password, aborted");
                return;
            }
            val packResponse = response.clone()
            if (Arrays.equals(packResponse, packBytes)) {
                showLog("The entered PACK is correct")
            } else {
                showError("entered PACK: " + bytesToHex(packBytes))
                showError("Respons PACK: " + bytesToHex(packResponse))
                showError("The entered PACK is NOT correct, abort")
                return
            }
        }
        try {
            val newNote = UUID.randomUUID().toString()
            showLog("New note: $newNote")
            val dataByte = newNote.toByteArray(Charsets.UTF_8)
            val dataPages = dataByte.size / 4
            val dataPagesMod = dataByte.size % 4

            for (i in 0 until dataPages) {
                println("starting round: $i")
                var commandW: ByteArray
                commandW = byteArrayOf(
                    0xA2.toByte(), (10 + i).toByte(),  // page 4 is the first user memory page
                    dataByte[0 + i * 4],
                    dataByte[1 + i * 4],
                    dataByte[2 + i * 4],
                    dataByte[3 + i * 4]
                )
                response = nfcA.transceive(commandW)
                if (response == null) {
                    showError("ERROR: null response")
                    return
                } else if (response.size === 1 && ((response[0] and 0x00A) !== 0x00A.toByte())) {
                    // NACK response according to Digital Protocol/T2TOP
                    // Log and return
                    showError("ERROR: NACK response: " + bytesToHex(response))
                    return
                } else {
                    showLog("Page $i writed! Response: " + bytesToHex(response))
                }
            }

            if (dataPagesMod == 0) {
                showLog("write result: SUCCESS")
                return
            }
            var commandW = ByteArray(0)
            if (dataPagesMod == 1) {
                commandW = byteArrayOf(
                    0xA2.toByte(),
                    (10 + dataPages).toByte(),  // page 4 is the first user memory page
                    dataByte[0 + dataPages * 4],
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte()
                )
            }
            if (dataPagesMod == 2) {
                commandW = byteArrayOf(
                    0xA2.toByte(),
                    (10 + dataPages).toByte(),  // page 4 is the first user memory page
                    dataByte[0 + dataPages * 4],
                    dataByte[1 + dataPages * 4],
                    0x00.toByte(),
                    0x00.toByte()
                )
            }
            if (dataPagesMod == 3) {
                commandW = byteArrayOf(
                    0xA2.toByte(),
                    (10 + dataPages).toByte(),  // page 4 is the first user memory page
                    dataByte[0 + dataPages * 4],
                    dataByte[1 + dataPages * 4],
                    dataByte[2 + dataPages * 4],
                    0x00.toByte()
                )
            }
            response = nfcA.transceive(commandW)
            if (response == null) {
                showError("ERROR: null response")
                return
            } else if (response.size === 1 && ((response[0] and 0x00A) !== 0x00A.toByte())) {
                // NACK response according to Digital Protocol/T2TOP
                // Log and return
                showError("ERROR: NACK response: " + bytesToHex(response))
                return
            } else {
                showLog("Last page writed! Response:" + bytesToHex(response))
            }
            nfcA.close()
            showLog("write result: SUCCESS")
            return
        } catch (e: Exception) {
            showError(e.stackTraceToString())
        }

    }

    fun setWriteProtection(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
        val packBytes = byteArrayOf(0x4F, 0x4B, 0x00, 0x00)                  // OK
        val startProtectionPage = 4
        nfcA.connect()
        showLog("Запись PWD")
        var responseSuccessful = writeTagData(nfcA, 229, passwordBytes)
        if (!responseSuccessful) {
            showError("Не удалось записать PWD")
            return
        }
        showLog("Запись PACK")
        responseSuccessful = writeTagData(nfcA, 230, packBytes)
        if (!responseSuccessful) {
            showError("Не удалось записать PACK")
            return
        }
        val configurationPages = ByteArray(16)
        showLog("Получение AUTH0")
        responseSuccessful = getTagData(nfcA, 227)
        if (!responseSuccessful) {
            showError("Не удалось получить AUTH0")
            return
        }
        val configurationPage0 = ByteArray(4)
        System.arraycopy(configurationPages, 0, configurationPage0, 0, 4)
        showLog("configuration page old: " + bytesToHex(configurationPage0))
        configurationPage0[3] = (startProtectionPage and 0x0ff).toByte()
        showLog("configuration page 0 new: " + bytesToHex(configurationPage0))
        responseSuccessful = writeTagData(nfcA, 227, configurationPage0)
        if (!responseSuccessful) {
            showError("Не удалось записать страницу!")
            return
        }
        showLog(bytesToHex(configurationPages))

        val configurationPage1 = ByteArray(4)
        System.arraycopy(configurationPages, 4, configurationPage1, 0, 4)
        var accessByte = configurationPage1[0]
        val abOld = ByteArray(1)
        abOld[0] = accessByte
        showLog(
            "Configuration page 1 old: " +
                    bytesToHex(configurationPage1) + " ACCESS byte: " + printByteArrayBinary(
                abOld
            )
        )
        val readProtectionChecked = false;

        // setting bit 7 depends on readProtectionEnabled
        if (readProtectionChecked) {
            // set bit 7 to 1, pos is 0 based
            accessByte = setBitInByte(accessByte, 7)
        } else {
            // set bit 7 to 0, pos is 0 based
            accessByte = unsetBitInByte(accessByte, 7)
        }
        // rebuild the page data
        configurationPage1[0] = accessByte
        val ab = ByteArray(1)
        ab[0] = accessByte
        showLog(
            "Configuration page 1 new: " + bytesToHex(configurationPage1) + " ACCESS byte: " + printByteArrayBinary(
                ab
            )
        )
        responseSuccessful = writeTagData(nfcA, 228, configurationPage1)
        if (!responseSuccessful) showError("Не удалось установить защиту паролем!");
        showLog("NFC tag is password protected now");
        nfcA.close()
    }

    fun removeWriteProtection(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
        val packBytes = byteArrayOf(0x4F, 0x4B)                   // OK
        nfcA.connect()
        val response = sendPwdAuthData(nfcA, passwordBytes)
        if (response == null) {
            showError("ERROR while verifying password, aborted");
            return;
        }
        val packResponseAccepted = Arrays.equals(response, packBytes);
        if (packResponseAccepted) {
            showLog("Password authentication successful, PACK is matching");
        } else {
            showError("Password authentication FAILURE, protection NOT removed");
            return;
        }

        val passwordByteDefault = byteArrayOf(
            0xff.toByte(),
            0xff.toByte(),
            0xff.toByte(),
            0xff.toByte()
        )
        showLog("Обнуление PWD")
        var responseSuccessful = writeTagData(nfcA, 229, passwordByteDefault);
        if (!responseSuccessful) {
            showError("ERROR Не удалось перезаписать PWD на стандартный!")
            return
        }
        val packByteDefault = byteArrayOf(
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
        )
        showLog("Обнуление PACK")
        responseSuccessful = writeTagData(nfcA, 230, packByteDefault);
        if (!responseSuccessful) {
            showError("ERROR Не удалось перезаписать PACK на стандартный!")
            return
        }
        val configurationPages = ByteArray(16)
        responseSuccessful = getTagData(nfcA, 227)
        if (!responseSuccessful) return
        val startProtectionPage = 255 // СТАНДАРТНОЕ ЗНАЧЕНИЕ
        val configurationPage1 = ByteArray(4)
        System.arraycopy(configurationPages, 0, configurationPage1, 0, 4)
        showLog("configuration page old: " + bytesToHex(configurationPage1))
        configurationPage1[3] = (startProtectionPage and 0x0ff).toByte()
        showLog("configuration page new: " + bytesToHex(configurationPage1))
        responseSuccessful = writeTagData(nfcA, 227, configurationPage1)
        if (!responseSuccessful) return
        showLog("Password protection removed")
        nfcA.close()
    }

    fun fixTag(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val passwordByteDefault = byteArrayOf(
            0xff.toByte(),
            0xff.toByte(),
            0xff.toByte(),
            0xff.toByte()
        )
        val packByteDefault = byteArrayOf(
            0x00.toByte(),
            0x00.toByte()
        )
        nfcA.connect()
        val response = sendPwdAuthData(nfcA, passwordByteDefault)
        if (response == null) {
            showError("ERROR while verifying password, aborted");
            return;
        }
        val packResponseAccepted = Arrays.equals(response, packByteDefault);
        if (packResponseAccepted) {
            showLog("Password authentication successful, PACK is matching");
        } else {
            showError("Password authentication FAILURE, protection NOT removed");
            return;
        }
        val configurationPages = ByteArray(16)
        var responseSuccessful = getTagData(nfcA, 227)
        if (!responseSuccessful) return
        val startProtectionPage = 255 // СТАНДАРТНОЕ ЗНАЧЕНИЕ
        val configurationPage1 = ByteArray(4)
        System.arraycopy(configurationPages, 0, configurationPage1, 0, 4)
        showLog("configuration page old: " + bytesToHex(configurationPage1))
        configurationPage1[3] = (startProtectionPage and 0x0ff).toByte()
        showLog("configuration page new: " + bytesToHex(configurationPage1))
        responseSuccessful = writeTagData(nfcA, 227, configurationPage1)
        if (!responseSuccessful) return
        showLog("Password protection removed")
        nfcA.close()

    }

    fun testPassword(checkIntent: Intent) {
        val tag = checkIntent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val nfcA: NfcA = NfcA.get(tag)
        val passwordBytes = byteArrayOf(0x31, 0x32, 0x33, 0x34)    // 1234
        val packBytes = byteArrayOf(0x4F, 0x4B, 0x00, 0x00)                  // OK

        nfcA.connect()
        showLog("Start auth")
        val response = sendPwdAuthData(nfcA, passwordBytes)
        if (response == null) {
            showLog("authentication FAILURE. Maybe wrong password or the tag is not write protected");
            return;
        }
        val packResponse = response.clone();
        if (packResponse == packBytes) {
            showLog("The entered PACK is correct");
        } else {
            showLog("entered PACK: " + bytesToHex(packBytes));
            showLog("Respons PACK: " + bytesToHex(packResponse));
            showLog("The entered PACK is NOT correct, abort");
            return;
        }
//        // write password to page 43/133/229 (NTAG 213/215/216)
//
//        var responseSuccessful = writeTagData(nfcA, 4, passwordBytes, response)
//        if (!responseSuccessful) showLog("Не удалось записать PWD")
//        responseSuccessful = writeTagData(nfcA, 5, packBytes, response)
//        if (!responseSuccessful) showLog("Не удалось записать PACK")
//        val configurationPages = ByteArray(16)
//        responseSuccessful = getTagData(nfcA, 227, configurationPages)
//        if (!responseSuccessful) showLog("Не удалось получить AUTH0")
    }

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
        val newNote = UUID.randomUUID().toString()
        showLog("Перезапись Note")
        val tag = checkIntent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val ndef = Ndef.get(tag)
        if (ndef.isWritable) {
            showLog("Запись на метку!")
            val message = NdefMessage(
                arrayOf(
                    NdefRecord.createTextRecord("ru", newNote)
                )
            )
            try {
                ndef.connect()
                ndef.writeNdefMessage(message)
                ndef.close()
            } catch (e: IOException) {
                showError(e.stackTraceToString())
                showError(e.message.toString())
            }
        }
        showLog(note)
        showLog(newNote)
        showLog(serialNumber)
        return IOData_NFC(note, newNote, serialNumber)
    }

    private fun writeTagData(
        nfcA: NfcA, page: Int, dataByte: ByteArray
    ): Boolean {
        val response: ByteArray?
        val result: Boolean
        val command = byteArrayOf(
            0xA2.toByte(),
            (page and 0x0ff).toByte(),  // page
            dataByte[0],
            dataByte[1],
            dataByte[2],
            dataByte[3]
        )
        try {
            response = nfcA.transceive(command) // response should be 16 bytes = 4 pages
            result = if (response == null) {
                // either communication to the tag was lost or a NACK was received
                showLog("ERROR: null response")
                return false
            } else if (response.size == 1 && response[0].toInt() and 0x00A != 0x00A) {
                // NACK response according to Digital Protocol/T2TOP
                // Log and return
                showLog("ERROR: NACK response: " + bytesToHex(response))
                return false
            } else {
                // success: response contains (P)ACK or actual data
                showLog("SUCCESS: response: " + bytesToHex(response))
                println("write to page " + page + ": " + bytesToHex(response))
                true
            }
        } catch (e: TagLostException) {
            // Log and return
            showLog("ERROR: Tag lost exception")
            return false
        } catch (e: IOException) {
            showLog("IOException: $e")
            e.printStackTrace()
            return false
        } catch (e: Exception) {
            showLog("Exception: $e")
            e.printStackTrace()
            return false
        }
        return result // response contains the response
    }

    private fun getTagData(
        nfcA: NfcA,
        page: Int,
    ): Boolean {
        val response: ByteArray?
        val result: Boolean
        val command = byteArrayOf(0x30.toByte(), (page and 0x0ff).toByte())
        try {
            response = nfcA.transceive(command) // response should be 16 bytes = 4 pages
            result = if (response == null) {
                showLog("ERROR: null response")
                return false
            } else if (response.size == 1 && response[0].toInt() and 0x00A != 0x00A) {
                showLog("ERROR: NACK response: " + bytesToHex(response))
                return false
            } else {
                showLog("SUCCESS: response: " + bytesToHex(response))
                true
            }
        } catch (e: TagLostException) {
            // Log and return
            showLog("ERROR: Tag lost exception")
            return false
        } catch (e: IOException) {
            showLog("IOException: $e")
            e.printStackTrace()
            return false
        }
        return result
    }

    private fun getPageBytes(
        nfcA: NfcA,
        page: Int
    ): ByteArray? {
        val response: ByteArray?
        val command = byteArrayOf(0x30.toByte(), (page and 0x0ff).toByte())
        try {
            response = nfcA.transceive(command) // response should be 16 bytes = 4 pages
            if (response == null) {
                showLog("ERROR: null response")
                return null
            } else if (response.size == 1 && response[0].toInt() and 0x00A != 0x00A) {
                showLog("ERROR: NACK response: " + bytesToHex(response))
                return null
            } else {
                showLog("SUCCESS: response: " + bytesToHex(response))
                return response
            }
        } catch (e: TagLostException) {
            // Log and return
            showLog("ERROR: Tag lost exception")
            return null
        } catch (e: IOException) {
            showLog("IOException: $e")
            e.printStackTrace()
            return null
        }
    }

    private fun sendPwdAuthData(
        nfcA: NfcA,
        passwordByte: ByteArray
    ): ByteArray? {
        showLog("Авторизация")
        val response: ByteArray? // the response is the PACK returned by the tag when successful authentication
        val command = byteArrayOf(
            0x1B.toByte(),  // PWD_AUTH
            passwordByte[0],
            passwordByte[1],
            passwordByte[2],
            passwordByte[3]
        )
        try {
            response = nfcA.transceive(command) // response should be 16 bytes = 4 pages
            if (response == null) {
                // either communication to the tag was lost or a NACK was received
                showLog("ERROR: null response")
                return null
            } else {
                // success: response contains (P)ACK or actual data
                showLog("SUCCESS: response: " + bytesToHex(response))
            }
        } catch (e: TagLostException) {
            showLog("ERROR: Tag lost exception OR Tag is not protected")
            return null
        } catch (e: IOException) {
            showLog("IOException: $e")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            showLog("IOException: $e")
            e.printStackTrace()
            return null
        }
        return response
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }?.uppercase().toString()
    }

    private fun setBitInByte(input: Byte, pos: Int): Byte {
        return (input.toInt() or (1 shl pos)).toByte()
    }

    // position is 0 based starting from right to left
    private fun unsetBitInByte(input: Byte, pos: Int): Byte {
        return (input.toInt() and (1 shl pos).inv()).toByte()
    }

    fun printByteArrayBinary(bytes: ByteArray): String? {
        var output = ""
        for (b1 in bytes) {
            val s1 =
                String.format("%8s", Integer.toBinaryString(b1.toInt() and 0xFF)).replace(' ', '0')
            output = "$output $s1"
        }
        return output
    }

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
    private fun showError(msg: String) = Log.e("NFCProjectTestDebug", msg)
}