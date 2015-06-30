package hu.bme.mit.inf.alf.uppaal.transformation

import de.uni_paderborn.uppaal.NTA
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl

/**
 * The class is responsible for saving the UPPAAL model into an XML file. This 
 * XML file cannot be loaded by the UPPAAL, it just serves as a serialization 
 * of the UPPAAL model.
 * 
 * The class has only static fields and methods, because it does not store
 * anything.
 * 
 * @author Benedek Horvath
 */
class UppaalModelSaver {

	/**
	 * Save the UPPAAL model to a given file, with extension "uppaal".
	 * 
	 * @param NTA
	 *            The model to be saved.
	 * @param filename
	 *            The path and the name of the output file.
	 */
	def static saveUppaalModel(NTA nta, String filename) {

		// the extension of the output file
		var fileExtension = "uppaal"

		// the name and the path of the output file
		var outputFileName = UppaalModelSaver.removeFileExtension(filename) + "." + fileExtension

		// create the new resource
		var uppaalResource = createAndAddResource(outputFileName, fileExtension, new ResourceSetImpl())

		// save the content of the model into the new resource
		uppaalResource.getContents().add(nta)

		// save the resource in XML format
		saveResource(uppaalResource)
	}

	/**
	 * Create the resource by the resource factory.
	 * 
	 * @param outputFile
	 *            The path and the name of the output file.
	 * @param fileextension
	 *            The extension of the file denoted by the outputFile.
	 * @param rs
	 *            The ResourceSet that is going to contain the resource.
	 */
	def static createAndAddResource(String outputFile, String fileextension, ResourceSet rs) {

		// register the file extension
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(fileextension, new XMLResourceFactoryImpl())

		// create the URI of the resource
		var uri = URI.createFileURI(outputFile)
		var resource = rs.createResource(uri)
		(resource as ResourceImpl).setIntrinsicIDToEObjectMap(new HashMap)
		return resource
	}

	/**
	 * Save the resource in XML format.
	 * 
	 * @param resource
	 *            The resource to be saved.
	 */
	def static saveResource(Resource resource) {

		// save the given resource in XML format
		var saveOptions = (resource as XMLResource).defaultSaveOptions
		saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE)
		saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList)
		try {
			resource.save(saveOptions)
		} catch(IOException e) {
			System.err.println("An error has occurred, while creating the model file. " + e.message)
		}
	}

	/**
	 * Remove the file extension from the denoted filename.
	 * 
	 * @param filename
	 *            A path for a file, that contains the name and the extension of
	 *            the file as well.
	 */
	def static removeFileExtension(String filename) {
		var pathParts = filename.split(File.pathSeparator)
		var fileName = getFileNameFromURIPath(filename)
		var returnName = new StringBuilder
		for (part : pathParts) {
			if(pathParts.indexOf(part) < pathParts.length - 1) {
				returnName.append(part + File.pathSeparator)
			}
		}
		returnName.append(fileName)
		return returnName.toString
	}

	/**
	 * Keep only the name of the file from the denoted path.
	 * 
	 * @param path
	 *            The path of the specified file.
	 */
	def static getFileNameFromURIPath(String path) {
		var pathParts = path.split(File.pathSeparator)
		var lastSegment = pathParts.get(pathParts.length - 1)
		return lastSegment.substring(0, lastSegment.length - 4)
	}

}
