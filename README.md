Watson NLC-Dialog robot
=======================
This library allow to make bots with using of combination of two IBM services : 
"Dialog" and "Natural Language Classifier". 

Classes
=======
Firstly about class data format - it use superset of Dialog markup. 
It adds "classes" element and you can use it in input grammars. E.g.

```
<dialog>
    ...
        <input>
            <grammar>
                <item> [temperature] (City)=${City} </item>
            </grammar>
        </input>
    ...
    <entities>
        <entity name="City">
            <value name="Tokyo" value="Tokyo">
                <grammar>
                    <item>Tokyo</item>
                </grammar>
            </value>
            <value name="Moscow" value="Moscow">
                <grammar>
                    <item>Moscow</item>
                </grammar>
            </value>
        </entity>
    </entities>
    <classes>
        <class name="temperature">
            <item>Isn't it too hot today?</item>
            <item>Isn't it cold hot today?</item>
            ...
        </class>
        <class name="conditions">
            <item>Is is windy now?</item>
        </class>
    </classes>    
</dialog>
```
And user input is "Isn't it too cold in Moscow now?". 
With right classifier source data - NLC will classify it as "temperature" with big confidence. 
So next string will be given to dialog input :
```
temperature Isn't it too cold in Moscow?
```
If classifier confidence is low - input will be 
```
 Isn't it too cold in Moscow?
```

Answer commands
===============
You can add commands that'll be used for Dialog answer postprocessing. 
E.g. 
```
<prompt selectionType="SEQUENTIAL">
    <item>I processed your request. Temperature in [Text:$City] - [Temperature:$City:SomeStringArgument]</item>
</prompt>
```
In this case - ater getting output from Dialog - it'll process two commands

- Text:$City - command with name "Text" and arguments ("$City"), where "$City" - value of profile variable "City"
- Temperature:$City:SomeStringArgument - command with name "Temperature" and arguments ("$City", "SomeStringArgument"),
  where "$City" - value of "City" profile variable, "SomeStringArgument" - just a string
  
For more - view code of "CommandHandler".

Translation
===========
Seems like in simple cases - input at unsupported languages can be translated, so you can use "WatsonTranslatorProgram" 
 and "TranslationCOnversation". 
 First translate NLC class items, input grammars, entity values and concepts.
 Second translate user input. 

Example
=======

Written at Kotlin:

```
import com.alex4321.bot.*

fun main(args: Array<String>) {
    val program = WatsonNlcDialogProgram(WatsonProgramPath("test/test.xml"))
    val robot = WatsonRobot(
        program,
        "", //Dialog ID empty at start
        "", //NLC ID empty at start
        WatsonRobotAuth("DialogUsername", "DialogPassword", "NLCUsername", "NLCPassword")
    )
    robot.train()
    println(robot.dialogID)
    println(robot.nlcID)
    while (! robot.available) {
        Thread.sleep(5000)
    }
    val conversation = robot.conversation()
    print(conversation().intro().response)
}
