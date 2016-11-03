// OCT Octree
// v0.1
// Thomas Wegener
// Example 3 - functions, volume representations, integration with toxiclibs

// Import OCT Octreeimport peasy.*;
import oct.enums.*;
import oct.math.Jama.*;
import oct.math.Jama.util.*;
import oct.math.*;
import oct.octree.*;
import oct.polygonization.*;
import oct.utils.*;

// Import toxiclibs noise function as an example
import toxi.math.noise.SimplexNoise;

// Setup camera using peasycam by Jonathan Feinberg
import peasy.*;
PeasyCam cam;

// Setup on octree
OctOctree myOctree = new OctOctree(this, 1000, 1000, 1000);

// Setup one function
OctNoise myFunction = new OctNoise();
float threshold = 0.2;
  
// Define one way of representing the octree, see at the end of draw() other possibilities
// with quads
// OctPoly myPoly = new OctCuberilleQuad(this,myOctree);
// with triangles
//OctPoly myPoly = new OctCuberilleTri(this,myOctree);
// marching cubes; some problems with adjacent nodes of different levels
OctPoly myPoly = new OctMarchingCubes(this,myOctree,myFunction,threshold);
// fan; some problems with adjacent nodes of different levels
// OctPoly myPoly = new OctDualFan(this,myOctree,myFunction,threshold);
// dualMasspointQuad
//OctPoly myPoly = new OctDualMasspointQuad(this,myOctree,myFunction,threshold);
// dualMasspointTri
// OctPoly myPoly = new OctDualMasspointTri(this,myOctree,myFunction,threshold);
// dualQEFQuad
//OctPoly myPoly = new OctDualQEFQuad(this,myOctree,myFunction,threshold);
// dualQEFTri
//OctPoly myPoly = new OctDualQEFTri(this,myOctree,myFunction,threshold);

void setup() {
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateX(-HALF_PI);
  cam.rotateY(-HALF_PI/3);
  myOctree.setMaxDepth(7);
    
  // Generate the octree recursively from start to end level
  int startLevel = 3;
  int endLevel = 6;
  myOctree.algGenerateByCorners(myFunction, threshold, startLevel, endLevel, false, true, true);  
  
  // to get a closed volume or not
  //myPoly.closeToggle();
  // positive or negative closed volume, node must be included
  myPoly.flipToggle();
  
  // clean or modify the tree before setting the mesh
  // subdive nodes so that no node a more than x level difference with its neighbors
  int x = 0;
  myOctree.algConstrain(x);
  
  // Use this once all the nodes are created.
  myPoly.setup();
  
}

void draw() {
  translate(-500,-500,-500);
  //lights();
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
  // Draw the octree
  myPoly.draw();
}

public class OctNoise extends SimplexNoise implements OctFunction {
  public float compute(float _x, float _y, float _z) {
    float value = threshold;
    float NS=0.01;
    float xx = (float) SimplexNoise.noise(((double) _x*value)
      * NS, ((double) _y*value)
      * NS, ((double) _z*value)
      * NS, NS);
    return xx;
  }
}