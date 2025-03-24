package com.example.learncook.fragmentos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.AgregarRecetaActivity
import com.example.learncook.CalificarRecetaActivity
import com.example.learncook.EditarRecetaActivity
import com.example.learncook.MainActivity
import com.example.learncook.adaptadores.RecetaAdapter
import com.example.learncook.databinding.FragmentRecetaBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.RecetaDatos

private const val ARG_ID_USUARIO = "idUsuario"

class RecetaFragment : Fragment(), ListenerRecycleReceta {
    private lateinit var binding: FragmentRecetaBinding
    private var idUsuario: Int = -1
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getInt(ARG_ID_USUARIO)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecetaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelo = LearnCookDB(requireContext())

        binding.btnAgregarReceta.setOnClickListener {
            val intent = Intent(requireContext(), AgregarRecetaActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }
        cargarMisRecetas()
        configuracionRecycle()

    }
    override fun onResume() {
        super.onResume()
        cargarMisRecetas()
    }

    companion object {
        @JvmStatic
        fun newInstance(idUsuario: Int) =
            RecetaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID_USUARIO, idUsuario)
                }
            }
    }

    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(requireContext(), EditarRecetaActivity::class.java)
        intent.putExtra("idReceta",receta.idReceta)
        startActivity(intent)
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        var idReceta = receta.idReceta
        modelo.eliminarIngredientes(idReceta)
        modelo.eliminarCalificaciones(idReceta)
        var elimidado = modelo.eliminarReceta(idReceta);
        if(elimidado>0){
            Toast.makeText(context, "Receta eliminada", Toast.LENGTH_SHORT).show()
            cargarMisRecetas()
        }else{
            Toast.makeText(context, "Error al eliminar la receta", Toast.LENGTH_SHORT).show()
        }
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(requireContext(), CalificarRecetaActivity::class.java)
        intent.putExtra("idUsuario",idUsuario)
        intent.putExtra("idReceta",receta.idReceta)
        intent.putExtra("nombreReceta",receta.nombreReceta)
        startActivity(intent)
    }

    override fun clicCompartirReceta(receta: RecetaDatos, position: Int) {
        val mensaje = "Receta: ${receta.nombreReceta}\n" +
                "Elaborada por: ${receta.nombreUsuario}\n" +
                "Ingredientes: ${receta.ingredientes.toString()}\n" +
                "Tiempo: ${receta.tiempo}\n" +
                "Elaboracion: ${receta.preparacion}" +
                "Presupuesto: ${receta.presupuesto}"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mensaje)
        startActivity(Intent.createChooser(intent, "Compartir Receta!"))
    }
    private fun cargarMisRecetas() {
        val recetas = modelo.obtenerRecetasDatosPorUsuario(idUsuario)

        if (recetas.isNotEmpty()) {
            for (receta in recetas) {
                receta.ingredientes = modelo.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }
            binding.tvMensajeRecetas.visibility = View.GONE
            binding.recycleRecetas.visibility = View.VISIBLE
            binding.recycleRecetas.adapter = RecetaAdapter(recetas, this@RecetaFragment)
        } else {
            binding.tvMensajeRecetas.visibility = View.VISIBLE
            binding.recycleRecetas.visibility = View.GONE
        }
    }

    private fun configuracionRecycle(){
        binding.recycleRecetas.layoutManager = LinearLayoutManager(context)
        binding.recycleRecetas.setHasFixedSize(true)
    }


}
