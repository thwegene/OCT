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
 * Vector class, provides relative coordinates internal to the tree. Between 0 and 1 if within bounds. 
 */
public class OctRST implements java.io.Serializable {

	public float r;
	public float s;
	public float t;
		
	public OctRST() {
		r = 0f;
		s = 0f;
		t = 0f;
	}
	
	public OctRST(float _r, float _s, float _t) {
		r = _r;
		s = _s;
		t = _t;
	}
	
	public OctRST(PVector _vertex) {		
		r = _vertex.x;
		s = _vertex.y;
		t = _vertex.z;
	}

	public OctRST(OctRST _vertex) {		
		r = _vertex.r;
		s = _vertex.s;
		t = _vertex.t;
	}
		
	public OctXYZ toXYZ(OctXYZ _origin, OctXYZ _scale) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = this.r * _scale.x + _origin.x;
		tempV.y = this.s * _scale.y + _origin.y;
		tempV.z = this.t * _scale.z + _origin.z;
		return tempV;
	}

	public OctXYZ toXYZ(OctOctree _octree) {
		OctXYZ tempV = new OctXYZ();
		tempV.x = this.r * _octree.getDimension().x + _octree.getOrigin().x;
		tempV.y = this.s * _octree.getDimension().y + _octree.getOrigin().y;
		tempV.z = this.t * _octree.getDimension().z + _octree.getOrigin().z;
		return tempV;
	}
	
	public PVector toPVector() {		
		PVector tempV = new PVector();
		tempV.x = this.r;
		tempV.y = this.s;
		tempV.z = this.t;
		return tempV;
	}
	
	public void addSelf(float _r, float _s, float _t) {
		r = r+_r;
		s = s+_s;
		t = t+_t;		
	}
	
	public void subSelf(float _r, float _s, float _t) {
		r = r-_r;
		s = s-_s;
		t = t-_t;		
	}
	
	public void addSelf(OctRST _tempV) {
		r = r+_tempV.r;
		s = s+_tempV.s;
		t = t+_tempV.t;		
	}
	
	public void subSelf(OctRST _tempV) {
		r = r-_tempV.r;
		s = s-_tempV.s;
		t = t-_tempV.t;		
	}
	
	public void scaleSelf(float _f) {
		r = r*_f;
		s = s*_f;
		t = t*_f;		
	}
	
	public OctRST add(float _r, float _s, float _t) {
		OctRST tempV = new OctRST();
		tempV.r = r+_r;
		tempV.s = s+_s;
		tempV.t = t+_t;
		return tempV;
	}
	
	public OctRST sub(float _r, float _s, float _t) {
		OctRST tempV = new OctRST();
		tempV.r = r-_r;
		tempV.s = s-_s;
		tempV.t = t-_t;	
		return tempV;
	}
	
	public OctRST add(OctRST _tempV) {
		OctRST tempV = new OctRST();
		tempV.r = r+_tempV.r;
		tempV.s = s+_tempV.s;
		tempV.t = t+_tempV.t;
		return tempV;
	}
	
	public OctRST sub(OctRST _tempV) {
		OctRST tempV = new OctRST();
		tempV.r = r-_tempV.r;
		tempV.s = s-_tempV.s;
		tempV.t = t-_tempV.t;	
		return tempV;
	}
	
	public OctRST scale(float _f) {
		OctRST tempV = new OctRST();
		tempV.r = r*_f;
		tempV.s = s*_f;
		tempV.t = t*_f;
		return tempV;
	}
	
	/**
	 * Is the RST point in the node? Requires an octree.
	 */
	public boolean isInNode(OctNode _n) {
		boolean cont = true;
		if (!(this.r < _n.getVertex(OCT_VERTEX.get(1)).r && this.r > _n.getVertex(OCT_VERTEX.get(0)).r))
			cont = false;
		if (!(this.s < _n.getVertex(OCT_VERTEX.get(3)).s && this.s > _n.getVertex(OCT_VERTEX.get(0)).s))
			cont = false;
		if (!(this.t < _n.getVertex(OCT_VERTEX.get(4)).t && this.t > _n.getVertex(OCT_VERTEX.get(0)).t))
			cont = false;
		return cont;
	}
	 
    public float dot(OctRST vec){
        return (this.r * vec.r +
        		this.s * vec.s +
                this.t * vec.t);
    }

    public OctRST cross(OctRST vec){
        float newR = this.s*vec.t - this.t*vec.s;
        float newS = this.t*vec.r - this.r*vec.t;
        float newT = this.r*vec.s - this.s*vec.r;
        return new OctRST(newR, newS, newT);
    }
    
    public float getNorm() {
        return PApplet.sqrt (r * r + s * s + t * t);
    }
    
    public OctRST normalize(){
        return new OctRST(r/this.getNorm(), s/this.getNorm(), t/this.getNorm());
    }
	
	public void draw(OctOctree _octree) {
		_octree.p5.beginShape(PConstants.POINTS);
		OctRST t = new OctRST(this);
		_octree.p5.vertex(t.toXYZ(_octree).x, t.toXYZ(_octree).y,
				t.toXYZ(_octree).z);
		_octree.p5.endShape();
	}
	
	// TODO is in node
	
	@Override	
	public int hashCode() {		
		return new String("r" + r + "s" + s + "t" + t).hashCode();
	}

	@Override
	public boolean equals(Object _tempV) {
		OctRST tempV = (OctRST) _tempV;
		if (tempV.r == this.r && tempV.s == this.s && tempV.t == this.t)
			return true;
		else
			return false;
	}
	
	public void invertSelf() {
		r = -r;
		s = -s;
		t = -t;	
	}

}