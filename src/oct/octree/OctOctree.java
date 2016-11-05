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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import oct.enums.OCT_EDGE;
import oct.enums.OCT_ENUM;
import oct.enums.OCT_FACE;
import oct.enums.OCT_VERTEX;
import oct.math.OctFunction;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * This is the main class of the library. Here are included all the methods that
 * operates on the structure of the octree. To start a project, you'll need to
 * define an OctOctree, with its dimensions in XYZ coordinates. The information
 * about the individual nodes is stored in a HashSet named 'nodeList' that can
 * be accessed directly.
 * Other fields such as the minimum and maximum depth of the tree are private and
 * need to be accessed by a function. Minimum and maximum levels are there to limit
 * the computing time of various functions.
 */
public class OctOctree {

	// *************************************************************************************
	// VARIABLES
	// *************************************************************************************

	protected PApplet p5;

	// xyz dimensions
	protected OctXYZ dimension = new OctXYZ();
	protected OctXYZ origin = new OctXYZ();

	// octree structure

	protected int minD = 0; // minimum depth, level 1 equals to 8 nodes
	protected int maxD = 8; // max depth, limits the scope of some calculations

	// list
	public HashSet<OctNode> nodeList;
	public HashSet<OctNode> selectedNodes = new HashSet<OctNode>();

	// *************************************************************************************
	// CONSTRUCTORS
	// *************************************************************************************

	/**
	 * Creates a new Octree. A default origin of (0,0,0) is assumed.
	 */
	public OctOctree(PApplet _p5, float _dimX, float _dimY, float _dimZ) {
		p5 = _p5;
		dimension.x = _dimX;
		dimension.y = _dimY;
		dimension.z = _dimZ;
		nodeList = new HashSet<OctNode>();
	}

	/**
	 * Creates a new Octree with a user-defined dimension and origin.
	 */
	public OctOctree(PApplet _p5, float _dimX, float _dimY, float _dimZ,
			float _originX, float _originY, float _originZ) {
		p5 = _p5;
		dimension.x = _dimX;
		dimension.y = _dimY;
		dimension.z = _dimZ;
		origin.x = _originX;
		origin.y = _originY;
		origin.z = _originZ;
		nodeList = new HashSet<OctNode>();
	}

	public OctOctree(PApplet _p5, PVector _dimension, PVector _origin) {
		new OctOctree(_p5, _dimension.x, _dimension.y, _dimension.z, _origin.x,
				_origin.y, _origin.z);
	}

	public OctOctree(PApplet _p5, OctXYZ _dimension, OctXYZ _origin) {
		new OctOctree(_p5, _dimension.x, _dimension.y, _dimension.z, _origin.x,
				_origin.y, _origin.z);
	}

	public OctOctree(PApplet _p5, PVector _dimension) {
		new OctOctree(_p5, _dimension.x, _dimension.y, _dimension.z);
	}

	public OctOctree(PApplet _p5, OctXYZ _dimension) {
		new OctOctree(_p5, _dimension.x, _dimension.y, _dimension.z);
	}

	// *************************************************************************************
	// GETTERS AND SETTERS
	// *************************************************************************************

	/**
	 * This resizes the octree. It is a scaling of the whole existing structure.
	 */
	public void setDimensions(OctXYZ _dimension) {
		dimension = _dimension;
	}

	public void setDimensions(PVector _dimension) {
		dimension.x = _dimension.x;
		dimension.y = _dimension.y;
		dimension.z = _dimension.z;
	}

	public void setDimensions(float _x, float _y, float _z) {
		dimension.x = _x;
		dimension.y = _y;
		dimension.z = _z;
	}

	/**
	 * This changes the origin of the octree. It is a translation of the whole
	 * existing structure.
	 */
	public void setOrigin(OctXYZ _origin) {
		origin = _origin;
	}

	public void setOrigin(float _x, float _y, float _z) {
		origin.x = _x;
		origin.y = _y;
		origin.z = _z;
	}

	public void setOrigin(PVector _origin) {
		origin.x = _origin.x;
		origin.y = _origin.y;
		origin.z = _origin.z;
	}

	public OctXYZ getMin() {
		OctXYZ t = origin.add(dimension);
		OctXYZ min = new OctXYZ(PApplet.min(origin.x,t.x),PApplet.min(origin.y,t.y),PApplet.min(origin.z,t.z));
		return min;
	}

	public OctXYZ getMax() {
		OctXYZ t = origin.add(dimension);
		OctXYZ max = new OctXYZ(PApplet.max(origin.x,t.x),PApplet.max(origin.y,t.y),PApplet.max(origin.z,t.z));
		return max;
	}	

