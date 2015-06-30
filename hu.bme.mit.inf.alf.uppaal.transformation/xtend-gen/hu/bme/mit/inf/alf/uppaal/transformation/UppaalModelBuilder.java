package hu.bme.mit.inf.alf.uppaal.transformation;

import com.google.common.base.Objects;
import de.uni_paderborn.uppaal.NTA;
import de.uni_paderborn.uppaal.declarations.DataVariableDeclaration;
import de.uni_paderborn.uppaal.declarations.Declaration;
import de.uni_paderborn.uppaal.declarations.GlobalDeclarations;
import de.uni_paderborn.uppaal.declarations.LocalDeclarations;
import de.uni_paderborn.uppaal.declarations.SystemDeclarations;
import de.uni_paderborn.uppaal.declarations.impl.DeclarationsFactoryImpl;
import de.uni_paderborn.uppaal.expressions.AssignmentExpression;
import de.uni_paderborn.uppaal.expressions.CompareExpression;
import de.uni_paderborn.uppaal.expressions.Expression;
import de.uni_paderborn.uppaal.expressions.impl.ExpressionsFactoryImpl;
import de.uni_paderborn.uppaal.impl.UppaalFactoryImpl;
import de.uni_paderborn.uppaal.templates.Edge;
import de.uni_paderborn.uppaal.templates.Location;
import de.uni_paderborn.uppaal.templates.Template;
import de.uni_paderborn.uppaal.templates.impl.TemplatesFactoryImpl;
import hu.bme.mit.inf.alf.uppaal.transformation.UppaalModelSaver;
import java.util.ArrayList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.InputOutput;

/**
 * The class is responsible for storing and modifying the model elements of the
 * UPPAAL, which are used during the transformation.
 * 
 * The class was designed owing to the Singleton design pattern, so the only
 * available instance of the class is stored in itself.
 * 
 * Notice: The UPPAAL meta model was created by engineers at the University of
 * Paderborn. The project is publicly available. It can be found at
 * https://svn-serv.cs.upb.de/muml_verification/trunk/. Special thanks to
 * Christopher Gerking and Stefan Dziwok.
 * 
 * @author Benedek Horvath
 */
@SuppressWarnings("all")
public class UppaalModelBuilder {
  /**
   * The only available instance of the class, because of the Singleton
   * design pattern.
   */
  private final static UppaalModelBuilder instance = new UppaalModelBuilder();
  
  /**
   * The top-level module of the UPPAAL model, the NTA.
   */
  private NTA nta = new UppaalFactoryImpl().createNTA();
  
  /**
   * The template within the NTA. During the transformation, only one
   * Template is used. (The second top-level element of the UPPAAL model.)
   */
  private ArrayList<Template> template = new ArrayList<Template>();
  
  /**
   * A list of the locations, within the UPPAAL model.
   */
  private ArrayList<Location> locations = new ArrayList<Location>();
  
  /**
   * A list of the edges (transitions), within the UPPAAL model.
   */
  private ArrayList<Edge> edges = new ArrayList<Edge>();
  
  /**
   * Private constructor of the class, because of the Singleton design
   * pattern.
   */
  private UppaalModelBuilder() {
  }
  
  public NTA getNTA() {
    return this.nta;
  }
  
  /**
   * Get the only available instance of the class, because of the Singleton
   * design pattern.
   */
  public static UppaalModelBuilder getInstance() {
    return UppaalModelBuilder.instance;
  }
  
  /**
   * Create and save a new NTA denoted by its name.
   * 
   * @param name
   *            The name of the NTA.
   */
  public void createNTA(final String name) {
    UppaalFactoryImpl _uppaalFactoryImpl = new UppaalFactoryImpl();
    NTA nta = _uppaalFactoryImpl.createNTA();
    nta.setName(name);
    DeclarationsFactoryImpl _declarationsFactoryImpl = new DeclarationsFactoryImpl();
    GlobalDeclarations _createGlobalDeclarations = _declarationsFactoryImpl.createGlobalDeclarations();
    nta.setGlobalDeclarations(_createGlobalDeclarations);
    DeclarationsFactoryImpl _declarationsFactoryImpl_1 = new DeclarationsFactoryImpl();
    SystemDeclarations _createSystemDeclarations = _declarationsFactoryImpl_1.createSystemDeclarations();
    nta.setSystemDeclarations(_createSystemDeclarations);
    this.nta = nta;
  }
  
