package com.example.ask.presentation

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.ask.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val questions = listOf(
        "¿Cuál es la capital de Francia?",
        "¿Cuál es el idioma oficial de Brasil?",
        "¿Cuántos continentes hay en el mundo?",
        "¿Cuál es el planeta más cercano al Sol?",
        "¿Cuál es el océano más grande?"
    )

    private val answers = listOf(
        listOf("París", "Roma", "Berlín"),
        listOf("Portugués", "Español", "Inglés"),
        listOf("5", "6", "7"),
        listOf("Venus", "Mercurio", "Tierra"),
        listOf("Atlántico", "Pacífico", "Índico")
    )

    private val correctAnswers = listOf(0, 0, 2, 1, 1)
    private var currentQuestion = 0
    private var score = 0

    // Firebase references
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa Firebase
        database = FirebaseDatabase.getInstance("https://cronometro-3e1e3-default-rtdb.firebaseio.com/")
        databaseReference = database.reference

        showQuestion()

        val restartButton: Button = findViewById(R.id.restart_button)
        restartButton.setOnClickListener {
            // Cargar la animación de explosión
            val explodeAnimation = AnimationUtils.loadAnimation(this, R.anim.explode_animation)
            restartButton.startAnimation(explodeAnimation)

            // Guardar la puntuación y fecha cuando termine el juego
            explodeAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    saveScoreToFirebase()
                    restartGame()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }
    }

    private fun showQuestion() {
        val questionText: TextView = findViewById(R.id.question_text)
        val answerButton1: Button = findViewById(R.id.answer_button1)
        val answerButton2: Button = findViewById(R.id.answer_button2)
        val answerButton3: Button = findViewById(R.id.answer_button3)
        val resultText: TextView = findViewById(R.id.result_text)
        val restartButton: Button = findViewById(R.id.restart_button)

        if (currentQuestion < questions.size) {
            questionText.text = questions[currentQuestion]
            answerButton1.text = answers[currentQuestion][0]
            answerButton2.text = answers[currentQuestion][1]
            answerButton3.text = answers[currentQuestion][2]

            answerButton1.setOnClickListener { checkAnswer(0, resultText) }
            answerButton2.setOnClickListener { checkAnswer(1, resultText) }
            answerButton3.setOnClickListener { checkAnswer(2, resultText) }
        } else {
            questionText.text = "¡Juego terminado! Puntuación: $score/${questions.size}"
            answerButton1.visibility = Button.GONE
            answerButton2.visibility = Button.GONE
            answerButton3.visibility = Button.GONE
            resultText.text = ""
            restartButton.visibility = Button.VISIBLE
        }
    }

    private fun checkAnswer(answerIndex: Int, resultText: TextView) {
        if (answerIndex == correctAnswers[currentQuestion]) {
            score++
            resultText.text = "¡Correcto!"
        } else {
            resultText.text = "Incorrecto"
        }
        currentQuestion++
        showQuestion()
    }

    private fun restartGame() {
        // Restablece el estado del juego
        currentQuestion = 0
        score = 0
        findViewById<Button>(R.id.answer_button1).visibility = Button.VISIBLE
        findViewById<Button>(R.id.answer_button2).visibility = Button.VISIBLE
        findViewById<Button>(R.id.answer_button3).visibility = Button.VISIBLE
        findViewById<Button>(R.id.restart_button).visibility = Button.GONE

        // Navegar de regreso a activity_welcome
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Guardar puntuación y fecha en Firebase
    private fun saveScoreToFirebase() {
        val scoreData = mapOf(
            "puntuacion" to score,
            "fecha" to getCurrentDate()
        )

        val scoreRef = databaseReference.child("scores").push() // Genera una ID única para cada puntuación
        scoreRef.setValue(scoreData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Puntuación y fecha guardados correctamente en Firebase")
            } else {
                println("Error al guardar la puntuación: ${task.exception?.message}")
            }
        }
    }

    // Función para obtener la fecha actual
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
