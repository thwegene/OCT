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

package oct.utils;

import static oct.enums.OCT_EDGE.BD;
import static oct.enums.OCT_EDGE.BU;
import static oct.enums.OCT_EDGE.FD;
import static oct.enums.OCT_EDGE.FU;
import static oct.enums.OCT_EDGE.LB;
import static oct.enums.OCT_EDGE.LD;
import static oct.enums.OCT_EDGE.LF;
import static oct.enums.OCT_EDGE.LU;
import static oct.enums.OCT_EDGE.RB;
import static oct.enums.OCT_EDGE.RD;
import static oct.enums.OCT_EDGE.RF;
import static oct.enums.OCT_EDGE.RU;
import static oct.enums.OCT_FACE.B;
import static oct.enums.OCT_FACE.D;
import static oct.enums.OCT_FACE.F;
import static oct.enums.OCT_FACE.L;
import static oct.enums.OCT_FACE.R;
import static oct.enums.OCT_FACE.U;
import static oct.enums.OCT_OCTANT.LBD;
import static oct.enums.OCT_OCTANT.LBU;
import static oct.enums.OCT_OCTANT.LFD;
import static oct.enums.OCT_OCTANT.LFU;
import static oct.enums.OCT_OCTANT.RBD;
import static oct.enums.OCT_OCTANT.RBU;
import static oct.enums.OCT_OCTANT.RFD;
import static oct.enums.OCT_OCTANT.RFU;
import oct.enums.OCT_EDGE;
import oct.enums.OCT_FACE;
import oct.enums.OCT_OCTANT;
import oct.enums.OCT_VERTEX;

/**
 * These tables store the relations between nodes and parts of nodes.
 * A OctNode or OCT_ENUM method is always provided to access these tables in an easier way.  
 * <P>
 * Based on:
 * Hanan Samet, Neighbor finding in images represented by octrees
 * Computer Vision, Graphics, and Image Processing, Volume 46, Issue 3, June 1989, Pages 367-386
 * http://dx.doi.org/10.1016/0734-189X(89)90038-8.
 * (http://www.sciencedirect.com/science/article/pii/0734189X89900388)
 */
public class OctTables {

	/**
	 * This table yields the type of the edge that is common to octant O and its sibling at the P
	 * position in the same node. Is null if no common edge!
	 * 
	 * <BR>commonEdgeLUT[P,   O  ]
	 * <BR>commonEdgeLUT[LBD, LBU] = LB
	 * <BR>commonEdgeLUT[LBD, LBD] = null because it is the same node
	 */
	public static OCT_EDGE commonEdgeLUT[][] = {

		// LBD_  RBD_  RFD_  LFD_  LBU_  RBU_  RFU_  LFU_
		{ null, BD,   null, LD,   LB,   null, null, null }, // LBD
		{ BD,   null, RD,   null, null, RB,   null, null }, // RBD
		{ null, RD,   null, FD,   null, null, RF,   null }, // RFD
		{ LD,   null, FD,   null, null, null, null, LF   }, // LFD
		{ LB,   null, null, null, null, BU,   null, LU   }, // LBU
		{ null, RB,   null, null, BU,   null, RU,   null }, // RBU
		{ null, null, RF,   null, null, RU,   null, FU   }, // RFU
		{ null, null, null, LF,   LU,   null, FU,   null }  // LFU
	};

