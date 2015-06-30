package hu.bme.mit.inf.alf.uppaal.transformation;

import de.uni_paderborn.uppaal.NTA;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;

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
@SuppressWarnings("all")
public class UppaalModelSaver {
  /**
   * Save the UPPAAL model to a given file, with extension "uppaal".
   * 
   * @param NTA
   *            The model to be saved.
   * @param filename
   *            The path and the name of the output file.
   */
  public static void saveUppaalModel(final NTA nta, final String filename) {
    String fileExtension = "uppaal";
    String _removeFileExtension = UppaalModelSaver.removeFileExtension(filename);
    String _plus = (_removeFileExtension + ".");
    String outputFileName = (_plus + fileExtension);
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    Resource uppaalResource = UppaalModelSaver.createAndAddResource(outputFileName, fileExtension, _resourceSetImpl);
    EList<EObject> _contents = uppaalResource.getContents();
    _contents.add(nta);
    UppaalModelSaver.saveResource(uppaalResource);
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
  public static Resource createAndAddResource(final String outputFile, final String fileextension, final ResourceSet rs) {
    Resource.Factory.Registry _resourceFactoryRegistry = rs.getResourceFactoryRegistry();
    Map<String, Object> _extensionToFactoryMap = _resourceFactoryRegistry.getExtensionToFactoryMap();
    XMLResourceFactoryImpl _xMLResourceFactoryImpl = new XMLResourceFactoryImpl();
    _extensionToFactoryMap.put(fileextension, _xMLResourceFactoryImpl);
    URI uri = URI.createFileURI(outputFile);
    Resource resource = rs.createResource(uri);
    HashMap<String, EObject> _hashMap = new HashMap<String, EObject>();
    ((ResourceImpl) resource).setIntrinsicIDToEObjectMap(_hashMap);
    return resource;
  }
  
  /**
   * Save the resource in XML format.
   * 
   * @param resource
   *            The resource to be saved.
   */
  public static void saveResource(final Resource resource) {
    Map<Object, Object> saveOptions = ((XMLResource) resource).getDefaultSaveOptions();
    saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
    ArrayList<Object> _arrayList = new ArrayList<Object>();
    saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, _arrayList);
    try {
      resource.save(saveOptions);
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        String _message = e.getMessage();
        String _plus = ("An error has occurred, while creating the model file. " + _message);
        System.err.println(_plus);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  /**
   * Remove the file extension from the denoted filename.
   * 
   * @param filename
   *            A path for a file, that contains the name and the extension of
   *            the file as well.
   */
  public static String removeFileExtension(final String filename) {
    String[] pathParts = filename.split(File.pathSeparator);
    String fileName = UppaalModelSaver.getFileNameFromURIPath(filename);
    StringBuilder returnName = new StringBuilder();
    for (final String part : pathParts) {
      final String[] _converted_pathParts = (String[])pathParts;
      int _indexOf = ((List<String>)Conversions.doWrapArray(_converted_pathParts)).indexOf(part);
      int _length = pathParts.length;
      int _minus = (_length - 1);
      boolean _lessThan = (_indexOf < _minus);
      if (_lessThan) {
        returnName.append((part + File.pathSeparator));
      }
    }
    returnName.append(fileName);
    return returnName.toString();
  }
  
  /**
   * Keep only the name of the file from the denoted path.
   * 
   * @param path
   *            The path of the specified file.
   */
  public static String getFileNameFromURIPath(final String path) {
    String[] pathParts = path.split(File.pathSeparator);
    int _length = pathParts.length;
    int _minus = (_length - 1);
    String lastSegment = pathParts[_minus];
    int _length_1 = lastSegment.length();
    int _minus_1 = (_length_1 - 4);
    return lastSegment.substring(0, _minus_1);
  }
}
