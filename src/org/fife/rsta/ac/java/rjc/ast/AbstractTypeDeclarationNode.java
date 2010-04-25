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
package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lexer.Offset;


public abstract class AbstractTypeDeclarationNode extends AbstractASTNode
												implements TypeDeclaration {

	private Modifiers modifiers;
	private List childTypes;
	private Offset bodyStartOffs;
	private Offset bodyEndOffs;
	private boolean deprecated;
	private String docComment;

	// --- "ClassBody"/"InterfaceBody"/EnumConstant fields ---
	private List memberList;


	public AbstractTypeDeclarationNode(String name, Offset start) {
		super(name, start);
		init();
	}


	public AbstractTypeDeclarationNode(String name, Offset start, Offset end) {
		super(name, start, end);
		init();
	}


	public void addMember(Member member) {
		memberList.add(member);
	}


	public void addTypeDeclaration(TypeDeclaration type) {
		if (childTypes==null) {
			childTypes = new ArrayList(1); // Usually small
		}
		childTypes.add(type);
	}


	public int getBodyEndOffset() {
		return bodyEndOffs!=null ? bodyEndOffs.getOffset() : Integer.MAX_VALUE;
	}


	public int getBodyStartOffset() {
		return bodyStartOffs==null ? 0 : bodyStartOffs.getOffset();
	}


	public TypeDeclaration getChildType(int index) {
		return (TypeDeclaration)childTypes.get(index);
	}


	public int getChildTypeCount() {
		return childTypes==null ? 0 : childTypes.size();
	}


	public String getDocComment() {
		return docComment;
	}


	public int getMemberCount() {
		return memberList.size();
	}


	public Iterator getMemberIterator() {
		return memberList.iterator();
	}


	public Modifiers getModifiers() {
		return modifiers;
	}


	private void init() {
		memberList = new ArrayList();
	}


	public boolean isDeprecated() {
		return deprecated;
	}


	public void setBodyEndOffset(Offset end) {
		bodyEndOffs = end;
	}


	public void setBodyStartOffset(Offset start) {
		bodyStartOffs = start;
	}


	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}


	public void setDocComment(String comment) {
		docComment = comment;
	}


	public void setModifiers(Modifiers modifiers) {
		this.modifiers = modifiers;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (modifiers!=null) {
			sb.append(modifiers.toString()).append(' ');
		}
		sb.append(getTypeString()).append(' ');
		sb.append(getName());
		return sb.toString();
	}


}