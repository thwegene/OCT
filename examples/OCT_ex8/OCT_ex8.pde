// OCT Octree
// v0.1.1
// Thomas Wegener
// Example 8 - save and open nodes

// press U and D to move up and down
// press S and B to add a smaller or bigger node
// click mouse to create the node
// press X to export and save
// press O to open a previously saved version

PGraphicsOpenGL pg; 
PJOGL pgl;
GL2ES2 gl;
GLU glu;

double[] projMatrix;
double[] mvMatrix;

int level = 2; // the level of the node you'd like to add, press mouse S and B to modifiy
int layer = 0; // the layer at which you'l position it, press mouse U and D to modifiy
OctNode currentNode = new OctNode(0, 0, layer, level);

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
OctOctree helperOctree = new OctOctree(this, 1000, 1000, 1000); // dim x, y, z

// Read/write
import java.io.*;
FileOutputStream fileOut;
ObjectOutputStream out;
FileInputStream fileIn;
ObjectInputStream in;

void setup() {
  // Init camera
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateX(-HALF_PI);
  cam.rotateY(-HALF_PI/3);
  projMatrix = new double[16];
  mvMatrix = new double[16]; 
  pg = (PGraphicsOpenGL) g; 
  pgl = (PJOGL) beginPGL();  
  gl = pgl.gl.getGL2ES2();
  glu = new GLU();
  generateHelperOctree();
}

void draw() {
  helperOctree.selectedNodes.clear();
  double[] pos = getMousePosition(mouseX, mouseY);
  OctXYZ clickPosition = new OctXYZ((float)pos[0], (float)pos[1], (float)pos[2]);
  helperOctree.selectNode(new OctXYZ(cam.getPosition()[0], cam.getPosition()[1], cam.getPosition()[2]), new OctXYZ(clickPosition.x-cam.getPosition()[0], clickPosition.y-cam.getPosition()[1], clickPosition.z-cam.getPosition()[2]) );
  for (OctNode tempNode : helperOctree.selectedNodes) {
    currentNode = tempNode;
  }

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
  fill(255, 0, 0, 100);
  currentNode.drawFaces(myOctree);
}

void generateHelperOctree() {
  helperOctree.clearNodes();
  for (int n = (int) (1 <<level); --n >= 0; ) {
    for (int m = (int) (1 << level); --m >= 0; ) {
      OctNode tempNode = new OctNode(m, n, layer, level);
      helperOctree.addNode(tempNode);
    }
  }
}

void mouseClicked() {
  myOctree.addNode(currentNode);
}

void keyPressed() {
  if (key == 'D' || key == 'd') { // Press D to move layer down
    if (layer > 0) layer--;
    generateHelperOctree();
  }
  if (key == 'U' || key == 'u') { // Press U to move layer up
    if (layer < pow(2, level)-1) layer++;
    generateHelperOctree();
  }
  if (key == 'B' || key == 'b') { // Press B to get a bigger node
    if (level > 0) {
      level--;
      layer = layer/2;
    }
    generateHelperOctree();
  }
  if (key == 'S' || key == 's') { // Press S to get a smaller node
    if (level < myOctree.getMaxDepth()) {
      level++;
      layer = layer*2;
    }
    generateHelperOctree();
  }
  if (key == 'O' || key == 'o') { // Press O to open file
    readFile(); // save a list of nodes
    // readFileOctree(); // save the whole octree
  }
  if (key == 'X' || key == 'x') { // Press X to export file
    writeFile(); // read a list of nodes
    //writeFileOctree(); // read the whole octree
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

void readFile() {
  myOctree.clearNodes();
  try {
    fileIn = new FileInputStream(sketchPath("")+"nodes.ser");
    in = new ObjectInputStream(fileIn);

    OctNode tempNode = (OctNode) in.readObject();
    try {
      while ((tempNode = (OctNode) in.readObject()) != null) {
        myOctree.addNode(tempNode);
      }
    }
    catch (EOFException e) {
    } 
    finally {
      in.close();
    }  
    fileIn.close();
  }
  catch(IOException i) {
    i.printStackTrace();
    return;
  }
  catch(ClassNotFoundException c) {
    System.out.println("OctNode class not found");
    c.printStackTrace();
    return;
  }
}

void writeFile() {
  try {
    fileOut = new FileOutputStream(sketchPath("")+"nodes.ser");
    out = new ObjectOutputStream(fileOut);
    for (OctNode tempNode : myOctree.nodeList) {
      out.writeObject(tempNode);
    }
    out.close();
    fileOut.close();
    System.out.printf("Serialized data is saved in nodes.ser");
  }
  catch(IOException i) {
    i.printStackTrace();
  }
}


void readFileOctree() {
  myOctree.clearNodes();
  try {
    fileIn = new FileInputStream(sketchPath("")+"octree.ser");
    in = new ObjectInputStream(fileIn);
    myOctree = new OctOctree(this,(OctOctree) in.readObject());
    in.close();  
    fileIn.close();
  }
  catch(IOException i) {
    i.printStackTrace();
    return;
  }
  catch(ClassNotFoundException c) {
    System.out.println("OctOctree class not found");
    c.printStackTrace();
    return;
  }
}

void writeFileOctree() {
  try {
    fileOut = new FileOutputStream(sketchPath("")+"octree.ser");
    out = new ObjectOutputStream(fileOut);
    out.writeObject(myOctree);
    out.close();
    fileOut.close();
    System.out.printf("Serialized data is saved in octree.ser");
  }
  catch(IOException i) {
    i.printStackTrace();
  }
}