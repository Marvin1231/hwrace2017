package com.marving.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;

public class Graph {
	
	public int countVertex, countCustomerNode;
	
	public int serverNodePerCost;
	
	public final int MAX_BAND = 50000;
	//存储服务器节点
	public ArrayList<Integer> serverNodes = new ArrayList<Integer>();
	
	public Node[] gNodes;
	
	//超级源点、超级汇点
	public int superSrcNode;
	public int superDesNode;
	public CustomerNode superCustomerNode;
	
	//消费节点数组
	public CustomerNode[] customerNodes;
	//路径集合
	public PathSet pHashSet = new PathSet();
	
	
	public Graph(int countVertex, int countEdge, int countCustomer,int serverNodePerCost){

		this.countVertex = countVertex + 2;
		this.serverNodePerCost = serverNodePerCost;
		this.countCustomerNode = countCustomer ;
		gNodes = new Node[this.countVertex];
		customerNodes = new CustomerNode[this.countCustomerNode];

	}
	
	/**
	 * 初始化网络节点
	 * @param graphContent
	 * @param size
	 */
	public void initNode(String[] graphContent,int size){
		int fromNodeNum,toNodeNum,totalBand,rentCost;
		BitSet nodeSet = new BitSet(countVertex);
		for(int i = 4; i < size + 4; i ++){
			String[] splitEdgeInfo = graphContent[i].split(" ");
			fromNodeNum = Integer.valueOf(splitEdgeInfo[0]);
			toNodeNum = Integer.valueOf(splitEdgeInfo[1]);
			
			totalBand = Integer.valueOf(splitEdgeInfo[2]);
			rentCost = Integer.valueOf(splitEdgeInfo[3]);
			if(!nodeSet.get(fromNodeNum)){
				gNodes[fromNodeNum] = new Node(fromNodeNum);
				nodeSet.set(fromNodeNum);
			}
			if(!nodeSet.get(toNodeNum)){
				gNodes[toNodeNum] = new Node(toNodeNum);
				nodeSet.set(toNodeNum);
			}
			addEdge(fromNodeNum, toNodeNum, totalBand, rentCost);
			addEdge(toNodeNum, fromNodeNum, totalBand, rentCost);

		}
		superSrcNode = countVertex -2;
		superDesNode = countVertex -1;
		
		gNodes[superSrcNode] = new Node(superSrcNode);
		gNodes[superDesNode] = new Node(superDesNode);
		superCustomerNode = new CustomerNode(countCustomerNode +1,superDesNode,0);
	}
	
	public void addEdge(int fromNodeNum,int toNodeNum,int totalBand, int rentCost){
		
		Edge e1 = new Edge(fromNodeNum,toNodeNum,totalBand,rentCost);
		Edge e11 = new Edge(toNodeNum,fromNodeNum,0,-rentCost);
		e1.reverseEdge = e11;
		e11.reverseEdge = e1;
		
		//维护A->B 与B->A的边
		gNodes[fromNodeNum].mEdgeList.add(e1);
		gNodes[toNodeNum].mEdgeList.add(e11);
	}
	
	/**
	 * 初始化消费节点
	 * @param graphContent
	 * @param start
	 * @param size
	 */
	public void initCustomerNode(String[] graphContent, int start, int size){
		int customerNodeNum,nodeNum,neededBand;
		for(int i = start; i < start + size; i ++){
			String[] splitCustomerInfo = graphContent[i].split(" ");
			customerNodeNum = Integer.valueOf(splitCustomerInfo[0]);
			nodeNum = Integer.valueOf(splitCustomerInfo[1]);
			neededBand = Integer.valueOf(splitCustomerInfo[2]);
			customerNodes[customerNodeNum] = new CustomerNode(customerNodeNum,nodeNum,neededBand);
			
			addEdge(nodeNum, superDesNode, neededBand, 0);
			superCustomerNode.totalNeededBand += neededBand;
			superCustomerNode.neededBand += neededBand;
		}
	}
	
