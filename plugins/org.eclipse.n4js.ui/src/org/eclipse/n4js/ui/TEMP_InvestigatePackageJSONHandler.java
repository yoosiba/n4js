package org.eclipse.n4js.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.n4js.utils.ProjectDescriptionHelper;

import com.google.inject.Inject;

public class TEMP_InvestigatePackageJSONHandler extends AbstractHandler {

	@Inject
	ProjectDescriptionHelper projectDescriptionHelper;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// MessageDialog.openInformation(
		// window.getShell(),
		// "org.eclipse.n4js.ui",
		// "Hello, Eclipse world");
		findFiles();
		return null;
	}

	private List<IFile> findFiles() {
		List<IFile> result = new ArrayList<>();
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (IProject project : root.getProjects()) {
				if (project.exists() && project.isOpen()) {
					for (IResource res : project.members()) {
						if (res instanceof IFile) {
							IFile file = (IFile) res;
							if (file.getName().equals("manifest.n4mf")) {
								System.out.println("=======================================================");
								System.out.println(file.getLocation());
								URI uri = URI.createPlatformResourceURI(project.getName(), true);
								projectDescriptionHelper.injectManifestIntoPackageJSON(uri);
								// try {
								// ProjectDescriptionExporter.writePackageJSON(pd, new StringWriter());
								// } catch (IOException e) {
								// // TODO Auto-generated catch block
								// e.printStackTrace();
								// }
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
