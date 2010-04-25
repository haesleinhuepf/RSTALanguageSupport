/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;


/**
 * Cell renderer for Java auto-completion.  This renderer attempts to be
 * fast due to the possibility of many (100+) auto-completions dynamically
 * generated for large Java classes.  Using Swing's HTML support is simply
 * too slow (see {@link CompletionCellRenderer}).<p>
 *
 * The color scheme for this renderer mimics that found in Eclipse.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaCellRenderer extends DefaultListCellRenderer {

	private JList list;
	private boolean selected;
	private JavaSourceCompletion jsc;

	/**
	 * Whether we're painting this row using the alternate background color.
	 */
	private boolean usingAltBG;

	/**
	 * The alternating background color, or <code>null</code> for none.
	 */
	private Color altBG = new java.awt.Color(240,240,240);

	/**
	 * This is used instead of jsc for "incomplete" stuff, like classes,
	 * interfaces, etc. read from jars (don't yet read the class in, for
	 * example).
	 */
	private BasicCompletion bc;


	/**
	 * Returns the background color to use on alternating lines.
	 *
	 * @return The alternate background color.  If this is <code>null</code>,
	 *         alternating colors are not used.
	 * @see #setAlternateBackground(Color)
	 */
	public Color getAlternateBackground() {
		return altBG;
	}


	/**
	 * Returns the renderer.
	 *
	 * @param list The list of choices being rendered.
	 * @param value The {@link Completion} being rendered.
	 * @param index The index into <code>list</code> being rendered.
	 * @param selected Whether the item is selected.
	 * @param hasFocus Whether the item has focus.
	 */
	public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean selected, boolean hasFocus) {

		super.getListCellRendererComponent(list, value, index, selected, hasFocus);

		setText("Foobar"); // Just something to give it proper height
		this.list = list;
		this.selected = selected;

		if (value instanceof JavaSourceCompletion) {
			jsc = (JavaSourceCompletion)value;
			bc = null;
			setIcon(jsc.getIcon());
		}
		else {
			jsc = null;
			bc = (BasicCompletion)value;
			setIcon(null); // TODO: emptyIcon
		}

		if (altBG!=null && !selected && (index&1)==0) {
			usingAltBG = true;
			setBackground(altBG);
		}
		else {
			usingAltBG = false;
		}

		return this;

	}


	protected void paintComponent(Graphics g) {

//		if (jsc!=null) {
//			setText(null); // Stop "Foobar" from being painted
//		}

		//super.paintComponent(g);
		// We never paint "selection" around the icon, to imitate Eclipse
		g.setColor(usingAltBG ? altBG : list.getBackground());
		int iconW = 18;
		g.fillRect(0, 0, iconW, getHeight());
		if (getIcon()!=null) {
			getIcon().paintIcon(this, g, 0, 0);
		}
		g.setColor(getBackground());
		g.fillRect(iconW,0, getWidth()-iconW,getHeight());

		if (jsc!=null) {
			int x = getX() + iconW + 2;
			g.setColor(selected ? list.getSelectionForeground() :
									list.getForeground());
			jsc.rendererText(g, x, g.getFontMetrics().getHeight(), selected);
		}
		else if (bc!=null) {
			int x = getX() + iconW + 2;
			g.setColor(selected ? list.getSelectionForeground() :
									list.getForeground());
			g.drawString(bc.toString(), x, g.getFontMetrics().getHeight());
		}

	}


	/**
	 * Sets the background color to use on alternating lines.
	 *
	 * @param altBG The new alternate background color.  If this is
	 *        <code>null</code>, alternating lines will not use different
	 *        background colors.
	 * @see #getAlternateBackground()
	 */
	public void setAlternateBackground(Color altBG) {
		this.altBG = altBG;
	}


}