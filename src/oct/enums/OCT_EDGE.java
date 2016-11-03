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
 * OCT_EDGE lists all possible edges of a node (12).
 */
public enum OCT_EDGE implements OCT_ENUM {

	/**
	 * BACK DOWN, Direction X,Y,Z: 0, -1, -1
	 */
	BD(0, -1, -1, OCT_VERTEX.LBD, OCT_VERTEX.RBD), // 0
	/**
	 * RIGHT DOWN, Direction X,Y,Z: 1, 0, -1
	 */
	RD(1, 0, -1, OCT_VERTEX.RBD, OCT_VERTEX.RFD), // 1
	/**
	 * FRONT DOWN, Direction X,Y,Z: 0, 1, -1
	 */
	FD(0, 1, -1, OCT_VERTEX.LFD, OCT_VERTEX.RFD), // 2
	/**
	 * LEFT DOWN, Direction X,Y,Z: -1, 0, -1
	 */
	LD(-1, 0, -1, OCT_VERTEX.LBD, OCT_VERTEX.LFD), // 3
	/**
	 * BACK UP, Direction X,Y,Z: 0, -1, -1
	 */
	BU(0, -1, 1, OCT_VERTEX.LBU, OCT_VERTEX.RBU), // 4
	/**
	 * RIGHT UP, Direction X,Y,Z: 1, 0, 1
	 */
	RU(1, 0, 1, OCT_VERTEX.RBU, OCT_VERTEX.RFU), // 5
	/**
	 * FRONT UP, Direction X,Y,Z: 0, 1, 1
	 */
	FU(0, 1, 1, OCT_VERTEX.LFU, OCT_VERTEX.RFU), // 6
	/**
	 * LEFT UP, Direction X,Y,Z: -1, 0, 1
	 */
	LU(-1, 0, 1, OCT_VERTEX.LBU, OCT_VERTEX.LFU), // 7
	/**
	 * LEFT BACK, Direction X,Y,Z: -1, -1, 0
	 */
	LB(-1, -1, 0, OCT_VERTEX.LBD, OCT_VERTEX.LBU), // 8
	/**
	 * RIGHT BACK, Direction X,Y,Z: 1, -1, 0
	 */
	RB(1, -1, 0, OCT_VERTEX.RBD, OCT_VERTEX.RBU), // 9
	/**
	 * RIGHT FRONT, Direction X,Y,Z: 1, 1, 0
	 */
	RF(1, 1, 0, OCT_VERTEX.RFD, OCT_VERTEX.RFU), // 10
	/**
	 * LEFT FRONT Direction X,Y,Z: -1, 1, 0
	 */
	LF(-1, 1, 0, OCT_VERTEX.LFD, OCT_VERTEX.LFU); // 11

	public final int r; // vector from the node center to the edge center
	public final int s;
	public final int t;

	final OCT_VERTEX v0;
	final OCT_VERTEX v1;

	OCT_EDGE(int _r, int _s, int _t, OCT_VERTEX _v0, OCT_VERTEX _v1) {
		r = _r;
		s = _s;
		t = _t;
		v0 = _v0;
		v1 = _v1;
	}

	/**
	 * Returns the direction from the node center to the edge center in the X
	 * axis.
	 */
	public final int getR() {
		return r;
	}

	/**
	 * Returns the direction from the node center to the edge center in the Y
	 * axis.
	 */
	public final int getS() {
		return s;
	}

	/**
	 * Returns the direction from the node center to the edge center in the Z
	 * axis.
	 */
	public final int getT() {
		return t;
	}

	/**
	 * Returns the position of the edge in the list of edges. Same result as the
	 * java.lang.Enum method ordinal().
	 */
	public final int getOrdinal() {
		return this.ordinal();
	}

	/**
	 * Returns the list of all edges as an ArrayList.
	 */
	public final static ArrayList<OCT_EDGE> getAll() {
		ArrayList<OCT_EDGE> t = new ArrayList<OCT_EDGE>();
		for (OCT_EDGE e : OCT_EDGE.values()) {
			t.add(e);
		}
		return t;
	}

