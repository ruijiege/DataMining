package cse.baffalo.edu.hw2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Stack;

public class AprioriG {
	public static final String DB_DRIVER="com.mysql.jdbc.Driver";
	public static final String DB_URL="jdbc:mysql://127.0.0.1:3306/601project1";
	public static final String DB_USER="root";
	public static final String DB_PASSWORD="123456";
	
	public static Connection conn=null;
	public static Statement stmt=null;
	public static PreparedStatement preparedStatement = null;
	public static ResultSet rs=null;
	
	
	private static Node root=new Node(null,null,0);
	private static LinkedList<Node>[] headers = new LinkedList[200];
	public static HashMap<Integer,Integer> forCompare=new HashMap<Integer,Integer>();
	
	public AprioriG(){
		for(int i=0;i<200;i++){
			headers[i]=new LinkedList<Node>();
		}
	}
	
	private static class Node{
		private String key;
		private Integer val;
		private int N;
		private Node prev;
		private boolean visited;
		private HashMap<String,Node> hashMap=new HashMap<String,Node>();
		
		public Node(String key,Integer val,int N){
			this.key=key;
			this.val=val;
			this.N=N;
		}
	}
	
	/**
	 * Insert node to the AprioriG tree
	 * @param nodes An array of node to be inserted
	 */
	public void put(Node[] nodes){
		put(root, nodes, 0);
	}
	
	public void put(Node root,Node[] nodes,int i){
		if(i>=nodes.length){
			return;
		}
		if(!root.hashMap.containsKey(nodes[i].key)){
			String[] splitKey=nodes[i].key.split("_");
			if(splitKey[1].equals("up")){
				headers[Integer.parseInt(splitKey[0].substring(1))*2-2].add(nodes[i]);
//				System.out.println("up head["+(Integer.parseInt(splitKey[0].substring(1))*2-2)+"]");
			}else{
				headers[Integer.parseInt(splitKey[0].substring(1))*2-1].add(nodes[i]);
//				System.out.println("down head["+(Integer.parseInt(splitKey[0].substring(1))*2-1)+"]");
			}
			root.hashMap.put(nodes[i].key, nodes[i]);
		}
		root.hashMap.get(nodes[i].key).N++;
//		System.out.println(root.hashMap.get(nodes[i].key).N);
		root.hashMap.get(nodes[i].key).prev=root;//point to its father
		put(root.hashMap.get(nodes[i].key),nodes,++i);
		
	}	
	
	/**
	 * get the number of times
	 * @param nodes An array of node to be find
	 * @return the number of times
	 */
	public static int get(Node node){
		Node[] nodes={node};
		return get(nodes);
	}
	public static int get(Node[] nodes){
		Arrays.sort(nodes,new MyComparatorNode());
		Node temp;
		HashMap<String,Node> hashMap=null;
		int num=0,numofHashSet=0;
		String[] splitkey=nodes[nodes.length-1].key.split("_");
		//System.out.println(splitkey[0]);
		if(splitkey[1].equals("up")){
			numofHashSet=headers[Integer.parseInt(splitkey[0].substring(1))*2-2].size();
			if(nodes.length==1){
				for(int i=0;i<numofHashSet;i++){
					temp=headers[Integer.parseInt(splitkey[0].substring(1))*2-2].get(i);
					num=num+temp.N;
				}
			}else{
				ArrayList<HashMap<String,Node>> list=new ArrayList<HashMap<String,Node>>();
				for(int i=0;i<numofHashSet;i++){
					hashMap=new HashMap<String,Node>();
					list.add(hashMap);
					temp=headers[Integer.parseInt(splitkey[0].substring(1))*2-2].get(i);
					while(temp!=null){
						hashMap.put(temp.key, temp);
						//System.out.println(temp.key);
						temp=temp.prev;				
					}
					boolean contain=true;
					for(int j=0;j<nodes.length-1;j++){
						contain=contain&&list.get(i).containsKey(nodes[j].key);
					}
					if(contain){
						num=num+list.get(i).get(nodes[nodes.length-1].key).N;
					}
				}
				
			}
			
		}else{
			numofHashSet=headers[Integer.parseInt(splitkey[0].substring(1))*2-1].size();
			if(nodes.length==1){
				for(int i=0;i<numofHashSet;i++){
					temp=headers[Integer.parseInt(splitkey[0].substring(1))*2-1].get(i);
					num=num+temp.N;
				}
			}else{
				ArrayList<HashMap<String,Node>> list=new ArrayList<HashMap<String,Node>>();
				for(int i=0;i<numofHashSet;i++){
					hashMap=new HashMap<String,Node>();
					list.add(hashMap);
					temp=headers[Integer.parseInt(splitkey[0].substring(1))*2-1].get(i);
					while(temp!=null){
						list.get(i).put(temp.key, temp);
						temp=temp.prev;
					}
					boolean contain=false;
					for(int j=0;j<nodes.length-1;j++){
						contain=contain&&list.get(i).containsKey(nodes[j].key);
					}
					if(contain){
						num=num+list.get(i).get(nodes[nodes.length-1].key).N;
					}
				}
			}
		}
		return num;
	}
	
	
	public static int getFrequentSets(ArrayList<Node> list,double sup){
		Iterator<Node> it=list.iterator();
		Node[] nodes=new Node[list.size()];
		int i=0;
		while(it.hasNext()){
			Node node =it.next();
			nodes[i++]=node;
		}
		return getFrequentSets(nodes,sup);
	}
	public static int getFrequentSets(Node node,double sup){
		Node[] nodes={node};
		return getFrequentSets(nodes,sup);
	}
	public static int getFrequentSets(Node[] nodes,double sup){
		return generator(nodes,sup,0);
	}
	public static int getAssociationRule(Node node,double sup,double con){
		Node[] nodes={node};
		return getAssociationRule(nodes,sup,con);
	}
	public static int getAssociationRule(Node[] nodes,double sup,double con){
		return generator(nodes,sup,con);
	}

