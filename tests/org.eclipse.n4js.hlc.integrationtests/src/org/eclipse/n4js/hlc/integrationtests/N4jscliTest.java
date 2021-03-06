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
package org.eclipse.n4js.hlc.integrationtests;

import static org.eclipse.n4js.hlc.integrationtests.HlcTestingConstants.TARGET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.eclipse.n4js.N4JSInjectorProvider;
import org.eclipse.n4js.binaries.BinaryCommandFactory;
import org.eclipse.n4js.binaries.nodejs.NodeJsBinary;
import org.eclipse.n4js.test.helper.hlc.N4CliHelper;
import org.eclipse.n4js.utils.process.ProcessResult;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

/**
 * The tests in this class test n4js-cli npm.
 */
@RunWith(XtextRunner.class)
@InjectWith(N4JSInjectorProvider.class)
public class N4jscliTest extends AbstractN4jscJarTest {

	private static final int PORT = 4873;

	private static final String LOCALHOST = "localhost";

	private static final String NODE_MODULES = "node_modules";

	private static final String PROJECT_NAME = "PSingleTestNpm";

	@Inject
	private NodeJsBinary nodeJsBinary;

	@Inject
	private BinaryCommandFactory commandFactory;

	private final String localNpmRegstry = "http://" + LOCALHOST + ":" + PORT;

	/**
	 * Constructor
	 *
	 */
	public N4jscliTest() {
		super("fixture");
	}

	private static boolean checkHostAvailability() {
		try (Socket s = new Socket(LOCALHOST, PORT)) {
			return true;
		} catch (IOException ex) {
			// Do nothing
		}
		return false;
	}

	@Override
	@Before
	public void setupWorkspace() throws IOException {
		// Before we make the effort to test anything, check if the npm local registry is available!
		assertTrue("Local npm registry is not found at " + localNpmRegstry, checkHostAvailability());

		super.setupWorkspace();
		// Create a fresh node_modules folder in WSP
		File nodeModulesFolder = new File(
				pathToProject(PROJECT_NAME).toString() + File.separatorChar + NODE_MODULES);
		if (nodeModulesFolder.exists()) {
			nodeModulesFolder.delete();
		}
		nodeModulesFolder.mkdirs();

		// Create .npmrc file in the PSingleTestNpm folder
		File npmrcFile = new File(pathToProject(PROJECT_NAME).toString() + File.separatorChar + ".npmrc");
		if (npmrcFile.exists()) {
			npmrcFile.delete();
		}
		npmrcFile.createNewFile();

		PrintWriter out = new PrintWriter(npmrcFile);
		out.println("registry=" + localNpmRegstry);
		out.close();
	}

	/**
	 * Create and start a process from {@value #PROJECT_NAME}-folder.
	 *
	 * @param args
	 *            arguments to pass after n4js-cli call
	 *
	 * @return running process
	 * @throws IOException
	 *             if error.
	 */
	@Override
	protected Process createAndStartProcess(String... args) throws IOException {
		return N4CliHelper.createAndStartProcessIntern(outputLogFile,
				pathToProject(PROJECT_NAME).toString(), args);
	}

	/**
	 * Test n4js-cli --help
	 */
	@Test
	public void testN4JSCliHelp() throws Exception {
		logFile();
		// Step 1: Call npm install in PSingleTestNpm folder
		final ProcessResult result1 = commandFactory
				.createInstallPackageCommand(pathToProject(PROJECT_NAME).toFile(), "", false).execute();
		assertEquals("Calling npm install failed", 0, result1.getExitCode());

		// Step 2: Call npm install n4js-cli in the project 'PSingleTestNpm'
		final ProcessResult result2 = commandFactory
				.createInstallPackageCommand(pathToProject(PROJECT_NAME).toFile(), "n4js-cli@test", false)
				.execute();
		assertEquals("Calling npm install n4js-cli@canary failed", 0, result2.getExitCode());

		// Step 3: Test that calling n4js-cli is OK
		String fullCmd = pathToProject(PROJECT_NAME).toString() + File.separatorChar + NODE_MODULES
				+ File.separatorChar
				+ "n4js-cli/bin/n4jsc.js";
		Process p = createAndStartProcess(nodeJsBinary.getBinaryAbsolutePath(), fullCmd, "--help");
		int exitCode = p.waitFor();
		assertEquals("Calling n4js-cli.js --help failed", 0, exitCode);
	}

	private Path pathToProject(String projectName) {
		return FileSystems.getDefault().getPath(TARGET + File.separatorChar + WORKSPACE_FOLDER + File.separatorChar + projectName)
				.normalize().toAbsolutePath();
	}

	/**
	 * Remove .npmrc file after test execution
	 */
	@After
	public void tearDown() {
		// Remove .npmrc file in the PSingleTestNpm folder
		File npmrcFile = new File(pathToProject(PROJECT_NAME).toString() + File.separatorChar + ".npmrc");
		if (npmrcFile.exists()) {
			npmrcFile.delete();
		}
	}
}
