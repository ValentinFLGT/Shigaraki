package com.example.vo

data class Product(val id: Int,
                   val name: String,
                   val price: Int,
                   val brand: String?) {

    fun toJson() {

    }
}

