package programacionproduccion.reglas;

import java.util.List;

/**
 * Rule: With precedence
 */
public class Prec {
    
    private int[] ect; //earliest completion time
    private int[] lct; //latest completion time
    private int m;
    private int jobs;
    private int[] p;
    private int[] r;
    
    /**
     * Para infinitas máquinas
     * @param ect
     * @param lct
     * @param jobs 
     * @param p 
     */
    public Prec(int[] ect, int[] lct, int jobs, int[] p){
        
        this.ect = ect;
        this.lct = lct;
        this.jobs = jobs;
        this.p = p;
        
        r = new int[jobs];
        for(int i=0;i<jobs;i++){
            r[i] = ect[i] - p[i]; //earliest delivery times
        }
        
    }
    
    /**
     * Para finitas máquinas
     * @param ect
     * @param lct
     * @param jobs
     * @param m 
     * @param p 
     */
    public Prec(int[] ect, int[] lct, int jobs, int m, int[] p){
        
        this.ect = ect;
        this.lct = lct;
        this.jobs = jobs;
        this.m = m;
        this.p  = p;
        
        r = new int[jobs];
        for(int i=0;i<jobs;i++){
            r[i] = ect[i] - p[i]; //earliest delivery times
        }
        
    }
    
    public int getCmax(){
        
        int max = 0;
        for(int i=0;i<jobs;i++){
            if(ect[i] > max) max = ect[i];
        }
        return max;
    }
    
    public int[] getCriticalJobs(){
        
        
        int cont = 0;
        for(int i=0;i<jobs;i++){
            if(lct[i]-ect[i] == 0) cont++;
        }
        
        int[] criticalJobs = new int[cont];
        int k=0;
        
        for(int i=0;i<jobs;i++){
            if(lct[i]-ect[i] == 0) {
                criticalJobs[k] = i+1;
                k++;
            }
        }
        
        return criticalJobs;
    }
    
    public int[] getSlackJobs(){
        int max = 0;
        for(int i=0;i<jobs;i++){
            if(lct[i]-ect[i] > max) max = lct[i]-ect[i];
        }
        
        int cont = 0;
        
        for(int i=0;i<jobs;i++){
            if(lct[i]-ect[i] == max) cont++;
        }
        
        int[] slackJobs = new int[cont];
        
        int k=0;
        for(int i=0;i<jobs;i++){
            if(lct[i]-ect[i] == max) {
                slackJobs[k] = i+1;
                k++;
            }
        }
        
        return slackJobs;
    }
    
    public int[][] getDoableJobs(int time, List<int[]> u){
        
        int cont = 0;
        
        for(int i=0;i<u.size();i++){
            //Verificamos que el trabajo i pueda ser ejecutado en el tiempo actual
            if(time >= r[u.get(i)[0]-1]) cont++;
        }
            
        
        if(cont == 0){
            return null;
        }
        int[][] doableJobs = new int[2][cont];
        
        
        int k=0;
        for(int i=0;i<u.size();i++){
            if(time >= r[u.get(i)[0]-1]) {
                doableJobs[0][k] = u.get(i)[0]; //numero del trabajo realizable
                doableJobs[1][k] = u.get(i)[1]; //tiempo restante del trabajo
                k++;
            }
        }
        
        return doableJobs;        
    }
    
    public int[][] sortJobsLRPT(int t, List<int[]> u){
        
        if(getDoableJobs(t,u) == null) return null;
        int[][] jobsMatrix = getDoableJobs(t,u);
        int doables = jobsMatrix[0].length; //numero de trabajos que se pueden ejecutar
        
        /*Ordenamos el tiempo restante de proceso, guardando el numero del trabajo*/
        
        int[][] sortedJobsMatrix = new int[2][doables];
        
        for(int i=0;i<doables;i++){
            int max = 0, pos = 0;
            for(int j=0;j<doables;j++){
                if(jobsMatrix[1][j] >= max){
                    pos = jobsMatrix[0][j]; //numero del trabajo
                    max = jobsMatrix[1][j]; //tiempo restante
                }
            }
            sortedJobsMatrix[0][i] = pos;
            sortedJobsMatrix[1][i] = max;
            
            for(int k=0; k<doables;k++){
                if(jobsMatrix[0][k] == pos){
                    jobsMatrix[0][k] = -1;
                    jobsMatrix[1][k] = -1;
                }
            }
        }
        
        return sortedJobsMatrix;
    }
    
}