	/**
	 * This changes the center of the octree. It is a translation of the whole
	 * existing structure.
	 */
	public void setCenter(OctXYZ _center) {
		origin = _center.sub(dimension.scale(0.5f));
	}

	public void setCenter(float _x, float _y, float _z) {
		origin.x = _x - dimension.x / 2;
		origin.y = _y - dimension.y / 2;
		origin.z = _z - dimension.z / 2;
	}

	public void setCenter(PVector _center) {
		origin.x = _center.x - dimension.x / 2;
		origin.y = _center.y - dimension.y / 2;
		origin.z = _center.z - dimension.z / 2;
	}

	// TODO a method to create a cropping instead of a scaling

	/**
	 * Changes the minimum depth of the octree. It might be necessary to clean
	 * the octree afterwards if you use this command with nodes already in the
	 * tree. If you increase the minimum level, some of the existing nodes might
	 * be too large. Use algCleanLevels() to remove those.
	 */
	public void setMinDepth(int _minD) {
		if (_minD <= maxD && _minD >= 0) {
			minD = _minD;
		} else {
			PApplet.println("ERROR in setMinDepth(): level not in range. Level not changed.");
		}

	}

	/**
	 * Changes the maximum depth of the octree. It might be necessary to clean
	 * the octree afterwards if you use this command with nodes already in the
	 * tree. If you decrease the maximum level, some of the existing nodes might
	 * be too small and create errors. Use algCleanLevels() to remove those.
	 */
	public void setMaxDepth(int _maxD) {
		if (_maxD >= minD && _maxD <= 127) {
			maxD = _maxD;
		} else {
			PApplet.println("ERROR in setMaxDepth(): level not in range. Level not changed.");
		}
	}

	public OctXYZ getDimension() {
		return dimension;
	}

	public OctXYZ getOrigin() {
		return origin;
	}

	public OctXYZ getCenter() {
		return origin.add(dimension.scale(0.5f));
	}

	public int getMinDepth() {
		return minD;
	}

	public int getMaxDepth() {
		return maxD;
	}

	public HashSet<OctNode> getNodes() {
		return nodeList;
	}

	// *************************************************************************************
	// RESET THE TREE
	// *************************************************************************************

	/**
	 * Clears the octree. Sets minD @ 0 and maxD @ 8, origin and dimension to
	 * (0,0,0).
	 */
	public void clear() {
		nodeList.clear();
		dimension = new OctXYZ();
		origin = new OctXYZ();
		minD = 0;
		maxD = 8;
	}

	/**
	 * Clears only the list of nodes.
	 */
	public void clearNodes() {
		nodeList.clear();
	}


	// *************************************************************************************
	// ADD AND REMOVE POINTS
	// *************************************************************************************

	/**
	 * Adds the node that contains the point at the specified level.
	 */
	public void addPoint(OctXYZ _tempPoint,int _level) {
		OctRST coord = _tempPoint.toRST(this);
		nodeList.add(new OctNode(PApplet.floor(coord.r*(1<<_level)),PApplet.floor(coord.s*(1<<_level)),PApplet.floor(coord.t*(1<<_level)),_level));
	}

	// *************************************************************************************
	// ADD AND REMOVE NODES
	// *************************************************************************************

	/**
	 * Adds a node. If the node is already in the octree, does nothing. Accepts
	 * node that are out of bounds or have a level outside the min/max depth.
	 */
	public void addNode(OctNode _tempNode) {
		nodeList.add(_tempNode);
	}

	/**
	 * Removes a node. If the node is not in the octree, does nothing.
	 */
	public void deleteNode(OctNode _tempNode) {
		nodeList.remove(_tempNode);
	}

	/**
	 * Adds a node. If the node is already in the octree, prints error.
	 */
	public void addNodeVerbose(OctNode _tempNode) {
		if (_tempNode != null) {
			if (nodeList.contains(_tempNode)) {
				PApplet.println("ERROR in nodeAdd: node already in octree.");
			} else
				nodeList.add(_tempNode);
		}
	}

	/**
	 * Removes a node. If the node is not in the octree, prints error.
	 */
	public void deleteNodeVerbose(OctNode _tempNode) {
		if (_tempNode != null) {
			if (nodeList.contains(_tempNode)) {
				nodeList.remove(_tempNode);
			} else
				PApplet.println("ERROR in nodeRemove: node not in octree.");
		}
	}

	/**
	 * Adds a list of nodes.
	 */
	public void addNode(ArrayList<OctNode> _tempNodeList) {
		for (OctNode _tempNode : _tempNodeList) {
			this.addNode(_tempNode);
		}
	}

	/**
	 * Removes a list of nodes.
	 */
	public void deleteNode(ArrayList<OctNode> _tempNodeList) {
		for (OctNode _tempNode : _tempNodeList) {
			this.deleteNode(_tempNode);
		}
	}

