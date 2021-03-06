/**
 * Copyright (c) 2016 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.ui.internal;

import static org.eclipse.n4js.internal.N4JSModel.DIRECT_RESOURCE_IN_PROJECT_SEGMENTCOUNT;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.n4js.internal.InternalN4JSWorkspace;
import org.eclipse.n4js.projectDescription.ProjectDescription;
import org.eclipse.n4js.projectDescription.ProjectReference;
import org.eclipse.n4js.utils.ProjectDescriptionLoader;
import org.eclipse.n4js.utils.ProjectDescriptionUtils;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 */
@Singleton
public class EclipseBasedN4JSWorkspace extends InternalN4JSWorkspace {

	private final IWorkspaceRoot workspace;

	private final ProjectDescriptionLoader projectDescriptionLoader;

	private final Map<URI, ProjectDescription> cache = Maps.newHashMap();

	private ProjectDescriptionLoadListener listener;

	/**
	 * Public for testing purpose.
	 */
	@Inject
	public EclipseBasedN4JSWorkspace(
			IWorkspaceRoot workspace,
			ProjectDescriptionLoader projectDescriptionLoader) {
		this.workspace = workspace;
		this.projectDescriptionLoader = projectDescriptionLoader;
	}

	IWorkspaceRoot getWorkspace() {
		return workspace;
	}

	@Override
	public URI findProjectWith(URI nestedLocation) {
		if (nestedLocation.isPlatformResource()
				&& nestedLocation.segmentCount() >= DIRECT_RESOURCE_IN_PROJECT_SEGMENTCOUNT) {
			return URI.createPlatformResourceURI(nestedLocation.segment(1), true);
		}
		return null;
	}

	@Override
	public ProjectDescription getProjectDescription(URI location) {
		if (!location.isPlatformResource()) {
			return null;
		}
		ProjectDescription existing = cache.get(location);
		if (existing == null) {
			existing = projectDescriptionLoader.loadProjectDescriptionAtLocation(location);
			if (existing != null) {
				cache.put(location, existing);
				if (listener != null) {
					listener.onDescriptionLoaded(location);
				}
			}
		}
		return existing;
	}

	@Override
	public URI getLocation(URI projectURI, ProjectReference projectReference) {
		if (projectURI.segmentCount() >= DIRECT_RESOURCE_IN_PROJECT_SEGMENTCOUNT) {
			String expectedProjectName = projectReference.getProjectId();
			if (expectedProjectName != null && expectedProjectName.length() > 0) {
				if (ProjectDescriptionUtils.isProjectNameWithScope(expectedProjectName)) {
					// cannot create projects using npm scopes in the name, e.g. "@scopeName/projectName"
					return null;
				}
				IProject existingProject = workspace.getProject(expectedProjectName);
				if (existingProject.isAccessible()) {
					return URI.createPlatformResourceURI(expectedProjectName, true);
				}
			}
		}
		return null;
	}

	@Override
	public UnmodifiableIterator<URI> getFolderIterator(URI folderLocation) {
		final IContainer container;
		if (DIRECT_RESOURCE_IN_PROJECT_SEGMENTCOUNT == folderLocation.segmentCount()) {
			container = workspace.getProject(folderLocation.lastSegment());
		} else {
			container = workspace.getFolder(new Path(folderLocation.toPlatformString(true)));
		}
		if (container != null && container.exists()) {
			final List<URI> result = Lists.newLinkedList();
			try {
				container.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getType() == IResource.FILE) {
							result.add(URI.createPlatformResourceURI(resource.getFullPath().toString(), true));
						}
						return true;
					}
				});
				return Iterators.unmodifiableIterator(result.iterator());
			} catch (CoreException e) {
				return Iterators.unmodifiableIterator(result.iterator());
			}
		}
		return Iterators.unmodifiableIterator(Collections.emptyIterator());
	}

	@Override
	public URI findArtifactInFolder(URI folderLocation, String folderRelativePath) {
		final String folderLocationString = folderLocation.toPlatformString(true);
		if (null != folderLocationString) {
			final IFolder folder = workspace.getFolder(new Path(folderLocationString));
			final String subPathStr = folderRelativePath.replace(File.separator, "/");
			final IPath subPath = new Path(subPathStr);
			final IFile file = folder != null ? folder.getFile(subPath) : null;
			if (file != null && file.exists()) {
				return folderLocation.appendSegments(subPathStr.split("/"));
			}
		}
		return null;
	}

	void discardEntry(URI uri) {
		cache.remove(uri);
	}

	void discardEntries() {
		cache.clear();
	}

	void setProjectDescriptionLoadListener(ProjectDescriptionLoadListener listener) {
		this.listener = listener;
	}

}
