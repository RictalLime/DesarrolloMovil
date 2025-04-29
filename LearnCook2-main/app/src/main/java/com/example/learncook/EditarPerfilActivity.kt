package com.example.learncook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityEditarPerfilBinding
import com.example.learncook.fragmentos.PerfilFragment
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario
import com.example.learncook.utilidades.ToastHelper

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var db: LearnCookDB
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = LearnCookDB(this)

        val idUsuario = intent.getIntExtra("idUsuario", -1)
        Log.d("EditarPerfilActivity", "idUsuario recibido: $idUsuario")
        usuario = db.traerUsuario2(idUsuario) ?: run {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.etNombreActual.setText(usuario.nombreUsuario)
        binding.etCorreoActual.setText(usuario.correo)
        binding.etContraseAActual.setText(usuario.contrasena)

        binding.btnGuardarCambios.setOnClickListener {
            ToastHelper.vibrate(this)
            val nombreNuevo = binding.etNuevoNombre.text.toString()
            val correoNuevo = binding.etNuevoCorreo.text.toString()
            val contrasenaNueva = binding.etNuevaContraseA.text.toString()

            if (nombreNuevo.isEmpty() || correoNuevo.isEmpty() || contrasenaNueva.isEmpty()) {
                ToastHelper.showWarning(this, "Por favor, complete todos los campos")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correoNuevo).matches()) {
                ToastHelper.showError(this, "Por favor, ingrese un correo electrónico válido")
                return@setOnClickListener
            }

            if (db.isCorreo(correoNuevo) && correoNuevo != usuario.correo) {
                ToastHelper.showError(this, "El correo electrónico ya está en uso")
                return@setOnClickListener
            }
            if(db.usuarioNombreRegistrado(nombreNuevo) && nombreNuevo != usuario.nombreUsuario){
                ToastHelper.showError(this, "El nombrede usuario ya está en uso")
                return@setOnClickListener
            }

            db.actualizarNombreUsuario(usuario.id, nombreNuevo)
            db.actualizarContrasena(usuario.correo, contrasenaNueva)
            db.actualizarCorreo(usuario.correo, correoNuevo)

            ToastHelper.showSuccess(this, "Cambios guardados")

     
            val intent = Intent(this, PerfilFragment::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)

            finish()
        }
        binding.btnCancelarCambios.setOnClickListener {
            ToastHelper.vibrate(this)
            finish()
        }
    }
}
