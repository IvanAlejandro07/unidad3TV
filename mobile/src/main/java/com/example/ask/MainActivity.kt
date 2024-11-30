package com.example.ask

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.example.ask.adapters.ScoreAdapter

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        database = FirebaseDatabase.getInstance("https://cronometro-3e1e3-default-rtdb.firebaseio.com/").reference


        recyclerView = findViewById(R.id.recyclerViewScores)
        recyclerView.layoutManager = LinearLayoutManager(this)


        adapter = ScoreAdapter()
        recyclerView.adapter = adapter


        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        database.child("scores").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scores = mutableListOf<String>()
                for (scoreSnapshot in snapshot.children) {
                    val fecha = scoreSnapshot.child("fecha").getValue(String::class.java)
                    val puntuacion = scoreSnapshot.child("puntuacion").getValue(Int::class.java)

                    if (fecha != null && puntuacion != null) {
                        scores.add("Puntuación: $puntuacion, Fecha: $fecha")
                    } else {
                        Log.w("MainActivity", "Error al obtener datos de una entrada")
                    }
                }
                adapter.setScores(scores)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Error al leer datos de Firebase: ${error.message}")
                Toast.makeText(this@MainActivity, "Error al cargar los datos.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
