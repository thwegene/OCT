/**
 * OCT Octree
 * A Processing library to create, modify and display Octree structures.
 * http://www.thomaswegener.ch/OCT
 *
 * Based on:
 * Hanan Samet, SmallerNbr finding in images represented by octrees
 * Computer Vision, Graphics, and Image Processing, Volume 46, Issue 3, June 1989, Pages 367-386
 * http://dx.doi.org/10.1016/0734-189X(89)90038-8.
 * (http://www.sciencedirect.com/science/article/pii/0734189X89900388)
 *
 * Copyright (C) 2015 Thomas Wegener
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Thomas Wegener
 * @modified    2015
 * @version     0.1 (1)
 */

package oct.octree;

// TODO correct casting with arrays from OCT_ENUM to <? extends OCT_ENUM> and remove getNbrs2

import java.util.ArrayList;

import oct.enums.OCT_EDGE;
import oct.enums.OCT_ENUM;
import oct.enums.OCT_FACE;
import oct.enums.OCT_OCTANT;
import oct.enums.OCT_VERTEX;
import oct.utils.OctTables;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * This is the basic unit of the octree.
 */
public class OctNode implements java.io.Serializable {

	// *************************************************************************************
	// VARIABLES
	// *************************************************************************************

	protected short codeR;
	protected short codeS;
	protected short codeT;
	protected byte level;
	protected boolean isSelected = false;

	// *************************************************************************************
	// CONSTRUCTORS
	// *************************************************************************************

	/**
	 * The position of the node in the tree is defined by its code (R, S, T
	 * coordinates) and its level. This is not the position is space (X, Y, Z
	 * coordinates) but the position in the data structure. At the level 3,
	 * there are 8x8x8 possible nodes, so the R, S, T coordinates can be an
	 * integer or a short between 0 and 7. Higher or lower integers are valid
	 * possible but will be out of bounds. Level can be between 0 and 127.
	 */
	public OctNode(short _codeR, short _codeS, short _codeT, byte _level) {
		codeR = _codeR;
		codeS = _codeS;
		codeT = _codeT;
		if (_level < 0 || _level > 127) {
			PApplet.println("ERROR in OctNode: level not in range. Level 0 was used.");
			_level = 0;
		}
		level = _level;
	}

	public OctNode(int _codeR, int _codeS, int _codeT, int _level) {
		codeR = (short) _codeR;
		codeS = (short) _codeS;
		codeT = (short) _codeT;
		if (_level < 0 || _level > 127) {
			PApplet.println("ERROR in OctNode: level not in range. Level 0 was used.");
			_level = 0;
		}
		level = (byte) _level;
	}

	public OctNode(PVector _code, int _level) {
		this.codeR = (short) _code.x;
		this.codeS = (short) _code.y;
		this.codeT = (short) _code.z;
		if (_level < 0 || _level > 127) {
			PApplet.println("ERROR in OctNode: level not in range. Level 0 was used.");
			_level = 0;
		}
		this.level = (byte) _level;
	}

	public OctNode(OctNode _tempNode) {
		codeR = _tempNode.codeR;
		codeS = _tempNode.codeS;
		codeT = _tempNode.codeT;
		level = _tempNode.level;
	}

	public OctNode() {
		codeR = (short) 0;
		codeS = (short) 0;
		codeT = (short) 0;
		level = (byte) 0;
	}
	
	// *************************************************************************************
	// SECTION GETTERS AND SETTERS
	// *************************************************************************************

	public short getLevel() {
		return level;
	}

	public void setLevel(byte _level) {
		if (_level < 0 || _level > 127) {
			PApplet.println("ERROR in setLevel(): level not in range. Level 0 was used.");
			_level = 0;
		}
		this.level = _level;
	}

	public short getCodeR() {
		return codeR;
	}

	public void setCodeR(short _codeR) {
		this.codeR = _codeR;
	}

	public short getCodeS() {
		return codeS;
	}

	public void setCodeS(short _codeS) {
		this.codeS = _codeS;
	}

	public short getCodeT() {
		return codeT;
	}

	public void setCodeT(short _codeT) {
		this.codeT = _codeT;
	}

	public void setCode(short _codeR, short _codeS, short _codeT) {
		this.codeR = _codeR;
		this.codeS = _codeS;
		this.codeT = _codeT;
	}

	public void setCode(int _codeR, int _codeS, int _codeT) {
		this.codeR = (short) _codeR;
		this.codeS = (short) _codeS;
		this.codeT = (short) _codeT;
	}
	
	public void setCode(PVector _code) {
		this.codeR = (short) _code.x;
		this.codeS = (short) _code.y;
		this.codeT = (short) _code.z;
	}

	public PVector getCode() {
		return new PVector(codeR, codeS, codeT);
	}

	// *************************************************************************************
	// RST GEOMETRY
	// *************************************************************************************

	/**
	 * Gets a specific vertex of the node. Can be converted to XYZ coordinated
	 * with '.toXYZ' function.
	 */
	public OctRST getVertex(OCT_VERTEX _v) {
		OctRST vLocation = new OctRST();
		float grid = 1f / (1 << level);
		vLocation.r = (float) (codeR + 0.5 + (_v.r / 2f)) * grid;
		vLocation.s = (float) (codeS + 0.5 + (_v.s / 2f)) * grid;
		vLocation.t = (float) (codeT + 0.5 + (_v.t / 2f)) * grid;
		return vLocation;
	}

	/**
	 * Gets all vertices of the node. Can be converted to XYZ coordinated with
	 * '.toXYZ' function.
	 */
	public ArrayList<OctRST> getVertices() {
		ArrayList<OctRST> tempVertexList = new ArrayList<OctRST>();
		for (OCT_VERTEX v : OCT_VERTEX.values()) {
			tempVertexList.add(getVertex(v));
		}
		return tempVertexList;
	}

