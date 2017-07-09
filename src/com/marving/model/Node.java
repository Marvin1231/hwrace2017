package com.marving.model;

import java.util.LinkedList;

public class Node {
	
	public final int name;
	
	public LinkedList<Edge> mEdgeList = new LinkedList<Edge>();

	public Node(int fromNodeNum) {
		this.name = fromNodeNum;
	}
	
	@Override
	public String toString(){
		return String.valueOf(name) ;
	}
}