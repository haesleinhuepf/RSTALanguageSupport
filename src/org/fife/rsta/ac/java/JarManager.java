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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Manages a list of jars and gets completions from them.  This can be shared
 * amongst multiple {@link JavaCompletionProvider} instances.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JarManager {

	/**
	 * Jars to get completions from.
	 */
	private List jars;


	/**
	 * Constructor.
	 */
	public JarManager() {
		jars = new ArrayList();
	}


	/**
	 * Adds completions matching the specified text to a list.
	 *
	 * @param p The parent completion provider.
	 * @param text The text to match.
	 * @param addTo The list to add completion choices to.
	 */
	public void addCompletions(CompletionProvider p, String text, Set addTo) {
		String[] pkgNames = Util.splitOnChar(text, '.');
		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			jar.addCompletions(p, pkgNames, addTo);
		}
	}


	/**
	 * Adds a jar to read from.
	 *
	 * @param info The jar to add.  If this is <code>null</code>, then
	 *        the current JVM's main JRE jar (rt.jar, or classes.jar on OS X)
	 *        will be added.  If this jar has already been added, adding it
	 *        again will do nothing (except possibly update its attached source
	 *        location).
	 * @return Whether this jar was added (e.g. it wasn't already loaded, or
	 *         it has a new source path).
	 * @throws IOException If an IO error occurs.
	 * @see #getJars()
	 * @see #removeJar(File)
	 */
	public boolean addJar(JarInfo info) throws IOException {

		// First see if this jar is already on the "build path."
		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			JarInfo info2 = jar.getJarInfo();
			if (info2.equals(info)) {
				// Only update if the source location is different.
				File source = info.getSourceLocation();
				File source2 = info2.getSourceLocation();
				if ((source==null && source2!=null) ||
						(source!=null && !source.equals(source2))) {
					jars.set(i, new JarReader((JarInfo)info.clone()));
					return true;
				}
				return false;
			}
		}

		// If it isn't on the build path, add it now.
		jars.add(new JarReader(info));
		return true;

	}


	/**
	 * Removes all jars from the "build path."
	 *
	 * @see #removeJar(File)
	 * @see #addJar(JarInfo)
	 * @see #getJars()
	 */
	public void clearJars() {
		jars.clear();
	}


	public ClassFile getClassEntry(String className) {

		String[] items = Util.splitOnChar(className, '.');

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			ClassFile cf = jar.getClassEntry(items);
			if (cf!=null) {
				return cf;
			}
		}

		return null;

	}


	/**
	 * 
	 * @param pkgName A package name.
	 * @return A list of all classes in that package.
	 */
	public List getClassesInPackage(String pkgName, boolean inPkg) {

		List list = new ArrayList();
		String[] pkgs = Util.splitOnChar(pkgName, '.');

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			jar.getClassesInPackage(list, pkgs, inPkg);
		}

		return list;

	}


	/**
	 * Returns the jars on the "build path."
	 *
	 * @return A list of {@link JarInfo}s.  Modifying a <tt>JarInfo</tt> in
	 *         this list will have no effect on this completion provider; in
	 *         order to do that, you must re-add the jar via
	 *         {@link #addJar(JarInfo)}. If there are no jars on the
	 *         "build path," this will be an empty list.
	 * @see #addJar(JarInfo)
	 */
	public List getJars() {
		List jarList = new ArrayList(jars.size());
		for (Iterator i=jars.iterator(); i.hasNext(); ) {
			JarReader reader = (JarReader)i.next();
			jarList.add(reader.getJarInfo().clone());
		}
		return jarList;
	}


	public SortedMap getPackageEntry(String pkgName) {

		String[] pkgs = Util.splitOnChar(pkgName, '.');

		SortedMap map = new TreeMap();

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			SortedMap map2 = jar.getPackageEntry(pkgs);
			if (map2!=null) {
				mergeMaps(map, map2);
			}
		}

		return map;

	}


public File getSourceLocForClass(String className) {
	File sourceLoc = null;
	for (int i=0; i<jars.size(); i++) {
		JarReader jar = (JarReader)jars.get(i);
		if (jar.containsClass(className)) {
			sourceLoc = jar.getJarInfo().getSourceLocation();
			break;
		}
	}
	return sourceLoc;
}


	private void mergeMaps(SortedMap map, SortedMap toAdd) {

		for (Iterator i=toAdd.entrySet().iterator(); i.hasNext(); ) {

			Map.Entry entry = (Map.Entry)i.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (map.containsKey(key)) {
				if ((map.get(key) instanceof SortedMap) &&
						(value instanceof SortedMap)) {
					mergeMaps((SortedMap)map.get(key), (SortedMap)value);
				}
			}

			else {
				map.put(key, value);
			}

		}

	}


	/**
	 * Removes a jar from the "build path."
	 *
	 * @param jar The jar to remove.
	 * @return Whether the jar was removed.  This will be <code>false</code>
	 *         if the jar was not on the build path.
	 * @see #addJar(JarInfo)
	 * @see #getJars()
	 */
	public boolean removeJar(File jar) {
		for (Iterator i=jars.iterator(); i.hasNext(); ) {
			JarInfo info = (JarInfo)i.next();
			File jar2 = info.getJarFile();
			if (jar.equals(jar2)) {
				i.remove();
				return true;
			}
		}
		return false;
	}


}