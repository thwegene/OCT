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
 * OCT_VERTEX lists all possible vertices of a node (8).
 */
public enum OCT_VERTEX implements OCT_ENUM {

	/**
	 * LBD, Direction X,Y,Z: -1, -1, -1
	 */
	LBD(-1, -1, -1), // 0
	/**
	 * RBD, Direction X,Y,Z: 1, -1, -1
	 */
	RBD(1, -1, -1), // 1
	/**
	 * RFD, Direction X,Y,Z: 1, 1, -1
	 */
	RFD(1, 1, -1), // 2
	/**
	 * LFD, Direction X,Y,Z: -1, 1, -1
	 */
	LFD(-1, 1, -1), // 3
	/**
	 * LBU, Direction X,Y,Z: -1, -1, 1
	 */
	LBU(-1, -1, 1), // 4
	/**
	 * RBU, Direction X,Y,Z: 1, -1, 1
	 */
	RBU(1, -1, 1), // 5
	/**
	 * RFU, Direction X,Y,Z: 1, 1, 1
	 */
	RFU(1, 1, 1), // 6
	/**
	 * LFU, Direction X,Y,Z: -1, 1, 1
	 */
	LFU(-1, 1, 1); // 7

	public final int r; // vector from the node center to the vertex
	public final int s;
	public final int t;

	OCT_VERTEX(int _r, int _s, int _t) {
		r = _r;
		s = _s;
		t = _t;
	}

	/**
	 * Returns the direction from the node center to the vertex in the Z axis.
	 */
	public final int getR() {
		return r;
	}

	/**
	 * Returns the direction from the node center to the vertex in the Z axis.
	 */
	public final int getS() {
		return s;
	}

	/**
	 * Returns the direction from the node center to the vertex in the Z axis.
	 */
	public final int getT() {
		return t;
	}

	/**
	 * Returns the position of the vertex in the list of vertices. Same result
	 * as the java.lang.Enum method ordinal().
	 */
	public final int getOrdinal() {
		return this.ordinal();
	}

	/**
	 * Returns the list of all vertices as an ArrayList.
	 */
	public final static ArrayList<OCT_VERTEX> getAll() {
		ArrayList<OCT_VERTEX> t = new ArrayList<OCT_VERTEX>();
		for (OCT_VERTEX e : OCT_VERTEX.values()) {
			t.add(e);
		}
		return t;
	}
	
	/**
	 * Returns the vertex at the 'i' position in the list of vertices.
	 */
	public final static OCT_VERTEX get(int _i) {
		if (_i > -1 && _i < OCT_VERTEX.values().length) {
			return OCT_VERTEX.values()[_i];
		} else
			return null;
	}

	/**
	 * Returns the vertex at the specified direction, if it exists. Otherwise returns null.
	 */
	public final static OCT_VERTEX get(int _dirR, int _dirS, int _dirT) {
		for (OCT_VERTEX v : OCT_VERTEX.values()) {
			if (v.r == _dirR && v.s == _dirS && v.t == _dirT) {
				return v;
			}
		}
		;
		return null;
	}

	// *************************************************************************************
	// GET OTHER PARTS OF THE NODE
	// *************************************************************************************

	/**
	 * Returns itself, a list with only one element.
	 */
	public final ArrayList<OCT_VERTEX> getVertices() {
		ArrayList<OCT_VERTEX> list = new ArrayList<OCT_VERTEX>();
		list.add(this);
		return list;
	}

	/**
	 * Returns a list with the three faces touching the vertex.
	 */
	public final ArrayList<OCT_FACE> getFaces() {
		ArrayList<OCT_FACE> list = new ArrayList<OCT_FACE>();
		switch (this) {
		case LBD:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.D);
			break;
		case RBD:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.D);
			break;
		case RFD:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.D);
			break;
		case LFD:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.D);
			break;
		case LBU:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.U);
			break;
		case RBU:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.U);
			break;
		case RFU:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.U);
			break;
		case LFU:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.U);
			break;
		}
		return list;
	}

	/**
	 * Returns a list with the three edges touching the vertex.
	 */
	public final ArrayList<OCT_EDGE> getEdges() {
		ArrayList<OCT_EDGE> list = new ArrayList<OCT_EDGE>();
		switch (this) {
		case LBD:
			list.add(OCT_EDGE.BD);
			list.add(OCT_EDGE.LD);
			list.add(OCT_EDGE.LB);
			break;
		case RBD:
			list.add(OCT_EDGE.BD);
			list.add(OCT_EDGE.RD);
			list.add(OCT_EDGE.RB);
			break;
		case RFD:
			list.add(OCT_EDGE.FD);
			list.add(OCT_EDGE.RD);
			list.add(OCT_EDGE.RF);
			break;
		case LFD:
			list.add(OCT_EDGE.FD);
			list.add(OCT_EDGE.LD);
			list.add(OCT_EDGE.LF);
			break;
		case LBU:
			list.add(OCT_EDGE.BU);
			list.add(OCT_EDGE.LU);
			list.add(OCT_EDGE.LB);
			break;
		case RBU:
			list.add(OCT_EDGE.BU);
			list.add(OCT_EDGE.RU);
			list.add(OCT_EDGE.RB);
			break;
		case RFU:
			list.add(OCT_EDGE.FU);
			list.add(OCT_EDGE.RU);
			list.add(OCT_EDGE.RF);
			break;
		case LFU:
			list.add(OCT_EDGE.FU);
			list.add(OCT_EDGE.LU);
			list.add(OCT_EDGE.LF);
			break;
		}
		return list;
	}

	/**
	 * Returns a list with the octant corresponding to the vertex.
	 */
	public final ArrayList<OCT_OCTANT> getOctants() {
		ArrayList<OCT_OCTANT> list = new ArrayList<OCT_OCTANT>();
		switch (this) {
		case LBD:
			list.add(OCT_OCTANT.LBD);
			break;
		case RBD:
			list.add(OCT_OCTANT.RBD);
			break;
		case RFD:
			list.add(OCT_OCTANT.RFD);
			break;
		case LFD:
			list.add(OCT_OCTANT.LFD);
			break;
		case LBU:
			list.add(OCT_OCTANT.LBU);
			break;
		case RBU:
			list.add(OCT_OCTANT.RBU);
			break;
		case RFU:
			list.add(OCT_OCTANT.RFU);
			break;
		case LFU:
			list.add(OCT_OCTANT.LFU);
			break;
		}
		return list;
	}
}