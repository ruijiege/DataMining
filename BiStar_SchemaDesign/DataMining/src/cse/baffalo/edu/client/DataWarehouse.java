package cse.baffalo.edu.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TestUtils;

import cse.baffalo.edu.service.PrintFormat;
import cse.baffalo.edu.service.RawData;


public class DataWarehouse {
	public static final String DB_DRIVER="com.mysql.jdbc.Driver";
	public static final String DB_URL="jdbc:mysql://127.0.0.1:3306/601project1";
	public static final String DB_USER="root";
	public static final String DB_PASSWORD="123456";
	
	public static Connection conn=null;
	public static Statement stmt=null;
	public static PreparedStatement preparedStatement = null;
	public static ResultSet rs=null;
	
	public static void main(String[] args){

		try {
			Class.forName(DB_DRIVER);
			conn=DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			
			List<double[]> list=null;				
			list=queryResult(RawData.rawdat("sqls_partII1"),1);
			
			System.out.println("********************* Answers for partII *********************");
			PrintFormat.printi(list, "partII1");
			List<String[]> listString=queryResult(RawData.rawdat("drugType"));
			PrintFormat.prints(listString, "partII2");
			list=queryResult(RawData.rawdat("mRNAvalue"),1);
			PrintFormat.printi(list, "partII3");
			
			list=queryResult(RawData.rawdat("sqls_ttest"),1);
			System.out.println();
			System.out.println("4: t statistics = "+TestUtils.t(list.get(0), list.get(1)));
			
			System.out.println();
			list=queryResult(RawData.rawdat("sqls_Fstatistic"),1);	
			double fvalue = TestUtils.oneWayAnovaFValue(list); 
			System.out.println("5: F statistics = "+fvalue);

			List<double[]> listALL=queryResult(RawData.rawdat("sqls_correlation"),2);
			double colValALL=colALL(listALL);
			System.out.println();
			System.out.println("6: Average correlation between 'ALL' = "+colValALL);
			
			List<double[]> listAML=queryResult(RawData.rawdat("sqls_correlationAML"),2);
			double colValAML=colAML(listALL,listAML);
			System.out.println("   Average correlation between 'ALL' and 'AML' = "+colValAML);

			List<double[]> listUID=queryResult(RawData.rawdat("UID"),1);
			List<double[]> listALL2=queryResult(RawData.rawdat("sqls_infoGeneALL"),2);
			List<double[]> listNALL=queryResult(RawData.rawdat("sqls_infoGeneNALL"),2);
			List<Double> infoGene=infoGene(listALL2,listNALL,listUID);
			int count=0;
			System.out.println();
			System.out.println("********************* Answers for partIII *********************");
			System.out.println("1. Informative genes: ");
			for(int i=0;i<infoGene.size();i++){
				System.out.print(infoGene.get(i).longValue()+"  ");
				if((i+1)%9==0){
					System.out.println();
				}
				count++;
			}			
			System.out.println();
			System.out.println("Total number: "+count);
			
			List<double[]> listALL3=queryResult(RawData.rawdat("sqls_ALL"),2);
			List<double[]> listNALL3=queryResult(RawData.rawdat("sqls_NALL"),2);
			String[] sql_exp=new String[1];
			String[] sql_exps=RawData.rawdat("sqls_exp");
			System.out.println();
			System.out.println("2. Use informative genes to classify a new patient:");
			for(int j=0;j<5;j++){
				sql_exp[0]=sql_exps[j];
				List<double[]> listexp=queryResult(sql_exp,1);
				double[] rA=colPnP(listALL3,listexp);
				double[] rB=colPnP(listNALL3,listexp);
				double rst=TestUtils.tTest(rA,rB);
				System.out.print("patient "+j+": p-value = "+rst);
				if(rst<=0.01){
					System.out.println("  classified 'ALL'");
				}else{
					System.out.println();
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static double[] colPnP(List<double[]> listALL, List<double[]> listexp) {
		PearsonsCorrelation pearsonsCorrelation=new PearsonsCorrelation();
		List<double[]> groupList1=new ArrayList<double[]>();
		groupList1=formToGroup(listALL);
		double[] rA=new double[groupList1.size()*listexp.size()];
		for(int n=0;n<groupList1.size();n++){
			for(int m=0;m<listexp.size();m++){
				rA[n*listexp.size()+m]=pearsonsCorrelation.correlation(groupList1.get(n), listexp.get(m));
				//System.out.println(rA[n*listexp.size()+m]);
			}
		}
		return rA;
	}


	private static List<Double> infoGene(List<double[]> listALL, List<double[]> listNALL, List<double[]> listUID) {
		List<Double> UIDindex=new ArrayList<Double>();
		List<double[]> groupList1=new ArrayList<double[]>();
		groupList1=formToGroup(listALL);
		List<double[]> groupList2=new ArrayList<double[]>();
		groupList2=formToGroup(listNALL);
		
		for(int n=0;n<1000;n++){
			double pVal=TestUtils.tTest(groupList1.get(n), groupList2.get(n));
			if(pVal<0.01){
				UIDindex.add(listUID.get(0)[n]);
			}
		}
		return UIDindex;
	}


	private static double colAML(List<double[]> listALL, List<double[]> listAML) {
		PearsonsCorrelation pearsonsCorrelation=new PearsonsCorrelation();
		List<double[]> groupList1=new ArrayList<double[]>();
		groupList1=formToGroup(listALL);
		List<double[]> groupList2=new ArrayList<double[]>();
		groupList2=formToGroup(listAML);
		double colTotalVal=0, colAveVal=0;
		for(int n=0;n<13;n++){
			for(int m=0;m<13;m++){
				colTotalVal=colTotalVal+pearsonsCorrelation.correlation(groupList1.get(n), groupList2.get(m));
			}
		}
		colAveVal=colTotalVal/169;
		return colAveVal;
	}

	public static double colALL(List<double[]> list){
		List<double[]> groupList=new ArrayList<double[]>();
		groupList=formToGroup(list);
		double[][] data=new double[24][13];
		for(int m=0;m<13;m++){
			for(int n=0;n<24;n++){
				data[n][m]=groupList.get(m)[n];
			}
		}
		RealMatrix correlationValMatrix=new PearsonsCorrelation().computeCorrelationMatrix(data);
		double[][] correlationVal=correlationValMatrix.getData();//size=13*13
		double aveCol=0, totalCol=0;
		for(int m=0;m<13;m++){
			for(int n=m+1;n<13;n++){
				totalCol=totalCol+correlationVal[m][n];
			}
		}
		aveCol=totalCol/78;		
		return aveCol;
	}
	
	public static List<double[]> formToGroup(List<double[]> list){
		double[] p_id=list.get(0);
		double[] exp=list.get(1);
		
		int i=0;
		List<double[]> groupList=new ArrayList<double[]>();
		while(i<p_id.length){				
			int count=0;			
			double val=p_id[i];
			while(i<p_id.length&&val==p_id[i]){
				count++;
				i++;
			}
			double[] group=new double[count];
			for(int j=i-count;j<i;j++){
				group[j+count-i]=exp[j];//group.length=24;
			}					
			groupList.add(group);//size=13;
		}
		return groupList;
	}
	
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
		}	
		rs.close();
		stmt.close();
		return list;
	}
	
	public static List<String[]> queryResult(String[] sqls) throws Exception{
		List<String[]> list=new ArrayList<String[]>();
		String sql=sqls[0];
		stmt=conn.createStatement();
		rs=stmt.executeQuery(sql);
		int count=0;
		while(rs.next()){
			count++;
		}
		int i=0;
		String[] sample=new String[count];
		rs.beforeFirst();
		while(rs.next()){
			sample[i++]=rs.getString(1);
		}
		list.add(sample);	
		rs.close();
		stmt.close();
		return list;
	}
}
