package com.example.ask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ask.R

class ScoreAdapter : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    private val scores = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    override fun getItemCount(): Int = scores.size

    fun setScores(newScores: List<String>) {
        scores.clear()
        scores.addAll(newScores)
        notifyDataSetChanged()
    }

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewScore: TextView = itemView.findViewById(R.id.textViewScore)

        fun bind(score: String) {
            textViewScore.text = score
        }
    }
}
