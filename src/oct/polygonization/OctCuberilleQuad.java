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

import java.util.ArrayList;

import oct.enums.OCT_FACE;
import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctXYZ;
import oct.polygonization.OctMeshFace;
import processing.core.PApplet;

/**
 * Draws the octree as quad faces, keeping only faces that don't have a
 * neighbor. Creates a closed volume. Min and max levels must be set up
 * correctly.
 */
public class OctCuberilleQuad extends OctPoly {

	public PApplet p5;
	public OctOctree myOctree;
	public OctMesh octMesh;

	public OctCuberilleQuad(PApplet _p5, OctOctree _octree) {
		p5 = _p5;
		myOctree = _octree;
		octMesh = new OctMesh(p5);
	}

	public OctMesh getMesh() {
		
		return octMesh;
		
	}
	
	private void recursion(OctNode _tempNode, OCT_FACE _e) {
		OctNode tempNode = _tempNode;
		OCT_FACE e = _e;
		// has the node a neighbor at the same level?
		ArrayList<OctNode> tempList = new ArrayList<OctNode>();
		//
		tempList.add(tempNode.getNbr(e));
		tempList = myOctree.filterNode(tempList);
		if (tempList.size() > 0) {
			return;
		}
		// has the node a neighbor at a larger level?
		tempList = new ArrayList<OctNode>();
		//
		tempList = tempNode.getAllBiggerNbrs(myOctree, e);
		tempList = myOctree.filterNode(tempList);
		if (tempList.size() > 0) {
			return;
		}
		// has the node a neighbor at a lower level? if yes, draw the part face.
		ArrayList<OctNode> tempList2 = new ArrayList<OctNode>();
		//
		tempList2 = tempNode.getAllSmallerNbrs(myOctree, e);
		tempList2 = myOctree.filterNode(tempList2);
		if (tempList2.size() > 0) {
			for (OctNode tempChildNode : tempNode.getChildren(1, e)) {
				recursion(tempChildNode, e);
			}
		} else {
			// draw full face
			OctXYZ tV0 = tempNode.getVertex(e.getV0()).toXYZ(myOctree);
			OctXYZ tV1 = tempNode.getVertex(e.getV1()).toXYZ(myOctree);
			OctXYZ tV2 = tempNode.getVertex(e.getV2()).toXYZ(myOctree);
			OctXYZ tV3 = tempNode.getVertex(e.getV3()).toXYZ(myOctree);
			octMesh.faceList.add(new OctMeshFace(tV0, tV1, tV2, tV3));
		}
	}

	public void setup() {
		octMesh.faceList.clear();
		for (OctNode tempNode : myOctree.getNodes()) {
			for (OCT_FACE e : OCT_FACE.values()) {
				recursion(tempNode, e);
			}
		}
	}

	public void draw() {
		for (OctMeshFace tempFace : octMesh.faceList) {
			p5.beginShape(PApplet.QUADS);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.vertex(tempFace.v3.x, tempFace.v3.y, tempFace.v3.z);
			p5.endShape();
		}
	}

}
