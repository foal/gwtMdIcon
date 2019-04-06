package org.jresearch.commons.gwt.mdIcon;

public enum Mode {
	/**
	 * Generate only interface with icon initialization (new in 1.1 mode is the same
	 * as original interface in DominoUI)
	 */
	ORIG,
	/**
	 * Generate only interface with icon initialization, all meta and collection of
	 * all icons all on client side (original mode from version 1.0)
	 */
	FULL_CLIENT,
}
