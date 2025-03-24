package com.example.learncook

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.CalificacionAdapter
import com.example.learncook.databinding.ActivityCalificarRecetaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Calificacion

class CalificarRecetaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalificarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private var puntuacion = -1
    private var idUsuario = -1
    private var idReceta =-1
    private var nombreReceta= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalificarRecetaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        modelo = LearnCookDB(this@CalificarRecetaActivity)
        idUsuario = intent.getIntExtra("idUsuario",-1)
        idReceta = intent.getIntExtra("idReceta",-1)
        nombreReceta = intent.getStringExtra("nombreReceta").toString()

        llenarcampos()
        mostarcalificaciones()
        configuracionRecycle()
        binding.btnCalificar.setOnClickListener {
            if (validardatos()){
                insertarCalificacion()
            }
        }
        binding.ibEstrella1.setOnClickListener {
            pintarEstrella1()
            puntuacion =1
        }
        binding.ibEstrella2.setOnClickListener {
            pintarEstrella2()
            puntuacion =2
        }
        binding.ibEstrella3.setOnClickListener {
            pintarEstrella3()
            puntuacion =3
        }
        binding.ibEstrella4.setOnClickListener {
            pintarEstrella4()
            puntuacion =4
        }
        binding.ibEstrella5.setOnClickListener {
            pintarEstrella5()
            puntuacion =5
        }

    }

    private fun pintarEstrella1() {
        binding.ibEstrella1.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella2.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella3.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella4.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella5.setImageResource(R.mipmap.ic_estrella_negra)
    }
    private fun pintarEstrella2() {
        binding.ibEstrella1.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella2.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella3.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella4.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella5.setImageResource(R.mipmap.ic_estrella_negra)
    }
    private fun pintarEstrella3() {
        binding.ibEstrella1.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella2.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella3.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella4.setImageResource(R.mipmap.ic_estrella_negra)
        binding.ibEstrella5.setImageResource(R.mipmap.ic_estrella_negra)
    }
    private fun pintarEstrella4() {
        binding.ibEstrella1.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella2.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella3.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella4.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella5.setImageResource(R.mipmap.ic_estrella_negra)
    }
    private fun pintarEstrella5() {
        binding.ibEstrella1.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella2.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella3.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella4.setImageResource(R.mipmap.ic_estrella_amarilla)
        binding.ibEstrella5.setImageResource(R.mipmap.ic_estrella_amarilla)
    }

    private fun insertarCalificacion() {
        var cometario = binding.tiComentario.text.toString()
        var calificacion = Calificacion(0,idUsuario,idReceta,puntuacion,cometario)
        if(modelo.agregarCalificacion(calificacion)>0){
            Toast.makeText(this@CalificarRecetaActivity, "Calificacion agregada correctamente", Toast.LENGTH_SHORT).show()
            mostarcalificaciones()
        }else{
            Toast.makeText(this@CalificarRecetaActivity, "Tu ya calificaste esta receta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validardatos(): Boolean {
        var bandera = false
        if(puntuacion<0){
            Toast.makeText(this@CalificarRecetaActivity, "Por favor elije una puntuación", Toast.LENGTH_SHORT).show()
        }else{
            if(binding.tiComentario.text.toString().isEmpty()){
                binding.tiComentario.setError("Llena este campo.")
            }else{
                bandera = true
            }
        }
        return bandera
    }

    private fun llenarcampos() {
        binding.tvNombreReceta.text = nombreReceta
        binding.tvNombreUsuario.text = modelo.traerNombreDeUsuario(idUsuario)
    }

    private fun mostarcalificaciones() {
        //cambiar por el id que se vaya a ocupar
        val calficaciones = modelo.traerCalificacionesDeReceta(idReceta);

        if(calficaciones.size>0){
            binding.tvTextoNoHayComentarios.visibility = View.GONE
            binding.recycleComentarios.visibility = View.VISIBLE
            binding.recycleComentarios.adapter = CalificacionAdapter(calficaciones)
        }else{
            binding.tvTextoNoHayComentarios.visibility = View.VISIBLE
            binding.recycleComentarios.visibility = View.GONE
        }
    }
    private fun configuracionRecycle(){
        binding.recycleComentarios.layoutManager = LinearLayoutManager(this@CalificarRecetaActivity)
        binding.recycleComentarios.setHasFixedSize(true)
    }
}