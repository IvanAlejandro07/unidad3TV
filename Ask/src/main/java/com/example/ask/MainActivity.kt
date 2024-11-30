package com.example.ask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ask.ui.theme.AskTheme
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AskTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuizScreen()
                }
            }
        }
    }

    @Composable
    fun QuizScreen() {
        var currentQuestion by remember { mutableStateOf(0) }
        var score by remember { mutableStateOf(0) }
        var isGameFinished by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF303C54)) // Fondo con color HEX #303c54
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isGameFinished) {
                // Pantalla de preguntas
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = questions[currentQuestion],
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White // Color del texto
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val options = answers[currentQuestion]
                    options.forEachIndexed { index, answer ->
                        Button(
                            onClick = {
                                if (index == correctAnswers[currentQuestion]) {
                                    score++
                                }
                                currentQuestion++
                                if (currentQuestion >= questions.size) {
                                    isGameFinished = true
                                    saveScoreToFirebase(score) // Guardado automático
                                }
                            },
                            modifier = Modifier
                                .width(200.dp) // Ancho fijo para botones más pequeños
                                .padding(vertical = 4.dp)
                        ) {
                            Text(text = answer)
                        }
                    }
                }
            } else {
                // Pantalla final
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Juego terminado! Puntuación: $score/${questions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White // Color del texto
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Reiniciar juego
                            currentQuestion = 0
                            score = 0
                            isGameFinished = false
                        },
                        modifier = Modifier.width(200.dp) // Botón pequeño
                    ) {
                        Text(text = "Jugar de nuevo")
                    }
                }
            }
        }
    }

    private fun saveScoreToFirebase(score: Int) {
        val database = FirebaseDatabase.getInstance("https://cronometro-3e1e3-default-rtdb.firebaseio.com/")
        val databaseReference = database.reference

        val scoreData = mapOf(
            "puntuacion" to score,
            "fecha" to getCurrentDate()
        )

        databaseReference.child("scores").push().setValue(scoreData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Puntuación guardada correctamente en Firebase")
                } else {
                    println("Error al guardar la puntuación: ${task.exception?.message}")
                }
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewQuizScreen() {
        AskTheme {
            QuizScreen()
        }
    }
}
