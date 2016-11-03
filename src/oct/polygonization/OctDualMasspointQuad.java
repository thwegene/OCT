package oct.polygonization;

import java.util.ArrayList;
import java.util.HashMap;

import oct.enums.OCT_EDGE;
import oct.enums.OCT_ENUM;
import oct.enums.OCT_FACE;
import oct.enums.OCT_VERTEX;
import oct.math.OctFunction;
import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctRST;
import oct.octree.OctXYZ;
import processing.core.PApplet;

public class OctDualMasspointQuad extends OctPoly {

	private PApplet p5;
	private OctOctree myOctree;
	public OctMesh octMesh;

	private OctXYZ[] vertexList;

	private float nodeSizeX;
	private float nodeSizeY;
	private float nodeSizeZ;

	private OctFunction myFunction;
	private float threshold;

	private HashMap<OctNode, OctXYZ> qef = new HashMap<OctNode, OctXYZ>();

	private float[] valueAt = new float[8];

	private int flip = -1;
	private int close = -1;
	private float adaptMultiplier = 1;
	private float closeValue = 1;

	float x1;
	float y1;
	float z1;
	float x2;
	float y2;
	float z2;

	public OctDualMasspointQuad(PApplet _p5, OctOctree _octree,
			OctFunction myFunction, float _threshold) {
		p5 = _p5;
		myOctree = _octree;
		vertexList = new OctXYZ[12];
		this.myFunction = myFunction;
		threshold = _threshold;
		closeValue = threshold;
		octMesh = new OctMesh(p5);
		x1 = myOctree.getMin().x;
		y1 = myOctree.getMin().y;
		z1 = myOctree.getMin().z;
		x2 = myOctree.getMax().x;
		y2 = myOctree.getMax().y;
		z2 = myOctree.getMax().z;
	}

	public void clear() {
		octMesh = new OctMesh(p5);
	}

