// OCT Octree
// v0.1
// Thomas Wegener
// Example 5 - opengl ray picking work in progress

// click to select a node, press D to delete, press M to add the 8 corner nodes
// cannot delete nodes outside of the octree bounds

PGraphicsOpenGL pg; 
PJOGL pgl;
GL2ES2 gl;
GLU glu;

double[] projMatrix;
double[] mvMatrix;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;

// Import OCT Octree
import oct.enums.*;
import oct.math.Jama.*;
import oct.math.Jama.util.*;
import oct.math.*;
import oct.octree.*;
import oct.polygonization.*;
import oct.utils.*;

// Setup camera using peasycam by Jonathan Feinberg
import peasy.*;
PeasyCam cam;

// Setup one octree
OctOctree myOctree = new OctOctree(this, 1000, 1000, 1000); // dim x, y, z

void setup() {
  // Init camera
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateX(-HALF_PI);
  cam.rotateY(-HALF_PI/3);
  projMatrix = new double[16];
  mvMatrix = new double[16]; 
  pg = (PGraphicsOpenGL) g;
  //pgl = beginPGL();  
  pgl = (PJOGL) beginPGL();  
  //gl = ((PJOGL) pgl).gl.getGL2();
  gl = pgl.gl.getGL2ES2();
  glu = new GLU();

  //myOctree.setCenter(0,0,0); // does not work with picking
  myOctree.setMinDepth(0);
  // The simplest way to add a node...
  myOctree.addNode(new OctNode(1, 1, 0, 1)); // pos x, y, z, level

  // ... or multiple nodes.
  for (int i=0; i<8; i++) {
    OctNode tempNode = new OctNode(i, i, i, 3);
    myOctree.addNode(tempNode);
  }
  // Create nodes by getting children of a bigger node
  // a) create a node 
  OctNode tempNode = new OctNode(0, 3, 0, 2);
  // b) get all the children of the tempNode three levels down and add them to the tree
  myOctree.addNode(tempNode.getChildren(4)); 
  // a) create a node and add it to the octree
  OctNode tempNode2 = new OctNode(3, 0, 3, 2);
  myOctree.addNode(tempNode2);
  // b) get all the children of tempNode2 adjacent to the top face, two levels down
  myOctree.addNode(tempNode2.getChildren(2, OCT_FACE.U));

  // Create nodes by getting neighors, smaller or bigger or of the same size
  // a) create node and add it to the tree
  OctNode tempNode3 = new OctNode(0, 0, 3, 2);
  myOctree.addNode(tempNode3);
  // b) get the neighbors
  myOctree.addNode(tempNode3.getNbr(OCT_FACE.L)); 
  myOctree.addNode(tempNode3.getBiggerNbrs(1, OCT_FACE.U)); 
  myOctree.addNode(tempNode3.getSmallerNbrs(2, OCT_FACE.D)); 
  myOctree.addNode(tempNode3.getSmallerNbrs(3, OCT_FACE.F));
}

void draw() {
  background(245, 245, 240);  
  ambientLight(48, 48, 48);
  lightSpecular(230, 230, 230);
  directionalLight(0, 200, 200, 1, 0, 0);
  directionalLight(200, 0, 200, 0, 1, 0);
  directionalLight(200, 200, 0, 0, 0, 1);
  directionalLight(0, 200, 200, -1, 0, 0);
  directionalLight(200, 0, 200, 0, -1, 0);
  directionalLight(200, 200, 0, 0, 0, -1);
  specular(200, 200, 200);
  shininess(16.0f);
  stroke(166);
  strokeWeight(1);
  noFill();
  myOctree.drawBoundingBox();
  strokeWeight(5);
  myOctree.drawAxis();
  stroke(50);
  strokeWeight(0.5);
  fill(255, 255, 255);
  myOctree.drawAsFaces();
}

void mouseClicked() {
  double[] pos = getMousePosition(mouseX, mouseY);
  OctXYZ clickPosition = new OctXYZ((float)pos[0], (float)pos[1], (float)pos[2]);
  myOctree.selectNode(new OctXYZ(cam.getPosition()[0], cam.getPosition()[1], cam.getPosition()[2]), new OctXYZ(clickPosition.x-cam.getPosition()[0], clickPosition.y-cam.getPosition()[1], clickPosition.z-cam.getPosition()[2]) );
}

void keyPressed() {
  if (key == 'D' || key == 'd') { // Press D to delete selected nodes
    for (OctNode tempNode : myOctree.selectedNodes) {
      myOctree.deleteNode(tempNode);
    }
    myOctree.selectedNodes.clear();
  }
  if (key == 'M' || key == 'm') { // Press M to add corner nodes
    for (OctNode tempNode : myOctree.selectedNodes) {
      myOctree.addNode(tempNode.getSmallerNbrs(1, OCT_VERTEX.getAll()));
    }
    myOctree.selectedNodes.clear();
  }
}

public double[] getMousePosition(int x, int y) {
  int viewport[] = new int[4];
  float winX, winY;
  double wcoord[] = new double[4];
  copyMatrices();
  gl.glGetIntegerv( GL2.GL_VIEWPORT, viewport, 0 );
  winX = (float)x;
  winY = (float)viewport[3] - (float)y;
  glu.gluUnProject( winX, winY, 0.0, mvMatrix, 0, projMatrix, 0, viewport, 0, wcoord, 0);
  return wcoord;
}

void copyMatrices() {
  PGraphicsOpenGL pg = (PGraphicsOpenGL) g;

  projMatrix[0] = pg.projection.m00;
  projMatrix[1] = pg.projection.m10;
  projMatrix[2] = pg.projection.m20;
  projMatrix[3] = pg.projection.m30;

  projMatrix[4] = pg.projection.m01;
  projMatrix[5] = pg.projection.m11;
  projMatrix[6] = pg.projection.m21;
  projMatrix[7] = pg.projection.m31;

  projMatrix[8] = pg.projection.m02;
  projMatrix[9] = pg.projection.m12;
  projMatrix[10] = pg.projection.m22;
  projMatrix[11] = pg.projection.m32;

  projMatrix[12] = pg.projection.m03;
  projMatrix[13] = pg.projection.m13;
  projMatrix[14] = pg.projection.m23;
  projMatrix[15] = pg.projection.m33;

  mvMatrix[0] = pg.modelview.m00;
  mvMatrix[1] = pg.modelview.m10;
  mvMatrix[2] = pg.modelview.m20;
  mvMatrix[3] = pg.modelview.m30;

  mvMatrix[4] = pg.modelview.m01;
  mvMatrix[5] = pg.modelview.m11;
  mvMatrix[6] = pg.modelview.m21;
  mvMatrix[7] = pg.modelview.m31;

  mvMatrix[8] = pg.modelview.m02;
  mvMatrix[9] = pg.modelview.m12;
  mvMatrix[10] = pg.modelview.m22;
  mvMatrix[11] = pg.modelview.m32;

  mvMatrix[12] = pg.modelview.m03;
  mvMatrix[13] = pg.modelview.m13;
  mvMatrix[14] = pg.modelview.m23;
  mvMatrix[15] = pg.modelview.m33;
}