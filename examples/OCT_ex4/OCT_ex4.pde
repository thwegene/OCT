// OCT Octree
// v0.1
// Thomas Wegener
// Example 4 - dxf export, press R to export

import processing.dxf.*;

// Import OCT Octree
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

// Setup one octree
OctOctree myOctree = new OctOctree(this, 1000, 1000, 1000);

// Setup one function
OctNoise myFunction = new OctNoise();
float threshold = 0.17;

// Define one way of representing the octree
OctPoly myPoly = new OctCuberilleQuad(this, myOctree);

boolean export = false;

void setup() {
  size(720, 720, P3D);
  cam = new PeasyCam(this, 2000);
  cam.rotateY(HALF_PI);
  cam.rotateY(HALF_PI/3);

  // Generate the octree recursively from start to end level
  int startLevel = 1;
  int endLevel = 5;
  myOctree.algGenerateByCorners(myFunction, threshold, startLevel, endLevel, true, true, false);

  // Use this once all the nodes are created.
  myPoly.setup();
}

void draw() {
  background(245, 245, 240);  
  translate(-500, -500, -500);
  ambientLight(48, 48, 48);
  lightSpecular(230, 230, 230);
  directionalLight(0, 200, 200, 1, 0, 0);
  directionalLight(200, 0, 200, 0, 1, 0);
  directionalLight(200, 200, 0, 0, 0, 1);
  directionalLight(0, 200, 200, -1, 0, 0);
  directionalLight(200, 0, 200, 0, -1, 0);
  directionalLight(200, 200, 0, 0, 0, -1);
  specular(200, 200, 200);
  shininess(8.0f);
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
  // myOctree.drawAsFaces();
  // with this, only the ouside faces are represented, so faster to navigate
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

void keyPressed() {
  if (key == 'R' || key == 'r') { // Press R to save the file
    myPoly.getMesh().export();
  }
}