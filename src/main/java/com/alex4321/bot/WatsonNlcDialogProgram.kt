package com.alex4321.bot

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Document
import java.io.StringWriter
import java.util.ArrayList
import java.util.HashMap
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants

open class WatsonNlcDialogProgram(code: String): WatsonProgram(code) {
    protected fun concepts(): List<List<String>> {
        val xpath = this.xpath()
        val concepts: NodeList = xpath.evaluate(".//concept", document.documentElement, XPathConstants.NODESET)
            as NodeList
        val result = ArrayList<ArrayList<String>>()
        for (i in 0..concepts.length-1) {
            val conceptVec = ArrayList<String>()
            val conceptItems: NodeList = xpath.evaluate("grammar/item", concepts.item(i), XPathConstants.NODESET)
                as NodeList
            for (j in 0..conceptItems.length-1) {
                val grammarText = conceptItems.item(j).firstChild.nodeValue
                conceptVec.add(grammarText)
            }
            result.add(conceptVec)
        }
        return result
    }

    protected fun split(text: String): List<String> {
        val decimeters = "`~!@\";:',.<>[]()\\/*&^%$-+{} \r\t\n"
        val preSplitted = ArrayList<String>()
        preSplitted.add("")
        for (char in text) {
            if (decimeters.indexOf(char) > -1) {
                preSplitted.add(char.toString())
                preSplitted.add("")
            } else {
                val old = preSplitted[preSplitted.size - 1]
                preSplitted[preSplitted.size - 1] = old + char.toString()
            }
        }
        val rejoined = ArrayList<String>()
        var i = 0;
        while (i < preSplitted.size) {
            val fullText: String
            val item = preSplitted[i]
            if (item == "(" && (i+6) < preSplitted.size &&
                    preSplitted[i+2] == ")" && preSplitted[i+3] == "=" &&
                    preSplitted[i+4] == "{" && preSplitted[i+6] == "}") {
                fullText = "(" + preSplitted[i+1] + ")={" + preSplitted[i+5] + "}"
                i += 6
            } else if (item == "[") {
                val fullTextBuilder = StringBuilder()
                fullTextBuilder.append("[")
                for (j in i+1..preSplitted.size-1) {
                    fullTextBuilder.append(preSplitted[j])
                    i++
                    if (preSplitted[j] == "]") {
                        break
                    }
                }
                fullText = fullTextBuilder.toString()
            } else {
                fullText = preSplitted[i]
            }
            rejoined.add(fullText)
            i++
        }
        return rejoined
    }

    private fun conceptItemIndex(source: String, concept: String): Int {
        assert(concept.length > 0)
        val specials = "\t\r\n `~!@\":;'<>,./\\?#№$%^&*()-_=+"
        for (i in 0..source.length - concept.length - 1) {
            val sourceSubstring = source.substring(i)
            if (sourceSubstring.indexOf(concept) == 0) {
                val previousChar = if (i == 0) {
                    ' '
                } else {
                    source[i - 1]
                }
                val nextChar = if (i + concept.length >= source.length) {
                    ' '
                } else {
                    source[i + concept.length]
                }
                if (specials.indexOf(previousChar) > -1 && specials.indexOf(nextChar) > -1) {
                    return i
                }
            }
        }
        return -1
    }

    private fun processConcepts(source: String): List<String> {
        val concepts = this.concepts()
        val result = ArrayList<String>()
        result.add(source)
        for (concept in concepts) {
            for (conceptItem in concept) {
                val index = conceptItemIndex(source, conceptItem)
                if (index > -1) {
                    for (otherConceptItem in concept) {
                        if (conceptItem == otherConceptItem) {
                            continue
                        }
                        val replaced = source.toLowerCase().replace(
                                conceptItem.toLowerCase(),
                                otherConceptItem.toLowerCase())
                        result.add(replaced)
                    }
                }
            }
        }
        return result
    }

    protected open fun classes(): Map<String, List<String>> {
        val xpath = this.xpath()
        val classes: NodeList = xpath.evaluate("classes/class", document.documentElement, XPathConstants.NODESET)
                as NodeList
        val result = HashMap<String, ArrayList<String>>()
        for (i in 0..classes.length - 1) {
            val classItem = classes.item(i)
            val className = classItem.attributes.getNamedItem("name").nodeValue
            val items: NodeList = xpath.evaluate("item", classItem, XPathConstants.NODESET) as NodeList
            val itemsVec = ArrayList<String>()
            for (j in 0..items.length - 1) {
                val text = items.item(j).firstChild.nodeValue
                itemsVec.addAll(processConcepts(text))
            }
            result.put(className, itemsVec)
        }
        return result
    }

    private fun documentCopy(): Document {
        val documentCopy: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val rootCopy = documentCopy.importNode(document.documentElement, true);
        documentCopy.appendChild(rootCopy)
        return documentCopy
    }

    private fun getStringFromDocument(doc: Document): String
    {
        val domSource = DOMSource(doc);
        val writer = StringWriter();
        val result = StreamResult(writer);
        val tf = TransformerFactory.newInstance();
        val transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    protected open fun preprocessDialog(): Document {
        val document = documentCopy()
        val xpath = this.xpath()
        val classesNode: Node = xpath.evaluate("classes", document.documentElement, XPathConstants.NODE) as Node
        classesNode.parentNode.removeChild(classesNode)
        return document
    }

    override fun toDialogConfig(): String {
        return getStringFromDocument(preprocessDialog());
    }

    override fun toClassifierConfig(): String {
        val classes = this.classes()
        val builder = StringBuilder()
        for (key in classes.keys) {
            val items: List<String> = classes[key] as List<String>;
            for (item: String in items) {
                builder.append(item)
                builder.append(",")
                builder.append(key)
                builder.append("\n")
            }
        }
        return builder.toString()
    }
}
