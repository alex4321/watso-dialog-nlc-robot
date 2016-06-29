package com.alex4321.bot

interface IConversation {
    fun intro() : Answer
    fun answer(text: String) : Answer
    var id: Int
    var clientID: Int
}
