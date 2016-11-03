package oct.polygonization;

import static oct.utils.OctTables.marchingCubesLUT;
import oct.enums.OCT_EDGE;
import oct.enums.OCT_VERTEX;
import oct.octree.OctNode;
import oct.octree.OctOctree;
import oct.octree.OctRST;
import oct.octree.OctXYZ;
import oct.math.OctFunction;
import processing.core.PApplet;

public class OctMarchingCubes extends OctPoly {

	private PApplet p5;
	private OctOctree myOctree;
	public OctMesh octMesh;

	private OctFunction myFunction;
	private float threshold = 1;
	private OctMeshFace face;
	private OctRST[] tempVertex;
	
	private float[] valueAt = new float[8];
	private float nodeSizeX;
	private float nodeSizeY;
	private float nodeSizeZ;

	private int flip = -1;
	private int close = -1;
	private float adaptMultiplier = 1;
	private float closeValue = 1;
	
	// //////////////////////////////////////////////////////////
	// CREATOR
	// //////////////////////////////////////////////////////////

	public OctMarchingCubes(PApplet _p5, OctOctree _myOctree, OctFunction _f,
			float _threshold) {

		p5 = _p5;
		myOctree = _myOctree;
		octMesh = new OctMesh(p5);

		myFunction = _f;
		threshold = _threshold;
		closeValue = threshold;

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
				if (close == 1 && (
						tempNode.getVertex(v).toXYZ(myOctree).x == x1 ||
						tempNode.getVertex(v).toXYZ(myOctree).x == x2 ||
						tempNode.getVertex(v).toXYZ(myOctree).y == y1 ||
						tempNode.getVertex(v).toXYZ(myOctree).y == y2 ||
						tempNode.getVertex(v).toXYZ(myOctree).z == z1 ||
						tempNode.getVertex(v).toXYZ(myOctree).z == z2)) {
					valueAt[v.getOrdinal()] = closeValue;
				} else {
					valueAt[v.getOrdinal()] = myFunction.compute(tempNode
							.getVertex(v).toXYZ(myOctree).x, tempNode
							.getVertex(v).toXYZ(myOctree).y, tempNode
							.getVertex(v).toXYZ(myOctree).z);
				}
			}

			nodeSizeX = 1f / (1 << tempNode.getLevel());
			nodeSizeY = 1f / (1 << tempNode.getLevel());
			nodeSizeZ = 1f / (1 << tempNode.getLevel());

			int r = tempNode.getCodeR();
			int s = tempNode.getCodeS();
			int t = tempNode.getCodeT();

			int isoCase = 0;
			if (flip >0)
				isoCase = marchingCubesIndexSmaller(valueAt);
			if (flip <0)
				isoCase = marchingCubesIndexLarger(valueAt);

			int j = 0;
			tempVertex = new OctRST[3];

			while (marchingCubesLUT[isoCase][j] >= 0) {
				for (int k = 0; k < 3; k++) {
					int i = marchingCubesLUT[isoCase][j + k];
					float adapt = PApplet.map(threshold, valueAt[OCT_EDGE
							.get(i).getV0().ordinal()], valueAt[OCT_EDGE.get(i)
							.getV1().ordinal()], -nodeSizeX / 2, nodeSizeX / 2);

					// 1 position inside the node
					tempVertex[k] = new OctRST((OCT_EDGE.get(i).r) * nodeSizeX
							/ 2, (OCT_EDGE.get(i).s) * nodeSizeY / 2,
							(OCT_EDGE.get(i).t) * nodeSizeZ / 2);

					// 2 smooth on the edge
					if (OCT_EDGE.get(i).r == 0)
						tempVertex[k].r += adapt*adaptMultiplier;// * nodeSizeX / 2;
					if (OCT_EDGE.get(i).s == 0)
						tempVertex[k].s += adapt*adaptMultiplier;// * nodeSizeY / 2;
					if (OCT_EDGE.get(i).t == 0)
						tempVertex[k].t += adapt*adaptMultiplier;// * nodeSizeZ / 2;

					// 3 position inside the octree
					tempVertex[k].addSelf((float) (r + 0.5) * nodeSizeX,
							(float) (s + 0.5) * nodeSizeY, (float) (t + 0.5)
									* nodeSizeZ);
				}

				face = new OctMeshFace(
						tempVertex[0].toXYZ(myOctree),
						tempVertex[1].toXYZ(myOctree),
						tempVertex[2].toXYZ(myOctree));
				octMesh.faceList.add(face);
				j = j + 3;
			}
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

	private int marchingCubesIndexSmaller(float[] valueAt) {
		int i = 0;
		if (valueAt[0] <= threshold) {
			i |= 1;
		}
		if (valueAt[4] <= threshold) {
			i |= 16;
		}
		if (valueAt[3] <= threshold) {
			i |= 8;
		}
		if (valueAt[7] <= threshold) {
			i |= 128;
		}
		if (valueAt[1] <= threshold) {
			i |= 2;
		}
		if (valueAt[5] <= threshold) {
			i |= 32;
		}
		if (valueAt[2] <= threshold) {
			i |= 4;
		}
		if (valueAt[6] <= threshold) {
			i |= 64;
		}
		return i;
	}

	private int marchingCubesIndexLarger(float[] valueAt) {
		int i = 0;
		if (valueAt[0] >= threshold) {
			i |= 1;
		}
		if (valueAt[4] >= threshold) {
			i |= 16;
		}
		if (valueAt[3] >= threshold) {
			i |= 8;
		}
		if (valueAt[7] >= threshold) {
			i |= 128;
		}
		if (valueAt[1] >= threshold) {
			i |= 2;
		}
		if (valueAt[5] >= threshold) {
			i |= 32;
		}
		if (valueAt[2] >= threshold) {
			i |= 4;
		}
		if (valueAt[6] >= threshold) {
			i |= 64;
		}
		return i;
	}
}