package com.example.nfcproject.Handlers

import android.os.StrictMode
import android.util.Log
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
        val connString: String?
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
    private fun readDB(query:String): ResultSet?{
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
    private fun writeDB (query:String):Boolean{
        return try {
            val conn = DBConnection().dbConn()
            val s: Statement = conn!!.createStatement()
            s.executeUpdate(query)
            true
        } catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
            false
        }
    }

    /**
     * Функция для поиска студента по его ID
     * @param StudentId id студента типа String
     * @return ResultSet (login and Pass)
     */
    fun searchStudentByCardId(StudentCardId: String): ResultSet {
        return readDB(
            String.format(
                "SELECT StudentLogin,StudentPassword FROM Students WHERE StudentCardId = '%s';",
                StudentCardId
            )
        ) as ResultSet
    }
    /**
     * Функция для добавления нового студента
     * @param StudentId id студента типа String
     * @param StudentFName имя студента
     * @param StudentLName фамилия студента
     *
     */
    fun addNewStudent(StudentCardId: String,StudentFName: String, StudentLName: String){
        writeDB(
            String.format(
                "INSERT INTO Students(StudentCardId,StudentLogin,StudentPassword) VALUES ('%s','%s','%s');",
                StudentCardId,
                StudentFName,
                StudentLName
            )
        )
    }

    //Получение StudentId по StudentCardId
    fun getStudentId(StudentCardId:String):String{
        var StudentId = ""
        try {
            val rs = readDB(String.format("SELECT StudentId FROM Students WHERE StudentCardId = '%s';", StudentCardId)) as ResultSet
            while (rs.next())
                StudentId = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug: ", ex.message.toString())
        }
        return StudentId
    }


    //TODO Доделать
    fun postStudentCheckout(StudentId: String, NFCTagId:String):Boolean{
        return try {
            val result = writeDB(String.format("INSERT INTO StudentCheckouts(StudentId,NFCTagId) VALUES ('%s','%s')",StudentId, NFCTagId))
            true
        } catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
            false
        }
    }

    //Получение NFC метки по серийному номеру
    fun getNFCTagId(SerialNumber: String): String{
        var NFCTag = ""
        try {
            val rs = readDB(String.format("SELECT NFCTagId FROM NFCTags WHERE SerialNumber = '%s';", SerialNumber)) as ResultSet
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
    fun getStudentCredentials(StudentCardId: String):List<String>{
        var StudentLogin = ""
        var StudentPassword = ""
        var Salt = ""
        try {
            val rs = readDB(String.format("SELECT StudentLogin, StudentPassword, Salt FROM Students WHERE StudentCardId = '%s';", StudentCardId)) as ResultSet
            while (rs.next()){
                StudentLogin = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
                StudentPassword = rs.getString(2).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
                Salt = rs.getString(3)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return listOf(StudentLogin, StudentPassword, Salt)
    }

    //?Получение соли для хашей студента по номеру студенческого
    fun getSault(StudentCardId:String):String{
        var result = ""
        try {
            val rs = readDB(String.format("SELECT Salt FROM Students WHERE StudentCardId = '%s';", StudentCardId)) as ResultSet
            while (rs.next()){
                result = rs.getString(1)
            }
        }
        catch (ex: Exception){
            Log.e("NFCProjectTestDebug : ", ex.message.toString())
        }
        return result
    }
    //Получение хэша сообщения
    fun getHash(text:String):String{
        var result = ""
        try {
            val rs = readDB(String.format("EXEC DoubleHash '%s';", text)) as ResultSet
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