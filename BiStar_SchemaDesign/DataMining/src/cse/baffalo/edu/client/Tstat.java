package cse.baffalo.edu.client;

/*******************************************************************************
 *  Name:    Kevin Wayne
 *  Login:   wayne
 *  Precept: P00
 *
 *  Compilation:  javac Tstat.java
 *  Execution:    java Tstat < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Reads an integer N and N paired sample from standard input;
 *  writes the t-statistic of the paired samples to standard output.
 *
 ******************************************************************************/

public class Tstat {

    //  return the mean of the array x[]
    public static double mean(double[] x) {
        int N = x.length;
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            sum += x[i];
        }
        return sum / N;
    }


    //  return the sample variance of the array x[]
    public static double variance(double[] x) {
        int N = x.length;
        double mu = mean(x);
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            double delta = x[i] - mu;
            sum += delta * delta;
        }
        return sum / (N-1);
    }

    public static double tstat(double[] x, double[] y) {
        int n1 = x.length;
        int n2 = y.length;
        //if (n1 != n2) throw new RuntimeException("array sizes must be equal");
        //int N = n1;

        // compute means
        double mu1 = mean(x);
        double mu2 = mean(y);

        // compute variances
        double var1 = variance(x);
        double var2 = variance(y);

        // compute t-statistic
        return (mu1 - mu2) / Math.sqrt(var1/n1 + var2/n2);
    }
        


    public static void main(String[] args) {

//        // read data from standard input
//        int N = StdIn.readInt();
//        double[] x = new double[N];
//        double[] y = new double[N];
//        for (int i = 0; i < N; i++) {
//            x[i] = StdIn.readDouble();
//            y[i] = StdIn.readDouble();
//        }
//
//        // compute t-statistic
//        double t = tstat(x, y);
//        StdOut.println(t);
    }
}