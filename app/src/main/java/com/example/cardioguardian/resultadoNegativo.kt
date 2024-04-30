package com.example.cardioguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class resultadoNegativo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_negativo)

        val casabtn: ImageButton = findViewById<ImageButton>(R.id.casa)

        casabtn.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val agregarBtn : ImageButton = findViewById<ImageButton>(R.id.Agregar)
        agregarBtn.setOnClickListener {
            val intent = Intent(this, modeloCardiaco::class.java)
            startActivity(intent)
        }
    }
}