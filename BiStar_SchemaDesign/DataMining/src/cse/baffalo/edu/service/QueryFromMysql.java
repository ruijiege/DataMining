package cse.baffalo.edu.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryFromMysql {
	public static Connection conn=null;
	public static Statement stmt=null;
	public static PreparedStatement preparedStatement = null;
	public static ResultSet rs=null;
	
	public static List<double[]> queryResult(String[] sqls, int n) throws Exception{
		List<double[]> list=new ArrayList<double[]>();
		stmt=conn.createStatement();
		for(String sql:sqls){
			rs=stmt.executeQuery(sql);
			int count=0;
			while(rs.next()){
				count++;
			}
			if(n==1){
				int i=0;
				double[] sample=new double[count];
				rs.beforeFirst();
				while(rs.next()){
					sample[i++]=rs.getInt(1);
				}
				list.add(sample);
			}else if(n==2){
				int i=0;
				double[] sampleAttr1=new double[count];
				double[] sampleAttr2=new double[count];
				rs.beforeFirst();
				while(rs.next()){
					sampleAttr1[i]=rs.getInt(1);
					sampleAttr2[i++]=rs.getInt(2);
					list.add(sampleAttr1);
					list.add(sampleAttr2);
				}
			}
//			else if(n==3){
//				int i=0;
//				double[] sampleAttr1=new double[count];
//				double[] sampleAttr2=new double[count];
//				double[] sampleAttr3=new double[count];
//				rs.beforeFirst();
//				while(rs.next()){
//					sampleAttr1[i]=rs.getInt(1);
//					sampleAttr2[i]=rs.getInt(2);
//					sampleAttr3[i++]=rs.getInt(3);
//					list.add(sampleAttr1);
//					list.add(sampleAttr2);
//					list.add(sampleAttr3);
//				}
//			}

		}	
		rs.close();
		stmt.close();
		return list;
	}
}
