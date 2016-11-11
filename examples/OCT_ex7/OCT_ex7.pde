// OCT Octree
// v0.1.1
// Thomas Wegener
// Example 7 - import PLY with correct transformations

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
OctOctree myOctree = new OctOctree(this, 100, 100, 100); // dim x, y, z

// What size level of details is needed?
int level = 6;
// Scale the model
int scaleFactor =1000;
// List of files
String[][] listOfFiles; // model is stored in multiple PLY files

void setup() {
  // Init camera
  size(720, 720, P3D);
  cam = new PeasyCam(this, 500);
  cam.rotateX(-2*HALF_PI);
  cam.rotateY(-PI);

  int n = 10; //number of files
  listOfFiles = new String[n][8]; // x files with attributes name trans x, trans y, trans z, q, q, q, q
  // A Read conf file 
  String confFile[] = loadStrings("bun.conf"); 
  int fileN =0;
  for (int f = 0; f < confFile.length; f++) {      // for each line in conf file
    String[] s = split(confFile[f], ' ');
    if (s[0].equals("bmesh")) {
      for (int k = 0; k < 8; k++) {
        listOfFiles[fileN][k] = s[k+1]; // file name
      }
      fileN++;
    }
  }
  
  for (int j = 0; j < listOfFiles.length; j++) { // pour chaque file
    String points[] = loadStrings(listOfFiles[j][0]);
    println("There are " + points.length + " points in file.");

    setup_transform_from_translation_and_quaternion(
      Float.parseFloat(listOfFiles[j][1]), 
      Float.parseFloat(listOfFiles[j][2]), 
      Float.parseFloat(listOfFiles[j][3]), 
      Float.parseFloat(listOfFiles[j][4]), 
      Float.parseFloat(listOfFiles[j][5]), 
      Float.parseFloat(listOfFiles[j][6]), 
      Float.parseFloat(listOfFiles[j][7])
      );

    for (int l = 24; l < points.length; l++) {      // for each line (in each file)
      String[] s = split(points[l], ' ');
      if (s.length < 3) {
        break;
      }
      double[] myPoint = new double[3];
      myPoint[0]=Float.parseFloat(s[0]);
      myPoint[1]=Float.parseFloat(s[1]);
      myPoint[2]=Float.parseFloat(s[2]);
      myPoint = transform(myPoint);
      OctXYZ p = new OctXYZ((float) myPoint[0], (float)myPoint[1], (float)myPoint[2]);
      p.scaleSelf(scaleFactor);
      myOctree.addNode(new OctNode(floor(p.toRST(myOctree, level).r), floor(p.toRST(myOctree, level).s), floor(p.toRST(myOctree, level).t), level));
    }
  }
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


double M[][] = new double[4][4];


// code from
// https://stackoverflow.com/questions/30783446/how-can-i-read-transform-the-range-images-of-the-stanford-bunny-ply-files

void setup_transform_from_translation_and_quaternion(
  double Tx, double Ty, double Tz, 
  double Qx, double Qy, double Qz, double Qw
  ) {
  /* for unit q, just set s = 2 or set xs = Qx + Qx, etc. */

  double s = 2.0 / (Qx*Qx + Qy*Qy + Qz*Qz + Qw*Qw);

  double xs = Qx * s;
  double ys = Qy * s;
  double zs = Qz * s;

  double wx = Qw * xs;
  double wy = Qw * ys;
  double wz = Qw * zs;

  double xx = Qx * xs;
  double xy = Qx * ys;
  double xz = Qx * zs;

  double yy = Qy * ys;
  double yz = Qy * zs;
  double zz = Qz * zs;

  M[0][0] = 1.0 - (yy + zz);
  M[0][1] = xy - wz;
  M[0][2] = xz + wy;
  M[0][3] = 0.0;

  M[1][0] = xy + wz;
  M[1][1] = 1 - (xx + zz);
  M[1][2] = yz - wx;
  M[1][3] = 0.0;

  M[2][0] = xz - wy;
  M[2][1] = yz + wx;
  M[2][2] = 1 - (xx + yy);
  M[2][3] = 0.0;

  M[3][0] = Tx;
  M[3][1] = Ty;
  M[3][2] = Tz;
  M[3][3] = 1.0;
}

double[] transform(double[] xyz) {
  double xyzw[] = new double[4] ;
  for ( int c=0; c<4; c++) {
    xyzw[c] = M[3][c] ;
  }
  for ( int j=0; j<4; j++) {
    for ( int i=0; i<3; i++) {
      xyzw[j] += M[i][j] * xyz[i] ;
    }
  }
  for ( int c=0; c<3; c++) {
    xyz[c] = xyzw[c] / xyzw[3] ;
  }
  return xyz;
}