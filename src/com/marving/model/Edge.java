package com.marving.model;

public class Edge {
	
	public int totalBand;	
	public int restBand;
	public int rentCost;
	public int rentCostBak;
	public int fromNode;//源结点
	public int toNode;//指向的结点
	
	public Edge reverseEdge;
	
	public Edge(int fromNode, int toNode, int totalBand,int rentCost){
		
		this.totalBand = totalBand;
		this.restBand =totalBand;
		this.rentCost = rentCost;
		this.rentCostBak = rentCost;
		this.fromNode = fromNode;
		this.toNode = toNode;
		
	}
/*
	@Override
	public String toString(){
		
		return "[" + "(" + fromNode +"," + toNode + ")" + totalBand +"," + restBand + "]";
	}*/

}