	/**
	 * This table yields the type of the face that is common to octant O and the neighbor in the D
	 * direction, represented as an edge or vertex. Is null if no common face!
	 * 
	 * <BR>commonFaceLUT[D,  O  ]
	 * <BR>commonFaceLUT[LD ,LUF] = L
	 * <BR>commonFaceLUT[LDB,LUF] = L
	 */
	public static OCT_FACE commonFaceLUT[][] = {

		// [row][columns]
		// LBD_  RBD_  RFD_  LFD_  LBU_  RBU_  RFU_  LFU_
		// EDGES
		{ null, null, D,    D,    B,    B,    null, null }, // BD, 0
		{ D,    null, null, D,    null, R,    R,    null }, // RD, 1
		{ D,    D,    null, null, null, null, F,    F    }, // FD, 2
		{ null, D,    D,    null, L,    null, null, L    }, // LD, 3
		{ B,    B,    null, null, null, null, U,    U    }, // BU, 4
		{ null, R,    R,    null, U,    null, null, U    }, // RU, 5
		{ null, null, F,    F,    U,    U,    null, null }, // FU, 6
		{ L,    null, null, L,    null, U,    U,    null }, // LU, 7
		{ null, B,    null, L,    null, B,    null, L    }, // LB, 8
		{ B,    null, R,    null, B,    null, R,    null }, // RB, 9
		{ null, R,    null, F,    null, R,    null, F    }, // RF, 10
		{ L,    null, F,    null, L,    null, F,    null }, // LF, 11
		// VERTICES
		{ null, null, D,    null, null, B,    null, L    }, // LBD, 0
		{ null, null, null, D,    B,    null, R,    null }, // RBD, 1
		{ D,    null, null, null, null, R,    null, F    }, // RFD, 2
		{ null, D,    null, null, L,    null, F,    null }, // LFD, 3
		{ null, B,    null, L,    null, null, U,    null }, // LBU, 4
		{ B,    null, R,    null, null, null, null, U    }, // RBU, 5
		{ null, R,    null, F,    U,    null, null, null }, // RFU, 6
		{ L,    null, F,    null, null, U,    null, null }  // LFU, 7
	};

