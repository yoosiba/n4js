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
package org.eclipse.n4js.n4JS.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.n4js.n4JS.N4JSPackage;
import org.eclipse.n4js.n4JS.StrictModeRelevant;

import org.eclipse.n4js.utils.emf.ProxyResolvingEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Strict Mode Relevant</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.n4js.n4JS.impl.StrictModeRelevantImpl#isStrictMode <em>Strict Mode</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class StrictModeRelevantImpl extends ProxyResolvingEObjectImpl implements StrictModeRelevant {
	/**
	 * The default value of the '{@link #isStrictMode() <em>Strict Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictMode()
	 * @generated
	 * @ordered
	 */
	protected static final boolean STRICT_MODE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStrictMode() <em>Strict Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictMode()
	 * @generated
	 * @ordered
	 */
	protected boolean strictMode = STRICT_MODE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StrictModeRelevantImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return N4JSPackage.Literals.STRICT_MODE_RELEVANT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isStrictMode() {
		return strictMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStrictMode(boolean newStrictMode) {
		boolean oldStrictMode = strictMode;
		strictMode = newStrictMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, N4JSPackage.STRICT_MODE_RELEVANT__STRICT_MODE, oldStrictMode, strictMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case N4JSPackage.STRICT_MODE_RELEVANT__STRICT_MODE:
				return isStrictMode();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case N4JSPackage.STRICT_MODE_RELEVANT__STRICT_MODE:
				setStrictMode((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case N4JSPackage.STRICT_MODE_RELEVANT__STRICT_MODE:
				setStrictMode(STRICT_MODE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case N4JSPackage.STRICT_MODE_RELEVANT__STRICT_MODE:
				return strictMode != STRICT_MODE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (strictMode: ");
		result.append(strictMode);
		result.append(')');
		return result.toString();
	}

} //StrictModeRelevantImpl
