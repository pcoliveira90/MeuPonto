package com.pcoliveira.meuponto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.model.Registro
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroAdapter(
    private var registros: List<Registro>,
    private val onAjustarClick: (registroId: Long) -> Unit
) : RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRegistroInfo: TextView = itemView.findViewById(R.id.tvRegistroInfo)
        val tvAjustar: TextView = itemView.findViewById(R.id.tvAjustar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        val texto = "ID ${registro.id}: ${dateFormat.format(Date(registro.timestamp))}"
        holder.tvRegistroInfo.text = texto

        holder.tvAjustar.setOnClickListener {
            onAjustarClick(registro.id.toLong())
        }
    }

    override fun getItemCount(): Int = registros.size

    fun updateList(newList: List<Registro>) {
        registros = newList
        this.notifyDataSetChanged()
    }
}
