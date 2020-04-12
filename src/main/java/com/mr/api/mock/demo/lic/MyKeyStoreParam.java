/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package com.mr.api.mock.demo.lic;

import de.schlichtherle.license.AbstractKeyStoreParam;
import de.schlichtherle.license.KeyStoreParam;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a convenience class implementing the {@link KeyStoreParam} interface.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class MyKeyStoreParam extends AbstractKeyStoreParam {

	private String alias, storePwd, keyPwd, pubPath;

	/**
	 * Constructs a new instance.
	 *
	 * @param clazz Used to retrieve the classloader required to load the
	 *        keystore as a resource.
	 * @param resource The resource identifier for the keystore
	 *        to be returned by {@link #getStream()}.
	 * @param alias The alias for the key entry in the key store
	 *        to be returned by {@link #getAlias()}.
	 * @param storePwd The key store password
	 *        to be returned by {@link #getStorePwd()}.
	 * @param keyPwd The password for the private key in the key store entry
	 *        to be returned by {@link #getStorePwd()}.
	 */
	public MyKeyStoreParam(
			final Class clazz,
			final String resource,
			final String alias,
			final String storePwd,
			final String keyPwd) {
		super(clazz, resource);
		this.alias = alias;
		this.storePwd = storePwd;
		this.keyPwd = keyPwd;
		this.pubPath = resource;
	}

	public String getAlias() {
		return alias;
	}

	public String getStorePwd() {
		return storePwd;
	}

	public String getKeyPwd() {
		return keyPwd;
	}


	/**
	 * Looks up the resource provided to the constructor using the classloader
	 * provided to the constructor and returns it as an {@link InputStream}.
	 */
	public InputStream getStream() throws IOException {
		final InputStream in = new FileInputStream(pubPath);
		if (null == in)
			throw new FileNotFoundException(pubPath);
		return in;
	}
}