	/**
	 * Adds a set of nodes.
	 */
	public void addNode(HashSet<OctNode> _tempNodeList) {
		for (OctNode _tempNode : _tempNodeList) {
			this.addNode(_tempNode);
		}
	}

	/**
	 * Removes a set of nodes.
	 */
	public void deleteNode(HashSet<OctNode> _tempNodeList) {
		for (OctNode _tempNode : _tempNodeList) {
			this.deleteNode(_tempNode);
		}
	}

	public void selectNode(OctXYZ start, OctXYZ end) {
		//selectedNodes.addAll( this.getRayNodes(start,end).keySet());
		OctNode closest = this.getClosestNode( start,  end);
		for (OctNode _tempNode : this.nodeList) {
			if (_tempNode.equals(closest))
			{_tempNode.isSelected=true;
			selectedNodes.add(_tempNode);
			//PApplet.println("one node selected");
			}
		}
	}	

	private HashMap<OctNode,Float> getRayNodes(OctXYZ start, OctXYZ end) {
		//PApplet.println("getraytnode");
		OctOctree tempTree = new OctOctree(p5,1000,1000,1000);
		tempTree.dimension=this.dimension;
		tempTree.origin=this.origin;
		tempTree.maxD=this.maxD;
		tempTree.minD=this.minD;
		
		HashMap<OctNode,Float> _tempNodes = new HashMap<OctNode, Float>();
		ArrayList<OctNode> listOfNodes = new ArrayList<OctNode>();
		ArrayList<OctNode> listOfNodes2 = new ArrayList<OctNode>();

		tempTree.algGenerate(this.minD);
		for (OctNode _tempNode : tempTree.nodeList) {
			//PApplet.println('1');
			OctRST point = _tempNode.intersectPoint(start.toRST(this), end.toRST(this),-999999999,999999999);
			if (point != null) {
				//PApplet.println("point found");
				_tempNodes.put(_tempNode, point.sub(start.toRST(this)).getNorm());
				listOfNodes.add(_tempNode);
				//selectedNodes.add(_tempNode);
			}
		}
		tempTree.clearNodes();
		listOfNodes2.addAll(listOfNodes);
		listOfNodes.clear();
		for (int i = 0; i<this.maxD-this.minD; i++) {
			for (OctNode _tempNode : listOfNodes2) {
				for (OctNode _tempNode2 : _tempNode.getChildren()) {

					OctRST point = _tempNode2.intersectPoint(start.toRST(this), end.toRST(this),-999999999,999999999);
					if (point != null) {
						_tempNodes.put(_tempNode2, point.sub(start.toRST(this)).getNorm());
						listOfNodes.add(_tempNode2);
						//selectedNodes.add(_tempNode2);
					}
				}
			}
			listOfNodes2.clear();
			listOfNodes2.addAll(listOfNodes);
			listOfNodes.clear();
		}
		return _tempNodes;
	}

	private OctNode getClosestNode(OctXYZ start, OctXYZ direction) {
		//PApplet.println("getclosestnode");
		OctNode _tempNode = new OctNode();
		float closest=999999999;
		//PApplet.println(start.x +"y" + start.y +"y" +start.z );
		//PApplet.println(direction.x +"y" + direction.y +"y" +direction.z );
		HashMap<OctNode,Float> _tempNodes = getRayNodes( start,  direction);
		//PApplet.println(_tempNodes);
		for (OctNode candidates : _tempNodes.keySet()) {
			if (this.nodeList.contains(candidates) && closest > _tempNodes.get(candidates))
			{
				closest = _tempNodes.get(candidates);
				_tempNode = candidates;
			}
		}
		//PApplet.println("selected"+_tempNode);
		return  _tempNode;
	}

	// *************************************************************************************
	// BOOLEAN AND LIST OPERATIONS
	// *************************************************************************************

	/**
	 * Boolean subtracts one node from the octree. Requires the min and max
	 * depth level to the octree to be set correctly.
	 */
	public void boolSub(OctNode subNode) {
		ArrayList<OctNode> listAdd = new ArrayList<OctNode>();
		ArrayList<OctNode> listDel = new ArrayList<OctNode>();
		if (nodeList.contains(subNode)) {
			nodeList.remove(subNode);
			return;
		}
		for (OctNode temp : subNode.getAllChildren(this.maxD - subNode.level)) {
			if (nodeList.contains(temp)) {
				listDel.add(temp);
			}
		}
		for (OctNode parent : subNode.getAllParents(subNode.level - this.minD)) {
			if (nodeList.contains(parent)) {
				listDel.add(parent);
				for (int i = subNode.level - parent.level; i >= 1; i--) {
					listAdd.addAll(subNode.getParent(i).getChildren(1));
					if (i > 1)
						listAdd.remove(subNode.getParent(i - 1));
				}
				listAdd.remove(subNode);
			}
		}
		nodeList.addAll(listAdd);
		nodeList.removeAll(listDel);
	}

