package com.example.learncook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityRegistroBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario
import com.example.learncook.utilidades.ToastHelper

class RegistroActivity : AppCompatActivity() {
    private lateinit var bingind: ActivityRegistroBinding
    private lateinit var modelo : LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bingind = ActivityRegistroBinding.inflate(layoutInflater)
        val view = bingind.root
        setContentView(view)
        modelo = LearnCookDB(this@RegistroActivity)

        bingind.btnRegistrarse.setOnClickListener {
            ToastHelper.vibrate(this)
            var correo = bingind.etCorreo.text.toString()
            var contrasena = bingind.etContrasena.text.toString()
            var nombreUsuario = bingind.etNombreUsuario.text.toString()
            if(validardatos(correo,contrasena,nombreUsuario)){
                var usuario = Usuario(0,correo,contrasena,nombreUsuario)
                var registrado = modelo.agregarUsuario(usuario)
                if (registrado>0){
                    ToastHelper.showSuccess(this, "Registro exitoso")
                    finish()
                }else{
                    ToastHelper.showError(this, "Error en el registro")
                }
            }
        }

    }

    private fun validardatos(correo: String, contrasena: String, nombreUsuario: String): Boolean {
        var bandera = true
        if (correo.isEmpty() || contrasena.isEmpty() || nombreUsuario.isEmpty()) {
            ToastHelper.showWarning(this, "Complete todos los campos")
            bandera = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this@RegistroActivity, "Correo no válido", Toast.LENGTH_SHORT).show()
            bandera = false
        } else if (contrasena.length <= 8) {
            Toast.makeText(this@RegistroActivity, "La contraseña debe tener más de 8 dígitos", Toast.LENGTH_SHORT).show()
            bandera = false
        }else if(modelo.usuarioEnBase(correo)){
            Toast.makeText(this@RegistroActivity, "Este correo ya esta registrado", Toast.LENGTH_SHORT).show()
            bandera = false
        }else if (modelo.usuarioNombreRegistrado(nombreUsuario)){
            Toast.makeText(this@RegistroActivity, "este nombre de usuario ya esta registrado", Toast.LENGTH_SHORT).show()
            bandera = false
        }

        return bandera
    }
}