package hu.bme.mit.inf.alf.uppaal.transformation

import de.uni_paderborn.uppaal.declarations.impl.DeclarationsFactoryImpl
import de.uni_paderborn.uppaal.expressions.impl.ExpressionsFactoryImpl
import de.uni_paderborn.uppaal.impl.UppaalFactoryImpl
import de.uni_paderborn.uppaal.templates.Edge
import de.uni_paderborn.uppaal.templates.Location
import de.uni_paderborn.uppaal.templates.impl.TemplatesFactoryImpl
import java.util.ArrayList
import de.uni_paderborn.uppaal.templates.Template

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
class UppaalModelBuilder {

	/**
	 * The only available instance of the class, because of the Singleton 
	 * design pattern.
	 */
	private static val instance = new UppaalModelBuilder

	/**
	 * The top-level module of the UPPAAL model, the NTA.
	 */
	private var nta = new UppaalFactoryImpl().createNTA

	/**
	 * The template within the NTA. During the transformation, only one
	 * Template is used. (The second top-level element of the UPPAAL model.)
	 */
	//private var template = new TemplatesFactoryImpl().createTemplate
	private ArrayList<Template> template = new ArrayList<Template>
	  
	/**
	 * A list of the locations, within the UPPAAL model.
	 */
	private ArrayList<Location> locations = new ArrayList<Location>

	/**
	 * A list of the edges (transitions), within the UPPAAL model.
	 */
	private ArrayList<Edge> edges = new ArrayList<Edge>

	/**
	 * Private constructor of the class, because of the Singleton design 
	 * pattern.
	 */
	private new() {
	}
	
	public def getNTA() {
		return nta;
	}

	/**
	 * Get the only available instance of the class, because of the Singleton 
	 * design pattern.
	 */
	static def getInstance() {
		return instance
	}

	/**
	 * Create and save a new NTA denoted by its name.
	 * 
	 * @param name
	 *            The name of the NTA.
	 */
	def void createNTA(String name) {
		var nta = new UppaalFactoryImpl().createNTA
		nta.name = name
		// Itt felvettem új változókat
		nta.globalDeclarations = new DeclarationsFactoryImpl().createGlobalDeclarations
		nta.systemDeclarations = new DeclarationsFactoryImpl().createSystemDeclarations
		//
		this.nta = nta
	}

	/**
	 * Create, initialize and return a new Template denoted by its name. The
	 * Template will be stored by the UppaalModelBuilder.
	 * 
	 * @param name
	 *            The name of the Template.
	 * @return The recently created and initialized Template.
	 */
	 /*
	def createTemplate(String name) {
		var template = new TemplatesFactoryImpl().createTemplate
		template.name = name
		this.template = template
		this.template.declarations = new DeclarationsFactoryImpl().createLocalDeclarations		
		return template
	}
	*/
	def createTemplate(String name) {
		var template = new TemplatesFactoryImpl().createTemplate
		template.name = name
		template.declarations = new DeclarationsFactoryImpl().createLocalDeclarations	
		this.template.add(template)			
		return template
	}

	/**
	 * Add a local declaration statement to the local declarations, within the
	 * Template.
	 * 
	 * @param expression
	 *            The local declaration statement, that should be stored.
	 */
	/*
	def addLocalDeclaration(String expression) {
		var declaration = new DeclarationsFactoryImpl().createDataVariableDeclaration
		declaration.exp = expression
		this.template.declarations.declaration.add(declaration)
	}
	*/
	def addLocalDeclaration(String expression, Template template) {
		var declaration = new DeclarationsFactoryImpl().createDataVariableDeclaration
		declaration.exp = expression
		template.declarations.declaration.add(declaration)
	}
	
	/**
	 * Add a global declaration statement to the global declarations, within the
	 * NTA.
	 * 
	 * @param expression 
	 *            The global declaration statement, that should be stored.
	 */
	def addGlobalDeclaration(String expression) {
		var declaration = new DeclarationsFactoryImpl().createDataVariableDeclaration
		declaration.exp = expression
		this.nta.globalDeclarations.declaration.add(declaration)
	}
	
	def addSystemDeclaration(String expression) {
		var declaration = new DeclarationsFactoryImpl().createDataVariableDeclaration
		declaration.exp = expression
		this.nta.systemDeclarations.declaration.add(declaration)
		println(nta.globalDeclarations.declaration.size)
	}

