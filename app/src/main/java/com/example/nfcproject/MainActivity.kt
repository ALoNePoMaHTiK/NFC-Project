package com.example.nfcproject

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.sql.Statement
import java.sql.ResultSet

lateinit var Label:TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Label = findViewById<TextView>(R.id.label)
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