	public void setThreshold(float _value) {
		threshold = _value;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setCloseValue(float _value) {
		closeValue = _value;
	}

	public float getCloseValue() {
		return closeValue;
	}

	public void flipToggle() {
		flip *= -1;
	}

	public void closeToggle() {
		close *= -1;
	}

	public void smooth(float _value) {
		adaptMultiplier = _value;
	}

	public void setup() {

		for (OctNode tempNode : myOctree.nodeList) {
			vertexList = new OctXYZ[12];
			int x = tempNode.getCodeR();
			int y = tempNode.getCodeS();
			int z = tempNode.getCodeT();
			nodeSizeX = 1f / (1 << tempNode.getLevel());
			nodeSizeY = 1f / (1 << tempNode.getLevel());
			nodeSizeZ = 1f / (1 << tempNode.getLevel());

			// could be pre-computed, since they are necessary anyway to include
			// or not the node
			// or even better the masspoint should be precomputed
			for (OCT_VERTEX v : OCT_VERTEX.getAll()) {
				if (close == 1
						&& (tempNode.getVertex(v).toXYZ(myOctree).x == x1
						|| tempNode.getVertex(v).toXYZ(myOctree).x == x2
						|| tempNode.getVertex(v).toXYZ(myOctree).y == y1
						|| tempNode.getVertex(v).toXYZ(myOctree).y == y2
						|| tempNode.getVertex(v).toXYZ(myOctree).z == z1 || tempNode
						.getVertex(v).toXYZ(myOctree).z == z2)) {
					valueAt[v.getOrdinal()] = closeValue;
				} else {
					valueAt[v.getOrdinal()] = myFunction.compute(tempNode
							.getVertex(v).toXYZ(myOctree).x, tempNode
							.getVertex(v).toXYZ(myOctree).y, tempNode
							.getVertex(v).toXYZ(myOctree).z);
				}
			}

			OctXYZ masspoint = new OctXYZ();
			masspoint = tempNode.getCenter().toXYZ(myOctree);

			for (OCT_EDGE e : OCT_EDGE.values()) {
				boolean bool1 = false;
				boolean bool2 = false;
				if (valueAt[e.getV0().getOrdinal()] * flip > threshold * flip) {
					bool1 = true;
				}
				if (valueAt[e.getV1().getOrdinal()] * flip > threshold * flip) {
					bool2 = true;
				}
				if (bool1 != bool2) {
					float adapt = PApplet.map(threshold, valueAt[e.getV0()
					                                             .getOrdinal()], valueAt[e.getV1().getOrdinal()],
							-1, 1);

					// 1 position inside the node
					OctRST vX = new OctRST((e.r) * nodeSizeX / 2, (e.s)
							* nodeSizeY / 2, (e.t) * nodeSizeZ / 2);

					// 2 smooth on the edge
					if (e.r == 0)
						vX.r += adapt * nodeSizeX / 2;
					if (e.s == 0)
						vX.s += adapt * nodeSizeY / 2;
					if (e.t == 0)
						vX.t += adapt * nodeSizeZ / 2;

					// 3 position inside the octree
					vX.addSelf((float) (x + 0.5) * nodeSizeX, (float) (y + 0.5)
							* nodeSizeY, (float) (z + 0.5) * nodeSizeZ);
					vertexList[e.getOrdinal()] = vX.toXYZ(myOctree);
				}
			}
			OctXYZ solution = new OctXYZ();
			int count = 0;
			for (int i = 0; i < 12; i++) {
				if (vertexList[i] != null) {
					solution.addSelf(vertexList[i]);
					count++;
				}
			}
			solution.scaleSelf(1f / count);
			solution = solution.scale(adaptMultiplier).add(
					masspoint.scale((1 - adaptMultiplier)));
			qef.put(tempNode, solution);
		}

		ArrayList<OCT_ENUM> dirList = new ArrayList<OCT_ENUM>();
		dirList.add(OCT_EDGE.RD);
		dirList.add(OCT_EDGE.RF);
		dirList.add(OCT_EDGE.FD);
		dirList.add(OCT_FACE.R);
		dirList.add(OCT_FACE.D);
		dirList.add(OCT_FACE.F);

		for (OctNode tempNode : myOctree.nodeList) {

			nodeSizeX = 1f / (1 << tempNode.getLevel());
			nodeSizeY = 1f / (1 << tempNode.getLevel());
			nodeSizeZ = 1f / (1 << tempNode.getLevel());

			for (OCT_VERTEX v : OCT_VERTEX.getAll()) {
				if (close == 1
						&& (tempNode.getVertex(v).toXYZ(myOctree).x == x1
						|| tempNode.getVertex(v).toXYZ(myOctree).x == x2
						|| tempNode.getVertex(v).toXYZ(myOctree).y == y1
						|| tempNode.getVertex(v).toXYZ(myOctree).y == y2
						|| tempNode.getVertex(v).toXYZ(myOctree).z == z1 || tempNode
						.getVertex(v).toXYZ(myOctree).z == z2)) {
					valueAt[v.getOrdinal()] = closeValue;
				} else {
					valueAt[v.getOrdinal()] = myFunction.compute(tempNode
							.getVertex(v).toXYZ(myOctree).x, tempNode
							.getVertex(v).toXYZ(myOctree).y, tempNode
							.getVertex(v).toXYZ(myOctree).z);
				}
			}

			for (OCT_EDGE e : OCT_EDGE.getAll()) {
				boolean bool1 = false;
				boolean bool2 = false;
				if (valueAt[e.getV0().getOrdinal()] * flip > threshold * flip) {
					bool1 = true;
				}
				if (valueAt[e.getV1().getOrdinal()] * flip > threshold * flip) {
					bool2 = true;
				}
				if (bool1 != bool2) {
					boolean v1 = false;
					boolean v2 = false;
					boolean v3 = false;
					boolean v1b = false;
					boolean v2b = false;
					boolean v3b = false;
					boolean doEdge = false;
					if (e == OCT_EDGE.RD || e == OCT_EDGE.RF
							|| e == OCT_EDGE.FD)
						doEdge = true;

					OctXYZ tV0 = qef.get(tempNode);
					OctXYZ tV1 = null;
					OctXYZ tV2 = null;
					OctXYZ tV3 = null;

					if (myOctree.getMaxDepth() == myOctree.getMinDepth()
							&& doEdge == true) {
						if (qef.get(tempNode.getNbr(e.getFaces().get(0))) != null) {
							tV1 = qef.get(tempNode.getNbr(e.getFaces().get(0)));
							v1 = true;
						}
						if (qef.get(tempNode.getNbr(e)) != null) {
							tV2 = qef.get(tempNode.getNbr(e));
							v2 = true;
						}
						if (qef.get(tempNode.getNbr(e.getFaces().get(1))) != null) {
							tV3 = qef.get(tempNode.getNbr(e.getFaces().get(1)));
							v3 = true;
						}
					} else {
						// check if there is same level neighbors, or lager
						// level neighbors
						if (qef.get(tempNode.getNbr(e.getFaces().get(0))) != null) {
							tV1 = qef.get(tempNode.getNbr(e.getFaces().get(0)));
							v1 = true;
						} else {
							for (OctNode t : tempNode.getAllBiggerNbrs(
									myOctree, e.getFaces().get(0))) {
								if (qef.get(t) != null) {
									tV1 = qef.get(t);
									v1b = true;
								}
							}
						}
						if (qef.get(tempNode.getNbr(e)) != null) {
							tV2 = qef.get(tempNode.getNbr(e));
							v2 = true;
						} else {
							for (OctNode t : tempNode.getAllBiggerNbrs(
									myOctree, e)) {
								if (qef.get(t) != null) {
									tV2 = qef.get(t);
									v2b = true;
								}
							}
						}
						if (qef.get(tempNode.getNbr(e.getFaces().get(1))) != null) {
							tV3 = qef.get(tempNode.getNbr(e.getFaces().get(1)));
							v3 = true;
						} else {
							for (OctNode t : tempNode.getAllBiggerNbrs(
									myOctree, e.getFaces().get(1))) {
								if (qef.get(t) != null) {
									tV3 = qef.get(t);
									v3b = true;
								}
							}
						}

						// if we are not going in the RD/RF/FD direction, we
						// still need to draw the triangle
						// if it would not be created otherwise, ie if ther is
						// not a node with the same level
						// with an edge looking in the right direction
						if (doEdge == false) {
							doEdge = true;
							if (v1) {
								if (tempNode.getNbr(e.getFaces().get(0))
										.getNbrs(dirList).contains(tempNode))
									doEdge = false;
							}

							if (v2) {
								if (tempNode.getNbr(e).getNbrs(dirList)
										.contains(tempNode))
									doEdge = false;
							}
							if (v3) {
								if (tempNode.getNbr(e.getFaces().get(1))
										.getNbrs(dirList).contains(tempNode))
									doEdge = false;
							}
						}

						// if we have not found a node with the same or bigger,
						// we still need to check if ther
						// is not a smaller node so we don't draw a face double
						if (v1 != true && v1b != true) {
							for (OctNode t : tempNode.getAllSmallerNbrs(
									myOctree, e.getFaces().get(0))) {
								if (qef.get(t) != null) {
									doEdge = false;
								}
							}
						}
						if (v2 != true && v2b != true) {
							for (OctNode t : tempNode.getAllSmallerNbrs(
									myOctree, e)) {
								if (qef.get(t) != null) {
									doEdge = false;
								}
							}
						}
						if (v3 != true && v3b != true) {
							for (OctNode t : tempNode.getAllSmallerNbrs(
									myOctree, e.getFaces().get(1))) {
								if (qef.get(t) != null) {
									doEdge = false;
								}
							}
						}
					}
					// if we can draw the edge, do it now
					if (doEdge == true) {
						if ((tV1 == null && tV2 != null && tV3 != null) || tV1 == tV0) {
							octMesh.faceList.add(new OctMeshFace(tV0, tV2,
									tV3));
						} else if ((tV2 == null && tV1 != null && tV3 != null) || tV2 == tV0) {
							octMesh.faceList.add(new OctMeshFace(tV0, tV1,
									tV3));
						} else if ((tV3 == null && tV2 != null && tV1 != null) || tV3 == tV0) {
							octMesh.faceList.add(new OctMeshFace(tV0, tV1,
									tV2));
						} else if (tV3 != null && tV2 != null && tV1 != null && tV0 != null && tV2 != tV3 && tV2 != tV1 && tV1 != tV3) {
							octMesh.faceList.add(new OctMeshFace(tV0, tV1, tV2,
									tV3));
						
					} else if (tV2 == tV1 && tV2 != null&& tV1 != null) {
						octMesh.faceList.add(new OctMeshFace(tV0, tV2,
								tV3));
					
				} else if (tV2 == tV3&& tV2 != null&& tV3 != null) {
					octMesh.faceList.add(new OctMeshFace(tV0, tV1,
							tV3));
				
			} else if (tV1 == tV3&& tV1 != null&& tV2 != null) {
				octMesh.faceList.add(new OctMeshFace(tV0, tV1, tV2
						));
			}
		}
	}
}
}
}

public OctXYZ getGradient(OctXYZ _v) {
	OctXYZ g = new OctXYZ();
	int r = 2;
	for (int i = -r; i <= r; i++) {
		for (int j = -r; j <= r; j++) {
			for (int k = -r; k <= r; k++) {
				float fac = 0.5f / (1 << myOctree.getMaxDepth());
				OctXYZ p = new OctXYZ();
				p = _v.add(new OctXYZ(fac * i * myOctree.getDimension().x,
						fac * j * myOctree.getDimension().y, fac * k
						* myOctree.getDimension().z));
				float ff = myFunction.compute(p.x, p.y, p.z);
				g.addSelf(_v.sub(p).scale(ff));
			}
		}
	}
	g = g.normalize().scale(-1f);
	return new OctXYZ(g);
}

public void drawGradient() {
	for (OctNode n : myOctree.nodeList) {
		p5.beginShape(PApplet.LINES);
		float fac = 0.5f / (1 << n.getLevel());
		OctXYZ vv = n.getCenter().toXYZ(myOctree);
		OctXYZ nn = this.getGradient(vv).scale(fac).scale(myOctree.getDimension().getNorm()).scale(2f);
		p5.vertex(vv.x, vv.y, vv.z);
		p5.vertex(vv.x + nn.x, vv.y + nn.y, vv.z + nn.z);
		p5.endShape();
	}
}

public void draw() {
	for (OctMeshFace tempFace : octMesh.faceList) {
		if (tempFace.type == 4) {
			p5.beginShape(PApplet.QUADS);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.vertex(tempFace.v3.x, tempFace.v3.y, tempFace.v3.z);
			p5.endShape();
		}
		if (tempFace.type == 3) {
			p5.beginShape(PApplet.TRIANGLES);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.endShape();
		}
	}
}
}