	/**
	 * Returns the edge at the 'i' position in the list of edges.
	 */
	public final static OCT_EDGE get(int _i) {
		if (_i > -1 && _i < OCT_EDGE.values().length) {
			return OCT_EDGE.values()[_i];
		} else
			return null;
	}

	/**
	 * Returns the edge at the specified direction, if it exists. Otherwise returns null.
	 */
	public final static OCT_EDGE get(int _dirR, int _dirS, int _dirT) {
		for (OCT_EDGE e : OCT_EDGE.values()) {
			if (e.r == _dirR && e.s == _dirS && e.t == _dirT) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Returns the first vertex of an edge.
	 */
	public final OCT_VERTEX getV0() {
		return v0;
	}

	/**
	 * Returns the second vertex of an edge.
	 */
	public final OCT_VERTEX getV1() {
		return v1;
	}

	// *************************************************************************************
	// GET OTHER PARTS OF THE NODE
	// *************************************************************************************

	/**
	 * Returns a list with the two vertices of the edge.
	 */
	public final ArrayList<OCT_VERTEX> getVertices() {
		ArrayList<OCT_VERTEX> list = new ArrayList<OCT_VERTEX>();
		list.add(v0);
		list.add(v1);
		return list;
	}

	/**
	 * Returns a list with the two faces of the edge.
	 */
	public final ArrayList<OCT_FACE> getFaces() {
		ArrayList<OCT_FACE> list = new ArrayList<OCT_FACE>();
		switch (this) {
		case BD:
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.D);
			break;
		case RD:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.D);
			break;
		case FD:
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.D);
			break;
		case LD:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.D);
			break;
		case BU:
			list.add(OCT_FACE.B);
			list.add(OCT_FACE.U);
			break;
		case RU:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.U);
			break;
		case FU:
			list.add(OCT_FACE.F);
			list.add(OCT_FACE.U);
			break;
		case LU:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.U);
			break;
		case LB:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.B);
			break;
		case RB:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.B);
			break;
		case RF:
			list.add(OCT_FACE.R);
			list.add(OCT_FACE.F);
			break;
		case LF:
			list.add(OCT_FACE.L);
			list.add(OCT_FACE.F);
			break;
		}
		return list;
	}

	/**
	 * Returns itself, a list with only one element.
	 */
	public final ArrayList<OCT_EDGE> getEdges() {
		ArrayList<OCT_EDGE> list = new ArrayList<OCT_EDGE>();
		list.add(this);
		return list;
	}

	/**
	 * Returns a list with the two octants touching the edge.
	 */
	public final ArrayList<OCT_OCTANT> getOctants() {
		ArrayList<OCT_OCTANT> list = new ArrayList<OCT_OCTANT>();
		switch (this) {
		case BD:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.RBD);
			break;
		case RD:
			list.add(OCT_OCTANT.RBD);
			list.add(OCT_OCTANT.RFD);
			break;
		case FD:
			list.add(OCT_OCTANT.LFD);
			list.add(OCT_OCTANT.RFD);
			break;
		case LD:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.LFD);
			break;
		case BU:
			list.add(OCT_OCTANT.LBU);
			list.add(OCT_OCTANT.RBU);
			break;
		case RU:
			list.add(OCT_OCTANT.RBU);
			list.add(OCT_OCTANT.RFU);
			break;
		case FU:
			list.add(OCT_OCTANT.LFU);
			list.add(OCT_OCTANT.RFU);
			break;
		case LU:
			list.add(OCT_OCTANT.LBU);
			list.add(OCT_OCTANT.LFU);
			break;
		case LB:
			list.add(OCT_OCTANT.LBD);
			list.add(OCT_OCTANT.LBU);
			break;
		case RB:
			list.add(OCT_OCTANT.RBD);
			list.add(OCT_OCTANT.RBU);
			break;
		case RF:
			list.add(OCT_OCTANT.RFD);
			list.add(OCT_OCTANT.RFU);
			break;
		case LF:
			list.add(OCT_OCTANT.LFD);
			list.add(OCT_OCTANT.LFU);
			break;
		}
		return list;
	}
}