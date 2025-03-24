package com.example.learncook

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.RecetaAdapter
import com.example.learncook.databinding.ActivityBuscarPresupuestoBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.RecetaDatos

class BuscarPresupuestoActivity : AppCompatActivity(), ListenerRecycleReceta {
    private lateinit var binding: ActivityBuscarPresupuestoBinding
    private lateinit var db: LearnCookDB
    private  var recetas = mutableListOf<RecetaDatos>()
    private lateinit var recetaAdapter: RecetaAdapter
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarPresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario",-1)

        binding.Buscar.setOnClickListener {
            var presupuestoMinimo = binding.edtPresupuestoMinimo.text.toString()
            var presupuestoMaximo = binding.edtPresupuestoMaximo.text.toString()
            if(validardatos(presupuestoMinimo,presupuestoMaximo)){
                configuracionRecycle()
                buscarRecetasPorPresupuesto(presupuestoMinimo, presupuestoMaximo)
            }
        }
    }

    private fun buscarRecetasPorPresupuesto(presupuestoMinimo: String, presupuestoMaximo: String) {
        var minimo = presupuestoMinimo.toDouble()
        var maximo = presupuestoMaximo.toDouble()
        val recetasUnicas = mutableSetOf<RecetaDatos>()
        val recetasEncontradas = db.buscarRecetasPorPresupuesto(minimo, maximo)
        recetasUnicas.addAll(recetasEncontradas)
        recetas.clear()
        recetas.addAll(recetasUnicas)
        if(recetas.isNotEmpty()){
            for (receta in recetas) {
                receta.ingredientes = db.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }
            binding.recyclerPresupuesto.visibility = View.VISIBLE
            binding.tvNoHayRecetas.visibility = View.GONE
            recetaAdapter = RecetaAdapter(recetas, this@BuscarPresupuestoActivity)
            binding.recyclerPresupuesto.adapter = recetaAdapter
            recetaAdapter.notifyDataSetChanged()
        }else{
            binding.recyclerPresupuesto.visibility = View.GONE
            binding.tvNoHayRecetas.visibility = View.VISIBLE
        }
    }


    private fun configuracionRecycle(){
        binding.recyclerPresupuesto.layoutManager = LinearLayoutManager(this)
        binding.recyclerPresupuesto.setHasFixedSize(true)
    }

    private fun validardatos(presupuestoMinimo: String, presupuestoMaximo: String): Boolean {
        var bandera =false
        if(presupuestoMinimo.isEmpty()){
            binding.edtPresupuestoMinimo.error = "llena este campo"
            return bandera
        }
        if(presupuestoMaximo.isEmpty()){
            binding.edtPresupuestoMaximo.error = "llena este campo"
            return bandera
        }

        var minimo :Double
        var maximo :Double

        try {
            minimo = presupuestoMinimo.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El presupuesto minimo no es un numero", Toast.LENGTH_SHORT).show()
            return bandera
        }
        try {
            maximo = presupuestoMaximo.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El presupuesto maximo no es un numero", Toast.LENGTH_SHORT).show()
            return bandera
        }
        if (!(minimo>0) || !(maximo>0)){
            Toast.makeText(this, "No puedes poner numeros negativos o menor a 1", Toast.LENGTH_SHORT).show()
            return bandera
        }
        if(!(maximo>=minimo)){
            Toast.makeText(this, "El presupuesto maximo no debe ser menor o igual al minimo", Toast.LENGTH_SHORT).show()
            return bandera
        }
        bandera = true

        return bandera
    }

    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(this@BuscarPresupuestoActivity, CalificarRecetaActivity::class.java)
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


}