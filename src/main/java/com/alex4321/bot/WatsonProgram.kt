package com.alex4321.bot

import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import org.w3c.dom.Document
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathFactory

open class WatsonProgram (val code: String) {
    val dialogConfig: String
        get() = toDialogConfig()
    val classifierConfig: String
        get() = toClassifierConfig()
    protected val document: Document;

    open protected fun toDialogConfig(): String {
        throw NotImplementedError()
    }

    open protected fun toClassifierConfig(): String {
        throw NotImplementedError()
    }

    protected fun xpath(): XPath {
        return XPathFactory.newInstance().newXPath()
    }

    init {
        val dbf = DocumentBuilderFactory.newInstance()
        val builder = dbf.newDocumentBuilder()
        val stream = ByteArrayInputStream(code.toByteArray())
        document = builder.parse(stream)
    }
}
