package com.example.cardioguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class resultadoPositivo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_positivo)

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