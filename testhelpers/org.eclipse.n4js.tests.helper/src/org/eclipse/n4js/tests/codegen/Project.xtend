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
package org.eclipse.n4js.tests.codegen

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path
import java.util.List
import java.util.Objects
import org.eclipse.n4js.projectDescription.ProjectType
import org.eclipse.n4js.projectModel.IN4JSProject
import org.eclipse.n4js.utils.io.FileDeleter

/**
 * Generates the code for a project.
 */
public class Project {

	/**
	 * Represents a source folder that has a name and contains modules.
	 */
	public static class SourceFolder {
		private String name;
		private List<Module> modules

		/**
		 * Creates a new instance with the given parameters.
		 *
		 * @param name the name of this source folder
		 */
		public new(String name) {
			this.name = Objects.requireNonNull(name);
		}


		/**
		 * Adds the given module to the source folder to created.
		 *
		 * @param module the module to add
		 *
		 * @return this source folder
		 */
		public def addModule(Module module) {
			if (modules === null)
				modules = newLinkedList();
			modules.add(Objects.requireNonNull(module));
			return this;
		}

		/**
		 * Creates this source folder within the given parent directory, which must exist.
		 *
		 * This method first creates a new folder within the given parent directory, and then
		 * it creates all of its modules within that folder by calling their {@link Module#create(File)}
		 * function with the newly created folder as the parameter.
		 *
		 * @param parentDirectory a file representing the parent directory of this source folder
		 */
		public def create(File parentDirectory) {
			Objects.requireNonNull(parentDirectory);
			if (!parentDirectory.exists)
				throw new IOException("Directory '" + parentDirectory + "' does not exist");
			if (!parentDirectory.directory)
				throw new IOException("'" + parentDirectory + "' is not a directory");

			var File sourceFolder = new File(parentDirectory, name);
			sourceFolder.mkdir();

			for (module: modules)
				module.create(sourceFolder)
		}
	}

	String projectId;
	String vendorId;
	String vendorName;
	ProjectType projectType = ProjectType.LIBRARY;
	String projectVersion = "1.0.0";
	String outputFolder = "src-gen";
	List<SourceFolder> sourceFolders;
	List<Project> projectDependencies;

	/**
	 * Creates a new instance with the given parameters.
	 *
	 * @param projectId the project ID
	 * @param vendorId the vendor ID
	 * @param vendorName the vendor name
	 */
	public new(String projectId, String vendorId, String vendorName) {
		this.projectId = Objects.requireNonNull(projectId);
		this.vendorId = Objects.requireNonNull(vendorId);
		this.vendorName = Objects.requireNonNull(vendorName);
	}

	/**
	 * Returns the project ID.
	 *
	 * @return the project ID
	 */
	public def String getProjectId() {
		return projectId;
	}

	/**
	 * Sets the project type.
	 *
	 * @param projectType the project type to set
	 */
	public def Project setType(ProjectType projectType) {
		this.projectType = projectType;
		return this;
	}

	/**
	 * Sets the project version.
	 *
	 * @param projectVersion the project version
	 */
	public def Project setVersion(String projectVersion) {
		this.projectVersion = projectVersion;
		return this;
	}

	/**
	 * Sets the output folder.
	 *
	 * @param outputFolder the output folder to set
	 */
	public def Project setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
		return this;
	}

	/**
	 * Creates a source folder with the given name to this project.
	 *
	 * @param name the name of the source folder to add
	 *
	 * @return the added source folder
	 */
	public def SourceFolder createSourceFolder(String name) {
		val SourceFolder result = new SourceFolder(name);
		addSourceFolder(result);
		return result;
	}


	/**
	 * Adds a source folder to this project.
	 *
	 * @param sourceFolder the source folder to add
	 */
	public def Project addSourceFolder(SourceFolder sourceFolder) {
		if (sourceFolders === null)
			sourceFolders = newLinkedList();
		sourceFolders.add(Objects.requireNonNull(sourceFolder));
		return this;
	}

	/**
	 * Adds a project dependency to this project.
	 *
	 * @param projectDependency the project dependency to add
	 */
	public def Project addProjectDependency(Project projectDependency) {
		if (projectDependencies === null)
			projectDependencies = newLinkedList();
		projectDependencies.add(Objects.requireNonNull(projectDependency));
		return this;
	}
	
	/**
	 * Generates the {@link IN4JSProject#PACKAGE_JSON} for this project.
	 */
	public def generate() '''
		{
			"name": "«projectId»",
			"version": "«projectVersion»",
			"n4js": {
				"vendorId": "«vendorId»",
				"vendorName": "«vendorName»",
				"projectType": "«projectType.projectTypeToString»",
				«IF !outputFolder.nullOrEmpty
					»"output": "«outputFolder»"
				«ENDIF»
				«IF !sourceFolders.nullOrEmpty
				»,"sources": {
						"source": [
							«FOR sourceFolder : sourceFolders SEPARATOR ','»
								"«sourceFolder.name»"
							«ENDFOR»
						]
					}
				«ENDIF»
			}
			«IF !projectDependencies.nullOrEmpty
			»,"dependencies": {
					«FOR dep : projectDependencies SEPARATOR ','»
						"«dep.projectId»": "*"
					«ENDFOR»
				}
			«ENDIF»
		}
	'''

	private static def projectTypeToString(ProjectType type) {
		return switch (type) {
			case API: "api"
			case APPLICATION: "application"
			case LIBRARY: "library"
			case PROCESSOR: "processor"
			case RUNTIME_ENVIRONMENT: "runtimeEnvironment"
			case RUNTIME_LIBRARY: "runtimeLibrary"
			case TEST: "test"
			case VALIDATION: "validation"
			case DEFINITION: "definition"
		};
	}

	/**
	 * Creates this project in the given parent directory, which must exist.
	 *
	 * This method first creates a directory with the same name as the {@link #projectId} within
	 * the given parent directory. If there already exists a file or directory with that name
	 * within the given parent directory, that file or directory will be (recursively) deleted.
	 *
	 * Afterward, the manifest file and the source folders are created within the newly created
	 * project directory.
	 *
	 * @param parentDirectoryPath the path to the parent directory
	 *
	 * @return the project directory
	 */
	public def create(Path parentDirectoryPath) {
		var File parentDirectory = Objects.requireNonNull(parentDirectoryPath).toFile
		if (!parentDirectory.exists)
			throw new IOException("'" + parentDirectory + "' does not exist")
		if (!parentDirectory.directory)
			throw new IOException("'" + parentDirectory + "' is not a directory");

		val File projectDirectory = new File(parentDirectory, projectId);
		if (projectDirectory.exists)
			FileDeleter.delete(projectDirectory);
		projectDirectory.mkdir();

		createProjectDescriptionFile(projectDirectory);
		createModules(projectDirectory);

		return projectDirectory;
	}

	private def createProjectDescriptionFile(File parentDirectory) {
		val File filePath = new File(parentDirectory, IN4JSProject.PACKAGE_JSON);
		var FileWriter out = null;
		try {
			out = new FileWriter(filePath);
			out.write(generate().toString());
		} finally {
			if (out !== null)
				out.close();
		}
	}

	private def createModules(File parentDirectory) {
		for (sourceFolder: sourceFolders)
			sourceFolder.create(parentDirectory)
	}
}
