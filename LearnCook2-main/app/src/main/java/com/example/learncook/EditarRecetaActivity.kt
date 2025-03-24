package com.example.learncook

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.IngredienteAdapter
import com.example.learncook.databinding.ActivityEditarRecetaBinding
import com.example.learncook.interfaces.ListenerRecycleIngrediente
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.Receta

class EditarRecetaActivity : AppCompatActivity(), ListenerRecycleIngrediente {
    private lateinit var binding: ActivityEditarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private lateinit var ingredientes: List<Ingrediente>
    private lateinit var listaDeIngredientes: MutableList<Ingrediente>
    private lateinit var adapter: ArrayAdapter<String>
    private var idReceta = -1
    private var presupuesto =0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modelo = LearnCookDB(this@EditarRecetaActivity)
        idReceta = intent.getIntExtra("idReceta", -1)


        listaDeIngredientes = modelo.traerIngredientes().toMutableList()

        adapter = ArrayAdapter(
            this,
            R.layout.spiner_item,
            listaDeIngredientes.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spIngredientesEditados.adapter = adapter

        binding.spIngredientesEditados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (position >= 1 && position < listaDeIngredientes.size) {
                    val ingredienteSeleccionado = listaDeIngredientes[position]
                    mostrarDialogCantidad(ingredienteSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spIngredientesEditados.setSelection(0)
            }
        }

        val receta = modelo.obtenerReceta(idReceta)
        configuracionRecycle()
        if (receta != null) {
            llenarDatos(receta)
            ingredientes =  modelo.optenerLosIngredientesPorIdRecetas(idReceta)
            actualizarPresupuesto()
            traerIngredientes()
        } else {
            Toast.makeText(this, "Error al obtener la receta", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnEditarReceta.setOnClickListener {
            if (validarDatos()) {
                var receta2 = receta?.let { it1 -> Receta(it1.id,receta.idUsuario,binding.etNombreReceta.text.toString(),binding.etTiempoReceta.text.toString(),presupuesto,binding.etPreparacion.text.toString()) }

                val numero = modelo.modificarReceta(receta2!!)
                if (numero > 0) {
                    Toast.makeText(this, "Receta editada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al editar receta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarPresupuesto() {
        presupuesto = 0.0
        for (ingre in ingredientes){
            var cantidad = ingre.cantidad
            var precio = ingre.precio
            presupuesto += cantidad* precio
        }
    }

    private fun llenarDatos(receta: Receta) {
        binding.etNombreReceta.setText(receta.nombreReceta)
        binding.etTiempoReceta.setText(receta.tiempo)
        binding.etPreparacion.setText(receta.preparacion)
    }

    private fun validarDatos(): Boolean {
        var bandera = false
        if(binding.etNombreReceta.text.isNotEmpty() && binding.etTiempoReceta.text.isNotEmpty() && binding.etPreparacion.text.isNotEmpty()){
            if(ingredientes.isNotEmpty()){
                bandera = true
            }else{
                Toast.makeText(this@EditarRecetaActivity, "Debes tener al menos un ingrediente", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "favor de llenar todos los datos", Toast.LENGTH_SHORT).show()
        }
        return bandera
    }

    override fun clicEliminarIngrediente(ingrediente: Ingrediente, position: Int) {
        if (modelo.eliminarIngrediente(ingrediente.id)>0){
            actualizarPresupuesto()
            actualizarPantalla()
            Toast.makeText(this, "Ingrediente eliminado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No se pudo eliminar el Ingrediente", Toast.LENGTH_SHORT).show()
        }

    }

    override fun clicEditarIngrediente(ingrediente: Ingrediente, position: Int, etCantidad: EditText){
        var cantidad= etCantidad.text.toString().toDouble()
        if(etCantidad.text.isNotEmpty() && cantidad > 0){
            ingrediente.cantidad = etCantidad.text.toString().toDouble()
            if(modelo.editarIngrediente(ingrediente)>0){
                actualizarPantalla()
                Toast.makeText(this, "Ingrediente editado", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "no puedes agregar un ingrediente sin un dato o menor a 0", Toast.LENGTH_SHORT).show()
        }
    }

    private fun traerIngredientes() {
        if (ingredientes != null) {
            if (ingredientes.isNotEmpty()) {
                binding.recycleIngredientes.visibility = View.VISIBLE
                binding.recycleIngredientes.adapter = IngredienteAdapter(ingredientes, this)
            } else {
                Toast.makeText(this@EditarRecetaActivity, "No hay ingredientes", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun configuracionRecycle(){
        binding.recycleIngredientes.layoutManager = LinearLayoutManager(this@EditarRecetaActivity)
        binding.recycleIngredientes.setHasFixedSize(true)
    }

    private fun mostrarDialogCantidad(ingrediente: Ingrediente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cantidad de ${ingrediente.nombre}")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val cantidad = input.text.toString().toDoubleOrNull()
            if (cantidad != null && cantidad > 0) {
                ingrediente.cantidad = cantidad
                var agregado = modelo.agregarIngrediente(idReceta, ingrediente)
                if (agregado>0){
                    actualizarPresupuesto()
                    Toast.makeText(this@EditarRecetaActivity, "Ingrediente Agregado", Toast.LENGTH_SHORT).show()
                    actualizarPantalla()
                }else{
                    Toast.makeText(this@EditarRecetaActivity, "Error al agregar Ingrediente", Toast.LENGTH_SHORT).show()
                }

                binding.spIngredientesEditados.setSelection(0)
            } else {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun actualizarPantalla() {
        ingredientes =  modelo.optenerLosIngredientesPorIdRecetas(idReceta)
        traerIngredientes()
    }

}