	private static int generator(Node[] nodes,double sup,double con){
		HashSet<String> hashSetBody=new HashSet<String>();//remove duplicate
		HashSet<String> hashSetHead=new HashSet<String>();//remove duplicate
		Node temp;
		int numBranch=0;
		int numTotal=0;
		Iterator<Node> listIteratorForBody=null;
		String[] splitkey=nodes[0].key.split("_");
		//System.out.println(splitkey[0]+".."+splitkey[1]);
		if(splitkey[1].equals("up")){
			listIteratorForBody=headers[Integer.parseInt(splitkey[0].substring(1))*2-2].iterator();
		}else{
			listIteratorForBody=headers[Integer.parseInt(splitkey[0].substring(1))*2-1].iterator();
		}
		numBranch=acountNum(listIteratorForBody);
		temp=listIteratorForBody.next();
//			System.out.println(numBranch+"numBranch");
		if((support(numBranch,100))>=sup){
			bodyWithItems(nodes,temp,sup,con,hashSetBody);
		}

		
		Iterator<Node> listIteratorForHead=null;
		String[] splitkey2=nodes[nodes.length-1].key.split("_");
		if(splitkey[1].equals("up")){
			listIteratorForHead=headers[Integer.parseInt(splitkey2[0].substring(1))*2-2].iterator();
		}else{
			listIteratorForHead=headers[Integer.parseInt(splitkey2[0].substring(1))*2-1].iterator();
		}
		while(listIteratorForHead.hasNext()){
			temp=listIteratorForHead.next();
			numBranch=temp.N;
			if((support(numBranch,100))>=sup){
				headWithItems(nodes,temp,sup,con,hashSetHead);
			}
		}
		numTotal=hashSetBody.size()+hashSetHead.size();
		return numTotal;
	}
	
	
	private static int acountNum(Iterator<Node> listIteratorForBody) {
		int count=0;
		Node node=null;
		while(listIteratorForBody.hasNext()){
			node=listIteratorForBody.next();
			count=count+node.N;
		}
		return count;
	}

