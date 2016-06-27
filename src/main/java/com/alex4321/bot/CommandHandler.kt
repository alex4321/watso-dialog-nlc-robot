package com.alex4321.bot

import java.util.*
import java.util.function.BiFunction

class CommandHandler {
    companion object {
        private val handlers: HashMap<String, BiFunction<List<String>, String, String>> = HashMap()

        fun register(command: String, handler: BiFunction<List<String>, String, String>) {
            handlers[command] = handler
        }

        fun run(command: String, args: List<String>, text: String) : String {
            if (handlers.containsKey(command)) {
                val handler = handlers[command] as BiFunction<List<String>, String, String>
                return handler.apply(args, text)
            } else {
                return command
            }
        }
    }
}
