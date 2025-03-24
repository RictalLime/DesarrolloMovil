package com.example.learncook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivitySeguirUsuarioBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario

class SeguirUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeguirUsuarioBinding
    private lateinit var modelo: LearnCookDB
    private var usuarioActual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeguirUsuarioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        modelo = LearnCookDB(this@SeguirUsuarioActivity)

        binding.btnBuscar.setOnClickListener {
            val nombreUsuario = binding.nombreUsuario.text.toString()
            if (nombreUsuario.isNotEmpty()) {
                val usuario = modelo.traerUsuarioPorNombre(nombreUsuario)
                if (usuario != null) {
                    binding.usernameTextView.text = usuario.nombreUsuario
                    binding.seguirButton.setOnClickListener {
                        seguirUsuario(usuario)
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor ingrese un nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seguirUsuario(usuario: Usuario) {
        val idUsuarioActual = binding.nombreUsuario.text.toString()

        val seguidoExitosamente = modelo.seguirUsuario(idUsuarioActual, usuario.id)

        if (seguidoExitosamente) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Usuario Seguido")
                .setMessage("Ahora estás siguiendo a ${usuario.nombreUsuario}")
                .setPositiveButton("Aceptar") { dialog, which ->
                    // Regresar al home o a la actividad anterior
                    onBackPressed() // Simplemente regresamos al home
                }
                .create()
            alertDialog.show()
        } else {
            // Manejar caso de error al seguir al usuario
            Toast.makeText(this, "Error al seguir al usuario", Toast.LENGTH_SHORT).show()
        }
    }

}