	public static void bodyWithItems(Node[] nodes,Node temp, double sup,double con,HashSet<String> hashSetBody) {
		ArrayList<Node> frequentTuple=new ArrayList<Node>();	
		StringBuilder sb=new StringBuilder();//remove duplicate
		
		for(int j=nodes.length-1;j>=0;j--){
			frequentTuple.add(nodes[j]);
			sb.append(nodes[j].key);
		}		
		
		while(temp.prev.key!=null){
			frequentTuple.add(temp.prev);
			sb.append(temp.prev.key);
			if(con==0){//getFrequentSets
				if(!hashSetBody.contains(sb.toString())){//remove duplicate
					for(int i=frequentTuple.size()-1;i>=nodes.length;i--){
						System.out.print(frequentTuple.get(i).key+" ");
					}
					System.out.print("-> ");
					for(int j=nodes.length-1;j>=0;j--){
						System.out.print(frequentTuple.get(j).key+" ");
					}
					System.out.println(support(get(convertToNodeArrayFull(frequentTuple,"body")),100)+" "+
							confidence(get(convertToNodeArrayFull(frequentTuple,"body")),get(convertToNodeArrayPart(frequentTuple,nodes,"body"))));					
				}
				hashSetBody.add(sb.toString());		
				temp=temp.prev;
			}else{//getAssociationRule
				if(!hashSetBody.contains(sb.toString())&&confidence(get(convertToNodeArrayFull(frequentTuple,"body")),get(convertToNodeArrayPart(frequentTuple,nodes,"body")))>=con){//remove duplicate
					for(int i=frequentTuple.size()-1;i>=nodes.length;i--){
						System.out.print(frequentTuple.get(i).key+" ");
					}
					System.out.print("-> ");
					for(int j=0;j<nodes.length;j++){
						System.out.print(frequentTuple.get(j).key+" ");
					}
					System.out.println(support(get(convertToNodeArrayFull(frequentTuple,"body")),100)+" "+
							confidence(get(convertToNodeArrayFull(frequentTuple,"body")),get(convertToNodeArrayPart(frequentTuple,nodes,"body"))));					
					hashSetBody.add(sb.toString());	
				}	
				temp=temp.prev;
			}						
		}	
		return ;
	}
	
