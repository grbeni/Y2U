package hu.bme.mit.inf.alf.uppaal.transformation.serialization

import hu.bme.mit.inf.alf.uppaal.transformation.UppaalModelBuilder
import java.io.FileWriter
import java.io.IOException

/**
 * The class is responsible for serializing the UPPAAL model specified by the
 * UppaalModelBuilder, to an XML file, that can be loaded by the UPPAAL.
 * 
 * The class strongly depends on the UppaalModelBuilder, and its saveToXML
 * method should only be called, after the UPPAAL model transformation was done
 * by the UppaalModelBuilder and the ModelTraverser classes.
 * 
 * The serialization is done by specifying the format of the output in character
 * sequences, and each model element is inserted to its place. 
 * 
 * The only limitation of the current implementation is that, it can only 
 * serialize the first update statement for each edge(*), and it cannot 
 * serialize the global declarations, since only the local declarations for the
 * specified Template has been stored.
 * 
 * (*) It was done because of the transformation: only one update for each
 * edge is stored, during the transformation. If there are multiple updates,
 * then multiple, sequential locations are created one after another, and each
 * one is connected with the next one by an edge, that has the current update
 * statement. This way multiple updates are cut in sequential pieces, that is
 * equivalent to the sequence of the statements within the Alf instance model.
 * 
 * The class has only static fields and methods, because it does not store
 * anything.
 * 
 * @author Benedek Horvath
 */
 
class UppaalModelSerializer {
	var static id1 = 0
	var static id2 = 0
	/**
	 * Save the UPPAAL model specified by the UppaalModelBuilder to an XML file,
	 * denoted by its file path. The created XML file can be loaded by the
	 * UPPAAL.
	 * 
	 * @param filepath
	 *            The path for the output file. It contains the file name also,
	 *            except for the file extension.
	 */
	def static saveToXML(String filepath) {
		try {
			var fw = new FileWriter(filepath + ".xml")
			val header = createHeader
			val body = createTemplate
			val footer = createFooter
			fw.write(header + body.toString + footer)
			fw.close
			// information message, about the completion of the transformation.
			println("Transformation was finished.")
		} catch (IOException ex) {
			System.err.println("An error has occurred, while creating the XML file. " + ex.message)
		}
	}
	
	/**
	 * Create the header and the beginning of the XML file, that contains 
	 * the declaration of the top-level UPPAAL module (NTA) and the global 
	 * declarations as well.
	 * 
	 * @return The header of the XML file in a char sequence.
	 */
	def static createHeader() '''
		<?xml version="1.0" encoding="utf-8"?>
		<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>
		<nta>
		<declaration>«FOR declaration : UppaalModelBuilder.getInstance.NTA.globalDeclarations.declaration SEPARATOR "\n"»
		«declaration.exp»
		«ENDFOR»</declaration>
	'''
	
	/**
	 * Create the main part of the XML file: the Template, and locations and the 
	 * edges within the Template. All the data for the serialization are fetched 
	 * from the UppaalModelBuilder.
	 * 
	 * @return The main part of the XML file in a char sequence.
	 */
	 /*
	def static createTemplate() '''
		<template>
		<name>«UppaalModelBuilder.instance.template.name»</name>
		<declaration>«FOR declaration : UppaalModelBuilder.instance.template.declarations.declaration SEPARATOR "\n"»
		«declaration.exp»
		«ENDFOR»</declaration>
			
		«FOR location : UppaalModelBuilder.instance.locations SEPARATOR "\n"»
		<location id="«location.name»">
		<name>«location.name»</name>
		«IF !(location.comment == null)»<label kind="comments">«location.comment»</label>«ENDIF»
		</location>
			«ENDFOR»
			
		<init ref="«UppaalModelBuilder.instance.template.init.name»"/>
			
		«FOR transition : UppaalModelBuilder.instance.edges SEPARATOR "\n"»
		<transition>
		<source ref="«transition.source.name»"/>
		<target ref="«transition.target.name»"/>
		«IF !(transition.guard == null)»<label kind="guard">«transition.guard.exp»</label>«ENDIF»
		«IF (transition.update.length == 1)»<label kind="assignment">«transition.update.get(0).exp»</label>«ENDIF»
		</transition>
		«ENDFOR»
		</template>
	'''
	*/
	def static createTemplate() '''
		«FOR template : UppaalModelBuilder.instance.templates SEPARATOR "\n"»
		<template>
		<name>«template.name»</name>
		<declaration>«FOR declaration : template.declarations.declaration SEPARATOR "\n"»
		«declaration.exp»
		«ENDFOR»</declaration>
			
		«FOR location : template.location SEPARATOR "\n"»
		<location id="«location.name»">
		<name>«location.name»</name>
		«IF !(location.comment == null)»<label kind="comments">«location.comment»</label>«ENDIF»
		</location>
		«ENDFOR»
		<init ref="«template.init.name»"/>
			
		«FOR transition : template.edge SEPARATOR "\n"»
		<transition>
		<source ref="«transition.source.name»"/>
		<target ref="«transition.target.name»"/>
		«IF !(transition.guard == null)»<label kind="guard">«transition.guard.exp»</label>«ENDIF»
		«IF (transition.update.length == 1)»<label kind="assignment">«transition.update.get(0).exp»</label>«ENDIF»
		</transition>
		«ENDFOR»
		</template>
		«ENDFOR»
	'''
	 
	
	/**
	 * Create the footer of the XML file, which contains the instantiation of 
	 * the recently created Template. The instance of the Template is called
	 * "Process" in this implementation.
	 * 
	 * @return The footer of the XML file in a char sequence.
	 */
	 /*
	def static createFooter() '''
		<system>
		Process = «UppaalModelBuilder.instance.template.name»();
		system Process;
		</system>
		</nta>
	'''
	*/
	def static createFooter() '''		
		<system>	
		
		«FOR template : UppaalModelBuilder.instance.templates SEPARATOR "\n"»
		Process«id1 = id1 + 1» =  «template.name»();	
		«ENDFOR»
		
		system 
		«FOR template : UppaalModelBuilder.instance.templates SEPARATOR ", "»
		Process«id2 = id2 + 1»
		«ENDFOR»;		
		</system>
		</nta>
	'''

}