	/**
	 * Gets a list of vertices of the node. Can be converted to XYZ coordinated
	 * with '.toXYZ' function.
	 */
	public ArrayList<OctRST> getVertices(ArrayList<OCT_VERTEX> _v) {
		ArrayList<OctRST> tempVertexList = new ArrayList<OctRST>();
		for (OCT_VERTEX v : _v) {
			tempVertexList.add(getVertex(v));
		}
		return tempVertexList;
	}

	/**
	 * Gets the center of the node. Can be converted to XYZ coordinates with
	 * '.toXYZ' function.
	 */
	public OctRST getCenter() {
		OctRST cLocation = new OctRST();
		float grid = 1f / (1 << level);
		// add 0.5 to take the node size into account
		cLocation.r = (float) (codeR + 0.5) * grid;
		cLocation.s = (float) (codeS + 0.5) * grid;
		cLocation.t = (float) (codeT + 0.5) * grid;
		return cLocation;
	}

	public OctRST getFaceCenter(OCT_FACE _f) {
		OctRST faceC = new OctRST();
		float grid = 1f / (1 << level);
		// add 0.5 to take the node size into account
		faceC.r = (float) (codeR + 0.5 + (_f.r / 2f)) * grid;
		faceC.s = (float) (codeS + 0.5 + (_f.s / 2f)) * grid;
		faceC.t = (float) (codeT + 0.5 + (_f.t / 2f)) * grid;
		return faceC;
	}

	public ArrayList<OctRST> getFaceCenters(ArrayList<OCT_FACE> _fList) {
		ArrayList<OctRST> faceCenters = new ArrayList<OctRST>();
		for (OCT_FACE f : _fList) {
			faceCenters.add(getFaceCenter(f));
		}
		return faceCenters;
	}

	public ArrayList<OctRST> getFaceCenters() {
		ArrayList<OctRST> faceCenters = new ArrayList<OctRST>();
		for (OCT_FACE f : OCT_FACE.getAll()) {
			faceCenters.add(getFaceCenter(f));
		}
		return faceCenters;
	}

	public OctRST getEdgeCenter(OCT_EDGE _e) {
		OctRST faceC = new OctRST();
		float grid = 1f / (1 << level);
		// add 0.5 to take the node size into account
		faceC.r = (float) (codeR + 0.5 + _e.r / 2f) * grid;
		faceC.s = (float) (codeS + 0.5 + _e.s / 2f) * grid;
		faceC.t = (float) (codeT + 0.5 + _e.t / 2f) * grid;
		return faceC;
	}

	public ArrayList<OctRST> getEdgeCenters(ArrayList<OCT_EDGE> _eList) {
		ArrayList<OctRST> edgeCenters = new ArrayList<OctRST>();
		for (OCT_EDGE e : _eList) {
			edgeCenters.add(getEdgeCenter(e));
		}
		return edgeCenters;
	}

	public ArrayList<OctRST> getEdgeCenters() {
		ArrayList<OctRST> edgeCenters = new ArrayList<OctRST>();
		for (OCT_EDGE e : OCT_EDGE.getAll()) {
			edgeCenters.add(getEdgeCenter(e));
		}
		return edgeCenters;
	}

	// *************************************************************************************
	// OCTANT AND SIBLINGS
	// *************************************************************************************

	/**
	 * Gets the octant value of the node. See 'oct.enum.OCT_OCTANT' for the list
	 * and naming.
	 */
	public OCT_OCTANT getOctant() {
		if ((codeR + 1) >> 1 == codeR >> 1) {
			if ((codeS + 1) >> 1 == codeS >> 1) {
				if ((codeT + 1) >> 1 == codeT >> 1) {
					return OCT_OCTANT.LBD;
				} else {
					return OCT_OCTANT.LBU;
				}
			} else {
				if ((codeT + 1) >> 1 == codeT >> 1) {
					return OCT_OCTANT.LFD;
				} else {
					return OCT_OCTANT.LFU;
				}
			}
		} else {
			if ((codeS + 1) >> 1 == codeS >> 1) {
				if ((codeT + 1) >> 1 == codeT >> 1) {
					return OCT_OCTANT.RBD;
				} else {
					return OCT_OCTANT.RBU;
				}
			} else {
				if ((codeT + 1) >> 1 == codeT >> 1) {
					return OCT_OCTANT.RFD;
				} else {
					return OCT_OCTANT.RFU;
				}
			}
		}
	}

	/**
	 * Gets a sibling of the node at the specified position in the parent.
	 */
	public OctNode getSibling(OCT_OCTANT _o) {
		int r = 1;
		if (_o.getR() == -1)
			r = 0;
		int s = 1;
		if (_o.getS() == -1)
			s = 0;
		int t = 1;
		if (_o.getT() == -1)
			t = 0;
		OctNode tempNode = new OctNode(((this.codeR >> 1) << 1) + r,
				((this.codeS >> 1) << 1) + s, ((this.codeT >> 1) << 1) + t,
				this.level);
		return tempNode;
	}

	/**
	 * Gets the siblings of the node adjacent to the specified position of the parent.
	 */
	public ArrayList<OctNode> getSiblings(OCT_ENUM _o) {
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		for (OctNode n : this.getParent().getChildren(_o)) {
			if (this.getOctant() != n.getOctant()) {
				tempNodeList.add(n);
			}
		}
		return tempNodeList;
	}
	
