package com.skot.kittytv

import android.graphics.Bitmap

enum class Entity (val movementDistance: Int, val imageName: String) {

    TROOPER(1, "troops_01.png"),
    TANK(2, "tank_01.png"),
    MECH(3, "mech_02.png");

//    val image: Bitmap
//
//    init {
//        image = loadImage(imageName)
//    }
//
//    private fun loadImage(fileName: String): Bitmap {
//        val resource = javaClass.classLoader.getResource(fileName)
//        return ImageIO.read(resource)
//    }

}