package cse.baffalo.edu.service;

import java.util.List;

public class PrintFormat {
	public static void printi(List<double[]> l, String ans){
		if(ans.equals("partII1")){
			System.out.println("1:");
			System.out.println("The number of patients who had 'tumor' : "+(int)l.get(0)[0]);
			System.out.println("The number of patients who had 'leukemia' : "+(int)l.get(1)[0]);
			System.out.println("The number of patients who had 'ALL' : "+(int)l.get(2)[0]);
		}else if(ans.equals("partII3")){
			System.out.println();
			System.out.println("3: mRNA values");
			double[] mRNA=l.get(0);
			for(int i=0;i<mRNA.length;i++){
				System.out.print((int)mRNA[i]+" ");
				if((i+1)%20==0){
					System.out.println();
				}
			}
			System.out.println(" ");
		}
	}
	
	public static void prints(List<String[]> l, String ans){
		if(ans.equals("partII2")){
			System.out.println();
			System.out.println("2: Types of drugs which have been applied to patients with 'tumor'");
			String[] drugType=l.get(0);
			for(int i=0;i<drugType.length;i++){
				System.out.print(drugType[i]+"  ");
				if((i+1)%5==0){
					System.out.println();
				}
			}
			System.out.println(" ");
		}
	}
}
