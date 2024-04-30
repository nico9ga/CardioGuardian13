package com.example.cardioguardian

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val modelbuttn: ImageButton = findViewById(R.id.Agregar)

//      Establecer un OnClickListener en el botón
        modelbuttn.setOnClickListener {
            // Crear un Intent para iniciar RegisterActivity
            val intent = Intent(this, modeloCardiaco::class.java)

            // Iniciar RegisterActivity
            startActivity(intent)
        }
    }
}