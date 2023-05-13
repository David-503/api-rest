package com.example.tallerpracticoi_dsm.teacher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.scholar.Alumno

class ProfesorAdapter (private val profesores: List<Profesor>) : RecyclerView.Adapter<com.example.tallerpracticoi_dsm.teacher.ProfesorAdapter.ViewHolder>() {
    private var onItemClick: ProfesorAdapter.OnItemClickListener? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.tvPNombre)
        val apellidoTextView: TextView = view.findViewById(R.id.tvPApellido)
        val edadTextView: TextView = view.findViewById(R.id.tvPEdad)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profesor_item, parent, false)
        return ProfesorAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorAdapter.ViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.nombreTextView.text = profesor.nombre
        holder.apellidoTextView.text = profesor.apellido
        holder.edadTextView.text = profesor.edad.toString()

        // Agrega el escuchador de clics a la vista del elemento de la lista
        holder.itemView.setOnClickListener {
            onItemClick?.onItemClick(profesor)
        }
    }

    override fun getItemCount(): Int {
        return profesores.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClick = listener
    }

    interface OnItemClickListener {
        fun onItemClick(profesor: Profesor)
    }
}