package oct.polygonization;

import java.util.ArrayList;

import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctRST;
import oct.octree.OctXYZ;
import oct.enums.OCT_EDGE;
import oct.enums.OCT_FACE;
import oct.enums.OCT_VERTEX;
import oct.math.OctFunction;
import processing.core.PApplet;

public class OctDualFan extends OctPoly {

	private PApplet p5;
	private OctOctree myOctree;
	public OctMesh octMesh;

	private float threshold;
	private OctFunction myFunction;

	private OctXYZ[] vertexList;
	private OctXYZ masspoint;
	private float[] valueAt = new float[8];

	private float nodeSizeX;
	private float nodeSizeY;
	private float nodeSizeZ;

	private int flip = -1;
	private int close = -1;
	private float adaptMultiplier = 1;
	private float closeValue = 1;

	public OctDualFan(PApplet _p5, OctOctree _octree, OctFunction myFunction,
			float _threshold) {
		p5 = _p5;
		myOctree = _octree;
		vertexList = new OctXYZ[12];
		this.myFunction = myFunction;
		threshold = _threshold;
		closeValue = threshold;
		octMesh = new OctMesh(p5);
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

		OctXYZ temp = myOctree.getOrigin().add(myOctree.getDimension());
		float x1 = myOctree.getOrigin().x;
		float y1 = myOctree.getOrigin().y;
		float z1 = myOctree.getOrigin().z;
		float x2 = temp.x;
		float y2 = temp.y;
		float z2 = temp.z;

		for (OctNode tempNode : myOctree.nodeList) {

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

			int r = tempNode.getCodeR();
			int s = tempNode.getCodeS();
			int t = tempNode.getCodeT();

			nodeSizeX = 1f / (1 << tempNode.getLevel());
			nodeSizeY = 1f / (1 << tempNode.getLevel());
			nodeSizeZ = 1f / (1 << tempNode.getLevel());

			vertexList = new OctXYZ[12];

			masspoint = new OctXYZ();
			masspoint = tempNode.getCenter().toXYZ(myOctree);

			//if (score != 0 && score != 8) {
				for (OCT_EDGE e : OCT_EDGE.values()) {
					boolean bool1 = false;
					boolean bool2 = false;
					if (valueAt[e.getV0().getOrdinal()] * flip > threshold
							* flip) {
						bool1 = true;
					}
					if (valueAt[e.getV1().getOrdinal()] * flip > threshold
							* flip) {
						bool2 = true;
					}
					if (bool1 != bool2) {
						float adapt = PApplet.map(threshold, valueAt[e.getV0()
						                                             .getOrdinal()],
						                                             valueAt[e.getV1().getOrdinal()], -1, 1);

						// 1 position inside the node
						OctRST vX = new OctRST((e.r) * nodeSizeX / 2, (e.s)
								* nodeSizeY / 2, (e.t) * nodeSizeZ / 2);

						// 2 smooth on the edge
						if (e.r == 0)
							vX.r += adapt  * nodeSizeX / 2;
						if (e.s == 0)
							vX.s += adapt  * nodeSizeY / 2;
						if (e.t == 0)
							vX.t += adapt  * nodeSizeZ / 2;

						// 3 position inside the octree
						vX.addSelf((float) (r + 0.5) * nodeSizeX,
								(float) (s + 0.5) * nodeSizeY,
								(float) (t + 0.5) * nodeSizeZ);
						// vX = new OctXYZ(vX.x, vX.y, vX.z);
						vertexList[e.getOrdinal()] = vX.toXYZ(myOctree);
						// PApplet.println(vX.x);
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

				solution = solution.scale(adaptMultiplier).add(masspoint.scale((1-adaptMultiplier)));
				
				for (OCT_FACE f : OCT_FACE.values()) {
					ArrayList<OCT_EDGE> list = f.getEdges();
					int ok = 0;
					for (int i = 0; i < list.size(); i++) {
						if (vertexList[list.get(i).getOrdinal()] != null
								&& vertexList[list.get((i + 1) % 4)
								              .getOrdinal()] != null) {
							OctXYZ tV0 = solution;
							OctXYZ tV1 = vertexList[list.get(i).getOrdinal()];
							OctXYZ tV2 = vertexList[list.get((i + 1) % 4)
							                        .getOrdinal()];
							octMesh.faceList
							.add(new OctMeshFace(tV0, tV1, tV2));
							ok = 1;
						}
					}
					if (ok == 0) {
						for (int i = 0; i < list.size(); i++) {

							if (vertexList[list.get(i).getOrdinal()] != null
									&& vertexList[list.get((i + 2) % 4)
									              .getOrdinal()] != null) {
								OctXYZ tV0 = solution;
								OctXYZ tV1 = vertexList[list.get(i)
								                        .getOrdinal()];
								OctXYZ tV2 = vertexList[list.get((i + 2) % 4)
								                        .getOrdinal()];
								octMesh.faceList.add(new OctMeshFace(tV0, tV1,
										tV2));
							}
						}
					}
				}
			//}
		}
	}

	public OctXYZ getGradient(OctXYZ _v) {
		OctXYZ g = new OctXYZ();
		int r = 3;
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
			p5.beginShape(PApplet.TRIANGLES);
			p5.vertex(tempFace.v0.x, tempFace.v0.y, tempFace.v0.z);
			p5.vertex(tempFace.v1.x, tempFace.v1.y, tempFace.v1.z);
			p5.vertex(tempFace.v2.x, tempFace.v2.y, tempFace.v2.z);
			p5.endShape();
		}
	}

}