package com.cacheserverdeploy.deploy;

import com.marving.model.Graph;
import com.marving.model.SwapG;
import com.marving.model.SwapM;
import com.marving.model.SwapS;

public class Deploy
{
    /**
     * 你需要完成的入口S
     * <功能详细描述>
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static String[] deployServer(String[] graphContent)
    {
    	//读取点个数、边个数以及消费节点个数
    	String[] graphInfo = graphContent[0].split(" ");
    	//网络图点数
    	int countVertex = Integer.parseInt(graphInfo[0]);
    	//网络图边数
        int countEdge = Integer.parseInt(graphInfo[1]);
        //消费节点个数
        int countCustomer = Integer.parseInt(graphInfo[2]);
        //服务器架设费用
        int serverPerCost = Integer.parseInt(graphContent[2]);
        
    	Graph g = new Graph(countVertex,countEdge,countCustomer, serverPerCost);
    	g.initNode(graphContent, countEdge);

    	int startCustomerStr = countEdge + 5;
    	g.initCustomerNode(graphContent, startCustomerStr, countCustomer);
    	if(countVertex < 200){
    		SwapS s = new SwapS(g, countCustomer, countVertex);
			return s.Solve();
    	} else if(countVertex < 400){
    		SwapM s = new SwapM(g, countCustomer, countVertex);
			return s.Solve();
    	} else{
    		SwapG s = new SwapG(g, countCustomer, countVertex);
			return s.Solve();
    	}
    	
    		
    }

}
