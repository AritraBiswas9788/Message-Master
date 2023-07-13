package com.example.messagemaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class messageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val phoneList=intent.getStringArrayListExtra("numbers")!!
        val mailList =intent.getStringArrayListExtra("emails")!!


    }
}