	/**
	 * Boolean adds one node from the octree. Requires the min and max depth
	 * level to the octree to be set correctly.
	 */
	public void boolAdd(OctNode addNode) {
		ArrayList<OctNode> listAdd = new ArrayList<OctNode>();
		ArrayList<OctNode> listDel = new ArrayList<OctNode>();
		if (!nodeList.contains(addNode)) {
			listAdd.add(addNode);
		}
		for (OctNode temp : addNode.getAllChildren(this.maxD - addNode.level)) {
			if (nodeList.contains(temp)) {
				listDel.add(temp);
			}
		}
		for (OctNode temp : addNode.getAllParents(addNode.level - this.minD)) {
			if (nodeList.contains(temp)) {
				listDel.add(addNode);
			}
		}
		nodeList.addAll(listAdd);
		nodeList.removeAll(listDel);
	}

	// *************************************************************************************
	// NODE METHODS
	// *************************************************************************************

	/**
	 * Will return a node if there is a node in the octree with the same code
	 * and level as the _tempNode. Else returns null.
	 */
	public OctNode filterNode(OctNode _tempNode) {
		for (OctNode tempNode : nodeList) {
			if (nodeList.contains(_tempNode)) {
				return tempNode;
			}
		}
		return null;
	}

	/**
	 * Filters a list of node to keep only the nodes in the octree.
	 */
	public ArrayList<OctNode> filterNode(ArrayList<OctNode> _tempNodeList) {
		ArrayList<OctNode> t = new ArrayList<OctNode>();
		for (OctNode _tempNode : _tempNodeList) {
			if (nodeList.contains(_tempNode)) {
				t.add(_tempNode);
			}
		}
		return t;
	}

	/**
	 * Filters a set of node to keep only the nodes in the octree.
	 */
	public HashSet<OctNode> filterNode(HashSet<OctNode> _tempNodeList) {
		HashSet<OctNode> t = new HashSet<OctNode>();
		for (OctNode _tempNode : _tempNodeList) {
			if (nodeList.contains(_tempNode)) {
				t.add(_tempNode);
			}
		}
		return t;
	}

	// TODO filter to keep only this or that octant

	/**
	 * Subdivides a node by replacing it by its 8 children. Don't use it in a
	 * loop, use nodeSubdivide(ArrayList) or nodeSubdivide(HashSet).
	 */
	public void subdivideNode(OctNode _tempNode) {
		nodeList.remove(_tempNode);
		nodeList.addAll(_tempNode.getChildren(1));
	}

	/**
	 * Subdivides a list of nodes by replacing it by their 8 children.
	 */
	public void subdivideNode(ArrayList<OctNode> _tempList) {
		HashSet<OctNode> _tempNodeList = new HashSet<OctNode>(_tempList);
		HashSet<OctNode> listAdd = new HashSet<OctNode>();
		for (OctNode temp : _tempNodeList) {
			if (nodeList.contains(temp)) {
				nodeList.remove(temp);
				listAdd.addAll(temp.getChildren(1));
			}
		}
		nodeList.addAll(listAdd);
	}

	/**
	 * Subdivides a list of nodes by replacing it by their 8 children.
	 */
	public void subdivideNode(HashSet<OctNode> _tempNodeList) {
		HashSet<OctNode> listAdd = new HashSet<OctNode>();
		for (OctNode temp : _tempNodeList) {
			if (nodeList.contains(temp)) {
				nodeList.remove(temp);
				listAdd.addAll(temp.getChildren(1));
			}
		}
		nodeList.addAll(listAdd);
	}

	/**
	 * Replaces a node by its parent. Removes the siblings that are also in the
	 * parent, but not smaller nodes! Can create nested nodes!
	 */
	public void mergeNode(OctNode _tempNode) {
		nodeList.remove(_tempNode);
		nodeList.removeAll(_tempNode.getSiblings());
		nodeList.add(_tempNode.getParent(1));
	}

	/**
	 * Replaces list of nodes by their parent. Removes the siblings that are
	 * also in the parent, but not smaller nodes! Can create nested nodes!
	 */
	public void mergeNode(ArrayList<OctNode> _tempList) {
		HashSet<OctNode> _toAdd = new HashSet<OctNode>();
		HashSet<OctNode> _toRemove = new HashSet<OctNode>();
		for (OctNode temp : _tempList) {
			if (nodeList.contains(temp)) {
				_toRemove.add(temp);
				_toRemove.addAll(temp.getSiblings());
				_toAdd.add(temp.getParent(1));
			}
		}
		nodeList.removeAll(_toRemove);
		nodeList.addAll(_toAdd);
	}