	/**
	 * Gets a list of siblings of the node in the specified directions, can be
	 * an edge, face or vertex/octant. Does not check if the siblings are
	 * actually in the octree or not.
	 */
	public ArrayList<OctNode> getSiblings(ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		for (OctNode n : this.getParent().getChildren(_o)) {
			if (this.getOctant() != n.getOctant()) {
				tempNodeList.add(n);
			}
		}
		return tempNodeList;
	}
	
	/**
	 * Gets all siblings of the node, returns 7 values. Does not check if the
	 * siblings are actually in the octree or not.
	 */
	public ArrayList<OctNode> getSiblings() {
		OCT_OCTANT thisOctant = this.getOctant();
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		for (OCT_OCTANT o : OCT_OCTANT.values()) {
			if (thisOctant != o) {
				tempNodeList.add(getSibling(o));
			}
		}
		return tempNodeList;
	}

	// *************************************************************************************
	// PARENT
	// *************************************************************************************

	/**
	 * Gets the single parent of the node, one level up. Does not check if the
	 * parent is actually in the octree or not.
	 */
	public OctNode getParent() {
		return getParent(1);
	}

	/**
	 * Gets the single parent of the node '_l' levels up. Does not check if the
	 * parent is actually in the octree or not.
	 */
	public OctNode getParent(int _l) {
		if (level < _l || _l <= 0) {
			PApplet.println("ERROR in getParent(): parent level cannot be 0 or negative. 'Null' returned.");
			return null;
		}
		OctNode tempNode = new OctNode(codeR >> _l, codeS >> _l, codeT >> _l,
				level - _l);
		return tempNode;
	}

	/**
	 * Gets all parents of the node from the node level (not included) up to
	 * '_l' levels up. Does not check if the parents are actually in the octree
	 * or not.
	 */
	public ArrayList<OctNode> getAllParents(int _l) {
		int up = _l;
		/*
		 * if (up <= 0) { PApplet.println(
		 * "ERROR in getAllParents(): level not in range. 'Null' returned.");
		 * return null; }
		 */
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		for (int i = 1; i <= up; i++) {
			tempNodeList.add(getParent(i));
		}
		return tempNodeList;
	}

	/**
	 * Gets all parents of the node from the node level (not included) up to the
	 * level 0 included. Does not check if the parents are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllParents() {
		int up = this.level;
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		for (int i = 1; i <= up; i++) {
			tempNodeList.add(getParent(i));
		}
		return tempNodeList;
	}

	/**
	 * Gets all parents of the node from the node level (not included) up to the
	 * octree minimum depth. Does not check if the parents are actually in the
	 * octree or not. The octree is there just to specify the level bounds.
	 */
	public ArrayList<OctNode> getAllParents(OctOctree _octree) {
		ArrayList<OctNode> tempNodeList = new ArrayList<OctNode>();
		if (this.level < _octree.minD) {
			PApplet.println("ERROR in getAllParents(octree): node outside of octree.");
			return tempNodeList;
		}
		for (int i = 1; i <= this.level - _octree.minD; i++) {
			tempNodeList.add(getParent(i));
		}
		return tempNodeList;
	}

	// *************************************************************************************
	// CHILDREN
	// *************************************************************************************

	/**
	 * Gets one specific child of the node, one level down. Does not check if
	 * the child is actually in the octree or not. If the level is lower than
	 * 127 will cause an error, but such a small level should probably never
	 * happen.
	 */
	public OctNode getChild(OCT_OCTANT _o) {
		int r = 1;
		if (_o.r == -1)
			r = 0;
		int s = 1;
		if (_o.s == -1)
			s = 0;
		int t = 1;
		if (_o.t == -1)
			t = 0;
		OctNode tempNode = new OctNode((codeR << 1) + r, (codeS << 1) + s,
				(codeT << 1) + t, level + 1);
		return tempNode;
	}

	/**
	 * Gets the children of the node, one level down, according to a list of
	 * octants. Does not check if the children are actually in the octree or
	 * not.
	 */
	public ArrayList<OctNode> getChildren(ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		for (OCT_ENUM o : _o) {
			tempChildList.addAll(getChildren(1, o));
		}
		return tempChildList;
	}

	/**
	 * Gets the children of the node, one level down, that are adjacent to one
	 * vertex, face or edge of the parent. Does not check if the children are
	 * actually in the octree or not. A face returns 4 nodes, an edge 2.
	 */
	public ArrayList<OctNode> getChildren(OCT_ENUM _o) {
		return getChildren(1, _o);
	}

	/**
	 * Gets the children of the node, '_l' level down, that are adjacent to a
	 * list if vertex, face or edge of the parent. Does not check if the
	 * children are actually in the octree or not.
	 */
	public ArrayList<OctNode> getChildren(int _l, ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		for (OCT_ENUM o : _o) {
			tempChildList.addAll(getChildren(_l, o));
		}
		return tempChildList;
	}