  /**
   * def createTemplate(String name) {
   * var template = new TemplatesFactoryImpl().createTemplate
   * template.name = name
   * this.template = template
   * this.template.declarations = new DeclarationsFactoryImpl().createLocalDeclarations
   * return template
   * }
   */
  public Template createTemplate(final String name) {
    TemplatesFactoryImpl _templatesFactoryImpl = new TemplatesFactoryImpl();
    Template template = _templatesFactoryImpl.createTemplate();
    template.setName(name);
    DeclarationsFactoryImpl _declarationsFactoryImpl = new DeclarationsFactoryImpl();
    LocalDeclarations _createLocalDeclarations = _declarationsFactoryImpl.createLocalDeclarations();
    template.setDeclarations(_createLocalDeclarations);
    this.template.add(template);
    return template;
  }
  
  /**
   * def addLocalDeclaration(String expression) {
   * var declaration = new DeclarationsFactoryImpl().createDataVariableDeclaration
   * declaration.exp = expression
   * this.template.declarations.declaration.add(declaration)
   * }
   */
  public boolean addLocalDeclaration(final String expression, final Template template) {
    boolean _xblockexpression = false;
    {
      DeclarationsFactoryImpl _declarationsFactoryImpl = new DeclarationsFactoryImpl();
      DataVariableDeclaration declaration = _declarationsFactoryImpl.createDataVariableDeclaration();
      declaration.setExp(expression);
      LocalDeclarations _declarations = template.getDeclarations();
      EList<Declaration> _declaration = _declarations.getDeclaration();
      _xblockexpression = _declaration.add(declaration);
    }
    return _xblockexpression;
  }
  
  /**
   * Add a global declaration statement to the global declarations, within the
   * NTA.
   * 
   * @param expression
   *            The global declaration statement, that should be stored.
   */
  public boolean addGlobalDeclaration(final String expression) {
    boolean _xblockexpression = false;
    {
      DeclarationsFactoryImpl _declarationsFactoryImpl = new DeclarationsFactoryImpl();
      DataVariableDeclaration declaration = _declarationsFactoryImpl.createDataVariableDeclaration();
      declaration.setExp(expression);
      GlobalDeclarations _globalDeclarations = this.nta.getGlobalDeclarations();
      EList<Declaration> _declaration = _globalDeclarations.getDeclaration();
      _xblockexpression = _declaration.add(declaration);
    }
    return _xblockexpression;
  }
  
  public Integer addSystemDeclaration(final String expression) {
    Integer _xblockexpression = null;
    {
      DeclarationsFactoryImpl _declarationsFactoryImpl = new DeclarationsFactoryImpl();
      DataVariableDeclaration declaration = _declarationsFactoryImpl.createDataVariableDeclaration();
      declaration.setExp(expression);
      SystemDeclarations _systemDeclarations = this.nta.getSystemDeclarations();
      EList<Declaration> _declaration = _systemDeclarations.getDeclaration();
      _declaration.add(declaration);
      GlobalDeclarations _globalDeclarations = this.nta.getGlobalDeclarations();
      EList<Declaration> _declaration_1 = _globalDeclarations.getDeclaration();
      int _size = _declaration_1.size();
      _xblockexpression = InputOutput.<Integer>println(Integer.valueOf(_size));
    }
    return _xblockexpression;
  }
  
  /**
   * def createLocation(String name) {
   * var location = new TemplatesFactoryImpl().createLocation
   * location.name = name + "_" + this.locations.length
   * if(name.isEmpty) {
   * location.name = "Location_" + this.locations.length
   * }
   * this.locations.add(location)
   * return location
   * }
   */
  public Location createLocation(final String name, final Template template) {
    TemplatesFactoryImpl _templatesFactoryImpl = new TemplatesFactoryImpl();
    Location location = _templatesFactoryImpl.createLocation();
    int _length = ((Object[])Conversions.unwrapArray(this.locations, Object.class)).length;
    String _plus = ((name + "_") + Integer.valueOf(_length));
    location.setName(_plus);
    boolean _isEmpty = name.isEmpty();
    if (_isEmpty) {
      int _length_1 = ((Object[])Conversions.unwrapArray(this.locations, Object.class)).length;
      String _plus_1 = ("Location_" + Integer.valueOf(_length_1));
      location.setName(_plus_1);
    }
    EList<Location> _location = template.getLocation();
    _location.add(location);
    return location;
  }
  
  /**
   * Set the initial location of the Template.
   * 
   * @param location
   *            The location that should be set as the initial one.
   */
  public void setInitialLocation(final Location location, final Template template) {
    template.setInit(location);
  }
  
  /**
   * Set a comment for the specified location.
   * 
   * @param location
   *            The location that will store the specified comment.
   * @param comment
   *            The comment that should be stored by the location.
   */
  public void setLocationComment(final Location location, final String comment) {
    location.setComment(comment);
  }
  
