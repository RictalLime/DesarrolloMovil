package com.example.learncook.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learncook.R
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.poko.RecetaDatos

class RecetaAdapter(private val recetas: List<RecetaDatos>, private val listener: ListenerRecycleReceta) :
    RecyclerView.Adapter<RecetaAdapter.ViewHolderReceta>() {

    inner class ViewHolderReceta(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreUsuario: TextView = itemView.findViewById(R.id.tv_nombre_usuario)
        val tvNombreReceta: TextView = itemView.findViewById(R.id.tv_titulo)
        val tvTiempo: TextView = itemView.findViewById(R.id.tv_tiempo)
        val tvIngredientes: TextView = itemView.findViewById(R.id.tv_ingredientes)
        val tvPresupuesto: TextView = itemView.findViewById(R.id.tv_presupuesto)
        val tvPreparacion: TextView = itemView.findViewById(R.id.tv_preparacion)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btn_delete)
        val btnCalificar: ImageButton = itemView.findViewById(R.id.calificar_receta)
        val btnCompartir: ImageButton = itemView.findViewById(R.id.btn_compartir)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btn_editar)

        init {
            btnCalificar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicCalificarReceta(recetas[position], position)
                }
            }

            btnCompartir.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicCompartirReceta(recetas[position], position)
                }
            }

            btnEliminar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicEliminarReceta(recetas[position], position)
                }
            }

            btnEditar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicEditarReceta(recetas[position], position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReceta {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_recetas, parent, false)
        return ViewHolderReceta(itemView)
    }

    override fun getItemCount(): Int {
        return recetas.size
    }

    override fun onBindViewHolder(holder: ViewHolderReceta, position: Int) {
        val receta = recetas[position]

        val ingredientes = receta.ingredientes?.joinToString(separator = "\n") { "${it.cantidad} - ${it.nombre}" }
        holder.tvNombreUsuario.text = receta.nombreUsuario
        holder.tvNombreReceta.text = receta.nombreReceta
        holder.tvTiempo.text = receta.tiempo
        holder.tvPresupuesto.text = "${receta.presupuesto}"
        holder.tvPreparacion.text = receta.preparacion
        holder.tvIngredientes.text = ingredientes
    }
}