	public void restNodes(){
		
		//重置路径信息
		pHashSet.clear();
		pHashSet.serverCount = 0;
		pHashSet.totalCost = 0;
		//重置节点信息
		for(Node node: gNodes){
			for(Edge e : node.mEdgeList){
				e.restBand = e.totalBand;
				e.rentCost = e.rentCostBak;
			}
		}
		superCustomerNode.neededBand = superCustomerNode.totalNeededBand;
		
		
/*		for(Edge e : gNodes[superSrcNode].mEdgeList){
			e.reverseEdge =null;
			e = null;
		}*/
		//清理超级服务结点的边
/*		Edge e11 =null;
		Integer node =null;
		//gNodes[superSrcNode].mEdgeList.clear();
		for(Edge e: gNodes[superSrcNode].mEdgeList){
			e11 = e.reverseEdge;
			node = e11.fromNode;
			gNodes[node].mEdgeList.remove(e11);
		}*/
		
		HashMap<Integer,Edge> map = new HashMap<Integer,Edge>();
		
		Edge e11 =null;
		Integer node =null;
		for(Edge e: gNodes[superSrcNode].mEdgeList){
			e11 = e.reverseEdge;
			node = e.toNode;
			//gNodes[node].mEdgeList.remove(e11);
			map.put(node, e11);
		}
		
		for(Entry<Integer, Edge> entry : map.entrySet())
			gNodes[entry.getKey()].mEdgeList.remove(entry.getValue());
		gNodes[superSrcNode].mEdgeList.clear();
		/*for(int node : serverNodes){
			for(Edge e: gNodes[node].mEdgeList){
				if(e.toNode == superSrcNode){
					gNodes[node].mEdgeList.remove(e);
				}	
			}
		}
			*/
		//重置服务节点
		serverNodes.clear();
	}

	public String[] findMinCostPath(BitSet bitSet){
		int cost = findMinCostByBitSet_small(bitSet,Integer.MAX_VALUE);
		if(cost == Integer.MAX_VALUE)
			return null;
		return getMinPath();
	}	
	

	
	private int initServerNode(BitSet bitSet) {
		int serverCount = 0;
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			serverNodes.add(i);
			addEdge(superSrcNode, i, MAX_BAND,0);
			serverCount ++;
		}
		return serverCount;
	}

	BitSet visited = new BitSet(countVertex);
	int cost = 0;
	int ans = 0;
	boolean meetTarget = true;
	
	public boolean modlabel() {
		int delta = Integer.MAX_VALUE;
		for (int u = 0; u < countVertex; u++)
			//找到访问过的aug节点
			if (visited.get(u))
				for (Edge e : gNodes[u].mEdgeList)
					if (e.restBand != 0 && !visited.get(e.toNode) && e.rentCost < delta)
						delta = e.rentCost;						
		if (delta == Integer.MAX_VALUE)
			return false;
		for (int u = 0; u < countVertex; u++)
			if (visited.get(u))
				for (Edge e : gNodes[u].mEdgeList){
					e.rentCost -= delta;
					e.reverseEdge.rentCost += delta;
				}		
		cost += delta;
		return true;
	}
	
	//-----针对smallcase----------------------------------------------------------------------------------------------	
	/**
	 * zkw算法
	 * @param u 消费结点
	 * @param f 需要流量
	 * @return
	 */
	public int aug_small(int u, int f){
		if(u == superDesNode){
			ans += cost * f;
			sumFlow += f;
			return f;
		}
		
		int tmp = f;
		visited.set(u);
		for(Edge e : gNodes[u].mEdgeList)
			if(e.restBand > 0 &&e.rentCost == 0 && !visited.get(e.toNode)){
				int delta = aug_small(e.toNode,tmp < e.restBand ? tmp : e.restBand); 
				if(delta == 0)
					continue;
				e.restBand -= delta;
				e.reverseEdge.restBand += delta;
				tmp -= delta; 
				if(tmp == 0) return f;
			}
		return f -tmp;		
	}
	
	
	
	public int sumFlow = 0;
	public int minCost = 0;
	public boolean findAllMinCost_small(int beforeCost){
		int flow;
		sumFlow = 0;
		cost = 0;
		ans = 0;
		minCost = beforeCost;
		do
			do {
				visited.clear();
				flow = aug_small(superSrcNode,superCustomerNode.neededBand);
			} while (flow != 0);
		while (modlabel());

		if(sumFlow < superCustomerNode.neededBand)
			return false;
		pHashSet.totalCost += ans;
		return true;
	}
	public int findMinCostByBitSet_small(BitSet bitSet,int minCost){
		
		//重置
		restNodes();
		int serverCount = initServerNode(bitSet);
		pHashSet.totalCost = serverCount * serverNodePerCost;
		if(!findAllMinCost_small(minCost))
			return Integer.MAX_VALUE; 
		return pHashSet.totalCost;
	}	
	//------------------------------------------------------------------------------------------------------------
	
	//-----针对bigcase----------------------------------------------------------------------------------------------	
	
	public int findMinCostByBitSet_big(BitSet bitSet,int minCost){
		
		//重置
		restNodes();
		int serverCount = initServerNode(bitSet);
		pHashSet.totalCost = serverCount * serverNodePerCost;
		if(!findAllMinCost_big(minCost))
			return Integer.MAX_VALUE; 
		return pHashSet.totalCost;
	}
	
	public int aug_big(int u, int f){
		if(u == superDesNode){
			ans += cost * f;
			sumFlow += f;
			if(minCost < ans + pHashSet.totalCost)
				meetTarget = false;
			return f;
		}
		
		int tmp = f;
		visited.set(u);
		for(Edge e : gNodes[u].mEdgeList)
			if(e.restBand > 0 &&e.rentCost == 0 && !visited.get(e.toNode)){
				int delta = aug_big(e.toNode,tmp < e.restBand ? tmp : e.restBand); 
				if(delta == 0)
					continue;
				e.restBand -= delta;
				e.reverseEdge.restBand += delta;
				tmp -= delta; 
				if(tmp == 0) return f;
			}
		return f -tmp;		
	}
	
	public boolean findAllMinCost_big(int beforeCost){
		int flow;
		sumFlow = 0;
		cost = 0;
		ans = 0;
		meetTarget = true;
		minCost = beforeCost;
		do
			do {
				visited.clear();
				flow = aug_big(superSrcNode,superCustomerNode.neededBand);
			} while (flow != 0 && meetTarget);
		while (modlabel() && meetTarget);
		if(!meetTarget)
			return false;
		if(sumFlow < superCustomerNode.neededBand)
			return false;
		pHashSet.totalCost += ans;
		return true;
	}