	/**
	 * Replaces list of nodes by their parent. Removes the siblings that are
	 * also in the parent, but not smaller nodes! Can create nested nodes!
	 */
	public void mergeNode(HashSet<OctNode> _tempList) {
		HashSet<OctNode> _toAdd = new HashSet<OctNode>();
		HashSet<OctNode> _toRemove = new HashSet<OctNode>();
		for (OctNode temp : _tempList) {
			if (nodeList.contains(temp)) {
				_toRemove.add(temp);
				_toRemove.addAll(temp.getSiblings());
				_toAdd.add(temp.getParent(1));
			}
		}
		nodeList.removeAll(_toRemove);
		nodeList.addAll(_toAdd);
	}

	// *************************************************************************************
	// ALGORITHMS
	// *************************************************************************************

	/**
	 * Automatically set the minimum and maximum depth of the octree according
	 * to its content. Can be run before other algorithms to ensure maximum
	 * efficiency or a consistent result.
	 */
	public void algFixDepth() {
		int _minD = 127;
		int _maxD = 0;
		for (OctNode temp : nodeList) {
			if (_minD > temp.getLevel())
				_minD = temp.getLevel();
			if (_maxD < temp.getLevel())
				_maxD = temp.getLevel();
		}
		minD = _minD;
		maxD = _maxD;
		PApplet.println("RESULT of algDepth: min depth: " + minD
				+ ", max depth: " + maxD + ".");
	}

	/**
	 * Generates all possible nodes at the specified level and add them to the
	 * tree. Requires the min and max depth level to the octree to be set
	 * correctly.
	 */
	public void algGenerate(int _depth) {
		int depth = _depth;
		if (depth > maxD) {
			PApplet.println("ERROR in algGenerate: start depth " + depth
					+ " larger than max depth " + maxD
					+ ". Max depth used instead.");
			depth = maxD;
		}
		if (depth < minD) {
			PApplet.println("ERROR in algGenerate: start depth " + depth
					+ " smaller than min depth " + maxD
					+ ". Min depth used instead.");
			depth = minD;
		}
		for (int p = (int) (1 << depth); --p >= 0;) {
			for (int n = (int) (1 << depth); --n >= 0;) {
				for (int m = (int) (1 << depth); --m >= 0;) {
					OctNode tempNode = new OctNode(m, n, p, depth);
					this.addNode(tempNode);
				}
			}
		}
	}

	/**
	 * Generates all nodes where the center has a value smaller/equal/larger to
	 * the threshold. Requires the min and max depth level to the octree to be
	 * set correctly.
	 */
	public void algGenerateByCenter(OctFunction _f, float _threshold,
			int _depth, boolean _smaller, boolean _equal, boolean _larger) {
		float data;
		int depth = _depth;
		if (depth > maxD) {
			PApplet.println("ERROR in algGenerate: start depth " + depth
					+ " larger than max depth " + maxD
					+ ". Max depth used instead.");
			depth = maxD;
		}
		if (depth < minD) {
			PApplet.println("ERROR in algGenerate: start depth " + depth
					+ " smaller than min depth " + maxD
					+ ". Min depth used instead.");
			depth = minD;
		}
		for (int m = (int) (1 << depth); --m >= 0;) {
			for (int n = (int) (1 << depth); --n >= 0;) {
				for (int p = (int) (1 << depth); --p >= 0;) {
					OctNode tempNode = new OctNode(m, n, p, depth);
					data = _f.compute(tempNode.getCenter().toXYZ(this).x,
							tempNode.getCenter().toXYZ(this).y, tempNode
							.getCenter().toXYZ(this).z);
					if (_smaller && data < _threshold) {
						this.addNode((OctNode) tempNode);
					}
					if (_equal && data == _threshold) {
						this.addNode((OctNode) tempNode);
					}
					if (_larger && data > _threshold) {
						this.addNode((OctNode) tempNode);
					}
				}
			}
		}
	}

	// TODO generate nodes at the border of the octree to close the surface