  /**
   * def createEdge() {
   * var edge = new TemplatesFactoryImpl().createEdge
   * this.edges.add(edge)
   * return edge
   * }
   */
  public Edge createEdge(final Template template) {
    TemplatesFactoryImpl _templatesFactoryImpl = new TemplatesFactoryImpl();
    Edge edge = _templatesFactoryImpl.createEdge();
    EList<Edge> _edge = template.getEdge();
    _edge.add(edge);
    return edge;
  }
  
  /**
   * Set the source location of the specified edge.
   * 
   * @param edge
   *            The specified edge, whose source location will be set.
   * @param source
   *            The source location of the edge.
   */
  public void setEdgeSource(final Edge edge, final Location source) {
    boolean _notEquals = (!Objects.equal(edge, null));
    if (_notEquals) {
      edge.setSource(source);
    }
  }
  
  /**
   * Set the target location of the specified edge.
   * 
   * @param edge
   *            The specified edge, whose target location will be set.
   * @param source
   *            The target location of the edge.
   */
  public void setEdgeTarget(final Edge edge, final Location target) {
    boolean _notEquals = (!Objects.equal(edge, null));
    if (_notEquals) {
      edge.setTarget(target);
    }
  }
  
  /**
   * Add an update expression for the specified edge.
   * 
   * @param edge
   *            The specified edge, whom the update expression will be set.
   * @param updateExpression
   *            The update expression of the edge.
   */
  public boolean setEdgeUpdate(final Edge edge, final String updateExpression) {
    boolean _xifexpression = false;
    boolean _notEquals = (!Objects.equal(edge, null));
    if (_notEquals) {
      boolean _xblockexpression = false;
      {
        ExpressionsFactoryImpl _expressionsFactoryImpl = new ExpressionsFactoryImpl();
        AssignmentExpression update = _expressionsFactoryImpl.createAssignmentExpression();
        update.setExp(updateExpression);
        EList<Expression> _update = edge.getUpdate();
        _xblockexpression = _update.add(update);
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  /**
   * Add a guard expression for the specified edge.
   * 
   * @param edge
   *            The specified edge, whom the guard expression will be set.
   * @param guardExpression
   *            The guard expression of the edge.
   */
  public void setEdgeGuard(final Edge edge, final String guardExpression) {
    boolean _notEquals = (!Objects.equal(edge, null));
    if (_notEquals) {
      ExpressionsFactoryImpl _expressionsFactoryImpl = new ExpressionsFactoryImpl();
      CompareExpression guard = _expressionsFactoryImpl.createCompareExpression();
      guard.setExp(guardExpression);
      edge.setGuard(guard);
    }
  }
  
  /**
   * Get a reference for the Template. This method is used by the
   * UpaalModelSerializer for accessing the second top-level element of the
   * UPPAAL model.
   * 
   * @return A reference for the stored Template.
   */
  public ArrayList<Template> getTemplates() {
    return this.template;
  }
  
  /**
   * def getLocations() {
   * return locations
   * }
   */
  public Object getLocations(final Template template) {
    return this.getLocations(template);
  }
  
  /**
   * def getEdges() {
   * return edges
   * }
   */
  public Object getEdges(final Template template) {
    return this.getEdges(template);
  }
  
  /**
   * def buildModel() {
   * for (edge : this.edges) {
   * template.edge.add(edge)
   * }
   * for (location : this.locations) {
   * template.location.add(location)
   * }
   * this.nta.template.add(template)
   * }
   */
  public void buildModel() {
    for (final Template template : this.template) {
      {
        for (final Edge edge : this.edges) {
          EList<Edge> _edge = template.getEdge();
          _edge.add(edge);
        }
        for (final Location location : this.locations) {
          EList<Location> _location = template.getLocation();
          _location.add(location);
        }
        EList<Template> _template = this.nta.getTemplate();
        _template.add(template);
      }
    }
  }
  
  /**
   * Save and serialize of the UPPAAL model to an XML file, denoted by its
   * file name. This XML file cannot be loaded by the UPPAAL, it just serves
   * as a serialization of the UPPAAL model.
   * 
   * @param filename
   *            The specified path and name for the serialized XML file.
   */
  public void saveUppaalModel(final String filename) {
    UppaalModelSaver.saveUppaalModel(this.nta, filename);
  }
  
  /**
   * Reset the UppaalModelBuilder.
   */
  public void reset() {
    UppaalFactoryImpl _uppaalFactoryImpl = new UppaalFactoryImpl();
    NTA _createNTA = _uppaalFactoryImpl.createNTA();
    this.nta = _createNTA;
    ArrayList<Template> _arrayList = new ArrayList<Template>();
    this.template = _arrayList;
    this.locations.clear();
    this.edges.clear();
  }
}