//---------------------------------------------------------------------------------------------------
	public String[] getMinPath(){
		for(Integer node : serverNodes){
			visited.clear();
			ArrayList<Integer> path = new ArrayList<Integer>();
			DFS(node,Integer.MAX_VALUE,path);
		}
		return path2Str();
	}
	
	//DFS遍历残余网络打印路径
	public int DFS(int node,int f, ArrayList<Integer> pathList){
		pathList.add(node);
		if(node == superDesNode){
			
			Path path;
			if(pathList.size() > 1){
				int lastNode = pathList.get(pathList.size() -2);
				for(CustomerNode cNode : customerNodes){
					if(lastNode == cNode.node){
						path = new Path(cNode.name,f);
						ArrayList<Node> nodePath = new ArrayList<Node>();
						for(int i : pathList)
							nodePath.add(gNodes[i]);
						//nodePath.remove(0);
						nodePath.remove(nodePath.size() - 1);
						path.nodePath = nodePath;
						pHashSet.add(path);
					}	
				}
			}
			return f;
		}
		
		visited.set(node);
		int tmp = f;
		for(Edge e : gNodes[node].mEdgeList){
			if(e.totalBand!=0 && e.restBand != e.totalBand  && !visited.get(e.toNode)){
				int minFlow = DFS(e.toNode,tmp < e.totalBand - e.restBand ? tmp : e.totalBand - e.restBand,pathList);
				e.restBand += minFlow;
				e.reverseEdge.restBand -= minFlow;
				if(e.restBand != e.totalBand){
					visited.set(e.fromNode,false);	
				}
				if(pathList.size() > 0){
					pathList.remove(pathList.size()-1);
				}	
				tmp -= minFlow; 
				if(tmp == 0) return f;
			}
			
		}
		return f -tmp;		
	}


	public String[] path2Str(){
		
		int i = 0;
		String[] graphContent = new String[pHashSet.size() + 2];
		graphContent[i++] = String.valueOf(pHashSet.size());
		graphContent[i++] = "";
		StringBuilder sb = new StringBuilder();
		for(Path path : pHashSet){
			for(Node node : path.nodePath){
				sb.append(node);
				sb.append(" ");
			}
			sb.append(path.cNode);
			sb.append(" ");
			sb.append(path.usedBand);
			graphContent[i++] = sb.toString();
			sb.delete(0, sb.length());
		}
		return graphContent;
		
	}

}