	/**
	 * Generates all nodes that are below/crossing/above the threshold by
	 * looking at the 8 corners. Requires the min and max depth level to the
	 * octree to be set correctly. Use the same start and end to avoid
	 * recursion.
	 */
	public void algGenerateByCorners(OctFunction _f, float _threshold,
			int _start, int _end, boolean _below, boolean _equal, boolean _above) {
		int start = _start;
		int end = _end;
		if (start > end) {
			PApplet.println("ERROR in algGenerate: start depth " + start
					+ " larger than end depth " + end
					+ ". Values have been inverted.");
			int temp = end;
			end = start;
			start = temp;
		}
		if (start > maxD) {
			PApplet.println("ERROR in algGenerate: start depth " + start
					+ " larger than max depth " + maxD
					+ ". Max depth used instead.");
			start = maxD;
		}
		if (start < minD) {
			PApplet.println("ERROR in algGenerate: start depth " + start
					+ " smaller than min depth " + maxD
					+ ". Min depth used instead.");
			start = minD;
		}
		if (end > maxD) {
			PApplet.println("ERROR in algGenerate: end depth " + end
					+ " larger than max depth " + maxD
					+ ". Max depth used instead.");
			end = maxD;
		}
		if (end < minD) {
			PApplet.println("ERROR in algGenerate: end depth " + end
					+ " smaller than min depth " + maxD
					+ ". Min depth used instead.");
			end = minD;
		}

		HashSet<OctNode> toAdd = new HashSet<OctNode>();
		for (int m = (int) (1 << start); --m >= 0;) {
			for (int n = (int) (1 << start); --n >= 0;) {
				for (int p = (int) (1 << start); --p >= 0;) {
					OctNode tempNode = new OctNode(m, n, p, start);
					float data = 0;
					for (OCT_VERTEX v : OCT_VERTEX.getAll()) {
						if (_f.compute(tempNode.getVertex(v).toXYZ(this).x,
								tempNode.getVertex(v).toXYZ(this).y, tempNode
								.getVertex(v).toXYZ(this).z) > _threshold) {
							data++;
						}
					}
					if (_below && data == 0) {
						this.addNode(tempNode);
					}
					if ((data > 0 && data < 8)) {
						toAdd.add(tempNode);
					}
					if (_above && (data == 8)) {
						this.addNode(tempNode);
					}
				}
			}
		}

		while (start < end) {
			start += 1;
			HashSet<OctNode> toAdd2 = new HashSet<OctNode>();
			for (OctNode tempNode1 : toAdd) {
				toAdd2.addAll(tempNode1.getChildren());
			}
			toAdd = new HashSet<OctNode>();
			for (OctNode tempNode2 : toAdd2) {
				float data = 0;
				for (OCT_VERTEX v : OCT_VERTEX.getAll()) {
					if (_f.compute(tempNode2.getVertex(v).toXYZ(this).x,
							tempNode2.getVertex(v).toXYZ(this).y, tempNode2
							.getVertex(v).toXYZ(this).z) > _threshold)
						data++;
				}
				if (_below && data == 0) {
					this.addNode(tempNode2);
				}
				if ((data > 0 && data < 8)) {
					toAdd.add(tempNode2);
				}
				if (_above && (data == 8)) {
					this.addNode(tempNode2);
				}
			}
		}

		if (_equal) this.addNode(toAdd);
	}

	// TODO simplify except if crossing a certain function, so that the surface is always clean

	/**
	 * Simplifies the octree by maximizing the size of the nodes, grouping nodes
	 * in bigger nodes. Requires the min and max depth level to the octree to be
	 * set correctly.
	 * 
	 * @param _mindepth
	 *            minimum node size to keep, even if the 8 octants are full
	 */
	public void algSimplify(int _mindepth) {
		int depth = _mindepth;
		if (depth > maxD) {
			PApplet.println("ERROR in algSimplify: start depth " + depth
					+ " larger than max depth " + maxD
					+ ". Max depth used instead.");
			depth = maxD;
		}
		if (depth < minD) {
			PApplet.println("ERROR in algSimplify: start depth " + depth
					+ " smaller than min depth " + maxD
					+ ". Min depth used instead.");
			depth = minD;
		}
		boolean needToIterate = false;
		PApplet.println("ITERATING algSimplify:\t" + nodeList.size() + " nodes");
		HashSet<OctNode> tempToAdd = new HashSet<OctNode>();
		HashSet<OctNode> tempToDelete = new HashSet<OctNode>();
		for (OctNode tempNode : nodeList) {
			int count = 0;
			if (tempNode.getLevel() > depth) {
				for (OctNode tempSibling : tempNode.getSiblings()) {
					if (nodeList.contains(tempSibling)) {
						count++;
					}
				}
				if (count == 7) {
					OctNode tempParentNode = tempNode.getParent();
					tempToAdd.add(tempParentNode);
					tempToDelete.addAll(tempParentNode.getChildren(1));
					needToIterate = true;
				}
			}
		}
		this.deleteNode(tempToDelete);
		this.addNode(tempToAdd);
		if (needToIterate) {
			PApplet.println("ITERATING algSimplify:\t" + nodeList.size()
			+ " nodes");
			algSimplify(depth);
		}
	}

