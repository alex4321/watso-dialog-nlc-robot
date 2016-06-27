package com.alex4321.bot

import com.ibm.watson.developer_cloud.dialog.v1.DialogService
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier.Status
import java.io.File

class WatsonRobot (val program: WatsonProgram,
                   var dialogID: String,
                   var nlcID: String,
                   val auth: WatsonRobotAuth) {
    val available: Boolean
        get() = isAvailable()

    private fun dialog() : DialogService {
        val result = DialogService()
        result.setUsernameAndPassword(auth.dialogUsername, auth.dialogPassword)
        return result
    }

    private fun nlc() : NaturalLanguageClassifier {
        val result = NaturalLanguageClassifier()
        result.setUsernameAndPassword(auth.nlcUsername, auth.nlcPassword)
        return result
    }

    fun train(name: String) {
        val dialogTempFile = File.createTempFile("dialog", ".xml")
        dialogTempFile.writeText(program.dialogConfig)
        dialogID = dialog().createDialog(name, dialogTempFile).execute().id
        dialogTempFile.delete()
        val nlcTempFile = File.createTempFile("classifier", ".csv")
        nlcTempFile.writeText(program.classifierConfig)
        nlcID = nlc().createClassifier(name, "en", nlcTempFile).execute().id
        nlcTempFile.delete()
    }

    fun delete() {
        dialog().deleteDialog(dialogID)
        nlc().deleteClassifier(nlcID)
    }

    private fun isAvailable(): Boolean {
        return nlc().getClassifier(nlcID).execute().status == Status.AVAILABLE
    }

    fun converse(): WatsonConversation {
        return WatsonConversation(this)
    }
}
