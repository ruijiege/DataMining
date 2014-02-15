package cse.baffalo.edu.hw2;

public class IndexSort {
	/**
	 * helper function
	 */
	private static boolean great(Comparable v, Comparable w){
		return (v.compareTo(w)>0);
	}
	
	/**
	 * Index merge Sort.
	 * return a permutation that gives the elements in a[] in ascending order
	 * do not change the original array a[]
	 */
	public static int[] indexSort(Comparable[] a){
		int N=a.length;
		int[] index=new int[N];
		for(int i=0;i<N;i++)
			index[i]=i;
		int[] aux=new int[N];
		sort(a,index,aux,0,N-1);
		return index;
	}
	/**
	 * mergesort a[lo..hi] using auxiliary array aux[lo..hi]
	 */
	private static void sort(Comparable[] a, int[] index, int[] aux, int lo, int hi){
		if(hi<=lo) return;
		int mid=lo+(hi-lo)/2;
		sort(a,index,aux,lo,mid);
		sort(a,index,aux,mid+1,hi);
		merge(a,index,aux,lo,mid,hi);
	}
	
	/**
	 *stably merge a[lo..mid] with a[mid+1..hi] using aux[lo..hi]
	 */
	private static void merge(Comparable[] a, int[] index, int[] aux, int lo, int mid, int hi){
		for(int k=lo;k<=hi;k++){
			aux[k]=index[k];
		}
		int i=lo,j=mid+1;
		for(int k=lo;k<=hi;k++){
			if(i>mid)                           index[k]=aux[j++];
			else if(j>hi)                       index[k]=aux[i++];
			else if(great(a[aux[j]],a[aux[i]])) index[k]=aux[j++];
			else                                index[k]=aux[i++];
		}
	}
}
