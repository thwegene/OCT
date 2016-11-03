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

package oct.polygonization;

import oct.octree.OctXYZ;
import processing.core.PApplet;
import oct.polygonization.OctMesh;
/**
 * Abstract class, allows to switch easily from one representation to the other.
 */
public abstract class OctPoly {

	OctMesh octMesh;
	
	public abstract void setup();

	public abstract void draw();
	
	public float getQEFclip() {
		PApplet.println("Not implemented for this polygonization");
		return 0f;
	}
	
	public void setQEFclip(float _value) {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public OctXYZ getGradient(OctXYZ _v) {
		PApplet.println("Not implemented for this polygonization");
		return new OctXYZ();
	}
	
	public void drawGradient() {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public void flipToggle() {
		PApplet.println("Not implemented for this polygonization");
	}

	public void closeToggle() {
		PApplet.println("Not implemented for this polygonization");
	}

	public void smooth(float _value) {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public void setCloseValue(float _value) {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public float getCloseValue() {
		PApplet.println("Not implemented for this polygonization");
		return 0f;
	}
	
	public void clear() {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public void setThreshold(float _value) {
		PApplet.println("Not implemented for this polygonization");
	}
	
	public float getThreshold() {
		PApplet.println("Not implemented for this polygonization");
		return 0f;
	}
	
	public OctMesh getMesh() {
		
		return octMesh;
		
	}

}
