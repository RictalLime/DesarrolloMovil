package com.example.learncook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityRecuperarContrasenaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.utilidades.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarContrasenaBinding
    private val emailUtil = Email()
    private lateinit var progressBar: ProgressBar
    private var codigo: Int = 0
    private var correo: String =""
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarContrasenaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        modelo = LearnCookDB(this@RecuperarContrasenaActivity)
        progressBar = binding.progressBar

        binding.btnEnviarCorreo.setOnClickListener {
            correo = binding.etCorreo.text.toString()
            if (validarDatosCorreo(correo)) {
                if(modelo.isCorreo(correo)){
                    enviarCorreo(correo)
                }else{
                    Toast.makeText(this@RecuperarContrasenaActivity, "Este correo no se encuentra registrado", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnEnviarCodigo.setOnClickListener {
            if (validarDatosCodigo()) {
                val codigoDeEntrada = binding.etCodigo.text.toString().toIntOrNull()
                if (codigoDeEntrada != null && esIgualElCodigo(codigo, codigoDeEntrada)) {
                    binding.etCodigo.visibility = View.GONE
                    binding.btnEnviarCodigo.visibility = View.GONE
                    binding.etContrasena.visibility = View.VISIBLE
                    binding.btnRestaurarContrasena.visibility = View.VISIBLE
                    Toast.makeText(this@RecuperarContrasenaActivity, "Escribe una nueva contraseña", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@RecuperarContrasenaActivity, "Error: el código no es correcto", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnRestaurarContrasena.setOnClickListener {
            var contrasena = binding.etContrasena.text.toString()
            if(validarDatosContrasena(contrasena)){
                var actualizado = modelo.actualizarContrasena(correo,contrasena)
                if (actualizado>0){
                    Toast.makeText(this@RecuperarContrasenaActivity, "Contraseña actualizada", Toast.LENGTH_LONG).show()
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@RecuperarContrasenaActivity, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validarDatosContrasena(contrasena:String): Boolean {
        var bandera = false
        if(contrasena.isNotEmpty()){
            if (contrasena.length >= 8){
                bandera = true
            }else{
                binding.etContrasena.error = "La contraseña debe ser mas de 8 caracteres"
            }
        }else{
            binding.etContrasena.error = "Favor de llenar este campo"
        }
        return bandera
    }

    private fun validarDatosCorreo(correo: String): Boolean {
        return if (correo.isNotBlank() && correo.contains("@")) {
            true
        } else {
            binding.etCorreo.error = "Favor de ingresar un correo válido"
            false
        }
    }

    private fun enviarCorreo(correo: String) {
        val subject = "Código de Recuperación"
        codigo = generarCodigo()
        val body = "Tu código de recuperación es: $codigo"

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val exitoEnvio = withContext(Dispatchers.IO) {
                emailUtil.sendEmail(correo, subject, body)
            }

            progressBar.visibility = View.GONE

            if (exitoEnvio) {
                binding.etCorreo.visibility = View.GONE
                binding.btnEnviarCorreo.visibility = View.GONE
                binding.etCodigo.visibility = View.VISIBLE
                binding.btnEnviarCodigo.visibility = View.VISIBLE
                Toast.makeText(this@RecuperarContrasenaActivity, "Se envió un código a tu correo electrónico", Toast.LENGTH_LONG).show()
            } else {
                Log.e("Envio", "Error al enviar el correo electrónico")
                Toast.makeText(this@RecuperarContrasenaActivity, "Error al enviar el correo", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generarCodigo(): Int {
        return Random.nextInt(1000, 10000)
    }

    private fun validarDatosCodigo(): Boolean {
        val codigo = binding.etCodigo.text.toString()
        return if (codigo.length == 4 && codigo.all { it.isDigit() }) {
            true
        } else {
            binding.etCodigo.error = "El código debe tener 4 dígitos"
            false
        }
    }

    private fun esIgualElCodigo(codigo: Int, codigoDeEntrada: Int): Boolean {
        return codigo == codigoDeEntrada
    }
}
