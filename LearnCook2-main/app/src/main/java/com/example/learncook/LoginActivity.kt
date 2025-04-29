package com.example.learncook

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityLoginBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario
import com.example.learncook.utilidades.ToastHelper


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        modelo = LearnCookDB(this@LoginActivity)
        binding.btnIngreso.setOnClickListener {
            ToastHelper.vibrate(this)
            val correo = binding.etCorreo.text.toString()
            val contrasena = binding.etContrasena.text.toString()
            if (validarCampos(correo, contrasena)) {
                var usuario = Usuario(-1, correo, contrasena, "")
                if (modelo.usuarioRegistrado(usuario)) {
                    usuario = modelo.traerUsuario(usuario)!!
                    ToastHelper.showSuccess(this, "Bienvenido ${usuario.nombreUsuario}")
                    irPantallaHome(usuario.id)
                } else {
                    ToastHelper.showError(this, "Usuario o contraseña incorrectos")
                }
            }
        }
        binding.twRecuperarContrasena.setOnClickListener {
            ToastHelper.vibrate(this)
            val intent = Intent(this,RecuperarContrasenaActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegistrarse.setOnClickListener {
            ToastHelper.vibrate(this)
            val intent = Intent(this,RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    fun validarCampos(correo: String, contrasena: String): Boolean {
        var bandera = false

        if (correo.isEmpty() && contrasena.isEmpty()) {
            binding.etContrasena.error = "Favor de llenar este campo!"
            binding.etCorreo.error = "Favor de llenar este campo!"
        } else {
            if (correo.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    if (contrasena.isNotEmpty()) {
                        if (contrasena.length >= 8) {
                            bandera = true
                        } else {
                            binding.etContrasena.error = "La contraseña debe tener al menos 8 caracteres!"
                        }
                    } else {
                        binding.etContrasena.error = "Favor de llenar este campo!"
                    }
                } else {
                    binding.etCorreo.error = "Favor de ingresar un correo electrónico válido!"
                }
            } else {
                binding.etCorreo.error = "Favor de llenar este campo!"
            }
        }
        return bandera
    }
    fun irPantallaHome(idUsuario:Int){
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("idUsuario",idUsuario)
        startActivity(intent)
        finish()
    }
}