	/**
	 * Ensures that one node has no neighbor smaller that a certain size by
	 * subdividing nodes. Requires the min and max depth level to the octree to
	 * be set correctly.
	 * 
	 * @param _constraint
	 *            maxmimum level difference between two adjacent nodes
	 */

	public void algConstrain(int _constraint) {
		int constraint = _constraint;
		if (constraint < 0) {
			PApplet.println("ERROR in algConstraint: Constraint cannot be negative, 0 used instead.");
			constraint = 0;
		}
		if (constraint > maxD - minD) {
			PApplet.println("ERROR in algConstraint: Constraint too large, maxD - minD used instead.");
			constraint = maxD - minD;
		}
		boolean needToIterate = false;
		PApplet.println("ITERATING algConstrain:\t" + nodeList.size()
		+ " nodes");
		HashSet<OctNode> tempToAdd = new HashSet<OctNode>();
		HashSet<OctNode> tempToRemove = new HashSet<OctNode>();
		ArrayList<OCT_ENUM> enumlist = new ArrayList<OCT_ENUM>();
		enumlist.addAll(OCT_FACE.getAll());
		enumlist.addAll(OCT_EDGE.getAll());
		for (OctNode tempNode : nodeList) {
			for (OCT_ENUM e : enumlist) {
				ArrayList<OctNode> tempList = tempNode.getAllBiggerNbrs(
						tempNode.level - this.minD, e);
				tempList = this.filterNode(tempList);
				if (tempList.size() > 0) {
					for (OctNode tempNeighborNode : tempList) {
						if (tempNode.getLevel() - tempNeighborNode.getLevel() > constraint
								&& tempToRemove.contains(tempNeighborNode) == false) {
							tempToAdd.addAll(tempNeighborNode.getChildren());
							tempToRemove.add(tempNeighborNode);
							needToIterate = true;
						}
					}
				}
			}
		}
		nodeList.removeAll(tempToRemove);
		for (OctNode tempNode : tempToAdd) {
			nodeList.add(tempNode);
		}
		if (needToIterate) {
			PApplet.println("ITERATING algConstrain:\t" + nodeList.size()
			+ " nodes");
			algConstrain(constraint);
		}
	}

	/**
	 * Ensures that no node is nested in another node. Requires the min and max
	 * depth level to the octree to be set correctly.
	 */
	public void algCleanNested() {
		Iterator<OctNode> itr = nodeList.iterator();
		while (itr.hasNext()) {
			OctNode t = itr.next();
			boolean delete = false;
			for (OctNode p : t.getAllParents(t.level - this.minD)) {
				if (nodeList.contains(p))
					delete = true;
			}
			if (delete)
				itr.remove();
		}
	}

	/**
	 * Ensures that no node is outside of the bounds of the octree. Requires the
	 * min and max depth level to the octree to be set correctly.
	 */
	public void algCleanOutOfBounds() {
		Iterator<OctNode> itr = nodeList.iterator();
		while (itr.hasNext()) {
			OctNode t = itr.next();
			if (t.isCodeWB(this) != true)
				itr.remove();
		}
	}

	/**
	 * Ensures that no node is outside of the bounds of the octree. Requires the
	 * min and max depth level to the octree to be set correctly.
	 */
	public void algCleanWrongLevels() {
		Iterator<OctNode> itr = nodeList.iterator();
		while (itr.hasNext()) {
			OctNode t = itr.next();
			if (t.isLevelWB(this) != true)
				itr.remove();
		}
	}

	// *************************************************************************************
	// DRAW
	// *************************************************************************************

	public void drawAsCenters() {
		p5.beginShape(PConstants.POINTS);
		for (OctNode tempNode : nodeList) {
			PVector center = tempNode.getCenter().toXYZ(this).toPVector();
			p5.vertex(center.x, center.y, center.z);
		}
		p5.endShape();
	}

	public void drawAsVertices() {
		p5.beginShape(PConstants.POINTS);
		for (OctNode tempNode : nodeList) {
			for (OctRST t : tempNode.getVertices()) {
				p5.vertex(t.toXYZ(this).x, t.toXYZ(this).y, t.toXYZ(this).z);
			}
		}
		p5.endShape();
	}

	public void drawAsEdges() {
		for (OctNode tempNode : nodeList) {
			p5.beginShape(PConstants.LINES);
			for (OCT_EDGE e : OCT_EDGE.values()) {
				for (OctRST t : tempNode.getVertices(e.getVertices())) {
					p5.vertex(t.toXYZ(this).x, t.toXYZ(this).y, t.toXYZ(this).z);
				}
			}
			p5.endShape();
		}
	}

