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

package oct.polygonization;

import oct.octree.OctXYZ;

/**
 * Basic mesh class. Faces.
 */
public class OctMeshFace {
	
	public OctXYZ v0 = new OctXYZ();
	public OctXYZ v1 = new OctXYZ();
	public OctXYZ v2 = new OctXYZ();
	public OctXYZ v3 = new OctXYZ();
	
	protected int type = 4;
	
	public OctMeshFace() {
	}
	
	public OctMeshFace(OctXYZ _v0, OctXYZ _v1, OctXYZ _v2, OctXYZ _v3) {
		v0 = _v0;
		v1 = _v1;
		v2 = _v2;
		v3 = _v3;
	}
	
	public OctMeshFace(OctXYZ _v0, OctXYZ _v1, OctXYZ _v2) {
		v0 = _v0;
		v1 = _v1;
		v2 = _v2;
		v3 = new OctXYZ();
		type = 3;
	}
	
}
