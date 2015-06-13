package programacionproduccion.objetivos;

import programacionproduccion.reglas.LRPT;
import java.util.Arrays;

/**
 * Rule: Minimization of total maximum lateness with preemption
 */
public class LmaxWPreemption {
    
    private final int jobs; //number of jobs
    private final int m; //number of machines
    private final int[] p; //period
    private final int[] d; //duration
    private int[] r; //date
    
    
    //Obtención de datos iniciales
    public LmaxWPreemption(int jobs, int m, int[] p, int[] d){
        
        this.jobs = jobs;
        this.m = m;
        this.p = (p.length == jobs) ? p : null;
        this.d = (d.length == jobs) ? d : null;        
        
        if(p == null || d == null) throw new IndexOutOfBoundsException();
    }
    
    //Lmax : retraso máximo en el programa
    public boolean toLRPT(){
        
        int[] temp = Arrays.copyOf(d, jobs);
        Arrays.sort(temp);
        int dmax = temp[temp.length-1];
        
        r = new int[jobs];
        
        for(int i=0; i< jobs; i++){
            r[i] = dmax - d[i];
        }
        return true;
    }
    
    public void schedule(){
        toLRPT();
        printR();
        LRPT lrpt = new LRPT(jobs, m, p, r);
        lrpt.schedule();
    }
    
    public void printR(){
        System.out.print("\nr[j]: |");
        for(int i=0;i<r.length;i++){
            System.out.print("  "+r[i]+"  |");
        }
        System.out.print("\n");
    }
    
}