	public void drawAsFaces() {
		for (OctNode tempNode : nodeList) {
			//p5.noFill();
			p5.beginShape(PConstants.QUADS);
			for (OCT_FACE f : OCT_FACE.values()) {
				for (OctRST t : tempNode.getVertices(f.getVertices())) {
					p5.vertex(t.toXYZ(this).x, t.toXYZ(this).y, t.toXYZ(this).z);
				}
			}
			p5.endShape();
		}
		for (OctNode tempNode : selectedNodes) {
			p5.fill(255, 0, 0,100);
			p5.beginShape(PConstants.QUADS);
			for (OCT_FACE f : OCT_FACE.values()) {
				for (OctRST t : tempNode.getVertices(f.getVertices())) {
					p5.vertex(t.toXYZ(this).x, t.toXYZ(this).y, t.toXYZ(this).z);
				}
			}
			p5.endShape();
		}
	}

	/**
	 * Requires the min and max depth level to the octree to be set correctly.
	 * Does not work with nested nodes.
	 */
	public void drawAsFaceSkeleton() {
		ArrayList<OCT_ENUM> tempList = new ArrayList<OCT_ENUM>();
		tempList.add(OCT_FACE.L);
		tempList.add(OCT_FACE.B);
		tempList.add(OCT_FACE.D);
		for (OctNode tempNode : nodeList) {
			p5.beginShape(PConstants.LINES);
			for (OCT_ENUM e : tempList) {
				if (nodeList.contains(tempNode.getNbr(e))) {
					OctXYZ p1 = tempNode.getCenter().toXYZ(this);
					OctXYZ p2 = tempNode.getNbr(e).getCenter().toXYZ(this);
					p5.vertex(p1.x, p1.y, p1.z);
					p5.vertex(p2.x, p2.y, p2.z);
				}
			}
			for (OctNode tempN : tempNode.getAllBiggerNbrs(tempNode.level
					- this.minD, new ArrayList<OCT_ENUM>(OCT_FACE.getAll()))) {
				if (nodeList.contains(tempN)) {
					OctXYZ p1 = tempNode.getCenter().toXYZ(this);
					OctXYZ p2 = tempN.getCenter().toXYZ(this);
					p5.vertex(p1.x, p1.y, p1.z);
					p5.vertex(p2.x, p2.y, p2.z);
				}
			}
			p5.endShape();
		}
	}

	/**
	 * Requires the min and max depth level to the octree to be set correctly.
	 * Does not work with nested nodes.
	 */
	public void drawAsEdgeSkeleton() {
		ArrayList<OCT_ENUM> tempList = new ArrayList<OCT_ENUM>();
		tempList.add(OCT_EDGE.LB);
		tempList.add(OCT_EDGE.BD);
		tempList.add(OCT_EDGE.LD);
		for (OctNode tempNode : nodeList) {
			p5.beginShape(PConstants.LINES);
			for (OCT_ENUM e : tempList) {
				if (nodeList.contains(tempNode.getNbr(e))) {
					OctXYZ p1 = tempNode.getCenter().toXYZ(this);
					OctXYZ p2 = tempNode.getNbr(e).getCenter().toXYZ(this);
					p5.vertex(p1.x, p1.y, p1.z);
					p5.vertex(p2.x, p2.y, p2.z);
				}
			}
			for (OctNode tempN : tempNode.getAllBiggerNbrs(tempNode.level
					- this.minD, new ArrayList<OCT_ENUM>(OCT_EDGE.getAll()))) {
				if (nodeList.contains(tempN)) {
					OctXYZ p1 = tempNode.getCenter().toXYZ(this);
					OctXYZ p2 = tempN.getCenter().toXYZ(this);
					p5.vertex(p1.x, p1.y, p1.z);
					p5.vertex(p2.x, p2.y, p2.z);
				}
			}
			p5.endShape();
		}
	}

	public void drawBoundingBox() {
		p5.pushMatrix();
		OctXYZ tempV = origin.add(dimension.scale(0.5f));
		p5.translate(tempV.x, tempV.y, tempV.z);
		p5.box(dimension.x, dimension.y, dimension.z);
		p5.popMatrix();
	}

	public void drawOrigin() {
		p5.point(origin.x, origin.y, origin.z);
	}

	public void drawAxis() {
		p5.stroke(0, 255, 255);
		p5.line(origin.x, origin.y, origin.z, origin.x + (dimension.x / 10),
				origin.y, origin.z);
		p5.stroke(255, 0, 255);
		p5.line(origin.x, origin.y, origin.z, origin.x, origin.y
				+ (dimension.y / 10), origin.z);
		p5.stroke(255, 255, 0);
		p5.line(origin.x, origin.y, origin.z, origin.x, origin.y, origin.z
				+ (dimension.z / 10));
	}

}