	/**
	 * Gets the children of the node, '_l' level down, that are adjacent to one
	 * vertex, face or edge of the parent. Does not check if the children are
	 * actually in the octree or not.
	 */
	public ArrayList<OctNode> getChildren(int _l, OCT_ENUM _e) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (_l <= 0) {
			return tempChildList;
		}
		tempChildList.add(this);
		return getChildrenRecursion(_l, _e, tempChildList);
	}

	private ArrayList<OctNode> getChildrenRecursion(int _l, OCT_ENUM _e,
			ArrayList<OctNode> _nodeList) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		for (OctNode n : _nodeList) {
			for (OCT_OCTANT o : _e.getOctants()) {
				tempChildList.add(n.getChild(o));
			}
		}
		if (_l == 1) {
			return tempChildList;
		} else {
			_l -= 1;
			return getChildrenRecursion(_l, _e, tempChildList);
		}
	}

	/**
	 * Gets the children of the node, '_l' level down. Does not check if the
	 * children are actually in the octree or not.
	 */
	public ArrayList<OctNode> getChildren(int _l) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (_l <= 0) {
			return tempChildList;
		}
		for (int m = (int) 1 << _l; --m >= 0;) {
			for (int n = (int) 1 << _l; --n >= 0;) {
				for (int p = (int) 1 << _l; --p >= 0;) {
					OctNode tempNode = new OctNode((codeR << _l) + m,
							(codeS << _l) + n, (codeT << _l) + p, level + _l);
					tempChildList.add(tempNode);
				}
			}
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node, one level down. Does not check if the
	 * children are actually in the octree or not.
	 */
	public ArrayList<OctNode> getChildren() {
		return getChildren(1);
	}

	/**
	 * Gets all children of the node from the node level (not included) up to
	 * '_l' levels down. Does not check if the children are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllChildren(int _l) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (_l <= 0) {
			return tempChildList;
		}
		for (int i = 1; i <= _l; i++) {
			tempChildList.addAll(getChildren(i));
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node up from the node level (not included) up to
	 * '_l' levels down. Does not check if the children are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllChildren(int _l, ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (_l <= 0) {
			return tempChildList;
		}
		for (int i = 1; i <= _l; i++) {
			for (OCT_ENUM o : _o) {
				tempChildList.addAll(getChildren(i, o));
			}
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node from the node level (not included) up to
	 * '_l' levels down. Does not check if the children are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllChildren(int _l, OCT_ENUM _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (_l <= 0) {
			// PApplet.println("ERROR in getAllChildren(): level not in range. 'Null' returned.");
			return tempChildList;
		}
		for (int i = 1; i <= _l; i++) {
			tempChildList.addAll(getChildren(i, _o));
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node up from the node level (not included) up to
	 * the max depth or the octree. Does not check if the children are actually
	 * in the octree or not. If the node is at the maximum level of the octree,
	 * returns null because no child is possible.
	 */
	public ArrayList<OctNode> getAllChildren(OctOctree _octree,
			ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllChildren(octree): node outside of octree.");
			return tempChildList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			for (OCT_ENUM o : _o) {
				tempChildList.addAll(getChildren(i, o));
			}
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node from the node level (not included) up to
	 * the max depth or the octree. Does not check if the children are actually
	 * in the octree or not.
	 */
	public ArrayList<OctNode> getAllChildren(OctOctree _octree) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllChildren(octree): node outside of octree.");
			return tempChildList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			tempChildList.addAll(getChildren(i));
		}
		return tempChildList;
	}

	/**
	 * Gets all children of the node from the node level (not included) up to
	 * the max depth or the octree. Does not check if the children are actually
	 * in the octree or not.
	 */
	public ArrayList<OctNode> getAllChildren(OctOctree _octree, OCT_ENUM _o) {
		ArrayList<OctNode> tempChildList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllChildren(octree): node outside of octree.");
			return tempChildList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			tempChildList.addAll(getChildren(i, _o));
		}
		return tempChildList;
	}

	// *************************************************************************************
	// NEIGHBORS (SAME LEVEL)
	// *************************************************************************************

	/**
	 * Gets the single neighbor of the node, moving in the specified direction
	 * and at the same level. If _dirR, _dirS, _dirT are different than +1, 0,
	 * or -1, the node will not be a neighbor! But it will still be returned.
	 * Does not check if the neighbor is in the Octree or not!
	 */
	public OctNode getNbr(OCT_ENUM _e) {
		OctNode tempNode = new OctNode(codeR, codeS, codeT, level);
		tempNode.codeR += _e.getR();
		tempNode.codeS += _e.getS();
		tempNode.codeT += _e.getT();
		return tempNode;
	}

	/**
	 * Gets the neighbors of the node at the same level and in the specified
	 * directions. Does not check if the neighbors are in the Octree or not!
	 */
	public ArrayList<OctNode> getNbrs(ArrayList<? extends OCT_ENUM> _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : _n) {
			t.add(getNbr(e));
		}
		return t;
	}

	/**
	 * Gets the neighbors of the node at the same level. Does not check if the
	 * neighbors are in the Octree or not! Returns the 26 nodes surrounding the
	 * node.
	 */
	public ArrayList<OctNode> getNbrs() {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : neighborEnums()) {
			t.add(getNbr(e));
		}
		return t;
	}

	private final static ArrayList<OCT_ENUM> neighborEnums() {
		ArrayList<OCT_ENUM> eList = new ArrayList<OCT_ENUM>();
		eList.addAll(OCT_VERTEX.getAll());
		eList.addAll(OCT_FACE.getAll());
		eList.addAll(OCT_EDGE.getAll());
		return eList;
	}

	// *************************************************************************************
	// SMALLER NEIGHBORS (HIGHER IN THE OCTREE)
	// *************************************************************************************

	/**
	 * Gets the neighbors of the node 1 level down and in the specified
	 * directions. Does not check if the neighbors are in the Octree or not!
	 */
	public ArrayList<OctNode> getSmallerNbrs(OCT_ENUM _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		t.addAll(getSmallerNbrs(1, _n));
		return t;
	}
	
	/**
	 * Gets the neighbors of the node 1 level down and in the specified
	 * directions. Does not check if the neighbors are in the Octree or not!
	 */
	public ArrayList<OctNode> getSmallerNbrs(ArrayList<? extends OCT_ENUM> _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		t.addAll(getSmallerNbrs(1, _n));
		return t;
	}

	/**
	 * Gets the neighbors of the node at the same level. Does not check if the
	 * neighbors are in the Octree or not! Returns the 26 nodes surrounding the
	 * node.
	 */
	public ArrayList<OctNode> getSmallerNbrs() {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : neighborEnums()) {
			t.addAll(getSmallerNbrs(1,e));
		}
		return t;
	}

	/**
	 * Gets the neighbors of the node for the specified level and OCT_ENUM. Does
	 * not check if the neighbors are in the Octree or not! Too get all
	 * intermediate levels, use getAllSmallerNbrs!
	 */
	public ArrayList<OctNode> getSmallerNbrs(int _l, OCT_ENUM _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_l > 0) {
			ArrayList<OctNode> tempNodeList = this.getChildren(_l, _n);
			for (OctNode tempNode2 : tempNodeList) {
				t.add(tempNode2.getNbr(_n));
			}
			return t;
		}
		// PApplet.println("ERROR in getSmallerNbrs(): level not in range. 'Null' returned.");
		return t;
	}

	/**
	 * Gets the all the neighbors of the node at the specified level. Does not
	 * check if the neighbors are in the Octree or not! Too get all intermediate
	 * levels, use getAllSmallerNbrs!
	 */
	public ArrayList<OctNode> getSmallerNbrs(int _l) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_l > 0) {
			for (OCT_ENUM e : neighborEnums()) {
				t.addAll(getSmallerNbrs(_l, e));
			}
			return t;
		}
		// PApplet.println("ERROR in getSmallerNbrs(): level not in range. 'Null' returned.");
		return t;
	}

	/**
	 * Gets the neighbors of the node for the specified OCT_ENUM list. Does not
	 * check if the neighbors are in the Octree or not! Too get all intermediate
	 * levels, use getAllSmallerNbrs!
	 */
	public ArrayList<OctNode> getSmallerNbrs(int _l, ArrayList<? extends OCT_ENUM> _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_l > 0) {
			for (OCT_ENUM e : _n) {
				t.addAll(getSmallerNbrs(_l, e));
			}
			return t;
		}
		// PApplet.println("ERROR in getSmallerNbrs(): level not in range. 'Null' returned.");
		return t;
	}
	
	/**
	 * Gets all neighbors of the node from the node level (not included) up to
	 * '_l' levels down. Does not check if the neighbors are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(int _l) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (_l <= 0) {
			// PApplet.println("ERROR in getAllSmallerNbrs(): level not in range. 'Null' returned.");
			return tempList;
		}
		for (int i = 1; i <= _l; i++) {
			tempList.addAll(getSmallerNbrs(i));
		}
		return tempList;
	}

	/**
	 * Gets all neighbors of the node up from the node level (not included) up
	 * to '_l' levels down. Does not check if the neighbors are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(int _l, ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (_l <= 0) {
			// PApplet.println("ERROR in getAllSmallerNbrs(): level not in range. 'Null' returned.");
			return tempList;
		}
		for (int i = 1; i <= _l; i++) {
			for (OCT_ENUM o : _o) {
				tempList.addAll(getSmallerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets all neighbors of the node from the node level (not included) up to
	 * '_l' levels down. Does not check if the neighbors are actually in the
	 * octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(int _l, OCT_ENUM _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (_l <= 0) {
			// PApplet.println("ERROR in getAllSmallerNbrs(): level not in range. 'Null' returned.");
			return tempList;
		}
		for (int i = 1; i <= _l; i++) {
			tempList.addAll(getSmallerNbrs(i, _o));
		}
		return tempList;
	}

	/**
	 * Gets all neighbors of the node up from the node level (not included) up
	 * to the max depth or the octree. Does not check if the neighbors are
	 * actually in the octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(OctOctree _octree,
			ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllSmallerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			for (OCT_ENUM o : _o) {
				tempList.addAll(getSmallerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets all neighbors of the node from the node level (not included) up to
	 * the max depth or the octree. Does not check if the neighbors are actually
	 * in the octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(OctOctree _octree) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllSmallerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			tempList.addAll(getSmallerNbrs(i));
		}
		return tempList;
	}

	/**
	 * Gets all neighbors of the node from the node level (not included) up to
	 * the max depth or the octree. Does not check if the neighbors are actually
	 * in the octree or not.
	 */
	public ArrayList<OctNode> getAllSmallerNbrs(OctOctree _octree, OCT_ENUM _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level > _octree.maxD) {
			PApplet.println("ERROR in getAllSmallerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= _octree.maxD - this.level; i++) {
			tempList.addAll(getSmallerNbrs(i, _o));
		}
		return tempList;
	}

	// *************************************************************************************
	// BIGGER NEIGHBORS (LOWER IN THE OCTREE)
	// *************************************************************************************

	/**
	 * Gets the node that is bigger (lower level) and touching the node. Be
	 * careful with this method as it can return a null. Does not check if the
	 * node is in the octree or not!
	 */
	public OctNode getBiggerNbr(OCT_ENUM _enum) {
		if (level == 0) {
			PApplet.println("ERROR in getBiggerNbr(): node level cannot be 0 or negative. 'Null' returned.");
			return null;
		}
		OctNode n = (this.getNbr(_enum)).getParent(1);
		if (!n.isParentOf(this))
			return n;
		else
			PApplet.println("ERROR in getBiggerNbr(): no possible node. 'Null' returned.");
		return null;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getBiggerNbrs() {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : neighborEnums()) {
			t.add(this.getBiggerNbr(e));
		}
		return t;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getBiggerNbrs(ArrayList<OCT_ENUM> _o) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : _o) {
			t.add(this.getBiggerNbr(e));
		}
		return t;
	}

	/**
	 * Gets the node that is bigger (lower level) and touching the node. Can be
	 * a parent of the node. Does not check if the node is in the octree or not!
	 */
	public ArrayList<OctNode> getBiggerNbrs(int _l, OCT_ENUM _enum) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (level < _l || _l <= 0) {
			// PApplet.println("ERROR in getBiggerNbrs(): level not in range. 'Null' returned.");
			return t;
		}
		OctNode tempParentNode = (this.getNbr(_enum)).getParent(_l);
		if (!tempParentNode.isParentOf(this))
			t.add(tempParentNode);
		return t;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getBiggerNbrs(int _l) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : neighborEnums()) {
			t.addAll(getBiggerNbrs(_l, e));
		}
		return t;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getBiggerNbrs(int _l, ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_ENUM e : _o) {
			t.addAll(getBiggerNbrs(_l, e));
		}
		return t;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(int _l, ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		for (int i = 1; i <= _l; i++) {
			for (OCT_ENUM o : _o) {
				tempList.addAll(getBiggerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(int _l, OCT_ENUM _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		for (int i = 1; i <= _l; i++) {
			tempList.addAll(getBiggerNbrs(i, _o));
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(int _l) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		for (int i = 1; i <= _l; i++) {
			for (OCT_ENUM o : neighborEnums()) {
				tempList.addAll(getBiggerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs() {
		int _l = this.level;
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		for (int i = 1; i <= _l; i++) {
			for (OCT_ENUM o : neighborEnums()) {
				tempList.addAll(getBiggerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(OctOctree _octree,
			ArrayList<? extends OCT_ENUM> _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level < _octree.minD) {
			PApplet.println("ERROR in getAllBiggerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= this.level - _octree.minD; i++) {
			for (OCT_ENUM o : _o) {
				tempList.addAll(getBiggerNbrs(i, o));
			}
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(OctOctree _octree, OCT_ENUM _o) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level < _octree.minD) {
			PApplet.println("ERROR in getAllBiggerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= this.level - _octree.minD; i++) {
			tempList.addAll(getBiggerNbrs(i, _o));
		}
		return tempList;
	}

	/**
	 * Gets the nodes that are bigger (lower level) and touching the node. Can
	 * be parents of the nodes. Does not check if the nodes are in the octree or
	 * not!
	 */
	public ArrayList<OctNode> getAllBiggerNbrs(OctOctree _octree) {
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		if (this.level < _octree.minD) {
			PApplet.println("ERROR in getAllBiggerNbrs(octree): node outside of octree.");
			return tempList;
		}
		for (int i = 1; i <= this.level - _octree.minD; i++) {
			for (OCT_ENUM o : neighborEnums()) {
				tempList.addAll(getBiggerNbrs(i, o));
			}
		}
		return tempList;
	}

	// *************************************************************************************
	// SWEEP
	// *************************************************************************************
	
	/**
	 * Generates all nodes that might be touching the node, at the same level,
	 * higher level or lower level. The octree is provided to give bounds to the
	 * list.
	 */
	public ArrayList<OctNode> sweep(OctOctree _octree, boolean _doSame,
			boolean _doUp, boolean _doDown) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_doSame)
			t.addAll(getNbrs());
		if (_doUp)
			t.addAll(getAllBiggerNbrs(this.level - _octree.minD));
		if (_doDown)
			t.addAll(getAllSmallerNbrs(_octree.maxD - this.level));
		return t;
	}

	/**
	 * Generates all nodes that might be touching the node, at the same level,
	 * higher level or lower level. The octree is provided to give bounds to the
	 * list.
	 */
	public ArrayList<OctNode> sweep(OctOctree _octree, boolean _doSame,
			boolean _doUp, boolean _doDown, OCT_ENUM _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_doSame)
			t.add(getNbr(_n));
		if (_doUp)
			t.addAll(getAllBiggerNbrs(this.level - _octree.minD, _n));
		if (_doDown)
			t.addAll(getAllSmallerNbrs(_octree.maxD - this.level, _n));
		return t;
	}

	/**
	 * Generates all nodes that might be touching the node, at the same level,
	 * higher level or lower level. The octree is provided to give bounds to the
	 * list.
	 */
	public ArrayList<OctNode> sweep(OctOctree _octree, boolean _doSame,
			boolean _doUp, boolean _doDown, ArrayList<OCT_ENUM> _n) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		if (_doSame)
			t.addAll(getNbrs(_n));
		if (_doUp)
			t.addAll(getAllBiggerNbrs(this.level - _octree.minD, _n));
		if (_doDown)
			t.addAll(getAllSmallerNbrs(_octree.maxD - this.level, _n));
		return t;
	}

	// *************************************************************************************
	// BOX AND HEXA
	// *************************************************************************************

	/**
	 * Computes the volume of the node based on its 8 vertices without any
	 * deformations. In XYZ units. Octree needs to be provided!
	 */
	public float volumeBox(OctOctree _octree) {
		float volume = _octree.getDimension().x / (1 << level)
				* _octree.getDimension().y / (1 << level)
				* _octree.getDimension().z / (1 << level);
		return volume;
	}

	public OctRST getMin() {
		OctRST m = this.getVertex(OCT_VERTEX.LBD);
		OctRST n = this.getVertex(OCT_VERTEX.RFU);
		OctRST min = new OctRST(PApplet.min(m.r,n.r),PApplet.min(m.s,n.s),PApplet.min(m.t,n.t));
		return min;
	}
	
	public OctRST getMax() {
		OctRST m = this.getVertex(OCT_VERTEX.LBD);
		OctRST n = this.getVertex(OCT_VERTEX.RFU);
		OctRST max = new OctRST(PApplet.max(m.r,n.r),PApplet.max(m.s,n.s),PApplet.max(m.t,n.t));
		return max;
	}
	
	/**
     * ADAPTED FROM https://gist.github.com/aadnk/7123926.
     * <p>
     * Calculates intersection with the given ray between a certain distance
     * interval.
     * <p>
     * Ray-box intersection is using IEEE numerical properties to ensure the
     * test is both robust and efficient, as described in:
     * <br>
     * <code>Amy Williams, Steve Barrus, R. Keith Morley, and Peter Shirley: "An
     * Efficient and Robust Ray-Box Intersection Algorithm" Journal of graphics
     * tools, 10(1):49-54, 2005</code>
     */
    public OctRST intersectPoint(OctRST start, OctRST direction, float minDist, float maxDist) {
    	direction = direction.normalize();
    	OctRST invDir = new OctRST(1f / direction.r, 1f / direction.s, 1f / direction.t);
   	
    	OctRST max = this.getMax();
    	OctRST min = this.getMin();
   		
        boolean signDirX = invDir.r < 0;
        boolean signDirY = invDir.s < 0;
        boolean signDirZ = invDir.t < 0;

        OctRST bbox = signDirX ? max : min;
        double tmin = (bbox.r-start.r) * invDir.r;
        bbox = signDirX ? min : max;
        double tmax = (bbox.r-start.r) * invDir.r;
        bbox = signDirY ? max : min;
        double tymin = (bbox.s-start.s) * invDir.s;
        bbox = signDirY ? min : max;
        double tymax = (bbox.s-start.s) * invDir.s;
 
      //  PApplet.println(tmin);
      //  PApplet.println(tmax);
        
        if ((tmin > tymax) || (tymin > tmax)) {
            return null;
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }

        bbox = signDirZ ? max : min;
        double tzmin = (bbox.t-start.t) * invDir.t;
        bbox = signDirZ ? min : max;
        double tzmax = (bbox.t-start.t) * invDir.t;

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return null;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
       // PApplet.println(tmin);
       // PApplet.println(tmax);
        if ((tmin < maxDist) && (tmax > minDist)) {
            return new OctRST(direction.normalize().scale((float) tmin).add(start));
        }
        return null;
    }

	/**
	 * Computes the volume of the node based on its 8 vertices, also valid if
	 * deformed. Based on "Efficient computation of volume of hexahedral cells",
	 * J. Grandy. http://www.osti.gov/scitech/servlets/purl/632793
	 */
	public float volumeHexa(OctOctree _octree) {
		float volume = 0;
		OctXYZ v1 = this.getVertex(OCT_VERTEX.get(6)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(0)).toXYZ(_octree));
		OctXYZ v2 = this.getVertex(OCT_VERTEX.get(3)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(0)).toXYZ(_octree));
		OctXYZ v3 = this.getVertex(OCT_VERTEX.get(2)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(7)).toXYZ(_octree));
		OctXYZ v4 = this.getVertex(OCT_VERTEX.get(4)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(0)).toXYZ(_octree));
		OctXYZ v5 = this.getVertex(OCT_VERTEX.get(7)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(5)).toXYZ(_octree));
		OctXYZ v6 = this.getVertex(OCT_VERTEX.get(1)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(0)).toXYZ(_octree));
		OctXYZ v7 = this.getVertex(OCT_VERTEX.get(5)).toXYZ(_octree)
				.sub(this.getVertex(OCT_VERTEX.get(2)).toXYZ(_octree));
		volume = PApplet.abs((v1.dot(v2.cross(v3)) + v1.dot(v4.cross(v5)) + v1
				.dot(v6.cross(v7))) / 6);
		return volume;
	}

	// *************************************************************************************
	// BOOLEAN CHECKS
	// *************************************************************************************

	/**
	 * Returns true if the node code is within the bounds of the octree. Returns
	 * false for nodes that have a negative or too large R, S or T code.
	 */
	public boolean isCodeWB(OctOctree _octree) {
		if (this.codeR >= 0 && this.codeS >= 0 && this.codeT >= 0
				&& this.codeR < 1 << level && this.codeS < 1 << level
				&& this.codeT < 1 << level)
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the node level is within the current bounds of the octree
	 * (between min. and max. level).
	 */
	public boolean isLevelWB(OctOctree _octree) {
		if (this.level <= _octree.getMaxDepth()
				&& this.level >= _octree.getMinDepth())
			return true;
		else
			return false;
	}

	/**
	 * Returns true if 'ifLevelWB' AND 'ifCodeWB' are true
	 */
	public boolean isWB(OctOctree _octree) {
		if (this.isCodeWB(_octree) && this.isLevelWB(_octree))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns true if node is selected.
	 */	
	public boolean isSelected() {
		if (this.isSelected)
			return true;
		else
			return false;
	}
	

	/**
	 * Returns true if the node is included in the '_t' node (check if the node
	 * is a child of _t). Is false if the two nodes have the same level or are
	 * equals.
	 */
	public boolean isChildOf(OctNode _t) {
		if (this.level == _t.level) {
			if (this.equals(_t))
				return false;
		} else if (this.level > _t.level) {
			if (this.getParent(this.level - _t.level).equals(_t))
				return true;
			else
				return false;
		} else if (this.level < _t.level)
			return false;
		return false;
	}

	/**
	 * Returns true if the node is a parent of '_t' (check if the node is a
	 * parent of _t). Is false if the two nodes have the same level.
	 */
	public boolean isParentOf(OctNode _t) {
		if (this.level == _t.level) {
			return false;
		} else if (this.level > _t.level) {
			return false;
		} else if (this.level < _t.level) {
			if (_t.getParent(_t.level - this.level).equals(this))
				return true;
			else
				return false;
		}
		return false;
	}

	/**
	 * Returns true if the node is a sibling of '_t' (check if the nodes have
	 * the same parent). Is false if the two nodes don't have the same level or
	 * are equals.
	 */
	public boolean isSiblingOf(OctNode _t) {
		if (this.getParent(1).equals(_t.getParent(1))
				&& this.equals(_t) == false) {
			return true;
		} else
			return false;
	}

	/**
	 * Returns true if the node is a neighbor of '_t'. Returns false if the node
	 * '_t' equals the node.
	 */
	public boolean isNeighborOf(OctNode _t) {
		OctNode t = new OctNode(_t);
		if (this.level == t.level) {
			if (this.getNbrs().contains(t) && this != _t)
				return true;
		}
		if (this.level > t.level) {
			for (OctNode temp : this.getNbrs()) {
				if (temp.isChildOf(t) && (this.isChildOf(t) == false))
					return true;
			}
		}
		if (this.level < t.level) {
			for (OctNode temp : t.getNbrs()) {
				if (temp.isChildOf(this) && (t.isChildOf(this) == false))
					return true;
			}
		}
		return false;
	}

	// *************************************************************************************
	// COMMON VERTICES, EDGES, FACES
	// *************************************************************************************

	/**
	 * Returns the other sibling node adjacent to the specified edge. Null if
	 * the edge is not touching the node.
	 * 
	 * @param _e
	 *            Edge to provide
	 */
	public OctNode getOtherNode(OCT_EDGE _e) {
		for (OCT_OCTANT e : _e.getOctants()) {
			if (this.getOctant() != e) {
				return this.getParent().getChild(e);
			}
		}
		return null;
	}

	/**
	 * Returns the three other sibling nodes adjacent to the specified face.
	 * Null if the edge is not touching the node.
	 * 
	 * @param _e
	 *            Edge to provide
	 */
	public ArrayList<OctNode> getOtherNodes(OCT_FACE _f) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OCT_OCTANT e : _f.getOctants()) {
			if (this.getOctant() != e) {
				t.add(this.getParent().getChild(e));
			}
		}
		return t;
	}

	// TODO generalize for nodes of different levels and not siblings?
	/**
	 * Returns the parent's edge that is shared with the sibling. Returns null
	 * if the nodes are not siblings.
	 */
	public OCT_EDGE getSharedEdge(OctNode _withThisSibling) {
		if (this.isSiblingOf(_withThisSibling)) {
			int thisOctant = this.getOctant().getOrdinal();
			int siblingOctant = _withThisSibling.getOctant().getOrdinal();
			return OctTables.commonEdgeLUT[siblingOctant][thisOctant];
		} else
			return null;
	}

	// TODO generalize for nodes of different levels and not siblings!
	/**
	 * Returns the parent's face that is shared with the sibling. Returns null
	 * if the nodes are not siblings.
	 */
	public OCT_FACE getSharedFace(OctNode _withThisSibling) {
		if (this.isSiblingOf(_withThisSibling)) {
			int tableShift = 12;
			int thisOctant = this.getOctant().getOrdinal();
			int siblingOctant = _withThisSibling.getOctant().getOrdinal();
			return OctTables.commonFaceLUT[siblingOctant + tableShift][thisOctant];
		} else
			return null;
	}

	// *************************************************************************************
	// DRAW
	// *************************************************************************************

	public void drawCenter(OctOctree _octree) {
		PVector center = getCenter().toXYZ(_octree).toPVector();
		_octree.p5.beginShape(PConstants.POINTS);
		_octree.p5.vertex(center.x, center.y, center.z);
		_octree.p5.endShape();
	}

	public void drawVertices(OctOctree _octree) {
		_octree.p5.beginShape(PConstants.POINTS);
		for (OctRST t : getVertices()) {
			_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
					t.toXYZ(_octree).z);
		}
		_octree.p5.endShape();
	}

	public void drawEdges(OctOctree _octree) {
		_octree.p5.beginShape(PConstants.LINES);
		for (OCT_EDGE e : OCT_EDGE.values()) {
			for (OctRST t : getVertices(e.getVertices())) {
				_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
						t.toXYZ(_octree).z);
			}
		}
		_octree.p5.endShape();
	}

	public void drawFaces(OctOctree _octree) {
		_octree.p5.beginShape(PConstants.QUADS);
		for (OCT_FACE f : OCT_FACE.values()) {
			for (OctRST t : getVertices(f.getVertices())) {
				_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
						t.toXYZ(_octree).z);
			}
		}
		_octree.p5.endShape();
	}

	public void drawVertex(OctOctree _octree, OCT_VERTEX _v) {
		_octree.p5.beginShape(PConstants.POINTS);
		OctRST t = this.getVertex(_v);
		_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
				t.toXYZ(_octree).z);
		_octree.p5.endShape();
	}

	public void drawEdge(OctOctree _octree, OCT_EDGE _e) {
		_octree.p5.beginShape(PConstants.LINES);
		for (OctRST t : getVertices(_e.getVertices())) {
			_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
					t.toXYZ(_octree).z);
		}
		_octree.p5.endShape();
	}

	public void drawFace(OctOctree _octree, OCT_FACE _f) {
		_octree.p5.beginShape(PConstants.QUADS);
		for (OctRST t : getVertices(_f.getVertices())) {
			_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
					t.toXYZ(_octree).z);
		}
		_octree.p5.endShape();
	}

	// *************************************************************************************
	// HASH CODE
	// *************************************************************************************

	public int hashCode() {
		return new String("r" + codeR + "s" + codeS + "t" + codeT + "l" + level)
				.hashCode();
	}

	public boolean equals(Object _t) {
		OctNode t = (OctNode) _t;
		if (t.level == this.level && t.codeR == this.codeR
				&& t.codeS == this.codeS && t.codeT == this.codeT)
			return true;
		else
			return false;
	}
}
