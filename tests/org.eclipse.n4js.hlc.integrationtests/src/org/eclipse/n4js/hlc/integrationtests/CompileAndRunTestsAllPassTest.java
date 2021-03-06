/**
 * Copyright (c) 2018 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.hlc.integrationtests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * Uses the {@code n4jsc.jar} to compile and run all tests in a given project.
 *
 * All tests in the project pass.
 */
public class CompileAndRunTestsAllPassTest extends AbstractN4jscJarTest {

	/** */
	public CompileAndRunTestsAllPassTest() {
		super("probands/GH-975-tester-allpass", true);
	}

	/**
	 * Compiles and runs all tests in a simple N4JS test project.
	 */
	@Test
	public void testRunTestsAllPass() throws IOException, InterruptedException {
		logFile();

		final String wsRoot = WORKSPACE_FOLDER;
		final String projectToTest = WORKSPACE_FOLDER + "/T";

		final Process p = createAndStartProcess(
				"--projectlocations", wsRoot,
				"--buildType", "allprojects",
				"--test", projectToTest);

		int exitCode = p.waitFor();

		assertEquals("The process terminates successfully with exit code 0.", 0, exitCode);
	}

}
