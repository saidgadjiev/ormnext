/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package ru.saidgadjiev.orm.next.core.exception;

public class InstantiationException extends RuntimeException {
	private final Class clazz;

	public InstantiationException(String message, Class clazz, Throwable cause) {
		super( message, cause );
		this.clazz = clazz;
	}

	public InstantiationException(String message, Class clazz) {
		this( message, clazz, null );
	}

	public InstantiationException(String message, Class clazz, Exception cause) {
		super( message, cause );
		this.clazz = clazz;
	}

	public Class getUninstantiatableClass() {
		return clazz;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " : " + clazz.getName();
	}

}
