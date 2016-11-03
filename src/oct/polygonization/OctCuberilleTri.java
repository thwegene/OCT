package oct.polygonization;

import java.util.ArrayList;

import oct.enums.OCT_FACE;
import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctXYZ;
import processing.core.PApplet;

/**
 * Draws the octree as triangles, keeping only faces that don't have a
 * neighbor. Creates a closed volume. Min and max levels must be set up
 * correctly.
 */
public class OctCuberilleTri extends OctCuberilleQuad {

	public OctCuberilleTri(PApplet _p5, OctOctree _octree) {
		super(_p5, _octree);
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
			p5.beginShape(PApplet.TRIANGLES);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.endShape();
		}
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
			for (OctNode tempChildNode : tempNode.getChildren(1,e)) {
				recursion(tempChildNode, e);
			}
		} 
		else {
		// draw full face
		OctXYZ tV0 = tempNode.getVertex(e.getV0()).toXYZ(myOctree);
		OctXYZ tV1 = tempNode.getVertex(e.getV1()).toXYZ(myOctree);
		OctXYZ tV2 = tempNode.getVertex(e.getV2()).toXYZ(myOctree);
		OctXYZ tV3 = tempNode.getVertex(e.getV3()).toXYZ(myOctree);
		octMesh.faceList.add(new OctMeshFace(tV0, tV1, tV2));
		octMesh.faceList.add(new OctMeshFace(tV0, tV2, tV3));
		}
	}
}
