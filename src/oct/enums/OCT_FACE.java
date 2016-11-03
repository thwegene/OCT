/**
 * OCT Octree
 * A Processing library to create, modify and display Octree structures.
 * http://www.thomaswegener.ch/OCT
 *
 * Based on:
 * Hanan Samet, Neighbor finding in images represented by octrees
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

package oct.enums;

import java.util.ArrayList;

/*
 * //EDGE INDEX 					// VERTEX INDEX
 *
 *		--------4--------          4---------------5
 *	   /|   		   /|	      /|			  /|
 *	  7 | 			  5 |        / |		     / |
 *	 /  8 		     /  9       /  |            /  |
 *	--------6--------   |      7---------------6   |
 *	|   |           |   |      |   |           |   |  
 *	|   --------0---|----      |   0-----------|---1
 *	11 /            10  /      |  /            |  /
 *	| 3             | 1        | /             | /
 *	|/              |/         |/              |/
 *	--------2--------          3---------------2
 *
 */

/**
 * OCT_FACE lists all possible faces of a node (6).
 */
public enum OCT_FACE implements OCT_ENUM {

	/**
	 * LEFT, Direction X,Y,Z: -1, 0, 0
	 */
	L(-1, 0, 0, OCT_VERTEX.LFD, OCT_VERTEX.LFU, OCT_VERTEX.LBU, OCT_VERTEX.LBD,
			OCT_EDGE.LF, OCT_EDGE.LU, OCT_EDGE.LB, OCT_EDGE.LD),
	/**
	 * RIGHT, Direction X,Y,Z: 1, 0, 0
	 */
	R(1, 0, 0, OCT_VERTEX.RBD, OCT_VERTEX.RBU, OCT_VERTEX.RFU, OCT_VERTEX.RFD,
			OCT_EDGE.RB, OCT_EDGE.RU, OCT_EDGE.RF, OCT_EDGE.RD),
	/**
	 * BACK, Direction X,Y,Z: 0, -1, 0
	 */
	B(0, -1, 0, OCT_VERTEX.LBD, OCT_VERTEX.LBU, OCT_VERTEX.RBU, OCT_VERTEX.RBD,
			OCT_EDGE.LB, OCT_EDGE.BU, OCT_EDGE.RB, OCT_EDGE.BD),
	/**
	 * FRONT, Direction X,Y,Z: 0, 1, 0
	 */
	F(0, 1, 0, OCT_VERTEX.RFD, OCT_VERTEX.RFU, OCT_VERTEX.LFU, OCT_VERTEX.LFD,
			OCT_EDGE.RF, OCT_EDGE.FU, OCT_EDGE.LF, OCT_EDGE.FD),
	/**
	 * DOWN, Direction X,Y,Z: 0, 0, -1
	 */
	D(0, 0, -1, OCT_VERTEX.LBD, OCT_VERTEX.RBD, OCT_VERTEX.RFD, OCT_VERTEX.LFD,
			OCT_EDGE.BD, OCT_EDGE.RD, OCT_EDGE.FD, OCT_EDGE.LD),
	/**
	 * UP, Direction X,Y,Z: 0, 0, 1
	 */
	U(0, 0, 1, OCT_VERTEX.LBU, OCT_VERTEX.LFU, OCT_VERTEX.RFU, OCT_VERTEX.RBU,
			OCT_EDGE.LU, OCT_EDGE.FU, OCT_EDGE.RU, OCT_EDGE.BU);

	public final int r; // vector from the node center to the EDGE center
	public final int s;
	public final int t;

	final OCT_VERTEX v0; // the FACE contains these 4 vertices
	final OCT_VERTEX v1;
	final OCT_VERTEX v2;
	final OCT_VERTEX v3;

	final OCT_EDGE e0; // the FACE contains these 4 edges
	final OCT_EDGE e1;
	final OCT_EDGE e2;
	final OCT_EDGE e3;

	OCT_FACE(int _r, int _s, int _t, OCT_VERTEX _v0, OCT_VERTEX _v1,
			OCT_VERTEX _v2, OCT_VERTEX _v3, OCT_EDGE _e0, OCT_EDGE _e1,
			OCT_EDGE _e2, OCT_EDGE _e3) {
		r = _r;
		s = _s;
		t = _t;
		v0 = _v0;
		v1 = _v1;
		v2 = _v2;
		v3 = _v3;
		e0 = _e0;
		e1 = _e1;
		e2 = _e2;
		e3 = _e3;
	}

	/**
	 * Returns the direction from the node center to the face center in the X
	 * axis.
	 */
	public final int getR() {
		return r;
	}

	/**
	 * Returns the direction from the node center to the face center in the Y
	 * axis.
	 */
	public final int getS() {
		return s;
	}

