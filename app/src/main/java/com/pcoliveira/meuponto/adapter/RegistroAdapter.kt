package com.pcoliveira.meuponto.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.data.Registro // Corrigido o caminho do import
import com.pcoliveira.meuponto.ui.SolicitarAjusteActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroAdapter(private var registros: List<Registro>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        holder.bind(registro)
    }

    override fun getItemCount(): Int = registros.size

    fun updateData(newRegistros: List<Registro>) {
        registros = newRegistros
        notifyDataSetChanged() // Para simplicidade. Em apps reais, DiffUtil é melhor.
    }

    inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRegistroInfo: TextView = itemView.findViewById(R.id.tvRegistroInfo)
        private val btnSolicitarAjuste: MaterialButton = itemView.findViewById(R.id.btnSolicitarAjusteItem)

        fun bind(registro: Registro) {
            // Assumindo que 'registro.id' é Int e 'registro.timestamp' é Long.
            // Adapte se os nomes dos campos ou tipos forem diferentes no seu modelo Registro.
            val formattedDate = dateFormat.format(Date(registro.timestamp))
            tvRegistroInfo.text = "ID ${registro.id}: $formattedDate"

            btnSolicitarAjuste.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, SolicitarAjusteActivity::class.java).apply {
                    // Supondo que 'registro.id' é o campo correto para passar como 'registroId'
                    putExtra("registroId", registro.id)
                }
                context.startActivity(intent)
            }
        }
    }
}
