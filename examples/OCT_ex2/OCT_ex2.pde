// OCT Octree
// v0.1
// Thomas Wegener
// Example 2 - recursion and representation

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

// Setup on octree
OctOctree myOctree = new OctOctree(this, 1000, 1000, 1000);

// Define one way of representing the octree, see at the end of draw() other possibilities
// with quads
OctPoly myCuberille = new OctCuberilleQuad(this,myOctree);
// with triangles
//OctPoly myCuberille = new OctCuberilleTri(this,myOctree);

void setup() {
  // Init camera
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateX(-HALF_PI);
  cam.rotateY(-HALF_PI/3);
  
  myOctree.setCenter(0,0,0);
  
  // Create first node for the reursion.
  myOctree.addNode(new OctNode(0, 0, 0, 0));
  
  // Try different values for k, from 1 to 5
  int k = 3;
  for(int i = 0; i<k; i++) {
    // Try uncommenting/commenting different functions
    recursion1();
    //recursion2();
    //recursion3();
  }
 
  // Use this once all the nodes are created.
  myCuberille.setup();
  
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
  fill(255);
  // if you use this, all faces of all nodes are represented
  //myOctree.drawAsFaces();
  // with this, only the ouside faces are represented, so it's faster adn the export is cleaner
  myCuberille.draw();
}

void recursion1() {
  ArrayList<OctNode> newNodes = new ArrayList();
  for (OctNode tempNode : myOctree.nodeList){
     newNodes.addAll(tempNode.getChildren(2,OCT_EDGE.getAll()));
  }
  myOctree.clearNodes();
  myOctree.addNode(newNodes);
}

void recursion2() {
  ArrayList<OctNode> newNodes = new ArrayList();
  ArrayList<OCT_FACE> faces = new ArrayList();
  faces.add(OCT_FACE.D);
  faces.add(OCT_FACE.R);
  faces.add(OCT_FACE.U);
  for (OctNode tempNode : myOctree.nodeList){
     newNodes.addAll(tempNode.getChildren(2,faces));
  }
  myOctree.clearNodes();
  myOctree.addNode(newNodes);
}

void recursion3() {
  ArrayList<OctNode> newNodes = new ArrayList();
  for (OctNode tempNode : myOctree.nodeList){
     newNodes.addAll(tempNode.getChildren(2,OCT_OCTANT.getAll()));
     newNodes.addAll(tempNode.getChildren(1,OCT_FACE.R));
  }
  myOctree.clearNodes();
  myOctree.addNode(newNodes);
}