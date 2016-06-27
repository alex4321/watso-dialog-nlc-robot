package com.alex4321.bot

import com.memetix.mst.translate.Translate
import com.memetix.mst.language.Language
import java.util.*

class Translator(private val clientID: String, private val clientSecret: String,
                 private val sourceLanguage: String, private val targetLanguage: String) {
    fun translate(source: String): String {
        synchronized(Translate::class) {
            Translate.setClientId(clientID)
            Translate.setClientSecret(clientSecret)
            val result: String = Translate.execute(source,
                    Language.fromString(sourceLanguage), Language.fromString(targetLanguage))
            return result
        }
    }

    fun translate(source: List<String>): List<String> {
        synchronized(Translate::class) {
            Translate.setClientId(clientID)
            Translate.setClientSecret(clientSecret)
            val array = source.toTypedArray()
            val result: Array<String> = Translate.execute(array,
                    Language.fromString(sourceLanguage), Language.fromString(targetLanguage))
            return ArrayList<String> (result.asList())
        }
    }
}
