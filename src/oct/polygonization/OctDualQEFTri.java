package oct.polygonization;

import java.util.ArrayList;
import java.util.HashMap;

import oct.enums.OCT_EDGE;
import oct.enums.OCT_ENUM;
import oct.enums.OCT_FACE;
import oct.enums.OCT_VERTEX;
import oct.math.OctFunction;
import oct.math.Jama.Matrix;
import oct.math.Jama.SingularValueDecomposition;
import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctRST;
import oct.octree.OctXYZ;
import processing.core.PApplet;

// TODO Solve problem when only two node, why so slow???? look at others as well

public class OctDualQEFTri extends OctPoly {

	private float qefClip = 0.1f;

	private PApplet p5;
	private OctOctree myOctree;
	public OctMesh octMesh;

	private OctFunction myFunction;
	private float threshold;

	private OctXYZ[] vertexList;
	private OctXYZ[] gList;

	private float nodeSizeX;
	private float nodeSizeY;
	private float nodeSizeZ;

	private HashMap<OctNode, OctXYZ> qef = new HashMap<OctNode, OctXYZ>();

	private float[] valueAt = new float[8];

	private Matrix A;
	private Matrix B;

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

	public OctDualQEFTri(PApplet _p5, OctOctree _octree,
			OctFunction _myFunction, float _threshold) {
		p5 = _p5;
		myOctree = _octree;
		myFunction = _myFunction;
		threshold = _threshold;
		closeValue = threshold;
		octMesh = new OctMesh(p5);
		vertexList = new OctXYZ[12];
		gList = new OctXYZ[12];
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

	public float getQEFclip() {
		return qefClip;
	}

	public void setQEFclip(float _value) {
		qefClip = _value;
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
					if (close == 1
							&& (p.x <= x1 || p.x >= x2 || p.y <= y1
									|| p.y >= y2 || p.z <= z1 || p.z >= z2)) {
						ff = closeValue;
					}
					g.addSelf(_v.sub(p).scale(ff));
				}
			}
		}
		g = g.normalize().scale(-1f);
		return new OctXYZ(g);
	}

	public void setup() {

		for (OctNode tempNode : myOctree.nodeList) {

			A = new Matrix(12, 3, 0);
			B = new Matrix(12, 1, 0);
			vertexList = new OctXYZ[12];
			gList = new OctXYZ[12];
			for (int c = 0; c < 12; c++) {
				vertexList[c] = null;
				gList[c] = null;
			}

			OctXYZ masspoint = tempNode.getCenter().toXYZ(myOctree);

			int posx = tempNode.getCodeR();
			int posy = tempNode.getCodeS();
			int posz = tempNode.getCodeT();
			nodeSizeX = 1f / (1 << tempNode.getLevel());
			nodeSizeY = 1f / (1 << tempNode.getLevel());
			nodeSizeZ = 1f / (1 << tempNode.getLevel());

			// could be pre-computed, since they are necessary anyway to include
			// or not the node
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

			for (OCT_EDGE e : OCT_EDGE.values()) {
				float adapt = 0;
				boolean bool1 = false;
				boolean bool2 = false;
				if (valueAt[e.getV0().getOrdinal()] * flip > threshold * flip) {
					bool1 = true;
				}
				if (valueAt[e.getV1().getOrdinal()] * flip > threshold * flip) {
					bool2 = true;
				}
				if (bool1 != bool2) {
					adapt = PApplet.map(threshold, valueAt[e.getV0()
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
					vX.addSelf((float) (posx + 0.5) * nodeSizeX,
							(float) (posy + 0.5) * nodeSizeY,
							(float) (posz + 0.5) * nodeSizeZ);

					OctXYZ g = getGradient(vX.toXYZ(myOctree));

					A.set(e.getOrdinal(), 0, g.x);
					A.set(e.getOrdinal(), 1, g.y);
					A.set(e.getOrdinal(), 2, g.z);
					// B.set(e.getOrdinal(), 0, g.dot(vX.toXYZ(myOctree).sub(masspoint)));
					vertexList[e.getOrdinal()] = vX.toXYZ(myOctree);
					gList[e.getOrdinal()] = g;
				}
			}

			masspoint = new OctXYZ();
			float count = 0;
			for (int a = 0; a < 12; a++) {
				if (vertexList[a] != null) {
					masspoint.addSelf(vertexList[a]);
					count++;
				}
			}
			masspoint.scaleSelf(1f / count);
			for (int a = 0; a < 12; a++) {
				if (vertexList[a] != null) {
					B.set(a, 0, gList[a].dot(vertexList[a].sub(masspoint)));
				}
			}

			// from
			// https://github.com/jarrettchisholm/glr/blob/master/src/terrain/dual_contouring/Qef.cpp

			SingularValueDecomposition mySvd;
			Matrix At = new Matrix(3, 12, 0);
			At = A.transpose();
			Matrix AtA = new Matrix(12, 12, 0);
			AtA = A.times(At);
			mySvd = new SingularValueDecomposition(AtA);
			OctXYZ vector = new OctXYZ((float) mySvd.getSingularValues()[0],
					(float) mySvd.getSingularValues()[1],
					(float) mySvd.getSingularValues()[2]);

			Matrix UU = new Matrix(12, 3);
			Matrix VV = new Matrix(3, 3);
			Matrix DD = new Matrix(3, 3);
			Matrix svdsolve = new Matrix(3, 1);

			UU = A.svd().getU();
			VV = A.svd().getV();
			DD = A.svd().getS();

			float param = qefClip;

			if (DD.get(2, 2) < param) {
				DD.set(2, 2, 0.0f);
			}
			if (DD.get(1, 1) < param) {
				DD.set(1, 1, 0.0f);
			}
			if (DD.get(0, 0) < param) {
				DD.set(0, 0, 0.0f);
			}

			int rows = 12;
			double w[] = new double[3];
			for (int ii = 0; ii < rows; ii++) {
				if (B.get(ii, 0) != 0.0)
					for (int jj = 0; jj < 3; jj++)
						w[jj] += B.get(ii, 0) * UU.get(ii, jj);

			}

			// // // introduce non-zero singular values in d into w
			for (int ii = 0; ii < 3; ii++) {
				if (DD.get(ii, ii) != 0.0)
					w[ii] /= DD.get(ii, ii);
			}

			// // // compute result vector x = V * w
			for (int ii = 0; ii < 3; ii++) {
				double tmp = 0.0;
				for (int jj = 0; jj < 3; jj++) {
					tmp += (w[jj] * VV.get(ii, jj));
				}
				svdsolve.set(ii, 0, tmp);
			}

			vector = new OctXYZ((float) svdsolve.get(0, 0), (float) svdsolve.get(1, 0), (float) svdsolve.get(2, 0));

			vector = (vector.scale(adaptMultiplier)).add(masspoint);
			
			qef.put(new OctNode(tempNode),vector);
			
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
					if (e == OCT_EDGE.RD || e == OCT_EDGE.RF || e == OCT_EDGE.FD) doEdge = true;
					
					OctXYZ tV0 = qef.get(tempNode);
					OctXYZ tV1 = null;
					OctXYZ tV2 = null;
					OctXYZ tV3 = null;
					
					if (myOctree.getMaxDepth() == myOctree.getMinDepth() && doEdge == true) {
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
					}
					else {
					// check if there is same level neighbors, or lager level neighbors
					if (qef.get(tempNode.getNbr(e.getFaces().get(0))) != null) {
						tV1 = qef.get(tempNode.getNbr(e.getFaces().get(0)));
						v1 = true;
					} else {
						for (OctNode t : tempNode.getAllBiggerNbrs(myOctree, e
								.getFaces().get(0))) {
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
						for (OctNode t : tempNode.getAllBiggerNbrs(myOctree, e)) {
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
						for (OctNode t : tempNode.getAllBiggerNbrs(myOctree, e
								.getFaces().get(1))) {
							if (qef.get(t) != null) {
								tV3 = qef.get(t);
								v3b = true;
							}
						}
					}
					
					// if we are not going in the RD/RF/FD direction, we still need to draw the triangle 
					// if it would not be created otherwise, ie if ther is not a node with the same level
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
					
					// if we have not found a node with the same or bigger, we still need to check if ther
					// is not a smaller node so we don't draw a face double
					if (v1 != true && v1b != true) {
						for (OctNode t : tempNode.getAllSmallerNbrs(myOctree, e
								.getFaces().get(0))) {
							if (qef.get(t) != null) {
								doEdge = false;
							}
						}
					}
					if (v2 != true && v2b != true) {
						for (OctNode t : tempNode
								.getAllSmallerNbrs(myOctree, e)) {
							if (qef.get(t) != null) {
								doEdge = false;
							}
						}
					}
					if (v3 != true && v3b != true) {
						for (OctNode t : tempNode.getAllSmallerNbrs(myOctree, e
								.getFaces().get(1))) {
							if (qef.get(t) != null) {
								doEdge = false;
							}
						}
					}
					}
					// if we can draw the edge, do it now
					if (doEdge == true) {
						if (tV1 == null && tV2 != null && tV3 != null) {
							octMesh.faceList
									.add(new OctMeshFace(tV0, tV2, tV3));
						} else if (tV2 == null && tV1 != null && tV3 != null) {
							octMesh.faceList
									.add(new OctMeshFace(tV0, tV1, tV3));
						} else if (tV3 == null && tV2 != null && tV1 != null) {
							octMesh.faceList
									.add(new OctMeshFace(tV0, tV1, tV2));
						} else if (tV3 != null && tV2 != null && tV1 != null) {
							octMesh.faceList
									.add(new OctMeshFace(tV0, tV1, tV2));
							octMesh.faceList
									.add(new OctMeshFace(tV0, tV2, tV3));
						}
					}
				}
			}
		}
	}

	// }

	public void draw() {
		for (OctMeshFace tempFace : octMesh.faceList) {
			p5.beginShape(PApplet.TRIANGLES);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.endShape();
		}
	}
}