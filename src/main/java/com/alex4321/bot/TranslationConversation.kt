package com.alex4321.bot

class TranslationConversation (private val translator: Translator, private val backend: WatsonConversation) : IConversation {
    override fun intro() : Answer {
        return backend.intro()
    }

    override fun answer(text: String) : Answer {
        return backend.answer(translator.translate(text))
    }

    override var id: Int
        get() = backend.id
        set(value) {
            backend.id = value
        }

    override var clientID: Int
        get() = backend.clientID
        set(value) {
            backend.clientID = value
        }
}
