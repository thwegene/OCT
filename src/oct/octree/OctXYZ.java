/**
 * OCT Octree
 * A Processing library to create, modify and display Octree structures.
 * http://www.thomaswegener.ch/OCT
 *
 * Based on:
 * Hanan Samet, Neighbor finding in images represented by octrees
 * Computer Vision, Graphics, and Image Processing, Volume 46, Issue 3, June 1989, Pages 367-386
 * http://dx.doi.org/10.1016/0734-189X(89)90038-8.
 * (http://www.sciencedirect.com/science/article/pii/0734189X89900388)
 *
 * Copyright (C) 2015 Thomas Wegener
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Thomas Wegener
 * @modified    2015
 * @version     0.1 (1)
 */

package oct.octree;

import oct.enums.OCT_VERTEX;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Vector class, provides absolute "real world" coordinates. Work with float values.
 */
public class OctXYZ implements java.io.Serializable {

	public float x;
	public float y;
	public float z;
		
	public OctXYZ() {
		x = 0f;
		y = 0f;
		z = 0f;
	}
	
	public OctXYZ(float _x, float _y, float _z) {
		x = _x;
		y = _y;
		z = _z;
	}

	public OctXYZ(PVector _vertex) {		
		x = _vertex.x;
		y = _vertex.y;
		z = _vertex.z;
	}
	
	public OctXYZ(OctXYZ _vertex) {		
		x = _vertex.x;
		y = _vertex.y;
		z = _vertex.z;
	}
		
	public OctRST toRST(OctXYZ _origin, OctXYZ _scale) {
		OctRST tempV = new OctRST();
		tempV.r = (this.x- _origin.x) / _scale.x ;
		tempV.s = (this.y- _origin.y) / _scale.y ;
		tempV.t = (this.z- _origin.z) / _scale.z ;
		return tempV;
	}

	public OctRST toRST(OctOctree _octree) {
		OctRST tempV = new OctRST();
		tempV.r = (this.x - _octree.getOrigin().x) / _octree.getDimension().x ;
		tempV.s = (this.y - _octree.getOrigin().y) / _octree.getDimension().y ;
		tempV.t = (this.z - _octree.getOrigin().z) / _octree.getDimension().z ;
		return tempV;
	}
	
	//TODO
	public OctRST toRST(OctOctree _octree, int level) {
		OctRST tempV = new OctRST();
		tempV.r = (this.x - _octree.getOrigin().x) / _octree.getDimension().x * PApplet.pow(2,level);
		tempV.s = (this.y - _octree.getOrigin().y) / _octree.getDimension().y * PApplet.pow(2,level) ;
		tempV.t = (this.z - _octree.getOrigin().z) / _octree.getDimension().z * PApplet.pow(2,level) ;
		return tempV;
	}
	
	public PVector toPVector() {		
		PVector tempV = new PVector();
		tempV.x = this.x;
		tempV.y = this.y;
		tempV.z = this.z;
		return tempV;
	}
	
	public void addSelf(float _x, float _y, float _z) {
		x = x+_x;
		y = y+_y;
		z = z+_z;		
	}
	
	public void subSelf(float _x, float _y, float _z) {
		x = x-_x;
		y = y-_y;
		z = z-_z;		
	}
	
	public void addSelf(OctXYZ _tempV) {
		x = x+_tempV.x;
		y = y+_tempV.y;
		z = z+_tempV.z;		
	}
	
	public void subSelf(OctXYZ _tempV) {
		x = x-_tempV.x;
		y = y-_tempV.y;
		z = z-_tempV.z;		
	}
	
	public void scaleSelf(float _f) {
		x = x*_f;
		y = y*_f;
		z = z*_f;		
	}
	
	public OctXYZ add(float _x, float _y, float _z) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = x+_x;
		tempV.y = y+_y;
		tempV.z = z+_z;
		return tempV;
	}
	
	public OctXYZ sub(float _x, float _y, float _z) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = x-_x;
		tempV.y = y-_y;
		tempV.z = z-_z;	
		return tempV;
	}
	
	public OctXYZ add(OctXYZ _tempV) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = x+_tempV.x;
		tempV.y = y+_tempV.y;
		tempV.z = z+_tempV.z;
		return tempV;
	}
	
	public OctXYZ sub(OctXYZ _tempV) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = x-_tempV.x;
		tempV.y = y-_tempV.y;
		tempV.z = z-_tempV.z;	
		return tempV;
	}
	
	public OctXYZ scale(float _f) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = x*_f;
		tempV.y = y*_f;
		tempV.z = z*_f;
		return tempV;
	}
	 
    public float dot(OctXYZ vec){
        return (this.x * vec.x +
        		this.y * vec.y +
                this.z * vec.z);
    }

    public OctXYZ cross(OctXYZ vec){
        float newX = this.y*vec.z - this.z*vec.y;
        float newY = this.z*vec.x - this.x*vec.z;
        float newZ = this.x*vec.y - this.y*vec.x;
        return new OctXYZ(newX, newY, newZ);
    }

    public float getNorm() {
        return PApplet.sqrt (x * x + y * y + z * z);
    }
    
    public OctXYZ normalize(){
        return new OctXYZ(x/this.getNorm(), y/this.getNorm(), z/this.getNorm());
    }
	
	/**
	 * Is the XYZ point in the node _n of the octree _o?
	 * Requires an octree to convert from XYZ to RST coordinates.
	 */
	public boolean isInNode(OctNode _n, OctOctree _o) {
		boolean cont = true;
		if (!(this.x <= _n.getVertex(OCT_VERTEX.get(1)).toXYZ(_o).x && this.x >= _n.getVertex(OCT_VERTEX.get(0)).toXYZ(_o).x))
			cont = false;
		if (!(this.y <= _n.getVertex(OCT_VERTEX.get(3)).toXYZ(_o).y && this.y >= _n.getVertex(OCT_VERTEX.get(0)).toXYZ(_o).y))
			cont = false;
		if (!(this.z <= _n.getVertex(OCT_VERTEX.get(4)).toXYZ(_o).z && this.z >= _n.getVertex(OCT_VERTEX.get(0)).toXYZ(_o).z))
			cont = false;
		return cont;
	}
	
	public void draw(OctOctree _octree) {
		_octree.p5.beginShape(PConstants.POINTS);
		OctXYZ t = new OctXYZ(this);
		_octree.p5.vertex(t.x, t.y, t.z);
		_octree.p5.endShape();
	}
	
	@Override	
	public int hashCode() {		
		return new String("x" + x + "y" + y + "z" + z).hashCode();
	}

	@Override
	public boolean equals(Object _tempV) {
		OctXYZ tempV = (OctXYZ) _tempV;
		if (tempV.x == this.x && tempV.y == this.y && tempV.z == this.z)
			return true;
		else
			return false;
	}

	public void invertSelf() {
		x = -x;
		y = -y;
		z = -z;	
	}

}