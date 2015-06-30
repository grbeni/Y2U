package hu.bme.mit.inf.alf.uppaal.transformation.serialization;

import com.google.common.base.Objects;
import de.uni_paderborn.uppaal.NTA;
import de.uni_paderborn.uppaal.declarations.Declaration;
import de.uni_paderborn.uppaal.declarations.GlobalDeclarations;
import de.uni_paderborn.uppaal.declarations.LocalDeclarations;
import de.uni_paderborn.uppaal.expressions.Expression;
import de.uni_paderborn.uppaal.templates.Edge;
import de.uni_paderborn.uppaal.templates.Location;
import de.uni_paderborn.uppaal.templates.Template;
import hu.bme.mit.inf.alf.uppaal.transformation.UppaalModelBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

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
@SuppressWarnings("all")
public class UppaalModelSerializer {
  private static int id1 = 0;
  
  private static int id2 = 0;
  
  /**
   * Save the UPPAAL model specified by the UppaalModelBuilder to an XML file,
   * denoted by its file path. The created XML file can be loaded by the
   * UPPAAL.
   * 
   * @param filepath
   *            The path for the output file. It contains the file name also,
   *            except for the file extension.
   */
  public static String saveToXML(final String filepath) {
    String _xtrycatchfinallyexpression = null;
    try {
      String _xblockexpression = null;
      {
        FileWriter fw = new FileWriter((filepath + ".xml"));
        final CharSequence header = UppaalModelSerializer.createHeader();
        final CharSequence body = UppaalModelSerializer.createTemplate();
        final CharSequence footer = UppaalModelSerializer.createFooter();
        String _string = body.toString();
        String _plus = (header + _string);
        String _plus_1 = (_plus + footer);
        fw.write(_plus_1);
        fw.close();
        _xblockexpression = InputOutput.<String>println("Transformation was finished.");
      }
      _xtrycatchfinallyexpression = _xblockexpression;
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException ex = (IOException)_t;
        String _message = ex.getMessage();
        String _plus = ("An error has occurred, while creating the XML file. " + _message);
        System.err.println(_plus);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return _xtrycatchfinallyexpression;
  }
  
  /**
   * Create the header and the beginning of the XML file, that contains
   * the declaration of the top-level UPPAAL module (NTA) and the global
   * declarations as well.
   * 
   * @return The header of the XML file in a char sequence.
   */
  public static CharSequence createHeader() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    _builder.newLine();
    _builder.append("<!DOCTYPE nta PUBLIC \'-//Uppaal Team//DTD Flat System 1.1//EN\' \'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd\'>");
    _builder.newLine();
    _builder.append("<nta>");
    _builder.newLine();
    _builder.append("<declaration>");
    {
      UppaalModelBuilder _instance = UppaalModelBuilder.getInstance();
      NTA _nTA = _instance.getNTA();
      GlobalDeclarations _globalDeclarations = _nTA.getGlobalDeclarations();
      EList<Declaration> _declaration = _globalDeclarations.getDeclaration();
      boolean _hasElements = false;
      for(final Declaration declaration : _declaration) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n", "");
        }
        _builder.newLineIfNotEmpty();
        String _exp = declaration.getExp();
        _builder.append(_exp, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</declaration>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  /**
   * def static createTemplate() '''
   * <template>
   * <name>«UppaalModelBuilder.instance.template.name»</name>
   * <declaration>«FOR declaration : UppaalModelBuilder.instance.template.declarations.declaration SEPARATOR "\n"»
   * «declaration.exp»
   * «ENDFOR»</declaration>
   * 
   * «FOR location : UppaalModelBuilder.instance.locations SEPARATOR "\n"»
   * <location id="«location.name»">
   * <name>«location.name»</name>
   * «IF !(location.comment == null)»<label kind="comments">«location.comment»</label>«ENDIF»
   * </location>
   * «ENDFOR»
   * 
   * <init ref="«UppaalModelBuilder.instance.template.init.name»"/>
   * 
   * «FOR transition : UppaalModelBuilder.instance.edges SEPARATOR "\n"»
   * <transition>
   * <source ref="«transition.source.name»"/>
   * <target ref="«transition.target.name»"/>
   * «IF !(transition.guard == null)»<label kind="guard">«transition.guard.exp»</label>«ENDIF»
   * «IF (transition.update.length == 1)»<label kind="assignment">«transition.update.get(0).exp»</label>«ENDIF»
   * </transition>
   * «ENDFOR»
   * </template>
   * '''
   */
  public static CharSequence createTemplate() {
    StringConcatenation _builder = new StringConcatenation();
    {
      UppaalModelBuilder _instance = UppaalModelBuilder.getInstance();
      ArrayList<Template> _templates = _instance.getTemplates();
      boolean _hasElements = false;
      for(final Template template : _templates) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n", "");
        }
        _builder.append("<template>");
        _builder.newLine();
        _builder.append("<name>");
        String _name = template.getName();
        _builder.append(_name, "");
        _builder.append("</name>");
        _builder.newLineIfNotEmpty();
        _builder.append("<declaration>");
        {
          LocalDeclarations _declarations = template.getDeclarations();
          EList<Declaration> _declaration = _declarations.getDeclaration();
          boolean _hasElements_1 = false;
          for(final Declaration declaration : _declaration) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate("\n", "");
            }
            _builder.newLineIfNotEmpty();
            String _exp = declaration.getExp();
            _builder.append(_exp, "");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("</declaration>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.newLine();
        {
          EList<Location> _location = template.getLocation();
          boolean _hasElements_2 = false;
          for(final Location location : _location) {
            if (!_hasElements_2) {
              _hasElements_2 = true;
            } else {
              _builder.appendImmediate("\n", "");
            }
            _builder.append("<location id=\"");
            String _name_1 = location.getName();
            _builder.append(_name_1, "");
            _builder.append("\">");
            _builder.newLineIfNotEmpty();
            _builder.append("<name>");
            String _name_2 = location.getName();
            _builder.append(_name_2, "");
            _builder.append("</name>");
            _builder.newLineIfNotEmpty();
            {
              String _comment = location.getComment();
              boolean _equals = Objects.equal(_comment, null);
              boolean _not = (!_equals);
              if (_not) {
                _builder.append("<label kind=\"comments\">");
                String _comment_1 = location.getComment();
                _builder.append(_comment_1, "");
                _builder.append("</label>");
              }
            }
            _builder.newLineIfNotEmpty();
            _builder.append("</location>");
            _builder.newLine();
          }
        }
        _builder.append("<init ref=\"");
        Location _init = template.getInit();
        String _name_3 = _init.getName();
        _builder.append(_name_3, "");
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.newLine();
        {
          EList<Edge> _edge = template.getEdge();
          boolean _hasElements_3 = false;
          for(final Edge transition : _edge) {
            if (!_hasElements_3) {
              _hasElements_3 = true;
            } else {
              _builder.appendImmediate("\n", "");
            }
            _builder.append("<transition>");
            _builder.newLine();
            _builder.append("<source ref=\"");
            Location _source = transition.getSource();
            String _name_4 = _source.getName();
            _builder.append(_name_4, "");
            _builder.append("\"/>");
            _builder.newLineIfNotEmpty();
            _builder.append("<target ref=\"");
            Location _target = transition.getTarget();
            String _name_5 = _target.getName();
            _builder.append(_name_5, "");
            _builder.append("\"/>");
            _builder.newLineIfNotEmpty();
            {
              Expression _guard = transition.getGuard();
              boolean _equals_1 = Objects.equal(_guard, null);
              boolean _not_1 = (!_equals_1);
              if (_not_1) {
                _builder.append("<label kind=\"guard\">");
                Expression _guard_1 = transition.getGuard();
                String _exp_1 = _guard_1.getExp();
                _builder.append(_exp_1, "");
                _builder.append("</label>");
              }
            }
            _builder.newLineIfNotEmpty();
            {
              EList<Expression> _update = transition.getUpdate();
              int _length = ((Object[])Conversions.unwrapArray(_update, Object.class)).length;
              boolean _equals_2 = (_length == 1);
              if (_equals_2) {
                _builder.append("<label kind=\"assignment\">");
                EList<Expression> _update_1 = transition.getUpdate();
                Expression _get = _update_1.get(0);
                String _exp_2 = _get.getExp();
                _builder.append(_exp_2, "");
                _builder.append("</label>");
              }
            }
            _builder.newLineIfNotEmpty();
            _builder.append("</transition>");
            _builder.newLine();
          }
        }
        _builder.append("</template>");
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  /**
   * def static createFooter() '''
   * <system>
   * Process = «UppaalModelBuilder.instance.template.name»();
   * system Process;
   * </system>
   * </nta>
   * '''
   */
  public static CharSequence createFooter() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<system>\t");
    _builder.newLine();
    _builder.newLine();
    {
      UppaalModelBuilder _instance = UppaalModelBuilder.getInstance();
      ArrayList<Template> _templates = _instance.getTemplates();
      boolean _hasElements = false;
      for(final Template template : _templates) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n", "");
        }
        _builder.append("Process");
        int _id1 = UppaalModelSerializer.id1 = (UppaalModelSerializer.id1 + 1);
        _builder.append(_id1, "");
        _builder.append(" =  ");
        String _name = template.getName();
        _builder.append(_name, "");
        _builder.append("();\t");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("system ");
    _builder.newLine();
    {
      UppaalModelBuilder _instance_1 = UppaalModelBuilder.getInstance();
      ArrayList<Template> _templates_1 = _instance_1.getTemplates();
      boolean _hasElements_1 = false;
      for(final Template template_1 : _templates_1) {
        if (!_hasElements_1) {
          _hasElements_1 = true;
        } else {
          _builder.appendImmediate(", ", "");
        }
        _builder.append("Process");
        int _id2 = UppaalModelSerializer.id2 = (UppaalModelSerializer.id2 + 1);
        _builder.append(_id2, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(";\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("</system>");
    _builder.newLine();
    _builder.append("</nta>");
    _builder.newLine();
    return _builder;
  }
}