	/**
	 * This table yields the octant type of the node of equal size
	 * that shares the I face, edge or vertex with the node of type O.
	 * 
	 * <BR>reflectLUT[I,  O  ]
	 * <BR>reflectLUT[L,  RDB] = LDB
	 * <BR>reflectLUT[LD, RDB] = LUB
	 * <BR>reflectLUT[RDB,RDB] = LUF.
	 */
	public static OCT_OCTANT reflectLUT[][] = {

		// [row][columns]
		// LBD_ RBD_ RFD_ LFD_ LBU_ RBU_ RFU_ LFU_
		// FACES
		{ RBD, LBD, LFD, RFD, RBU, LBU, LFU, RFU }, // L, 0
		{ RBD, LBD, LFD, RFD, RBU, LBU, LFU, RFU }, // R, 1
		{ LFD, RFD, RBD, LBD, LFU, RFU, RBU, LBU }, // B, 2
		{ LFD, RFD, RBD, LBD, LFU, RFU, RBU, LBU }, // F, 3
		{ LBU, RBU, RFU, LFU, LBD, RBD, RFD, LFD }, // D, 4
		{ LBU, RBU, RFU, LFU, LBD, RBD, RFD, LFD }, // U, 5
		// EDGES
		{ LFU, RFU, RBU, LBU, LFD, RFD, RBD, LBD }, // BD, 0
		{ RBU, LBU, LFU, RFU, RBD, LBD, LFD, RFD }, // RD, 1
		{ LFU, RFU, RBU, LBU, LFD, RFD, RBD, LBD }, // FD, 2
		{ RBU, LBU, LFU, RFU, RBD, LBD, LFD, RFD }, // LD, 3
		{ LFU, RFU, RBU, LBU, LFD, RFD, RBD, LBD }, // BU, 4
		{ RBU, LBU, LFU, RFU, RBD, LBD, LFD, RFD }, // RU, 5
		{ LFU, RFU, RBU, LBU, LFD, RFD, RBD, LBD }, // FU, 6
		{ RBU, LBU, LFU, RFU, RBD, LBD, LFD, RFD }, // LU, 7
		{ RFD, LFD, LBD, RBD, RFU, LFU, LBU, RBU }, // LB, 8
		{ RFD, LFD, LBD, RBD, RFU, LFU, LBU, RBU }, // RB, 9
		{ RFD, LFD, LBD, RBD, RFU, LFU, LBU, RBU }, // RF, 10
		{ RFD, LFD, LBD, RBD, RFU, LFU, LBU, RBU }, // LF, 11
		// VERTICES
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // LBD, 0
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // RBD, 1
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // RFD, 2
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // LFD, 3
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // LBU, 4
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // RBU, 5
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }, // RFU, 6
		{ RFU, LFU, LBU, RBU, RFD, LFD, LBD, RBD }  // LFU, 7
	};

	/**
	 * This table is true if and only if octant O is adjacent to the I face,
	 * edge, or vertex of its parent.
	 * 
	 * <BR>adjLUT[I,  O  ]
	 * <BR>adjLUT[L,  LBD] is true.
	 * <BR>adjLUT[LD, LBD] is true.
	 * <BR>adjLUT[LBD,LBD] is true.
	 */
	public static boolean adjLUT[][] = {

		// [row][columns]
		// LBD_ RBD_ RFD_ LFD_ LBU_ RBU_ RFU_ LFU_
		// FACES
		{ true,  false, false, true,  true,  false, false, true  }, // L, 0
		{ false, true,  true,  false, false, true,  true,  false }, // R, 1
		{ true,  true,  false, false, true,  true,  false, false }, // B, 2
		{ false, false, true,  true,  false, false, true,  true  }, // F, 3
		{ true,  true,  true,  true,  false, false, false, false }, // D, 4
		{ false, false, false, false, true,  true,  true,  true  }, // U, 5
		// EDGES
		{ true,  true,  false, false, false, false, false, false }, // BD, 0
		{ false, true,  true,  false, false, false, false, false }, // RD, 1
		{ false, false, true,  true,  false, false, false, false }, // FD, 2
		{ true,  false, false, true,  false, false, false, false }, // LD, 3
		{ false, false, false, false, true,  true,  false, false }, // BU, 4
		{ false, false, false, false, false, true,  true,  false }, // RU, 5
		{ false, false, false, false, false, false, true,  true  }, // FU, 6
		{ false, false, false, false, true,  false, false, true  }, // LU, 7
		{ true,  false, false, false, true,  false, false, false }, // LB, 8
		{ false, true,  false, false, false, true,  false, false }, // RB, 9
		{ false, false, true,  false, false, false, true,  false }, // RF, 10
		{ false, false, false, true,  false, false, false, true  }, // LF, 11
		// VERTICES
		{ true,  false, false, false, false, false, false, false }, // LBD, 0
		{ false, true,  false, false, false, false, false, false }, // RBD, 1
		{ false, false, true,  false, false, false, false, false }, // RFD, 2
		{ false, false, false, true,  false, false, false, false }, // LFD, 3
		{ false, false, false, false, true,  false, false, false }, // LBU, 4
		{ false, false, false, false, false, true,  false, false }, // RBU, 5
		{ false, false, false, false, false, false, true,  false }, // RFU, 6
		{ false, false, false, false, false, false, false, true  }  // LFU, 7
	};

	/**
	 * This table stores the 256 possibilities of the marching cubes algorithm in the form
	 * of triangles between three node's edges.
	 */
	public static int marchingCubesLUT[][] = {

		{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1 },
		{ 8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1 },
		{ 3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1 },
		{ 4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1 },
		{ 4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1 },
		{ 9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1 },
		{ 10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1 },
		{ 5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1 },
		{ 5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1 },
		{ 8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1 },
		{ 2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1 },
		{ 2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1 },
		{ 11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1 },
		{ 5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1 },
		{ 11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1 },
		{ 11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1 },
		{ 2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1 },
		{ 6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1 },
		{ 3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1 },
		{ 6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1 },
		{ 6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1 },
		{ 8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1 },
		{ 7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1 },
		{ 3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1 },
		{ 0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1 },
		{ 9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1 },
		{ 8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1 },
		{ 5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1 },
		{ 0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1 },
		{ 6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1 },
		{ 10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1 },
		{ 1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1 },
		{ 0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1 },
		{ 3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1 },
		{ 6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1 },
		{ 9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1 },
		{ 8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1 },
		{ 3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1 },
		{ 10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1 },
		{ 10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1 },
		{ 2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1 },
		{ 7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1 },
		{ 2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1 },
		{ 1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1 },
		{ 11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1 },
		{ 8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1 },
		{ 0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1 },
		{ 7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1 },
		{ 7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1 },
		{ 10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1 },
		{ 0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1 },
		{ 7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1 },
		{ 6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1 },
		{ 4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1 },
		{ 10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1 },
		{ 8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1 },
		{ 1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1 },
		{ 10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1 },
		{ 10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1 },
		{ 9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1 },
		{ 7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1 },
		{ 3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1 },
		{ 7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1 },
		{ 3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1 },
		{ 6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1 },
		{ 9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1 },
		{ 1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1 },
		{ 4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1 },
		{ 7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1 },
		{ 6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1 },
		{ 0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1 },
		{ 6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1 },
		{ 0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1 },
		{ 11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1 },
		{ 6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1 },
		{ 5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1 },
		{ 9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1 },
		{ 1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1 },
		{ 10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1 },
		{ 0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1 },
		{ 11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1 },
		{ 9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1 },
		{ 7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1 },
		{ 2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1 },
		{ 9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1 },
		{ 9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1 },
		{ 1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1 },
		{ 0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1 },
		{ 10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1 },
		{ 2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1 },
		{ 0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1 },
		{ 0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1 },
		{ 9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1 },
		{ 5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1 },
		{ 5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1 },
		{ 8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1 },
		{ 9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1 },
		{ 1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1 },
		{ 3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1 },
		{ 4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1 },
		{ 9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1 },
		{ 11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1 },
		{ 11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1 },
		{ 2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1 },
		{ 9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1 },
		{ 3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1 },
		{ 1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1 },
		{ 4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1 },
		{ 0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1 },
		{ 9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1 },
		{ 1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ 0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
		{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
	};

	/**
	 * Given one OCT_VERTEX.ordinal and an index (0 to 2), returns the corresponding OCT_EDGE.
	 */	
	public static OCT_EDGE vertexEdgeList[][] = {
		{OCT_EDGE.BD, OCT_EDGE.LD, OCT_EDGE.LB},	//LBD
		{OCT_EDGE.BD, OCT_EDGE.RD, OCT_EDGE.RB},	//RBD
		{OCT_EDGE.FD, OCT_EDGE.RD, OCT_EDGE.RF},	//RFD
		{OCT_EDGE.FD, OCT_EDGE.LD, OCT_EDGE.LF},	//LFD
		{OCT_EDGE.BU, OCT_EDGE.LU, OCT_EDGE.LB},	//LBU
		{OCT_EDGE.BU, OCT_EDGE.RU, OCT_EDGE.RB},	//RBU
		{OCT_EDGE.FU, OCT_EDGE.RU, OCT_EDGE.RF},	//RFU
		{OCT_EDGE.FU, OCT_EDGE.LU, OCT_EDGE.LF}		//LFU
	};

	/**
	 * Given one OCT_VERTEX.ordinal and an index (0 to 2), returns the corresponding OCT_FACE.
	 */		
	public static OCT_FACE vertexFaceList[][] = {
		{OCT_FACE.L,OCT_FACE.B,OCT_FACE.D},			//LBD
		{OCT_FACE.R,OCT_FACE.B,OCT_FACE.D},			//RBD
		{OCT_FACE.R,OCT_FACE.F,OCT_FACE.D},			//RFD
		{OCT_FACE.L,OCT_FACE.F,OCT_FACE.D},			//LFD
		{OCT_FACE.L,OCT_FACE.B,OCT_FACE.U},			//LBU
		{OCT_FACE.R,OCT_FACE.B,OCT_FACE.U},			//RBU
		{OCT_FACE.R,OCT_FACE.F,OCT_FACE.U},			//RFU
		{OCT_FACE.L,OCT_FACE.F,OCT_FACE.U}			//LFU
	};

	/**
	 * Given one OCT_EDGE.ordinal and an index (0 or 1), returns the corresponding OCT_VERTEX.
	 */	
	public static OCT_VERTEX edgeVertexList[][] = {
		{OCT_VERTEX.LBD, OCT_VERTEX.RBD},	//BD	
		{OCT_VERTEX.RBD, OCT_VERTEX.RFD},	//RD
		{OCT_VERTEX.LFD, OCT_VERTEX.RFD},	//FD
		{OCT_VERTEX.LBD, OCT_VERTEX.LFD},	//LD
		{OCT_VERTEX.LBU, OCT_VERTEX.RBU},	//BU
		{OCT_VERTEX.RBU, OCT_VERTEX.RFU},	//RU
		{OCT_VERTEX.LFU, OCT_VERTEX.RFU},	//FU
		{OCT_VERTEX.LBU, OCT_VERTEX.LFU},	//LU
		{OCT_VERTEX.LBD, OCT_VERTEX.LBU},	//LB
		{OCT_VERTEX.RBD, OCT_VERTEX.RBU},	//RB
		{OCT_VERTEX.RFD, OCT_VERTEX.RFU},	//RF
		{OCT_VERTEX.LFD, OCT_VERTEX.LFU}	//LF
	};

	/**
	 * Given one OCT_EDGE.ordinal and an index (0 or 1), returns the corresponding OCT_FACE.
	 */		
	public static OCT_FACE edgeFaceList[][] = {
		{OCT_FACE.B, OCT_FACE.D},			//BD	
		{OCT_FACE.R, OCT_FACE.D},			//RD
		{OCT_FACE.F, OCT_FACE.D},			//FD
		{OCT_FACE.L, OCT_FACE.D},			//LD
		{OCT_FACE.B, OCT_FACE.U},			//BU
		{OCT_FACE.R, OCT_FACE.U},			//RU
		{OCT_FACE.F, OCT_FACE.U},			//FU
		{OCT_FACE.L, OCT_FACE.U},			//LU
		{OCT_FACE.L, OCT_FACE.B},			//LB
		{OCT_FACE.R, OCT_FACE.B},			//RB
		{OCT_FACE.R, OCT_FACE.F},			//RF
		{OCT_FACE.L, OCT_FACE.F}			//LF
	};

	/**
	 * Given one OCT_EDGE.ordinal and an index (0 or 1), returns the corresponding OCT_OCTANT.
	 */		
	public static OCT_OCTANT edgeOctantList[][] = {
		{OCT_OCTANT.LBD, OCT_OCTANT.RBD},	//BD	
		{OCT_OCTANT.RBD, OCT_OCTANT.RFD},	//RD
		{OCT_OCTANT.LFD, OCT_OCTANT.RFD},	//FD
		{OCT_OCTANT.LBD, OCT_OCTANT.LFD},	//LD
		{OCT_OCTANT.LBU, OCT_OCTANT.RBU},	//BU
		{OCT_OCTANT.RBU, OCT_OCTANT.RFU},	//RU
		{OCT_OCTANT.LFU, OCT_OCTANT.RFU},	//FU
		{OCT_OCTANT.LBU, OCT_OCTANT.LFU},	//LU
		{OCT_OCTANT.LBD, OCT_OCTANT.LBU},	//LB
		{OCT_OCTANT.RBD, OCT_OCTANT.RBU},	//RB
		{OCT_OCTANT.RFD, OCT_OCTANT.RFU},	//RF
		{OCT_OCTANT.LFD, OCT_OCTANT.LFU}	//LF
	};

	/**
	 * Given one OCT_FACE.ordinal and an index (0 to 3), returns the corresponding OCT_VERTEX.
	 */	
	public static OCT_VERTEX faceVertexList[][] = {
		{OCT_VERTEX.LFD, OCT_VERTEX.LFU, OCT_VERTEX.LBU, OCT_VERTEX.LBD},	//L
		{OCT_VERTEX.RBD, OCT_VERTEX.RBU, OCT_VERTEX.RFU, OCT_VERTEX.RFD},	//R
		{OCT_VERTEX.LBD, OCT_VERTEX.LBU, OCT_VERTEX.RBU, OCT_VERTEX.RBD},	//B
		{OCT_VERTEX.RFD, OCT_VERTEX.RFU, OCT_VERTEX.LFU, OCT_VERTEX.LFD},	//F
		{OCT_VERTEX.LBD, OCT_VERTEX.RBD, OCT_VERTEX.RFD, OCT_VERTEX.LFD},	//D
		{OCT_VERTEX.LBU, OCT_VERTEX.LFU, OCT_VERTEX.RFU, OCT_VERTEX.RBU} 	//U
	};

	/**
	 * Given one OCT_FACE.ordinal and an index (0 to 3), returns the corresponding OCT_FACE.
	 */		
	public static OCT_EDGE faceEdgeList[][] = {
		{OCT_EDGE.LF, OCT_EDGE.LU, OCT_EDGE.LB, OCT_EDGE.LD},				//L
		{OCT_EDGE.RB, OCT_EDGE.RU, OCT_EDGE.RF, OCT_EDGE.RD},				//R
		{OCT_EDGE.LB, OCT_EDGE.BU, OCT_EDGE.RB, OCT_EDGE.BD},				//B
		{OCT_EDGE.RF, OCT_EDGE.FU, OCT_EDGE.LF, OCT_EDGE.FD},				//F
		{OCT_EDGE.BD, OCT_EDGE.RD, OCT_EDGE.FD, OCT_EDGE.LD},				//D
		{OCT_EDGE.LU, OCT_EDGE.FU, OCT_EDGE.RU, OCT_EDGE.BU} 				//U
	};

	/**
	 * Given one OCT_FACE.ordinal and an index (0 to 3), returns the corresponding OCT_OCTANT.
	 */	
	public static OCT_OCTANT faceOctantList[][] = {
		{OCT_OCTANT.LFD, OCT_OCTANT.LFU, OCT_OCTANT.LBU, OCT_OCTANT.LBD},	//L
		{OCT_OCTANT.RBD, OCT_OCTANT.RBU, OCT_OCTANT.RFU, OCT_OCTANT.RFD},	//R
		{OCT_OCTANT.LBD, OCT_OCTANT.LBU, OCT_OCTANT.RBU, OCT_OCTANT.RBD},	//B
		{OCT_OCTANT.RFD, OCT_OCTANT.RFU, OCT_OCTANT.LFU, OCT_OCTANT.LFD},	//F
		{OCT_OCTANT.LBD, OCT_OCTANT.RBD, OCT_OCTANT.RFD, OCT_OCTANT.LFD},	//D
		{OCT_OCTANT.LBU, OCT_OCTANT.LFU, OCT_OCTANT.RFU, OCT_OCTANT.RBU} 	//U
	};

	/**
	 * Given one OCT_OCTANT.ordinal and an index (0 to 2), returns the corresponding OCT_EDGE.
	 */		
	public static OCT_EDGE octantEdgeList[][] = {
		{OCT_EDGE.BD, OCT_EDGE.LD, OCT_EDGE.LB},	//LBD
		{OCT_EDGE.BD, OCT_EDGE.RD, OCT_EDGE.RB},	//RBD
		{OCT_EDGE.FD, OCT_EDGE.RD, OCT_EDGE.RF},	//RFD
		{OCT_EDGE.FD, OCT_EDGE.LD, OCT_EDGE.LF},	//LFD
		{OCT_EDGE.BU, OCT_EDGE.LU, OCT_EDGE.LB},	//LBU
		{OCT_EDGE.BU, OCT_EDGE.RU, OCT_EDGE.RB},	//RBU
		{OCT_EDGE.FU, OCT_EDGE.RU, OCT_EDGE.RF},	//RFU
		{OCT_EDGE.FU, OCT_EDGE.LU, OCT_EDGE.LF}		//LFU
	};

	/**
	 * Given one OCT_OCTANT.ordinal and an index (0 to 2), returns the corresponding OCT_FACE.
	 */				
	public static OCT_FACE octantFaceList[][] = {
		{OCT_FACE.L,OCT_FACE.B,OCT_FACE.D},			//LBD
		{OCT_FACE.R,OCT_FACE.B,OCT_FACE.D},			//RBD
		{OCT_FACE.R,OCT_FACE.F,OCT_FACE.D},			//RFD
		{OCT_FACE.L,OCT_FACE.F,OCT_FACE.D},			//LFD
		{OCT_FACE.L,OCT_FACE.B,OCT_FACE.U},			//LBU
		{OCT_FACE.R,OCT_FACE.B,OCT_FACE.U},			//RBU
		{OCT_FACE.R,OCT_FACE.F,OCT_FACE.U},			//RFU
		{OCT_FACE.L,OCT_FACE.F,OCT_FACE.U}			//LFU
	};
}
