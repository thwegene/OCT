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
 * OCT_OCTANT lists all possible octants of a node (8).
 */
public enum OCT_OCTANT implements OCT_ENUM {

	/**
	 * LBD, Direction X,Y,Z: -1, -1, -1
	 */
	LBD(-1, -1, -1, OCT_FACE.L, OCT_FACE.B, OCT_FACE.D, OCT_EDGE.LB,
			OCT_EDGE.BD, OCT_EDGE.LD, OCT_VERTEX.LBD), // 0
	/**
	 * RBD, Direction X,Y,Z: 1, -1, -1
	 */
	RBD(1, -1, -1, OCT_FACE.R, OCT_FACE.B, OCT_FACE.D, OCT_EDGE.RB,
			OCT_EDGE.BD, OCT_EDGE.RD, OCT_VERTEX.RBD), // 1
	/**
	 * RFD, Direction X,Y,Z: 1, 1, -1
	 */
	RFD(1, 1, -1, OCT_FACE.R, OCT_FACE.F, OCT_FACE.D, OCT_EDGE.RF, OCT_EDGE.FD,
			OCT_EDGE.RD, OCT_VERTEX.RFD), // 2
	/**
	 * LFD, Direction X,Y,Z: -1, 1, -1
	 */
	LFD(-1, 1, -1, OCT_FACE.L, OCT_FACE.F, OCT_FACE.D, OCT_EDGE.LF,
			OCT_EDGE.FD, OCT_EDGE.LD, OCT_VERTEX.LFD), // 3
	/**
	 * LBU, Direction X,Y,Z: -1, -1, 1
	 */
	LBU(-1, -1, 1, OCT_FACE.L, OCT_FACE.B, OCT_FACE.U, OCT_EDGE.LB,
			OCT_EDGE.BU, OCT_EDGE.LU, OCT_VERTEX.LBU), // 4
	/**
	 * RBU, Direction X,Y,Z: 1, -1, 1
	 */
	RBU(1, -1, 1, OCT_FACE.R, OCT_FACE.B, OCT_FACE.U, OCT_EDGE.RB, OCT_EDGE.BU,
			OCT_EDGE.RU, OCT_VERTEX.RBU), // 5
	/**
	 * RFU, Direction X,Y,Z: 1, 1, 1
	 */
	RFU(1, 1, 1, OCT_FACE.R, OCT_FACE.F, OCT_FACE.U, OCT_EDGE.RF, OCT_EDGE.FU,
			OCT_EDGE.RU, OCT_VERTEX.RFU), // 6
	/**
	 * LFU, Direction X,Y,Z: -1, 1, 1
	 */
	LFU(-1, 1, 1, OCT_FACE.L, OCT_FACE.F, OCT_FACE.U, OCT_EDGE.LF, OCT_EDGE.FU,
			OCT_EDGE.FU, OCT_VERTEX.LFU); // 7

	public final int r; // vector from the node center to the octant
	public final int s;
	public final int t;

	final OCT_VERTEX v0; // the OCTANT contains this vertex

	final OCT_EDGE e0; // the OCTANT contains these 3 edges
	final OCT_EDGE e1;
	final OCT_EDGE e2;

	final OCT_FACE f0; // the OCTANT contains these 3 faces
	final OCT_FACE f1;
	final OCT_FACE f2;

	OCT_OCTANT(int _r, int _s, int _t, OCT_FACE _f0, OCT_FACE _f1,
			OCT_FACE _f2, OCT_EDGE _e0, OCT_EDGE _e1, OCT_EDGE _e2,
			OCT_VERTEX _v0) {
		r = _r;
		s = _s;
		t = _t;
		v0 = _v0;
		e0 = _e0;
		e1 = _e1;
		e2 = _e2;
		f0 = _f0;
		f1 = _f1;
		f2 = _f2;
	}

	/**
	 * Returns the direction from the node center to the octant in the Z axis.
	 */
	public final int getR() {
		return r;
	}

	/**
	 * Returns the direction from the node center to the octant in the Z axis.
	 */
	public final int getS() {
		return s;
	}

	/**
	 * Returns the direction from the node center to the octant in the Z axis.
	 */
	public final int getT() {
		return t;
	}

	/**
	 * Returns the position of the octant in the list of vertices. Same result
	 * as the java.lang.Enum method ordinal().
	 */
	public final int getOrdinal() {
		return this.ordinal();
	}

	/**
	 * Returns the list of all octants as an ArrayList.
	 */
	public final static ArrayList<OCT_OCTANT> getAll() {
		ArrayList<OCT_OCTANT> t = new ArrayList<OCT_OCTANT>();
		for (OCT_OCTANT e : OCT_OCTANT.values()) {
			t.add(e);
		}
		return t;
	}

	/**
	 * Returns the octant at the 'i' position in the list of octants.
	 */
	public final static OCT_OCTANT get(int _i) {
		if (_i > -1 && _i < OCT_OCTANT.values().length) {
			return OCT_OCTANT.values()[_i];
		} else
			return null;
	}

	/**
	 * Returns the octant at the specified direction, if it exists. Otherwise returns null.
	 */
	public final static OCT_OCTANT get(int _dirR, int _dirS, int _dirT) {
		for (OCT_OCTANT v : OCT_OCTANT.values()) {
			if (v.r == _dirR && v.s == _dirS && v.t == _dirT) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Returns the vertex corresponding to the octant.
	 */
	public final OCT_VERTEX getV0() {
		return v0;
	}

	/**
	 * Returns the first edge touching the octant.
	 */
	public final OCT_EDGE getE0() {
		return e0;
	}

	/**
	 * Returns the second edge touching the octant.
	 */
	public final OCT_EDGE getE1() {
		return e1;
	}

	/**
	 * Returns the third edge touching the octant.
	 */
	public final OCT_EDGE getE2() {
		return e2;
	}

	/**
	 * Returns the first face touching the octant.
	 */
	public final OCT_FACE getF0() {
		return f0;
	}

	/**
	 * Returns the second face touching the octant.
	 */
	public final OCT_FACE getF1() {
		return f1;
	}

	/**
	 * Returns the third face touching the octant.
	 */
	public final OCT_FACE getF2() {
		return f2;
	}

	// *************************************************************************************
	// GET OTHER PARTS OF THE NODE
	// *************************************************************************************

	/**
	 * Returns a list of size 1 with the vertex touching the octant.
	 */
	public final ArrayList<OCT_VERTEX> getVertices() {
		ArrayList<OCT_VERTEX> list = new ArrayList<OCT_VERTEX>();
		list.add(v0);
		return list;
	}

	/**
	 * Returns a list with the three edges touching the octant.
	 */
	public final ArrayList<OCT_FACE> getFaces() {
		ArrayList<OCT_FACE> list = new ArrayList<OCT_FACE>();
		list.add(f0);
		list.add(f1);
		list.add(f2);
		return list;
	}

	/**
	 * Returns a list with the three faces touching the octant.
	 */
	public final ArrayList<OCT_EDGE> getEdges() {
		ArrayList<OCT_EDGE> list = new ArrayList<OCT_EDGE>();
		list.add(e0);
		list.add(e1);
		list.add(e2);
		return list;
	}

	/**
	 * Returns itself, a list with only one element.
	 */
	public final ArrayList<OCT_OCTANT> getOctants() {
		ArrayList<OCT_OCTANT> list = new ArrayList<OCT_OCTANT>();
		list.add(this);
		return list;
	}
}