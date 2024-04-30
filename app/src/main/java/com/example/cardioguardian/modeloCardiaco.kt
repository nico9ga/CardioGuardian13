package com.example.cardioguardian

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import java.nio.FloatBuffer

class modeloCardiaco : AppCompatActivity() {
    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var ortSession: OrtSession

    // Media y desviación estándar de tus datos de entrenamiento
    private val mean = floatArrayOf(
        53.510895F, 0.21023965F, 2.251634F, 132.39651F, 198.79956F,
        0.23311546F, 0.6034858F, 136.80937F, 0.40413943F, 0.88736385F,
        0.6383442F
    )
    private val stdDev = floatArrayOf(
        9.427478F, 0.40747875F, 0.93052363F, 18.504066F, 109.324554F,
        0.42281514F, 0.8055289F, 25.446463F, 0.4907247F, 1.065989F,
        0.60672545F)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelo_cardiaco)

        val casabtn: ImageButton = findViewById<ImageButton>(R.id.casa)

        casabtn.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val sirena: ImageButton = findViewById<ImageButton>(R.id.Sirena)

        sirena.setOnClickListener{
            val intent = Intent(this, Emergencias::class.java)
            startActivity(intent)
        }

        val spinner2: Spinner = findViewById(R.id.spinner2)
        val spinner3: Spinner = findViewById(R.id.spinner3)
        val spinner6: Spinner = findViewById(R.id.spinner6)
        val spinner7: Spinner = findViewById(R.id.spinner7)
        val spinner9: Spinner = findViewById(R.id.spinner9)
        val spinner11: Spinner = findViewById(R.id.spinner11)
        // Crear un ArrayAdapter usando el string array y un default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.sexo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner2.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.tipodolorpecho_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner3.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.azucarensangre_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner6.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.electroreposo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner7.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.anginainducida_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner9.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.pendientest_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adapter al spinner
            spinner11.adapter = adapter
        }

        val firstCallMap = hashMapOf<Int, Boolean>(
            R.id.spinner2 to true,
            R.id.spinner3 to true,
            R.id.spinner6 to true,
            R.id.spinner7 to true,
            R.id.spinner9 to true,
            R.id.spinner11 to true
        )

        val spinnerIds = listOf(R.id.spinner2, R.id.spinner3, R.id.spinner6, R.id.spinner7, R.id.spinner9, R.id.spinner11)

        for (spinnerId in spinnerIds) {
            val spinner = findViewById<Spinner>(spinnerId)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (firstCallMap[spinnerId] == true) {
                        firstCallMap[spinnerId] = false
                    } else {
                        val seleccionado = parent.getItemAtPosition(position).toString()
                        if (seleccionado != "Seleccione una opción") {
                            Toast.makeText(
                                this@modeloCardiaco,
                                "Opción seleccionada: $seleccionado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            Toast.makeText(
                                this@modeloCardiaco,
                                "Por favor, selecciona una opción",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Aquí puedes escribir código para realizar alguna acción cuando no se selecciona nada
                }
            }
        }

        // Crear el entorno ONNX y la sesión
        ortEnvironment = OrtEnvironment.getEnvironment()
        ortSession = createORTSession(ortEnvironment)

        // Configurar el botón para ejecutar el modelo
        val buttonRunModel = findViewById<ImageButton>(R.id.buttonRunModel)
        buttonRunModel.setOnClickListener {
            // Obtener los valores de los EditTexts
            val floatInputs = mutableListOf<Float>()
            for (i in 1..11) {
                val editTextId = resources.getIdentifier("editText$i", "id", packageName)
                val spinnerId = resources.getIdentifier("spinner$i", "id", packageName)

                val editText = findViewById<EditText>(editTextId)
                val spinner = findViewById<Spinner>(spinnerId)

                try {
                    if (editText != null) {
                        // Es un EditText
                        val text = editText.text.toString()
                        if (text.isEmpty() || text.toFloat() < 0) {
                            throw IllegalArgumentException("Por favor, llena todos los campos.")
                        }
                        floatInputs.add(text.toFloat())
                    } else if (spinner != null) {
                        // Es un Spinner
                        val selectedItemPosition = spinner.selectedItemPosition
                        // Aquí puedes convertir la posición seleccionada a tu valor numérico correspondiente
                        val numericValue = selectedItemPosition.toFloat() - 1
                        floatInputs.add(numericValue)
                    }
                } catch (e: Exception) {
                    // Mostrar un Toast con el error
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            // Estandarizar los datos de entrada
            val standardizedInputs = standardizeData(floatInputs.toFloatArray(), mean, stdDev)

            // Ejecutar el modelo con los valores obtenidos
            try {
                val resultado = runPrediction(standardizedInputs, ortSession, ortEnvironment)

                if(resultado == 1.toFloat()){
                    val intent = Intent(this, resultadoPositivo::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(this, resultadoNegativo::class.java)
                    startActivity(intent)
                }

            } catch (e: Exception) {
                // Manejar cualquier excepción que pueda ocurrir durante la predicción
                Toast.makeText(this, "Error al ejecutar el modelo: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun createORTSession(ortEnvironment: OrtEnvironment): OrtSession {
        val modelBytes = resources.openRawResource(R.raw.svm2_model).readBytes()
        return ortEnvironment.createSession(modelBytes)
    }

    // Función para estandarizar los datos
    private fun standardizeData(inputData: FloatArray, mean: FloatArray, stdDev: FloatArray): FloatArray {
        val standardizedData = FloatArray(inputData.size)
        for (i in inputData.indices) {
            standardizedData[i] = (inputData[i] - mean[i]) / stdDev[i]
        }
        return standardizedData
    }

    // Función para hacer predicciones con las entradas dadas
    private fun runPrediction(
        floatInputs: FloatArray,
        ortSession: OrtSession,
        ortEnvironment: OrtEnvironment
    ): Float {
        // Obtener los nombres de los nodos de entrada del modelo
        val floatInputName = ortSession.inputNames?.get(0).toString()

        // Crear tensores de entrada con los datos proporcionados
        val floatInputsBuffer = FloatBuffer.wrap(floatInputs)
        val floatInputTensor = OnnxTensor.createTensor(
            ortEnvironment,
            floatInputsBuffer,
            longArrayOf(1, floatInputs.size.toLong())
        )

        // Ejecutar el modelo
        val results = ortSession.run(
            mapOf(
                floatInputName to floatInputTensor
            )
        )

        // Obtener y devolver los resultados
        val outputLong = results[0].value as LongArray
        val outputFloat = outputLong.map { it.toFloat() }.toFloatArray()
        return outputFloat[0]
    }
}

private fun <E> MutableSet<E>.get(i: Int): E? {
    return this.elementAtOrNull(i)
}