/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.scoping.utils;

import java.util.Objects;

import org.eclipse.n4js.naming.N4JSQualifiedNameConverter;
import org.eclipse.n4js.projectModel.IN4JSProject;
import org.eclipse.xtext.naming.QualifiedName;

/**
 * Utility that computes meta information about import specifiers.
 */
public class ImportSpecifierUtil {

	/** Returns provided project or one of its dependencies with matching project ID. */
	public static IN4JSProject getDependencyWithID(String projectId, IN4JSProject project) {
		if (Objects.equals(project.getProjectId(), projectId))
			return project;

		for (IN4JSProject p : project.getDependencies()) {
			if (Objects.equals(p.getProjectId(), projectId))
				return p;
		}
		return null;
	}

	/**
	 * Convenience method over {@link ImportSpecifierUtil#computeImportType(QualifiedName, boolean, IN4JSProject)}
	 */
	public static ImportType computeImportType(QualifiedName name, IN4JSProject project) {
		final String firstSegment = name.getFirstSegment();
		final IN4JSProject targetProject = getDependencyWithID(firstSegment, project);
		final boolean firstSegmentIsProjectId = targetProject != null;
		return ImportSpecifierUtil.computeImportType(name, firstSegmentIsProjectId, targetProject);
	}

	/**
	 * Computes {@link ImportType} for a given name in the given project.
	 *
	 * @param name
	 *            for which import specifier type is computed
	 * @param useProjectId
	 *            flag if project name should be taken into consideration
	 * @param project
	 *            the project from which import will be specified
	 * @return the {@link ImportType}
	 */
	public static ImportType computeImportType(QualifiedName name, boolean useProjectId,
			IN4JSProject project) {
		if (useProjectId) {
			// PRIORITY 1: 'name' is a complete module specifier, i.e. projectId+'/'+moduleSpecifier
			// -> search all Xtext index entries that match moduleSpecifier and filter by projectId
			final QualifiedName moduleSpecifier;
			if (name.getSegmentCount() == 1) {
				// special case: no module specifier given (only a project ID), i.e. we have a pure project import
				// -> interpret this as an import of the target project's main module
				moduleSpecifier = getMainModuleOfProject(project);
				if (moduleSpecifier == null) {
					// error: we have a project import to a project that does not define a main module via
					// the 'MainModule' property in the manifest -> unresolved reference error
					return ImportType.PROJECT_IMPORT_NO_MAIN;
				} else {
					return ImportType.PROJECT_IMPORT;
				}
			} else {
				return ImportType.COMPLETE_IMPORT;
			}
		}
		// PRIORITY 2: interpret 'name' as a plain module specifier (i.e. without project ID)
		// -> simplest case, because this is exactly how elements are identified within the Xtext index,
		// so we can simply forward this request to the parent scope
		return ImportType.SIMPLE_IMPORT;
	}

	/** returns qualified name of the {@link IN4JSProject#getMainModule() } */
	public static QualifiedName getMainModuleOfProject(IN4JSProject project) {
		if (project != null) {
			final String mainModuleSpec = project.getMainModule();
			if (mainModuleSpec != null) {
				final QualifiedName mainModuleQN = QualifiedName.create(
						mainModuleSpec.split(N4JSQualifiedNameConverter.DELIMITER));
				return mainModuleQN;
			}
		}
		return null;
	}

}