	public static void headWithItems(Node[] nodes,Node temp, double sup,double con, HashSet<String> hashSetHead) {
		ArrayList<Node> frequentTuple=new ArrayList<Node>();	
		StringBuilder sb=new StringBuilder();
		Stack<Node> stack=new Stack<Node>();
		
		for(int j=0;j<nodes.length;j++){
			frequentTuple.add(nodes[j]);
			sb.append(nodes[j].key);
		}
		Iterator<Entry<String, Node>> it=temp.hashMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Node> entry=it.next();
			Node node=entry.getValue();
			stack.push(node);
		}
		while(!stack.isEmpty()){
			Node subNode=stack.peek();
			if(subNode.visited==false&&support(subNode.N,100)>=sup){
				frequentTuple.add(subNode);
				sb.append(subNode.key);
				if(con==0){
					if(!hashSetHead.contains(sb.toString())){//remove duplicate
						for(int i=0;i<nodes.length;i++){
							System.out.print(frequentTuple.get(i).key+" ");
						}
						System.out.print("-> ");
						for(int i=nodes.length;i<frequentTuple.size();i++){
							System.out.print(frequentTuple.get(i).key+" ");
						}
						System.out.println(support(get(convertToNodeArrayFull(frequentTuple,"head")),100)+" "+
								confidence(get(convertToNodeArrayFull(frequentTuple,"head")),get(convertToNodeArrayPart(frequentTuple,nodes,"head"))));
					}
					hashSetHead.add(sb.toString());
					if(subNode.visited==false&&subNode.hashMap!=null){
						Iterator<Entry<String, Node>> subIt=subNode.hashMap.entrySet().iterator();
						while(subIt.hasNext()){
							Entry<String, Node> subEntry=subIt.next();
							Node sub2Node=subEntry.getValue();
							stack.push(sub2Node);
						}
					}else{
						stack.pop();
						frequentTuple.remove(frequentTuple.size()-1);
					}
					subNode.visited=true;
				}else{
					if(!hashSetHead.contains(sb.toString())&&confidence(get(convertToNodeArrayFull(frequentTuple,"head")),get(convertToNodeArrayPart(frequentTuple,nodes,"head")))>=con){
						for(int i=0;i<nodes.length;i++){
							System.out.print(frequentTuple.get(i).key+" ");
						}
						System.out.print("-> ");
						for(int i=nodes.length;i<frequentTuple.size();i++){
							System.out.print(frequentTuple.get(i).key+" ");
						}
						System.out.println(support(get(convertToNodeArrayFull(frequentTuple,"head")),100)+" "+
								confidence(get(convertToNodeArrayFull(frequentTuple,"head")),get(convertToNodeArrayPart(frequentTuple,nodes,"head"))));
						hashSetHead.add(sb.toString());
					}
					if(subNode.visited==false&&subNode.hashMap!=null){
						Iterator<Entry<String, Node>> subIt=subNode.hashMap.entrySet().iterator();
						while(subIt.hasNext()){
							Entry<String, Node> subEntry=subIt.next();
							Node sub2Node=subEntry.getValue();
							stack.push(sub2Node);
						}
					}else{
						stack.pop();
						frequentTuple.remove(frequentTuple.size()-1);
					}
					subNode.visited=true;
				}
			}else{
				stack.pop();
				if(subNode.visited==true){
					frequentTuple.remove(frequentTuple.size()-1);
				}
			}		
		}
		return ;
	}

	private static Node[] convertToNodeArrayPart(ArrayList<Node> frequentTuple,Node[] nodesO,String type) {
		
		if(type.equals("body")){
			Node[] nodes=new Node[frequentTuple.size()-nodesO.length];
			for(int i=frequentTuple.size()-1;i>=nodesO.length;i--){
				Node node=new Node(frequentTuple.get(i).key,frequentTuple.get(i).val,frequentTuple.get(i).N);
				nodes[frequentTuple.size()-1-i]=node;
			}
			return nodes;
		}else{
			Node[] nodes=new Node[nodesO.length];
			for(int i=0;i<nodesO.length;i++){
				Node node=new Node(frequentTuple.get(i).key,frequentTuple.get(i).val,frequentTuple.get(i).N);
				nodes[i]=node;
			}
			return nodes;
		}
	}

	private static Node[] convertToNodeArrayFull(ArrayList<Node> frequentTuple,String type) {
		Node[] nodes=new Node[frequentTuple.size()];
		if(type.equals("body")){
			for(int i=frequentTuple.size()-1;i>=0;i--){
				Node node=new Node(frequentTuple.get(i).key,frequentTuple.get(i).val,frequentTuple.get(i).N);
				nodes[frequentTuple.size()-1-i]=node;
			}
		}else{
			for(int i=0;i<frequentTuple.size();i++){
				Node node=new Node(frequentTuple.get(i).key,frequentTuple.get(i).val,frequentTuple.get(i).N);
				nodes[i]=node;
			}
		}
		
		return nodes;
	}

	public static double support(int times,int total){
		return ((double)times/(double)total);
	}
	public static double confidence(int togetherTimes, int individualTimes){
		return ((double)togetherTimes/(double)individualTimes);
	}
	
	private static class MyComparator implements Comparator<Integer> {
	    @Override
	    public int compare(Integer i1, Integer i2) {
	    	return forCompare.get(i1).compareTo(forCompare.get(i2));
	    }
	}
	private static class MyComparatorNode implements Comparator<Node> {		
	    @Override
	    public int compare(Node node1, Node node2) {
	    	String[] splitkey1=node1.key.split("_");
	    	String[] splitkey2=node2.key.split("_");
	    	int i1,i2;
	    	if(splitkey1[1].equals("up")){
	    		i1=Integer.parseInt(splitkey1[0].substring(1))*2-2;
	    	}else{
	    		i1=Integer.parseInt(splitkey1[0].substring(1))*2-1;
	    	}
	    	if(splitkey2[1].equals("up")){
	    		i2=Integer.parseInt(splitkey2[0].substring(1))*2-2;
	    	}else{
	    		i2=Integer.parseInt(splitkey2[0].substring(1))*2-1;
	    	}
	    	return forCompare.get(i1).compareTo(forCompare.get(i2));
	    }
	}

	private static void queryResults(String[] sqls, Integer[] frequency) throws Exception{
		stmt=conn.createStatement();
		int i=0;
		for(String sql:sqls){
			rs=stmt.executeQuery(sql);
			while(rs.next()){
//				if(i==116){
//					System.out.println(rs.getInt(1));
//					System.out.println(sql);
//				}
				frequency[i++]=rs.getInt(1);
			}
		}
		
	}
	
	private static Node[] queryNodes(String sql,int[] indexSorted) throws Exception{
		Node[] nodes=new Node[100];
		Node node=null;
		Integer[] a=new Integer[100];
		String upDown=null;
		stmt=conn.createStatement();
		rs=stmt.executeQuery(sql);
		while(rs.next()){
			for(int i=2;i<102;i++){
				upDown=rs.getString(i);
				if(upDown.equalsIgnoreCase("up")){
					a[i-2]=(i-1)*2-2;
				}else{
					a[i-2]=(i-1)*2-1;
				}
			}
			Arrays.sort(a, new MyComparator());
//			System.out.println("...................................");
//			for(int i=0;i<100;i++){
//				System.out.println(a[i]);
//			}
			for(int i=0;i<100;i++){
				if(a[i]%2==0){
					node=new Node("G"+(a[i]+2)/2+"_up",1,0);
				}else{
					node=new Node("G"+(a[i]+1)/2+"_down",0,0);
				}
				nodes[i]=node;
			}
		}
//		for(int i=0;i<100;i++){
//			System.out.println(nodes[i].key+".."+nodes[i].val);
//		}
		return nodes;
	}

	
	
	
	public static void main(String[] args){
		AprioriG apriori=new AprioriG();
		Node[] nodes=new Node[100];
		String sqls[]=new String[200];
		Integer[] frequency=new Integer[200];
		for(int i=0;i<200;i=i+2){
			sqls[i]="select count(*) from association where association.g"+(i+2)/2+"='up';";
			sqls[i+1]="select count(*) from association where association.g"+(i+2)/2+"='down';";
		}
		
		try {
			Class.forName(DB_DRIVER);
			conn=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			queryResults(sqls,frequency);
			//System.out.println(frequency[116]);
			int[] indexSorted=new int[frequency.length];
			indexSorted=IndexSort.indexSort(frequency);
			for(int i=0;i<indexSorted.length;i++){
//				System.out.println(indexSorted[i]);
				forCompare.put(indexSorted[i], i);
			}
			for(int i=1;i<101;i++){
				String sql="select * from association where association.sample_id='sample"+i+"';";
				nodes=queryNodes(sql,indexSorted);
				apriori.put(nodes);
			}
			//System.out.println(headers[117].size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Node[] list={new Node("G72_up",1,0),new Node("G1_up",1,0),new Node("G2_up",1,0)};
		System.out.println(get(list));
		
//		Node[] list2={new Node("G72_up",1,0)};
//		System.out.println(getFrequentSets(list2,0.5));
//		Node[] list3={new Node("G28_down",0,0)};
//		System.out.println(getFrequentSets(list3,0.5));
//		Node[] list4={new Node("G38_down",0,0)};
//		System.out.println(getFrequentSets(list4,0.5));
//		Node[] list5={new Node("G59_up",1,0)};
//		System.out.println(getFrequentSets(list5,0.5));
		
//		int count=0;
//		for(int i=1;i<101;i++){
//			Node nodeeach=new Node("G"+i+"_up",1,0);
//			count=count+getFrequentSets(nodeeach,0.3);
//		}
//		for(int i=1;i<101;i++){
//			Node nodeeach=new Node("G"+i+"_down",1,0);
//			count=count+getFrequentSets(nodeeach,0.3);
//		}
//		System.out.println(count);
		
//		int num=countWithSupport(0.5);
//		System.out.println(num);
//		
//		Iterator<Entry<String, Node>> it=root.hashMap.entrySet().iterator();
//		while(it.hasNext()){
//			System.out.println(it.next().getValue().N);
//		}
//		for(int i=0;i<200;i++){
//			System.out.println(headers[i].size());
//		}
		
				
		//test();
	}

	private static int countWithSupport(double d) {
		int threshold=(int)(d*100);
		int headListSize,count=0;
		Node node=null;
		LinkedList<Node> list=null;
		HashSet<String> set=null;
		for(int i=0;i<200;i++){
			headListSize=headers[i].size();
			set=new HashSet<String>();
			for(int j=0;j<headListSize;j++){
				list=new LinkedList<Node>();
				node=headers[i].get(j);
				while(node!=null){
					list.add(node);
					node=node.prev;
				}
				
			}
		}
		return 0;
	}

}
