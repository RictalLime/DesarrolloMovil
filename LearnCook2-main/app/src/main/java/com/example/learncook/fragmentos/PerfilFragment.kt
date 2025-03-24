package com.example.learncook.fragmentos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.learncook.EditarPerfilActivity
import com.example.learncook.LoginActivity
import com.example.learncook.SeguirUsuarioActivity
import com.example.learncook.databinding.FragmentPerfilBinding
import com.example.learncook.modelo.LearnCookDB

private const val ARG_ID_USUARIO = "idUsuario"

class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private lateinit var modelo: LearnCookDB
    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getInt(ARG_ID_USUARIO)
        }
        modelo = LearnCookDB(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCerrarSesion.setOnClickListener {

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnEliminarP.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
        binding.btnBuscar.setOnClickListener {
            val intent = Intent(requireContext(), SeguirUsuarioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.btnEditarP.setOnClickListener{
            val intent = Intent(requireContext(), EditarPerfilActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }
        traerDatos()
    }

    private fun mostrarDialogoConfirmacion() {
        // Crear un AlertDialog para confirmar la eliminación del perfil
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar perfil")
            .setMessage("¿Estás seguro de que deseas eliminar tu perfil? Se perderá toda tu información.")
            .setPositiveButton("Eliminar") { dialog, which ->
                eliminarCuenta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCuenta() {
        // Aquí llamamos al método para eliminar el usuario de la base de datos
        val eliminadoExitosamente = modelo.eliminarUsuario(idUsuario)

        if (eliminadoExitosamente) {
            // Mostrar mensaje de éxito y guiar al usuario de vuelta al inicio de sesión o pantalla principal
            mostrarMensaje("Cuenta eliminada correctamente.")
            // Crear un Intent para ir a LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish() // Cerrar la actividad actual

        } else {
            // Mostrar mensaje de error si no se pudo eliminar la cuenta
            mostrarMensaje("Error al eliminar la cuenta. Por favor, inténtalo nuevamente.")
        }
    }

    override fun onResume() {
        super.onResume()
        traerDatos()
    }




    private fun mostrarMensaje(mensaje: String) {
        // Método para mostrar un Toast o AlertDialog con el mensaje proporcionado
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun traerDatos(){
        var usuario = modelo.traerNombreDeUsuario(idUsuario)
        binding.nombreUser.text = usuario
    }
    companion object {
        @JvmStatic
        fun newInstance(idUsuario: Int) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID_USUARIO, idUsuario)
                }
            }
    }
}