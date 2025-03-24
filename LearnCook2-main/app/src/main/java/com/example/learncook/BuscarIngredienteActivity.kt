package com.example.learncook

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learncook.adaptadores.RecetaAdapter
import com.example.learncook.databinding.ActivityBuscarIngredienteBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.RecetaDatos

class BuscarIngredienteActivity : AppCompatActivity(), ListenerRecycleReceta {
    private lateinit var binding: ActivityBuscarIngredienteBinding
    private lateinit var learnCookDB: LearnCookDB
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listaDeIngredientes: List<Ingrediente>
    private lateinit var recetaAdapter: RecetaAdapter
    private  var recetas = mutableListOf<RecetaDatos>()
    private lateinit var ingredienteSeleccionado: Ingrediente
    private var idIngrediente = mutableListOf<Int>()
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarIngredienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        learnCookDB = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario",-1)
        listaDeIngredientes = learnCookDB.traerIngredientes()

        adapter = ArrayAdapter(
            this,
            R.layout.spiner_item,
            listaDeIngredientes.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerIngredientes.adapter = adapter

        binding.spinnerIngredientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (position >= 1 && position < listaDeIngredientes.size) {
                    ingredienteSeleccionado = listaDeIngredientes[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spinnerIngredientes.setSelection(0)
            }
        }

        binding.btnAgregar.setOnClickListener {
            agregarIngrediente(ingredienteSeleccionado)
        }

        binding.btnBuscar.setOnClickListener {
            configuracionRecycle()
            val ingredientesSeleccionados = obtenerIngredientesSeleccionados()
            if (ingredientesSeleccionados.isNotEmpty()) {
                buscarRecetasPorIngredientes()
            } else {
                Toast.makeText(this, "Agregue al menos un ingrediente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarIngrediente(ingrediente: Ingrediente) {
        val nuevaFila = TableRow(this)
        val textView = TextView(this)
        textView.text = ingrediente.nombre
        textView.setTextColor(Color.WHITE)
        textView.setPadding(16, 16, 16, 16)
        nuevaFila.addView(textView)


        binding.tblIngredientes.addView(nuevaFila)
        idIngrediente.add(ingrediente.id)

        Toast.makeText(this, "Ingrediente agregado: ${ingrediente.nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun obtenerIngredientesSeleccionados(): List<String> {
        val ingredientesSeleccionados = mutableListOf<String>()
        for (i in 0 until binding.tblIngredientes.childCount) {
            val fila = binding.tblIngredientes.getChildAt(i) as TableRow
            val textView = fila.getChildAt(0) as TextView
            ingredientesSeleccionados.add(textView.text.toString())
        }
        return ingredientesSeleccionados
    }

    private fun buscarRecetasPorIngredientes() {
        //mutableSetOf guarda los conjuntos no permiten elementos duplicados por naturaleza.
        val recetasUnicas = mutableSetOf<RecetaDatos>()
        if (idIngrediente.isNotEmpty()) {
            for (id in idIngrediente) {
                val recetasEncontradas = learnCookDB.buscarRecetasPorIngredientes(id)
                recetasUnicas.addAll(recetasEncontradas)
            }

            recetas.clear()
            recetas.addAll(recetasUnicas)

            for (receta in recetas) {
                receta.ingredientes = learnCookDB.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }
            if(recetas.isNotEmpty()) {
                recetaAdapter = RecetaAdapter(recetas, this@BuscarIngredienteActivity)
                binding.recycleRecetas.adapter = recetaAdapter
                recetaAdapter.notifyDataSetChanged()
            }else {
                Toast.makeText(this, "No se encontraron recetas con los ingredientes seleccionados", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "no puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "no puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(this@BuscarIngredienteActivity, CalificarRecetaActivity::class.java)
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
    private fun configuracionRecycle(){
        binding.recycleRecetas.layoutManager = LinearLayoutManager(this)
        binding.recycleRecetas.setHasFixedSize(true)
    }
}
