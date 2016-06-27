package com.alex4321.bot

import java.io.FileInputStream
import java.io.File

class WatsonProgramPath(val path: String)
{
    fun read(): String {
        val f = File(path)
        val input = FileInputStream(f)
        input.use {
            val text = input.bufferedReader().readText()
            return text
        }
    }
}
