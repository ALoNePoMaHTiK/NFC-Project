package com.example.nfcproject

import android.os.StrictMode
import android.util.Log
import java.nio.charset.Charset
import java.sql.*



class DBConnection {
    private val ip = "msuniversity.ru:1450" // your database server ip
    private val db = "nfcattend" // your database name
    private val username = "nfcattend"// your database username
    private val password = "nfcattend" // your database password

    private fun dbConn() : Connection? {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        var connString: String?
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connString = "jdbc:jtds:sqlserver://$ip;databaseName=$db;user=$username;password=$password;"
            conn = DriverManager.getConnection(connString)
        }catch (ex: SQLException){
            Log.e("Error : ", ex.message.toString())
        }catch (ex1: ClassNotFoundException){
            Log.e("Error : ", ex1.message.toString())
        }catch (ex2: Exception){
            Log.e("Error : ", ex2.message.toString())
        }
        return conn
    }
    fun readDB(query:String): ResultSet?{
        var rs: ResultSet? = null
        try {
            val conn = dbConn()
            val s: Statement = conn!!.createStatement()
            rs = s.executeQuery(query)
        }
        catch (ex: Exception){
            Log.e("Error : ", ex.message.toString())
        }
        finally {
            return rs
        }
    }
    fun writeDB(query:String):Boolean{
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var result = s.executeUpdate(query)
            return true
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
            return false
        }
    }

    //Получение StudentId по StudentCardId
    fun getStudentId(StudentCardId:String):String{
        var StudentId = ""
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var rs = s.executeQuery(String.format("SELECT StudentId FROM Students WHERE StudentCardId = '%s';",StudentCardId))
            while (rs.next()){
                StudentId = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return StudentId
    }


    //TODO Доделать
    fun postStudentCheckout(StudentId: String, NFCTagId:String):Boolean{
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var result = s.executeUpdate(String.format("INSERT INTO StudentCheckouts(StudentId,NFCTagId) VALUES ('%s','%s')",))
            return true
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
            return false
        }
    }

    //Получение NFC метки по серийному номеру
    fun getNFCTag(SerialNumber:String):String{
        var NFCTag = ""
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var rs = s.executeQuery(String.format("SELECT NFCTagId FROM NFCTags WHERE SerialNumber = '%s';",SerialNumber))
            while (rs.next()){
                NFCTag = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return NFCTag
    }

    //?Получение Логина и Пароля студента по номеру студенческого
    fun getStudentCredentials(StudentCardId:String):List<String>{
        var StudentLogin = ""
        var StudentPassword = ""
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var rs = s.executeQuery(String.format("SELECT StudentLogin,StudentPassword FROM Students WHERE StudentCardId = '%s';",StudentCardId))
            while (rs.next()){
                StudentLogin = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
                StudentPassword = rs.getString(2).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return listOf(StudentLogin,StudentPassword)
    }

    //?Получение соли для хашей студента по номеру студенческого
    fun getSault(StudentCardId:String):String{
        var result = ""
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var rs = s.executeQuery(String.format("SELECT Salt FROM Students WHERE StudentCardId = '%s';",StudentCardId))
            while (rs.next()){
                result = rs.getString(1)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return result
    }

    //?Получение хэша сообщения
    fun getHash(text:String):String{
        var result = ""
        try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            var rs = s.executeQuery(String.format("EXEC DoubleHash '%s';",text))
            while (rs.next()){
                result = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return result
    }
}