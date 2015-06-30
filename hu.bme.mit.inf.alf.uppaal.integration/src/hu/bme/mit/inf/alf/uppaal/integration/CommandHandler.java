package hu.bme.mit.inf.alf.uppaal.integration;

import hu.bme.mit.inf.alf.uppaal.incquery.traversal.ModelTraverser;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.papyrus.uml.alf.alf.DocumentedStatement;
import org.eclipse.papyrus.uml.alf.alf.Test;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A command handler class that is responsible for delegating the right click
 * menu call to the ModelTraverser. It is the entry point of the transformation,
 * because it is integrated into the Eclipse itself.
 * 
 * If the user clicks on a file in the Project Explorer with the right mouse
 * button, and chooses "AlfToUppaal", then the execute function will be called.
 * 
 * The class can be run on a new thread, because the transformation takes a long
 * time, and I do not want the Eclipse GUI to be frozen during the
 * transformation.
 * 
 * @author Benedek Horvath
 * 
 */
public class CommandHandler extends AbstractHandler implements Runnable {

	/**
	 * The elements of the Alf instance model which should be transformed.
	 */
	private static EList<DocumentedStatement> statements = null;

	/**
	 * The event handler method, that is responsible for fetching the Alf model
	 * from the specified resource, and getting the list of the
	 * DocumentedStatements from itself, that will be transformed to a UPPAAL
	 * model.
	 * 
	 * @param event
	 *            The object that is received from the Eclipse, it stores a
	 *            reference for the selected file in the Runtime Eclipse.
	 * @throws ExecutionException
	 *             Signals that an exception occurred during the execution of a
	 *             command.
	 * @return The result of the execution. Reserved for future use, must be
	 *         null.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		try {
			if (sel instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) sel;
				if (selection.getFirstElement() != null) {
					if (selection.getFirstElement() instanceof IFile) {
						IFile file = (IFile) selection.getFirstElement();
						ResourceSet resSet = new ResourceSetImpl();
					
						URI fileURI = URI.createPlatformResourceURI(file
								.getFullPath().toString(), true);
						Resource resource;
						try {
							resource = resSet.getResource(fileURI, true);
						} catch (RuntimeException e) {
							return null;
						}

						if (resource.getContents() != null) {
							if (resource.getContents().get(0) instanceof Test) {
								Test testBlock = (Test) resource.getContents()
										.get(0);
								if (testBlock.getBlock() != null) {
									if (testBlock.getBlock().getSequence() != null) {
										if (testBlock.getBlock().getSequence()
												.getStatements() != null) {
											// save the resource and the
											// locationURI of the selected file
											ModelTraverser
													.getInstance()
													.setResource(
															resource,
															file.getLocationURI()
																	.toString());
											// save the list of the
											// DocumentedStatements
											CommandHandler.statements = testBlock
													.getBlock().getSequence()
													.getStatements();
											// start the transformation
											new Thread(new CommandHandler())
													.start();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {

		}
		return null;
	}

	/**
	 * The transformation runs on a new Thread, because it takes a long time,
	 * and I do not want the Eclipse GUI to be frozen during the transformation.
	 */
	@Override
	public void run() {
		ModelTraverser.getInstance().traverse(CommandHandler.statements);
	}

}
