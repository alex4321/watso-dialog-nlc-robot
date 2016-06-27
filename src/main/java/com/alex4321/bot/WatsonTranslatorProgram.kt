package com.alex4321.bot

import java.util.*
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants

open class WatsonTranslatorProgram(
        private val translator: Translator,
        code: String
    ): WatsonNlcDialogProgram(code) {

    override fun classes(): Map<String, List<String>> {
        val classes = super.classes()
        val result = HashMap<String, ArrayList<String>>()
        for (className in classes.keys) {
            val items : List<String> = classes[className] as List<String>
            val translated = ArrayList<String>(translator.translate(items))
            result.put(className, translated)
        }
        return result
    }

    private fun translateGrammars(document: Document, parent: String) {
        val xpath = this.xpath()
        val decimeters = "`~!@\";:',.<>[]()\\/*&^%$-+{} \r\t\n"
        val inputGrammars : NodeList = xpath.evaluate(
                ".//$parent/grammar/item", document.documentElement, XPathConstants.NODESET) as NodeList
        for (i in 0..inputGrammars.length-1) {
            val grammar = inputGrammars.item(i)
            val text = grammar.firstChild.nodeValue
            val slitted = split(text)
            val translatedTextBuilder = StringBuilder()
            for (item in slitted) {
                if (item.length == 0) {
                    continue
                }
                if (decimeters.indexOf(item[0]) == -1) {
                    translatedTextBuilder.append(translator.translate(item))
                } else {
                    translatedTextBuilder.append(item)
                }
            }
            val translatedText = translatedTextBuilder.toString()
            grammar.firstChild.nodeValue = translatedText
        }
    }

    override fun preprocessDialog(): Document {
        val document = super.preprocessDialog()
        translateGrammars(document, "input")
        translateGrammars(document, "entity/value")
        translateGrammars(document, "concept")
        return document
    }
}
