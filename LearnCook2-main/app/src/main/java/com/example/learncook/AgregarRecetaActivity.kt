package com.example.learncook

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityAgregarRecetaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.Receta
import com.example.learncook.utilidades.ToastHelper

class AgregarRecetaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listaDeIngredientes: MutableList<Ingrediente>
    private var idUsuario = 0
    private var presupuesto = 0.0
    private val listaId = mutableListOf<Int>()
    private val listaCantidad = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario", -1)
        listaDeIngredientes = modelo.traerIngredientes().toMutableList()

        adapter = ArrayAdapter(
            this,
            R.layout.spiner_item,
            listaDeIngredientes.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spIngredientesReceta.adapter = adapter

        binding.spIngredientesReceta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                if (position >= 1 && position < listaDeIngredientes.size) {
                    val ingredienteSeleccionado = listaDeIngredientes[position]

                    mostrarDialogCantidad(ingredienteSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spIngredientesReceta.setSelection(0)
            }
        }

        binding.btnAgregarReceta.setOnClickListener {
            ToastHelper.vibrate(this)
            if (validarDatos()) {
                val receta = Receta(
                    0,
                    idUsuario,
                    binding.etNombreReceta.text.toString(),
                    binding.etTiempoReceta.text.toString(),
                    presupuesto,
                    binding.etPreparacion.text.toString()
                )
                val agregado = modelo.agregarReceta(receta)
                if (agregado > 0) {
                    val ultimoId = modelo.traerUltimoIdDeReceta()
                    val agregadoIngredientes = modelo.agregarIngredientes(ultimoId, listaId, listaCantidad)
                    if (agregadoIngredientes == 1) {
                        ToastHelper.showSuccess(this, "Receta creada exitosamente")
                        finish()
                    } else {
                        ToastHelper.showError(this, "Error al agregar los ingredientes")
                    }
                }
            }
        }
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
                listaCantidad.add(cantidad)

                val textoActual = binding.etIngredientes.text.toString()
                binding.etIngredientes.setText(textoActual + "Ingrediente: ${ingrediente.nombre} - Cantidad: $cantidad\n")
                presupuesto += ingrediente.precio * cantidad
                listaId.add(ingrediente.id)

                binding.spIngredientesReceta.setSelection(0)
                ToastHelper.vibrate(this)
            } else {
                ToastHelper.showWarning(this, "Cantidad inválida", withVibration = true)
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun validarDatos(): Boolean {
        var bandera = true
        if (binding.etNombreReceta.text.isEmpty() ||
            binding.etTiempoReceta.text.isEmpty() ||
            binding.etPreparacion.text.isEmpty() ||
            binding.etIngredientes.text.isEmpty()
        ) {
            bandera = false
            ToastHelper.showWarning(this, "Por favor llena todos los campos", withVibration = true)
        }
        return bandera
    }
}
