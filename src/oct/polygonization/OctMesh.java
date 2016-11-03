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

import java.util.HashSet;

import oct.octree.OctXYZ;
import processing.core.PApplet;

/**
 * Basic mesh class.
 */
public class OctMesh {

	PApplet p5;
	public HashSet<OctMeshFace> faceList = new HashSet<OctMeshFace>();
	
	public OctMesh(PApplet _p5) {
		p5 = _p5;
	}
	
	public void addFace(OctXYZ _v0, OctXYZ _v1, OctXYZ _v2, OctXYZ _v3) {
		faceList.add(new OctMeshFace(_v0,_v1,_v2,_v3));
	}
	
	public void addFace(OctXYZ _v0, OctXYZ _v1, OctXYZ _v2) {
		faceList.add(new OctMeshFace(_v0,_v1,_v2,new OctXYZ()));
	}
	
	public void addFace(OctMeshFace _f) {
		faceList.add(_f);
	}
	
	public void export() {
		p5.beginRaw(PApplet.DXF, "output.dxf");
		for (OctMeshFace tempFace : faceList) {
			p5.beginShape(PApplet.QUADS);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.vertex(tempFace.v3.x, tempFace.v3.y, tempFace.v3.z);
			p5.endShape();
		}
		p5.endRaw();
	}
	
}
