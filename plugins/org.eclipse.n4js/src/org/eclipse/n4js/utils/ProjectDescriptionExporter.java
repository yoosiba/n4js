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
package org.eclipse.n4js.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.n4js.json.JSON.JSONArray;
import org.eclipse.n4js.json.JSON.JSONBooleanLiteral;
import org.eclipse.n4js.json.JSON.JSONFactory;
import org.eclipse.n4js.json.JSON.JSONNullLiteral;
import org.eclipse.n4js.json.JSON.JSONNumericLiteral;
import org.eclipse.n4js.json.JSON.JSONObject;
import org.eclipse.n4js.json.JSON.JSONStringLiteral;
import org.eclipse.n4js.json.JSON.JSONValue;
import org.eclipse.n4js.json.JSON.NameValuePair;
import org.eclipse.n4js.n4mf.BootstrapModule;
import org.eclipse.n4js.n4mf.DeclaredVersion;
import org.eclipse.n4js.n4mf.ModuleFilter;
import org.eclipse.n4js.n4mf.ModuleFilterSpecifier;
import org.eclipse.n4js.n4mf.ModuleFilterType;
import org.eclipse.n4js.n4mf.ProjectDependency;
import org.eclipse.n4js.n4mf.ProjectDependencyScope;
import org.eclipse.n4js.n4mf.ProjectDescription;
import org.eclipse.n4js.n4mf.ProjectReference;
import org.eclipse.n4js.n4mf.ProjectType;
import org.eclipse.n4js.n4mf.SourceContainerDescription;
import org.eclipse.n4js.n4mf.SourceContainerType;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class ProjectDescriptionExporter {

	private static void reportWarning(ProjectDescription pd, String msg) {
		System.out.println("WARNING: " + msg + " (in: " + pd.getProjectId() + ")");
	}

	public static void writePackageJSON(ProjectDescription pd, Writer out) throws IOException {
		writePackageJSON(toJSON(pd), out);
	}

	public static void writePackageJSON(JSONObject json, Writer out) throws IOException {
		JSONExporter.export(out, json, 0);
	}

	public static void injectIntoPackageJSON(JSONObject target, ProjectDescription source) {
		// assertProperty(target, ProjectDescriptionHelper.PROP__NAME,
		// source.getProjectId(), source);
		// assertProperty(target, ProjectDescriptionHelper.PROP__VERSION,
		// getVersionString(source.getProjectVersion()), source);

		mergeProperty(target, ProjectDescriptionHelper.PROP__VERSION,
				getVersionString(source.getProjectVersion()), source);
		mergeProperty(target, ProjectDescriptionHelper.PROP__NAME,
				source.getProjectId(), source);

		if (target.getNameValuePairs().removeIf(pair -> ProjectDescriptionHelper.PROP__N4JS.equals(pair.getName()))) {
			reportWarning(source, "injectIntoPackageJSON: replaced existing property 'n4js' in target!");
		}
		addProperty(target, ProjectDescriptionHelper.PROP__N4JS, createN4jsValue(source));

		// check dependencies
		Set<String> actualDeps = collectDependencies(target);
		Set<String> depsFromSource = collectDependencies(source);
		if (!actualDeps.containsAll(depsFromSource)) {
			depsFromSource.removeAll(actualDeps);
			reportWarning(source, "missing dependencies in package.json: " + Joiner.on(", ").join(depsFromSource));
		}
	}

	public static JSONObject toJSON(ProjectDescription pd) {
		JSONObject result = JSONFactory.eINSTANCE.createJSONObject();
		addProperty(result, ProjectDescriptionHelper.PROP__NAME, pd.getProjectId());
		addProperty(result, ProjectDescriptionHelper.PROP__VERSION, getVersionString(pd.getProjectVersion()));
		// TODO dependencies
		addProperty(result, ProjectDescriptionHelper.PROP__N4JS, createN4jsValue(pd));
		return result;
	}

	private static JSONObject createN4jsValue(ProjectDescription pd) {
		JSONObject result = JSONFactory.eINSTANCE.createJSONObject();
		addProperty(result, ProjectDescriptionHelper.PROP__PROJECT_TYPE, getEnumAsString(pd.getProjectType()));
		addProperty(result, ProjectDescriptionHelper.PROP__VENDOR_ID, pd.getVendorId());
		addProperty(result, ProjectDescriptionHelper.PROP__VENDOR_NAME, pd.getVendorName());
		addProperty(result, ProjectDescriptionHelper.PROP__OUTPUT, pd.getOutputPath());
		addProperty(result, ProjectDescriptionHelper.PROP__MAIN_MODULE, pd.getMainModule());
		addProperty(result, ProjectDescriptionHelper.PROP__SOURCES, createSourcesValue(pd));
		addProperty(result, ProjectDescriptionHelper.PROP__MODULE_FILTERS, createModuleFiltersValue(pd));
		addProperty(result, ProjectDescriptionHelper.PROP__EXTENDED_RUNTIME_ENVIRONMENT,
				getProjectReferenceAsString(pd.getExtendedRuntimeEnvironment()));
		addProperty(result, ProjectDescriptionHelper.PROP__PROVIDED_RUNTIME_LIBRARIES,
				getProjectReferencesAsStrings(pd.getProvidedRuntimeLibraries()));
		addProperty(result, ProjectDescriptionHelper.PROP__REQUIRED_RUNTIME_LIBRARIES,
				getProjectReferencesAsStrings(pd.getRequiredRuntimeLibraries()));
		addProperty(result, ProjectDescriptionHelper.PROP__IMPLEMENTATION_ID, pd.getImplementationId());
		addProperty(result, ProjectDescriptionHelper.PROP__IMPLEMENTED_PROJECTS,
				getProjectReferencesAsStrings(pd.getImplementedProjects()));
		addProperty(result, ProjectDescriptionHelper.PROP__TESTED_PROJECTS,
				pd.getTestedProjects().stream().map(dep -> dep.getProjectId()).collect(Collectors.toList()));
		addProperty(result, ProjectDescriptionHelper.PROP__MODULE_LOADER, getEnumAsString(pd.getModuleLoader()));
		// execModule
		BootstrapModule execModule = pd.getExecModule();
		if (execModule != null) {
			addProperty(result, ProjectDescriptionHelper.PROP__EXEC_MODULE,
					execModule.getModuleSpecifierWithWildcard());
		}
		// initModules
		List<BootstrapModule> initModules = pd.getInitModules();
		if (!initModules.isEmpty()) {
			addProperty(result, ProjectDescriptionHelper.PROP__INIT_MODULES,
					initModules.stream().map(m -> m.getModuleSpecifierWithWildcard()).collect(Collectors.toList()));
		}
		// warn about unsupported properties
		if (hasUnsupportedProperty(pd.getExtendedRuntimeEnvironment())) {
			reportWarning(pd, "a ProjectDependency in 'extendedRuntimeEnvironment' has a value other than 'projectId'");
		}
		if (pd.getProvidedRuntimeLibraries().stream().anyMatch(dep -> hasUnsupportedProperty(dep))) {
			reportWarning(pd, "a ProjectDependency in 'providedRuntimeLibraries' has a value other than 'projectId'");
		}
		if (pd.getRequiredRuntimeLibraries().stream().anyMatch(dep -> hasUnsupportedProperty(dep))) {
			reportWarning(pd, "a ProjectDependency in 'requiredRuntimeLibraries' has a value other than 'projectId'");
		}
		if (pd.getImplementedProjects().stream().anyMatch(dep -> hasUnsupportedProperty(dep))) {
			reportWarning(pd, "a ProjectReference in 'implementedProjects' has a value other than 'projectId'");
		}
		if (pd.getTestedProjects().stream().anyMatch(dep -> hasUnsupportedProperty(dep))) {
			reportWarning(pd, "a ProjectDependency in 'testedProjects' has a value other than 'projectId'");
		}
		if (execModule != null && execModule.getSourcePath() != null) {
			reportWarning(pd, "non-null property 'sourcePath' in BootstrapModule of execModule");
		}
		if (initModules.stream().anyMatch(m -> m.getSourcePath() != null)) {
			reportWarning(pd, "non-null property 'sourcePath' in BootstrapModule of initModules");
		}
		return result;
	}

	private static JSONObject createSourcesValue(ProjectDescription pd) {
		JSONObject result = JSONFactory.eINSTANCE.createJSONObject();
		List<String> sourcePaths = new ArrayList<>();
		List<String> externalPaths = new ArrayList<>();
		List<String> testPaths = new ArrayList<>();
		for (SourceContainerDescription scd : pd.getSourceContainers()) {
			switch (scd.getSourceContainerType()) {
			case SOURCE:
				sourcePaths.addAll(scd.getPaths());
				break;
			case EXTERNAL:
				externalPaths.addAll(scd.getPaths());
				break;
			case TEST:
				testPaths.addAll(scd.getPaths());
				break;
			}
		}
		addProperty(result, getEnumAsString(SourceContainerType.SOURCE), sourcePaths);
		addProperty(result, getEnumAsString(SourceContainerType.EXTERNAL), externalPaths);
		addProperty(result, getEnumAsString(SourceContainerType.TEST), testPaths);
		return result;
	}

	private static JSONObject createModuleFiltersValue(ProjectDescription pd) {
		JSONObject result = JSONFactory.eINSTANCE.createJSONObject();
		List<ModuleFilterSpecifier> noValidate = new ArrayList<>();
		List<ModuleFilterSpecifier> noModuleWrapping = new ArrayList<>();
		for (ModuleFilter mf : pd.getModuleFilters()) {
			switch (mf.getModuleFilterType()) {
			case NO_VALIDATE:
				noValidate.addAll(mf.getModuleSpecifiers());
				break;
			case NO_MODULE_WRAPPING:
				noModuleWrapping.addAll(mf.getModuleSpecifiers());
				break;
			}
		}
		addProperty(result, getEnumAsString(ModuleFilterType.NO_VALIDATE),
				createModuleFilterSpecifierValue(noValidate));
		addProperty(result, getEnumAsString(ModuleFilterType.NO_MODULE_WRAPPING),
				createModuleFilterSpecifierValue(noModuleWrapping));
		return result;
	}

	private static JSONArray createModuleFilterSpecifierValue(List<ModuleFilterSpecifier> mfs) {
		if (mfs == null) {
			return null;
		}
		return createArray(mfs.stream()
				.map(ProjectDescriptionExporter::createModuleFilterSpecifierValue)
				.filter(str -> str != null)
				.collect(Collectors.toList()));
	}

	private static JSONValue createModuleFilterSpecifierValue(ModuleFilterSpecifier mfs) {
		String sourcePath = mfs.getSourcePath();
		if (sourcePath == null || sourcePath.isEmpty()) {
			return createStringLiteral(mfs.getModuleSpecifierWithWildcard());
		} else {
			JSONObject result = JSONFactory.eINSTANCE.createJSONObject();
			addProperty(result, ProjectDescriptionHelper.PROP__MODULE, mfs.getModuleSpecifierWithWildcard());
			addProperty(result, ProjectDescriptionHelper.PROP__SOURCE_CONTAINER, sourcePath);
			return result;
		}
	}

	private static void assertProperty(JSONObject obj, String key, String value, ProjectDescription pd) {
		if (value == null) {
			reportWarning(pd, "TEMP: value is null for property " + key);
			return;
		}
		NameValuePair matchingPair = obj.getNameValuePairs().stream()
				.filter(pair -> key.equals(pair.getName()))
				.findFirst().orElse(null);
		if (matchingPair != null) {
			JSONValue actualValue = matchingPair.getValue();
			if (!(actualValue instanceof JSONStringLiteral
					&& value.equals(((JSONStringLiteral) actualValue).getValue()))) {
				reportWarning(pd, "mismatch in property '" + key + "'; "
						+ "expected: " + value + "; actual: " + actualValue);
			}
		} else {
			reportWarning(pd, "TEMP: property '" + key + "' missing; expected value was: " + value);
		}

	}

	private static void mergeProperty(JSONObject obj, String key, String value, ProjectDescription pd) {
		if (value == null) {
			return;
		}
		NameValuePair matchingPair = obj.getNameValuePairs().stream()
				.filter(pair -> key.equals(pair.getName()))
				.findFirst().orElse(null);
		if (matchingPair != null) {
			JSONValue actualValue = matchingPair.getValue();
			if (!(actualValue instanceof JSONStringLiteral
					&& value.equals(((JSONStringLiteral) actualValue).getValue()))) {
				reportWarning(pd, "mismatch in property '" + key + "'; "
						+ "expected: " + value + "; actual: " + actualValue);
			}
		} else {
			addProperty(obj, key, createStringLiteral(value), true);
		}
	}

	private static void addProperty(JSONObject obj, String key, String value) {
		addProperty(obj, key, createStringLiteral(value));
	}

	private static void addProperty(JSONObject obj, String key, List<String> values) {
		if (values == null || values.isEmpty()) {
			return;
		}
		JSONArray value = createArray(
				values.stream().map(ProjectDescriptionExporter::createStringLiteral).collect(Collectors.toList()));
		addProperty(obj, key, value);
	}

	private static void addProperty(JSONObject obj, String key, JSONValue value) {
		addProperty(obj, key, value, false);
	}

	private static void addProperty(JSONObject obj, String key, JSONValue value, boolean insertAtBeginning) {
		if (value == null
				|| (value instanceof JSONArray && ((JSONArray) value).getElements().isEmpty())
				|| (value instanceof JSONObject && ((JSONObject) value).getNameValuePairs().isEmpty())) {
			return;
		}
		NameValuePair pair = JSONFactory.eINSTANCE.createNameValuePair();
		pair.setName(key);
		pair.setValue(value);
		if (insertAtBeginning && !obj.getNameValuePairs().isEmpty()) {
			obj.getNameValuePairs().add(0, pair);
		} else {
			obj.getNameValuePairs().add(pair);
		}
	}

	private static JSONStringLiteral createStringLiteral(String str) {
		if (str == null) {
			return null;
		}
		JSONStringLiteral result = JSONFactory.eINSTANCE.createJSONStringLiteral();
		result.setValue(str);
		return result;
	}

	private static JSONArray createArray(List<JSONValue> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		JSONArray result = JSONFactory.eINSTANCE.createJSONArray();
		result.getElements().addAll(values);
		return result;
	}

	private static final String getVersionString(DeclaredVersion version) {
		if (version == null) {
			return null;
		}
		return version.getMajor()
				+ "." + version.getMinor()
				+ "." + version.getMicro()
				+ (version.getQualifier() != null ? "-" + version.getQualifier() : "");
	}

	private static final String getProjectReferenceAsString(ProjectReference pref) {
		if (pref == null) {
			return null;
		}
		return pref.getProjectId(); // TODO vendorId
	}

	private static final List<String> getProjectReferencesAsStrings(List<ProjectReference> prefs) {
		return prefs.stream()
				.map(ProjectDescriptionExporter::getProjectReferenceAsString)
				.filter(str -> !Strings.isNullOrEmpty(str))
				.collect(Collectors.toList());
	}

	private static final boolean hasUnsupportedProperty(ProjectReference ref) {
		if (ref == null) {
			return false;
		}
		if (ref.getDeclaredVendorId() != null) {
			System.err.println(ref);
			return true;
		}
		if (ref instanceof ProjectDependency) {
			ProjectDependency dep = (ProjectDependency) ref;
			if (dep.getVersionConstraint() != null
					|| (dep.getDeclaredScope() != null && dep.getDeclaredScope() != ProjectDependencyScope.COMPILE)) {
				System.err.println(dep);
				return true;
			}
		}
		return false;
	}

	// NOTE: for now we ignore RequiredRuntimeLibraries, etc. here
	private static final Set<String> collectDependencies(ProjectDescription pd) {
		return Stream.concat(pd.getProjectDependencies().stream(), pd.getTestedProjects().stream())
				.map(dep -> dep.getProjectId())
				.filter(depStr -> !Strings.isNullOrEmpty(depStr))
				.collect(Collectors.toSet());
	}

	private static final Set<String> collectDependencies(JSONObject root) {
		return root.getNameValuePairs().stream()
				.filter(pair -> ProjectDescriptionHelper.PROP__DEPENDENCIES.equals(pair.getName())
						|| ProjectDescriptionHelper.PROP__DEV_DEPENDENCIES.equals(pair.getName()))
				.map(pair -> pair.getValue())
				.filter(value -> value instanceof JSONObject)
				.flatMap(value -> ((JSONObject) value).getNameValuePairs().stream())
				.map(pair -> pair.getName())
				.filter(depStr -> !Strings.isNullOrEmpty(depStr))
				.collect(Collectors.toSet());
	}

	private static final String getEnumAsString(Enumerator enumValue) {
		if (enumValue == null) {
			return null;
		}
		if (enumValue == ProjectType.RUNTIME_ENVIRONMENT)
			return "runtimeEnvironment";
		if (enumValue == ProjectType.RUNTIME_LIBRARY)
			return "runtimeLibrary";
		if (enumValue == ModuleFilterType.NO_VALIDATE)
			return "noValidate";
		if (enumValue == ModuleFilterType.NO_MODULE_WRAPPING)
			return "noModuleWrapping";
		return enumValue.getName().toLowerCase();
	}

	private static final class JSONExporter {
		static final String INDENT = "  ";

		final Writer w;

		int indentLevel;

		public static void export(Writer w, JSONValue value, int indentLevel) throws IOException {
			JSONExporter e = new JSONExporter(w, indentLevel);
			e.write(value);
			// e.newLine(); // TODO reconsider
		}

		private JSONExporter(Writer w, int indentLevel) {
			this.w = w;
			this.indentLevel = indentLevel;
		}

		/** Changes current indent level by given delta (may be positive or negative or 0). */
		private void indent(int delta) {
			indentLevel += delta;
		}

		private void write(JSONValue value) throws IOException {
			if (value instanceof JSONNullLiteral) {
				write("null");
			} else if (value instanceof JSONBooleanLiteral) {
				write(((JSONBooleanLiteral) value).isBooleanValue() ? "true" : "false");
			} else if (value instanceof JSONNumericLiteral) {
				write(((JSONNumericLiteral) value).getValue().toString()); // TODO luca fragen ob toString hier ok
			} else if (value instanceof JSONStringLiteral) {
				write(quote(((JSONStringLiteral) value).getValue()));
			} else if (value instanceof JSONArray) {
				write((JSONArray) value);
			} else if (value instanceof JSONObject) {
				write((JSONObject) value);
			}
		}

		private void write(JSONArray arr) throws IOException {
			if (arr.getElements().isEmpty()) {
				write("[]");
				return;
			}
			write("[");
			newLine();
			indent(1);
			Iterator<JSONValue> iter = arr.getElements().iterator();
			while (iter.hasNext()) {
				JSONValue innerValue = iter.next();
				writeIndent();
				write(innerValue);
				if (iter.hasNext())
					write(",");
				newLine();
			}
			indent(-1);
			writeIndent();
			write("]");
		}

		private void write(JSONObject obj) throws IOException {
			if (obj.getNameValuePairs().isEmpty()) {
				write("{}");
				return;
			}
			write("{");
			newLine();
			indent(1);
			Iterator<NameValuePair> iter = obj.getNameValuePairs().iterator();
			while (iter.hasNext()) {
				NameValuePair pair = iter.next();
				writeIndent();
				write(quote(pair.getName()));
				write(": ");
				write(pair.getValue());
				if (iter.hasNext())
					write(",");
				newLine();
			}
			indent(-1);
			writeIndent();
			write("}");
		}

		private void write(String str) throws IOException {
			if (w == null) {
				System.out.print(str);
			} else {
				w.write(str);
			}
		}

		private void writeIndent() throws IOException {
			for (int i = 0; i < indentLevel; i++)
				write(INDENT);
		}

		private void newLine() throws IOException {
			if (w == null) {
				System.out.println();
			} else {
				w.write(System.lineSeparator());
			}
		}

		private static final String quote(String str) {
			if (str == null) {
				return null;
			}
			return "\"" + str + "\"";
		}
	}
}