	/**
	 * Returns the direction from the node center to the face center in the Z
	 * axis.
	 */
	public final int getT() {
		return t;
	}

	/**
	 * Returns the position of the face in the list of faces. Same result as the
	 * java.lang.Enum method ordinal().
	 */
	public final int getOrdinal() {
		return this.ordinal();
	}

	/**
	 * Returns the list of all faces as an ArrayList.
	 */
	public final static ArrayList<OCT_FACE> getAll() {
		ArrayList<OCT_FACE> t = new ArrayList<OCT_FACE>();
		for (OCT_FACE e : OCT_FACE.values()) {
			t.add(e);
		}
		return t;
	}

	/**
	 * Returns the face at the 'i' position in the list of faces.
	 */
	public final static OCT_FACE get(int _i) {
		if (_i > -1 && _i < OCT_FACE.values().length) {
			return OCT_FACE.values()[_i];
		} else
			return null;
	}

	/**
	 * Returns the face at the specified direction, if it exists. Otherwise returns null.
	 */
	public final static OCT_FACE get(int _dirR, int _dirS, int _dirT) {
		for (OCT_FACE f : OCT_FACE.values()) {
			if (f.r == _dirR && f.s == _dirS && f.t == _dirT) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Returns the first vertex of a face.
	 */
	public final OCT_VERTEX getV0() {
		return v0;
	}

	/**
	 * Returns the second vertex of a face.
	 */
	public final OCT_VERTEX getV1() {
		return v1;
	}

	/**
	 * Returns the third vertex of a face.
	 */
	public final OCT_VERTEX getV2() {
		return v2;
	}

	/**
	 * Returns the fourth vertex of a face.
	 */
	public final OCT_VERTEX getV3() {
		return v3;
	}

	/**
	 * Returns the first edge of a face.
	 */
	public final OCT_EDGE getE0() {
		return e0;
	}

	/**
	 * Returns the second edge of a face.
	 */
	public final OCT_EDGE getE1() {
		return e1;
	}

	/**
	 * Returns the third edge of a face.
	 */
	public final OCT_EDGE getE2() {
		return e2;
	}

	/**
	 * Returns the fourth edge of a face.
	 */
	public final OCT_EDGE getE3() {
		return e3;
	}

	// *************************************************************************************
	// GET OTHER PARTS OF THE NODE
	// *************************************************************************************

	/**
	 * Returns a list with the four vertices of the face.
	 */
	public final ArrayList<OCT_VERTEX> getVertices() {
		ArrayList<OCT_VERTEX> list = new ArrayList<OCT_VERTEX>();
		list.add(v0);
		list.add(v1);
		list.add(v2);
		list.add(v3);
		return list;
	}

	/**
	 * Returns a list with the four edges of the face.
	 */
	public final ArrayList<OCT_EDGE> getEdges() {
		ArrayList<OCT_EDGE> list = new ArrayList<OCT_EDGE>();
		list.add(e0);
		list.add(e1);
		list.add(e2);
		list.add(e3);
		return list;
	}

	/**
	 * Returns itself, a list with only one element.
	 */
	public final ArrayList<OCT_FACE> getFaces() {
		ArrayList<OCT_FACE> list = new ArrayList<OCT_FACE>();
		list.add(this);
		return list;
	}

	/**
	 * Returns a list with the four octants touching the face.
	 */
	public final ArrayList<OCT_OCTANT> getOctants() {
		ArrayList<OCT_OCTANT> list = new ArrayList<OCT_OCTANT>();
		switch (this) {
		case L:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.LFD);
			list.add(OCT_OCTANT.LBU);
			list.add(OCT_OCTANT.LFU);
			break;
		case R:
			list.add(OCT_OCTANT.RBD);
			list.add(OCT_OCTANT.RFD);
			list.add(OCT_OCTANT.RBU);
			list.add(OCT_OCTANT.RFU);
			break;
		case B:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.RBD);
			list.add(OCT_OCTANT.LBU);
			list.add(OCT_OCTANT.RBU);
			break;
		case F:
			list.add(OCT_OCTANT.RFD);
			list.add(OCT_OCTANT.LFD);
			list.add(OCT_OCTANT.RFU);
			list.add(OCT_OCTANT.LFU);
			break;
		case D:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.RBD);
			list.add(OCT_OCTANT.RFD);
			list.add(OCT_OCTANT.LFD);
			break;
		case U:
			list.add(OCT_OCTANT.LBU);
			list.add(OCT_OCTANT.RBU);
			list.add(OCT_OCTANT.RFU);
			list.add(OCT_OCTANT.LFU);
			break;
		}
		return list;
	}
}