	/**
	 * Create and return a new location denoted by its name.
	 * 
	 * If the name parameter is empty, then the default location name is
	 * Location_ plus the index of the location within the list.
	 * 
	 * If the name parameter is not empty, then the index is also added to the
	 * name connected by a _ mark.
	 * 
	 * The location will be stored in the locations' list by the
	 * UppaalModelBuilder.
	 * 
	 * @param name
	 *            The name of the location.
	 * @return The recently created location.
	 */
	 /*
	def createLocation(String name) {
		var location = new TemplatesFactoryImpl().createLocation
		location.name = name + "_" + this.locations.length
		if(name.isEmpty) {
			location.name = "Location_" + this.locations.length
		}
		this.locations.add(location)
		return location
	}
	*/
	def createLocation(String name, Template template) {
		var location = new TemplatesFactoryImpl().createLocation
		location.name = name + "_" + this.locations.length
		if(name.isEmpty) {
			location.name = "Location_" + this.locations.length
		}
		template.location.add(location)
		return location
	}

	/**
	 * Set the initial location of the Template.
	 * 
	 * @param location
	 *            The location that should be set as the initial one.
	 */
	def setInitialLocation(Location location, Template template) {
		template.init = location
	}

	/**
	 * Set a comment for the specified location.
	 * 
	 * @param location
	 *            The location that will store the specified comment.
	 * @param comment
	 *            The comment that should be stored by the location.
	 */
	def setLocationComment(Location location, String comment) {
		location.comment = comment
	}

	/**
	 * Create and return a new edge. The edge will be stored in the edges' list 
	 * by the UppaalModelBuilder.
	 * 
	 * @return The recently created edge.
	 */
	 /*
	def createEdge() {
		var edge = new TemplatesFactoryImpl().createEdge
		this.edges.add(edge)
		return edge
	}
	* */
	def createEdge(Template template) {
		var edge = new TemplatesFactoryImpl().createEdge
		template.edge.add(edge)
		return edge
	}
	
	
	/**
	 * Set the source location of the specified edge.
	 * 
	 * @param edge
	 *            The specified edge, whose source location will be set.
	 * @param source
	 *            The source location of the edge.
	 */
	def setEdgeSource(Edge edge, Location source) {
		if(edge != null) {
			edge.source = source
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
	def setEdgeTarget(Edge edge, Location target) {
		if(edge != null) {
			edge.target = target
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
	def setEdgeUpdate(Edge edge, String updateExpression) {
		if(edge != null) {
			var update = new ExpressionsFactoryImpl().createAssignmentExpression
			update.exp = updateExpression
			edge.update.add(update)
		}
	}

	/**
	 * Add a guard expression for the specified edge.
	 * 
	 * @param edge
	 *            The specified edge, whom the guard expression will be set.
	 * @param guardExpression
	 *            The guard expression of the edge.
	 */
	def setEdgeGuard(Edge edge, String guardExpression) {
		if(edge != null) {
			var guard = new ExpressionsFactoryImpl().createCompareExpression
			guard.exp = guardExpression
			edge.guard = guard
		}
	}

	/**
	 * Get a reference for the Template. This method is used by the
	 * UpaalModelSerializer for accessing the second top-level element of the
	 * UPPAAL model.
	 * 
	 * @return A reference for the stored Template.
	 */
	def getTemplates() {
		return template
	}

	/**
	 * Get a reference for the stored locations. This method is used by the
	 * UppaalModelSerializer.
	 * 
	 * @return A reference for the stored locations.
	 */
	/*
	def getLocations() {
		return locations
	}*/
	def getLocations(Template template) {
		return template.locations
	}

	/**
	 * Get a reference for the stored locations. This method is used by the
	 * UppaalModelSerializer.
	 * 
	 * @return A reference for the stored locations.
	 */
	 /*
	 def getEdges() {
		return edges
	}
	*/
	def getEdges(Template template) {
		return template.edges
	}

	/**
	 * Assemble the UPPAAL model: add the locations and the edges to the
	 * already stored Template, and add the Template to the NTA.
	 */
	 /*
	 def buildModel() {
			for (edge : this.edges) {
				template.edge.add(edge)
			}
			for (location : this.locations) {
				template.location.add(location)
			}
			this.nta.template.add(template)
	}
	*/
	def buildModel() {
		for (template : this.template) {
			for (edge : this.edges) {
				template.edge.add(edge)
			}
			for (location : this.locations) {
				template.location.add(location)
			}
			this.nta.template.add(template)
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
	def saveUppaalModel(String filename) {
		UppaalModelSaver.saveUppaalModel(this.nta, filename)
	}

	/**
	 * Reset the UppaalModelBuilder.
	 */
	def reset() {
		this.nta = new UppaalFactoryImpl().createNTA
		//this.template = new TemplatesFactoryImpl().createTemplate
		this.template = new ArrayList<Template>
		this.locations.clear
		this.edges.clear
	}
}
