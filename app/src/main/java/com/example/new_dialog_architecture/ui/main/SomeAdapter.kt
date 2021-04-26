package com.example.new_dialog_architecture.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SomeAdapter(private val state: List<String>, private val click: (String) -> Unit) : RecyclerView.Adapter<SomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SomeViewHolder =
            SomeViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))

    override fun onBindViewHolder(holder: SomeViewHolder, position: Int) {
        holder.bind(state[position], click)
    }

    override fun getItemCount(): Int = state.count()

}

class SomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val text: TextView = view.findViewById(android.R.id.text1)

    fun bind(item: String, click: (String) -> Unit) {
        text.text = item
        text.setOnClickListener {
            click(item)
        }
    }
}
