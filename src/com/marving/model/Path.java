package com.marving.model;

import java.util.ArrayList;

public class Path {
	
	public int sNode;
	
	public ArrayList<Node> nodePath = new ArrayList<Node>();

	public int cNode;
	
	public int totalCost;
	public int usedBand;
	Path(int sNode, int cNode, int totalCost,int usedBand){
		
		this.sNode = sNode;
		this.cNode = cNode;
		this.totalCost = totalCost;
		this.usedBand = usedBand;
	}
	
	Path(int cNode, int usedBand){
		this.cNode = cNode;
		this.usedBand = usedBand;
	}

	@Override
	public String toString() {
		return this.nodePath.toString() + " " + this.cNode + " " +this.totalCost;
	}
	
}
