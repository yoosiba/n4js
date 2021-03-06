// Generated by N4JS transpiler; for copyright see original N4JS source file.

(function(System) {
	'use strict';
	System.register([
		'org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/TestController',
		'org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/IInstrumentedTest',
		'org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/TestFunctionType',
		'org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/TestMethodDescriptor'
	], function($n4Export) {
		var TestController, IInstrumentedTest, TestFunctionType, TestMethodDescriptor, InstrumentedTest;
		InstrumentedTest = function InstrumentedTest(testClass, info, testObject, parameterizedName, parameterizedTests) {
			this.tests = [];
			this.beforeAlls = [];
			this.afterAlls = [];
			this.befores = [];
			this.afters = [];
			this.fqn = "";
			this.child = null;
			this.error = undefined;
			this.classIgnoreAnnotation = undefined;
			IInstrumentedTest.$fieldInit.call(this, undefined, {
				tests: undefined,
				beforeAlls: undefined,
				afterAlls: undefined,
				befores: undefined,
				afters: undefined,
				fqn: undefined,
				child: undefined,
				error: undefined,
				classIgnoreAnnotation: undefined
			});
			if (testClass) {
				this.load(testClass, info);
				this.parameterizedName = parameterizedName;
				this.setTestObject(testObject);
				if (info && info.testMethods) {
					this.filterTests(info.testMethods);
				}
			}
			if (parameterizedTests) {
				this.hasParameterizedTests = true;
				this.parameterizedTests = parameterizedTests;
			}
		};
		$n4Export('InstrumentedTest', InstrumentedTest);
		return {
			setters: [
				function($exports) {
					// org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/TestController
					TestController = $exports.TestController;
				},
				function($exports) {
					// org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/IInstrumentedTest
					IInstrumentedTest = $exports.IInstrumentedTest;
				},
				function($exports) {
					// org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/TestFunctionType
					TestFunctionType = $exports.TestFunctionType;
				},
				function($exports) {
					// org.eclipse.n4js.mangelhaft/src-gen/org/eclipse/n4js/mangelhaft/types/TestMethodDescriptor
					TestMethodDescriptor = $exports.TestMethodDescriptor;
				}
			],
			execute: function() {
				$makeClass(InstrumentedTest, N4Object, [
					IInstrumentedTest
				], {
					filterTests: {
						value: function filterTests___n4(testNames) {
							this.tests = this.tests.filter(function(test) {
								return testNames.indexOf(test.name) !== -1;
							});
						}
					},
					getFixmeScope: {
						value: function getFixmeScope___n4(fixme) {
							let scopes;
							if (fixme) {
								let scope;
								if (fixme.details.length > 1) {
									scope = fixme.details[1];
								}
								if (scope) {
									scopes = new Set(scope.split(",").map((scope)=>scope.trim()));
								}
							}
							return scopes;
						}
					},
					getTestMethodDescriptors: {
						value: function getTestMethodDescriptors___n4(meths, tftype) {
							return meths.map((methodDescriptor)=>{
								const desc = methodDescriptor.anyAnnotation("Description");
								const details = desc ? desc.details : [];
								const fixmeAnnotation = methodDescriptor.anyAnnotation("Fixme");
								const ignoreAnnotation = this.classIgnoreAnnotation ? this.classIgnoreAnnotation : methodDescriptor.anyAnnotation("Ignore");
								const timeoutAnnotation = methodDescriptor.anyAnnotation("Timeout");
								return new TestMethodDescriptor({
									timeout: timeoutAnnotation && timeoutAnnotation.details ? parseInt(timeoutAnnotation.details.pop()) : 60 * 1000,
									description: details.length ? details.join(" ") : "",
									ignore: !!ignoreAnnotation,
									ignoreReason: ignoreAnnotation ? ignoreAnnotation.details.join(" ") : "",
									fixme: !!fixmeAnnotation,
									fixmeReason: fixmeAnnotation ? fixmeAnnotation.details.join(" ") : "",
									fixmeScopes: this.getFixmeScope(fixmeAnnotation),
									name: methodDescriptor.name,
									value: methodDescriptor.jsFunction,
									type: tftype
								});
							});
						}
					},
					setTestObject: {
						value: function setTestObject___n4(test) {
							this.testObject = test;
							return this;
						}
					},
					setError: {
						value: function setError___n4(error) {
							this.error = error;
							return this;
						}
					},
					load: {
						value: function load___n4(testClass, info) {
							this.classIgnoreAnnotation = testClass.n4type.allAnnotations("Ignore")[0];
							this.beforeAlls = this.getTestMethodDescriptors(testClass.n4type.methodsWithAnnotation("BeforeAll", true, false, false), TestFunctionType.BEFORE_ALL);
							this.afterAlls = this.getTestMethodDescriptors(testClass.n4type.methodsWithAnnotation("AfterAll", true, false, false), TestFunctionType.AFTER_ALL);
							this.befores = this.getTestMethodDescriptors(testClass.n4type.methodsWithAnnotation("Before", true, false, false), TestFunctionType.BEFORE_TEST);
							this.afters = this.getTestMethodDescriptors(testClass.n4type.methodsWithAnnotation("After", true, false, false), TestFunctionType.AFTER_TEST);
							this.tests = this.getTestMethodDescriptors(testClass.n4type.methodsWithAnnotation("Test", true, true, false), TestFunctionType.TEST);
							if (info) {
								this.fqn = info.fqn;
							}
							this.fqn = this.fqn || testClass.n4type.fqn;
							this.name = this.fqn;
							if (info && info.testMethods && info.testMethods.length) {
								this.tests = this.tests.filter(function(test) {
									return info.testMethods.indexOf(test.name) !== -1;
								});
							}
							let parentClass = Object.getPrototypeOf(testClass);
							let parentClassFn = parentClass;
							if (parentClassFn !== Object) {
								this.parent = new InstrumentedTest().load(parentClass);
								this.parent.child = this;
							}
							return this;
						}
					},
					tests: {
						value: undefined,
						writable: true
					},
					beforeAlls: {
						value: undefined,
						writable: true
					},
					afterAlls: {
						value: undefined,
						writable: true
					},
					befores: {
						value: undefined,
						writable: true
					},
					afters: {
						value: undefined,
						writable: true
					},
					fqn: {
						value: undefined,
						writable: true
					},
					child: {
						value: undefined,
						writable: true
					},
					error: {
						value: undefined,
						writable: true
					},
					classIgnoreAnnotation: {
						value: undefined,
						writable: true
					},
					parent: {
						value: undefined,
						writable: true
					},
					hasParameterizedTests: {
						value: undefined,
						writable: true
					},
					parameterizedTests: {
						value: undefined,
						writable: true
					},
					testObject: {
						value: undefined,
						writable: true
					},
					name: {
						value: undefined,
						writable: true
					},
					parameterizedName: {
						value: undefined,
						writable: true
					}
				}, {
					getParameterizedFields: {
						value: function getParameterizedFields___n4(testClass) {
							let parameterizedFields = new Map();
							for(let field of testClass.n4type.dataFieldsWithAnnotation("Parameter", true, true, false)) {
								let [
									indexStr
								] = field.anyAnnotation("Parameter").details || [
									""
								];
								let argNum = Number.parseInt(indexStr) || 0;
								parameterizedFields.set(argNum, field.name);
							}
							return parameterizedFields;
						}
					},
					getParameterizedInstrumentedTests: {
						value: function getParameterizedInstrumentedTests___n4(testClass, info, testInjector, parameterGroups, nameTemplate) {
							nameTemplate = nameTemplate || "{index}";
							let tests = [];
							let parameterizedFields = this.getParameterizedFields(testClass);
							if (parameterGroups && parameterGroups.length) {
								let ii = 0;
								for(let pGroup of parameterGroups) {
									let testObject = testInjector.create(testClass);
									let jj = 0;
									for(let parm of pGroup) {
										if (parameterizedFields.has(jj)) {
											(testObject)[parameterizedFields.get(jj)] = parm;
										}
										++jj;
									}
									let parameterizedName = nameTemplate.replace(/{(index|[0-9]*)}/g, (match, item)=>{
										if (item === "index") {
											return ii;
										} else {
											let paramNumber = Number.parseInt(item);
											return pGroup[paramNumber] || "";
										}
									});
									tests.push(new InstrumentedTest(testClass, info, testObject, parameterizedName));
									++ii;
								}
							} else {
								tests.push(new InstrumentedTest(testClass, info, testInjector.create(testClass)));
							}
							return tests;
						}
					},
					getInstrumentedTest: {
						value: function getInstrumentedTest___n4(testClass, info, testInjector, controller) {
							let parameters = null;
							let nameTemplate = null;
							let pMeth = testClass.n4type.methodsWithAnnotation("Parameters", true, true, true).pop();
							if (pMeth) {
								let anno = pMeth.anyAnnotation("Parameters");
								[
									nameTemplate
								] = anno.details || [
									""
								];
								parameters = pMeth.jsFunction.call(testClass);
							}
							let parameterizedTests;
							if (parameters) {
								parameterizedTests = this.getParameterizedInstrumentedTests(testClass, info, testInjector, parameters, nameTemplate);
							}
							const instance = testInjector.internalCreate(testClass, testInjector, new Map([
								[
									`${TestController.n4type.origin}${TestController.n4type.fqn}`,
									controller
								]
							]));
							return new InstrumentedTest(testClass, info, instance, null, parameterizedTests);
						}
					}
				}, function(instanceProto, staticProto) {
					var metaClass = new N4Class({
						name: 'InstrumentedTest',
						origin: 'org.eclipse.n4js.mangelhaft',
						fqn: 'org.eclipse.n4js.mangelhaft.InstrumentedTest.InstrumentedTest',
						n4superType: N4Object.n4type,
						allImplementedInterfaces: [
							'org.eclipse.n4js.mangelhaft.types.IInstrumentedTest.IInstrumentedTest'
						],
						ownedMembers: [
							new N4DataField({
								name: 'tests',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'beforeAlls',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'afterAlls',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'befores',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'afters',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'fqn',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'child',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'error',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'classIgnoreAnnotation',
								isStatic: false,
								annotations: []
							}),
							new N4Method({
								name: 'filterTests',
								isStatic: false,
								jsFunction: instanceProto['filterTests'],
								annotations: []
							}),
							new N4Method({
								name: 'constructor',
								isStatic: false,
								jsFunction: instanceProto['constructor'],
								annotations: []
							}),
							new N4Method({
								name: 'getParameterizedFields',
								isStatic: true,
								jsFunction: staticProto['getParameterizedFields'],
								annotations: []
							}),
							new N4Method({
								name: 'getParameterizedInstrumentedTests',
								isStatic: true,
								jsFunction: staticProto['getParameterizedInstrumentedTests'],
								annotations: []
							}),
							new N4Method({
								name: 'getInstrumentedTest',
								isStatic: true,
								jsFunction: staticProto['getInstrumentedTest'],
								annotations: []
							}),
							new N4Method({
								name: 'getFixmeScope',
								isStatic: false,
								jsFunction: instanceProto['getFixmeScope'],
								annotations: []
							}),
							new N4Method({
								name: 'getTestMethodDescriptors',
								isStatic: false,
								jsFunction: instanceProto['getTestMethodDescriptors'],
								annotations: []
							}),
							new N4Method({
								name: 'setTestObject',
								isStatic: false,
								jsFunction: instanceProto['setTestObject'],
								annotations: []
							}),
							new N4Method({
								name: 'setError',
								isStatic: false,
								jsFunction: instanceProto['setError'],
								annotations: []
							}),
							new N4Method({
								name: 'load',
								isStatic: false,
								jsFunction: instanceProto['load'],
								annotations: []
							})
						],
						consumedMembers: [
							new N4DataField({
								name: 'parent',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'hasParameterizedTests',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'parameterizedTests',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'testObject',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'name',
								isStatic: false,
								annotations: []
							}),
							new N4DataField({
								name: 'parameterizedName',
								isStatic: false,
								annotations: []
							})
						],
						annotations: []
					});
					return metaClass;
				});
			}
		};
	});
})(typeof module !== 'undefined' && module.exports ? require('n4js-node').System(require, module) : System);
//# sourceMappingURL=InstrumentedTest.map
