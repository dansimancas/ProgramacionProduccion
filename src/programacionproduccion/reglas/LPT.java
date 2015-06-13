package programacionproduccion.reglas;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule: Longest Processing Time First
 */
public class LPT {
    
    private final int jobs;
    private final int m;
    private final int[] p;
    private List<int[]> schedule;
    
    public LPT(int jobs, int m, int[] p){
        
        this.jobs = jobs;
        this.m = m;
        this.p = (p.length == jobs) ? p : null;
        
        if(p == null) throw new IndexOutOfBoundsException();
    }
        
    public int[][] sortJobs(List<int[]> u){
        
        int doableJobs = u.size();
        int[][] jobsMatrix = new int[2][doableJobs];
        
        for(int i=0;i<doableJobs;i++){
            jobsMatrix[0][i] = u.get(i)[0]; //numero del trabajo realizable
            jobsMatrix[1][i] = u.get(i)[1]; //tiempo restante del proceso
        }
                
        /*Ordenamos el tiempo restante de proceso, guardando el numero del trabajo*/
        
        int[][] sortedJobsMatrix = new int[2][doableJobs];
        
        for(int i=0;i<doableJobs;i++){
            int max = 0, pos = 0;
            for(int j=0;j<doableJobs;j++){
                if(jobsMatrix[1][j] > max){
                    pos = jobsMatrix[0][j]; //numero del trabajo
                    max = jobsMatrix[1][j]; //tiempo restante
                }
            }
            sortedJobsMatrix[0][i] = pos;
            sortedJobsMatrix[1][i] = max;
            
            for(int k=0; k<doableJobs;k++){
                if(jobsMatrix[0][k] == pos){
                    jobsMatrix[0][k] = -1;
                    jobsMatrix[1][k] = -1;
                }
            }
        }
        
        return sortedJobsMatrix;
    }
    
    
}
