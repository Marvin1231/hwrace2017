package com.marving.model;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

public class SwapS {
	
	public BitSet bitSet;
	public final int customerNodeCount;
	public final int nodeNum;
	private Graph graph;
	private Random ra = new Random();
	private BitSet bestBit;
	private int bestCost = Integer.MAX_VALUE; 
	private int currentGenes;
	//存储已计算的值
	private HashSet<BitSet> gensFitness = new HashSet<BitSet>(80000);
	
	private int minCost = Integer.MAX_VALUE;

	public SwapS(Graph graph, int customerNodeCount, int nodeNum){
		this.graph =graph;
		this.customerNodeCount = customerNodeCount;
		this.nodeNum = nodeNum;
		bitSet = new BitSet(nodeNum);
		bestBit = new BitSet(nodeNum);
	}
	
	public void init(){
		bitSet.clear();
		minCost = Integer.MAX_VALUE;
		for(CustomerNode cNode : graph.customerNodes){
			bitSet.set(cNode.node);
		}
		deleteItem();
	}
	
	public void swapNode(int sourceNode,int destNode){
		bitSet.set(sourceNode,false);
		bitSet.set(destNode);
		if(!find(bitSet)){
			bitSet.set(sourceNode);
			bitSet.set(destNode,false);
		}	
	}
	
	public void deleteNode(int node){
		bitSet.set(node,false);
		if(!find(bitSet)){
			bitSet.set(node);
		}
	}
	public void addNode(int node){
		bitSet.set(node);
		if(!find(bitSet)){
			bitSet.set(node,false);
		}
	}
	
	public boolean deleteItem(){
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			bitSet.set(i,false);
			if(!find(bitSet))
				bitSet.set(i,true);
		}
		return true;
	}
	int deltaDistance = 0;
	double p;
	public boolean find(BitSet bits){
		if(gensFitness.contains(bits)){
			return false;
		}
		currentGenes++;
		int costs = graph.findMinCostByBitSet_small(bits,minCost);
		if(costs == Integer.MAX_VALUE)
			return false;
		
		gensFitness.add(bits);
		if(costs < minCost){
			minCost = costs;
			if(costs < bestCost){
				bestCost =costs;
				bestBit.clear();
				bestBit.or(bitSet);
				//System.out.println(currentGenes + "----" + costs);
			}
			return true;
		} else {
			deltaDistance = costs -minCost;
			p = Math.exp(-deltaDistance / 100)/25;
			if(Math.random() < p){
				minCost = costs;
				//System.out.println("-------------");
				return true;
			}
		}
		return false;
	}		

	public void findMinestCost(long difftime){
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		init();
		while(endTime- startTime < difftime){
			int bitIndex;
			int tabuIndex;
			int operation = ra.nextInt(65535) % 3;	
			//添加一个元素
			if(operation == 0){
				tabuIndex = ra.nextInt(65535)%nodeNum;
				tabuIndex = bitSet.nextClearBit(tabuIndex);
				if(tabuIndex ==-1){
					tabuIndex = bitSet.nextClearBit(0);
				}	
				addNode(tabuIndex);
			}
			//交换一个元素
			if(operation == 1){
				bitIndex = ra.nextInt(65535)%nodeNum;
				tabuIndex = ra.nextInt(65535)%nodeNum;
				
				bitIndex = bitSet.nextSetBit(bitIndex);
				if(bitIndex ==-1){
					bitIndex = bitSet.nextSetBit(0);
				}
				
				tabuIndex = bitSet.nextClearBit(tabuIndex);
				if(tabuIndex ==-1){
					tabuIndex = bitSet.nextClearBit(0);
				}	
				swapNode(bitIndex,tabuIndex);
			}
			//删除一个元素
			if(operation == 2){
				bitIndex = ra.nextInt(65535)%nodeNum;
				bitIndex = bitSet.nextSetBit(bitIndex);
				if(bitIndex ==-1){
					bitIndex = bitSet.nextSetBit(0);
				}
				deleteNode(bitIndex);
			}
			endTime = System.currentTimeMillis();
		}
	}
	
	public String[] Solve(){
/*    	long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();*/
		//init();
/*		while(endTime - startTime < 77000){
			findMinestCost(20000);
			endTime = System.currentTimeMillis();
		}*/
		findMinestCost(22000);
		findMinestCost(22000);
		findMinestCost(22000);
		findMinestCost(22000);
		/*System.out.println("-----------------------");

		System.out.println(minCost);
		System.out.println(bestCost);
		System.out.println(bitSet);
		System.out.println(currentGenes);
		endTime = System.currentTimeMillis();
		System.out.println("耗时："+(endTime - startTime));*/
		return graph.findMinCostPath(bitSet);
	}
}
