package com.example.learncook.poko

data class Ingrediente(val id:Int, val nombre: String, val precio:Double, var cantidad:Double){
    override fun toString(): String {
        return nombre
    }
}
