package com.example.nfcproject

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnection {
    private val ip = "msuniversity.ru:1450" // your database server ip
    private val db = "nfcattend" // your database name
    private val username = "nfcattend" // your database username
    private val password = "nfcattend" // your database password

    fun dbConn() : Connection? {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        var connString: String? = null
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
}