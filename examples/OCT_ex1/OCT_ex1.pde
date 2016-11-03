// OCT Octree
// v0.1
// Thomas Wegener
// Example 1 - adding nodes, parents, neighbors, siblings, children

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
OctOctree myOctree = new OctOctree(this, 234, 234, 234); // dim x, y, z

void setup() {
  // Init camera
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateX(-HALF_PI);
  cam.rotateY(-HALF_PI/3);
  
  // You can change the size and or the position of the octree
  myOctree.setDimensions(1000,1000,1000);
  myOctree.setCenter(0,0,0);
  
  // The simplest way to add a node...
  myOctree.addNode(new OctNode(1, 1, 0, 1)); // pos x, y, z, level
  
  // ... or multiple nodes.
  for (int i=0; i<8; i++) {
    OctNode tempNode = new OctNode(i,i,i,3);
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
  myOctree.addNode(tempNode3.getBiggerNbrs(1,OCT_FACE.U)); 
  myOctree.addNode(tempNode3.getSmallerNbrs(2,OCT_FACE.D)); 
  myOctree.addNode(tempNode3.getSmallerNbrs(3,OCT_FACE.F));
  
  // Other possible functions, uncomment to test
  // myOctree.addNode(tempNode2.getParent());
  // myOctree.addNode(tempNode2.getSiblings());
  
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
  myOctree.drawAsFaces();
}