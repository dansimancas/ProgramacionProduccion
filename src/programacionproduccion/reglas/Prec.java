package programacionproduccion.reglas;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule: With precedence
 */
public class Prec {
    
    public int[] ect; //earliest completion time
    public int[] lct; //latest completion time
    private int m;
    private int jobs;
    private int[] p;
    private int[] r;
    private int[][] prec;
    private int[][] reversePrec;
    
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
    
    /**
     * Sin tiempos
     * @param prec
     * @param jobs
     * @param m
     * @param p 
     */
    public Prec(int[][] prec, int jobs, int m, int[] p){
        
        this.prec = prec;
        this.jobs = jobs;
        this.m = m;
        this.p  = p;
        this.ect = getEarlyTimesFromPrecedence(prec);
        this.reversePrec = getReversePrecedences();
        printPrecedences(reversePrec);
        this.lct = getLateTimes();
    }
    
    public int[] getEarlyTimesFromPrecedence(int[][] prec){
        
        int[] est = new int[jobs];
        int[] ect = new int[jobs];
        
        List<Integer> unknown = new ArrayList();
        for(int i=0;i<jobs;i++){
            int j = i+1;
            unknown.add(j);
        }
        
        while(unknown.size() > 0){
            
            for(int i=unknown.get(0)-1;i<prec.length && unknown.size() > 0;i++){ //el primero de los trabajos inasignados
                if(prec[i] == null){
                    est[i] = 0;
                    ect[i] = p[i];
                    int job = i+1; int k=0;
                    boolean found = false;
                    while(!found){
                        if(unknown.get(k) == job) {
                            unknown.remove(k);
                            found = true;
                        }
                        k++;
                    }
                }else{
                    int cont=0;
                    for(int j=0;j<prec[i].length;j++){
                        int job = prec[i][j];
                        if(unknown.contains(job)) cont++; //probablemente tenga que hacerlo a mano
                    }
                    if(cont==0){ //si sé los tiempos de inicio y finalización de todos los predecesores
                        //encontrar el predecesor con mayor tiempo de finalización
                        int max=0;
                        for(int j=0;j<prec[i].length;j++){
                            if(ect[prec[i][j]-1] > max) max = ect[prec[i][j]-1];
                        }
                        est[i] = max;
                        ect[i] = p[i] + est[i];
                        int job = i+1; int k=0;
                        boolean found = false;
                        while(!found){
                            if(unknown.get(k) == job) {
                                unknown.remove(k);
                                found = true;
                            }
                            k++;
                        }
                    }
                }
            }
            
        }
        
        return ect;
    }
    
    public int[][] sortJobsByECT(List<int[]> u){
        
        int doableJobs = u.size();
        int[][] jobsMatrix = new int[2][doableJobs];
        
        for(int i=0;i<doableJobs;i++){
            jobsMatrix[0][i] = u.get(i)[0]; //numero del trabajo realizable
            jobsMatrix[1][i] = u.get(i)[1]; //earliest completion time
        }
                
        /*Ordenamos el ECT, guardando el numero del trabajo*/
        
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
    
    public int[][] getReversePrecedences(){
        
        int[][] reversePrecedences = new int[jobs][];
        
        for(int i=0;i<jobs;i++){
            int cont = 0;
            for(int j=0;j<jobs; j++){
                if(prec[j] != null && i != j){
                    int k=0; boolean found = false;
                    while(!found && k<prec[j].length){
                        if(i+1 == prec[j][k]) {
                            cont++;
                            found = true;
                        }
                        k++;
                    }
                }
            }
            
            if(cont == 0) { //cuántos elementos tiene por delante el trabajo i
                reversePrecedences[i] = null;
            }else{
                reversePrecedences[i] = new int[cont];
            }
            int l = 0;
            
            for(int j=1;j<jobs; j++){
                if(prec[j] != null && i != j){
                    int k=0; boolean found = false;
                    while(!found && k<prec[j].length){
                        if(i+1 == prec[j][k]) {
                            reversePrecedences[i][l] = j+1;
                            l++;
                            found = true;
                        }
                        k++;
                    }
                }
            }
            
        }
        
        return reversePrecedences;
        
    }
    
    public int[] getLateTimes(){
        
        int cmax = getCmax();
        int[] lst = new int[jobs];
        int[] lct = new int[jobs];
               
        List<int[]> unknown = new ArrayList();
        for(int i=0;i<jobs;i++){
            int[] job = new int[2];
            job[0] = i+1;
            job[1] = ect[i];
            unknown.add(job);
        }
        
        int[][] sortedJobs = sortJobsByECT(unknown);
        
        while(unknown.size() > 0){
            
            for(int i=0;i<sortedJobs[0].length && unknown.size() > 0;i++){ //el primero de los trabajos inasignados
                
                int currentJob = sortedJobs[0][i]-1;
                
                if(ect[currentJob] == cmax){//si es el ultimo en terminar
                    lct[currentJob] = ect[currentJob]; //es un trabajo critico
                    lst[currentJob] = ect[currentJob] - p[currentJob];  //holgura = 0
                    int job = currentJob+1; int k=0;
                    boolean found = false;
                    while(!found){
                        if(unknown.get(k)[0] == job) {
                            unknown.remove(k);
                            found = true;
                        }
                        k++;
                    }
                }
                else{
                    if(reversePrec[currentJob] != null){
                        int cont=0;
                        for(int j=0;j<reversePrec[currentJob].length;j++){
                            int[] job = new int[2];
                            job[0] = reversePrec[currentJob][j];
                            job[1] = ect[job[0]-1];
                            if(unknown.contains(job)) cont++; //si ya se halló el lct del trabajo en cuestión
                        }
                        if(cont==0){ //si sé los tiempos de inicio y finalización tardíos de todos los predecesores
                            //encontrar el predecesor con mayor tiempo de finalización
                            int max=0;
                            for(int j=0;j<reversePrec[currentJob].length;j++){
                                if(lst[reversePrec[currentJob][j]-1] > max) max = lst[reversePrec[currentJob][j]-1];
                            }
                            lct[currentJob] = max;
                            lst[currentJob] = lct[currentJob] - p[currentJob];
                            int job = currentJob+1; int k=0;
                            boolean found = false;
                            while(!found){
                                if(unknown.get(k)[0] == job) {
                                    unknown.remove(k);
                                    found = true;
                                }
                                k++;
                            }
                        }
                    }
                    else{
                        //Si no tiene ninguna tarea después, se le deja tiempo terminación = cmax
                        lct[currentJob] = cmax; //es un trabajo critico
                        lst[currentJob] = cmax - p[currentJob];  //holgura = 0
                        int job = currentJob+1; int k=0;
                        boolean found = false;
                        while(!found){
                            if(unknown.get(k)[0] == job) {
                                unknown.remove(k);
                                found = true;
                            }
                            k++;
                        }
                    }
                }
            }
            
        }
        
        return lct;
        
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
    
    public void printPrecedences(int[][] p){
        
        for(int i=0;i<p.length;i++){
            System.out.print("\n"+(i+1)+":\t|");
            if(p[i] == null){
                System.out.print("  -  |\n");
            }else{
                for(int j=0;j<p[i].length;j++){
                    System.out.print("  "+p[i][j]+"  |");
                }
                System.out.print("\n");
            }
        }
        
    }
    
}
