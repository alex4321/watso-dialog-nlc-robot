package com.alex4321.bot

class TranslationConversation (private val translator: Translator, private val backend: WatsonConversation) {
    fun intro() : Answer {
        return backend.intro()
    }

    fun answer(text: String) : Answer {
        return backend.answer(translator.translate(text))
    }
}
