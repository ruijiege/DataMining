package cse.baffalo.edu.service;

import java.io.InputStream;
import java.util.Properties;

import cse.baffalo.edu.client.DataWarehouse;

public class RawData {

	public static String[] rawdat(String match){
		Properties props = new Properties();
		InputStream ips = DataWarehouse.class
				.getResourceAsStream("config.properties");
		try {
			props.load(ips);
			ips.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(match.equals("sqls_partII1")){
			String patientsTumor = props.getProperty("patientsTumor");
			String patientsLeukemia = props.getProperty("patientsLeukemia");
			String patientsALL = props.getProperty("patientsALL");				
			String sqls_partII1[]=new String[3];
			sqls_partII1[0]=patientsTumor;
			sqls_partII1[1]=patientsLeukemia;
			sqls_partII1[2]=patientsALL;
			return sqls_partII1;
		}else if(match.equals("drugType")){
			String drugType = props.getProperty("drugType");
			String drugTypes[]=new String[1];
			drugTypes[0]=drugType;
			return drugTypes;
		}else if(match.equals("mRNAvalue")){
			String mRNAvalue = props.getProperty("mRNAvalue");
			String sqls_partII3[]=new String[1];
			sqls_partII3[0]=mRNAvalue;
			return sqls_partII3;
		}else if(match.equals("sqls_ttest")){
			String sqls_ttest[]=new String[2];
			String sql_ttest0=props.getProperty("sql_ttest0");
			String sql_ttest1=props.getProperty("sql_ttest1");
			sqls_ttest[0]=sql_ttest0;
			sqls_ttest[1]=sql_ttest1;
			return sqls_ttest;
		}else if(match.equals("sqls_Fstatistic")){
			String sqls_Fstatistic[]=new String[4];
			String sql_Fstatistic0=props.getProperty("sql_Fstatistic0");
			String sql_Fstatistic1=props.getProperty("sql_Fstatistic1");
			String sql_Fstatistic2=props.getProperty("sql_Fstatistic2");
			String sql_Fstatistic3=props.getProperty("sql_Fstatistic3");
			sqls_Fstatistic[0]=sql_Fstatistic0;
			sqls_Fstatistic[1]=sql_Fstatistic1;
			sqls_Fstatistic[2]=sql_Fstatistic2;
			sqls_Fstatistic[3]=sql_Fstatistic3;
			return sqls_Fstatistic;
		}else if(match.equals("sqls_correlation")){
			String[] sqls_correlation=new String[1];
			String sql_correlation0=props.getProperty("sql_correlation0");
			sqls_correlation[0]=sql_correlation0;
			return sqls_correlation;
		}else if(match.equals("sqls_correlationAML")){
			String[] sqls_correlationAML=new String[1];
			String sql_correlationAML=props.getProperty("sql_correlationAML");
			sqls_correlationAML[0]=sql_correlationAML;
			return sqls_correlationAML;
		}else if(match.equals("UID")){
			String[] UID=new String[1];
			String sql_UID="select gene.UID from gene order by gene.UID;";
			UID[0]=sql_UID;
			return UID;
		}else if(match.equals("sqls_infoGeneALL")){
			String[] sqls_infoGeneALL=new String[1];
			String sql_infoGeneALL=props.getProperty("sql_infoGeneALL");
			sqls_infoGeneALL[0]=sql_infoGeneALL;
			return sqls_infoGeneALL;
		}else if(match.equals("sqls_infoGeneNALL")){
			String[] sqls_infoGeneNALL=new String[1];
			String sql_infoGeneNALL=props.getProperty("sql_infoGeneNALL");
			sqls_infoGeneNALL[0]=sql_infoGeneNALL;
			return sqls_infoGeneNALL;
		}else if(match.equals("sqls_ALL")){
			String[] sqls_ALL=new String[1];
			String sql_ALL=props.getProperty("sql_ALL");
			sqls_ALL[0]=sql_ALL;
			return sqls_ALL;
		}else if(match.equals("sqls_NALL")){
			String[] sqls_NALL=new String[1];
			String sql_NALL=props.getProperty("sql_NALL");
			sqls_NALL[0]=sql_NALL;
			return sqls_NALL;
		}else if(match.equals("sqls_exp")){
			String[] sqls_exp=new String[5];
			String sql_exp1=props.getProperty("sql_exp1");
			String sql_exp2=props.getProperty("sql_exp2");
			String sql_exp3=props.getProperty("sql_exp3");
			String sql_exp4=props.getProperty("sql_exp4");
			String sql_exp5=props.getProperty("sql_exp5");
			sqls_exp[0]=sql_exp1;
			sqls_exp[1]=sql_exp2;
			sqls_exp[2]=sql_exp3;
			sqls_exp[3]=sql_exp4;
			sqls_exp[4]=sql_exp5;
			return sqls_exp;
		}
		return null;
	}
}
