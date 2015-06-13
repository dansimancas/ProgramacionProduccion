package programacionproduccion.reglas;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule: Longest Remaining Processing Time First
 */
public class LRPT {
    
    private final int jobs;
    private final int m;
    private final int[] p;
    private final int[] r;
    private List<int[]> schedule;
    
    public LRPT(int jobs, int m, int[] p, int[] r){
        
        this.jobs = jobs;
        this.m = m;
        this.p = (p.length == jobs) ? p : null;
        this.r = (r.length == jobs) ? r : null;
        schedule = new ArrayList();
        
        if(p == null || r == null) throw new IndexOutOfBoundsException();
        
    }
    
    public void schedule(){
        /*Start time | Remaining process time per job | Schedule time from-to | Job on machine and status */
        
        int startTime = 0;
        
        int[] scheduleRow = new int[1+jobs+2+m*2];
        
        for(int i=0;i<m*2;i++){//Inicializando asignaciones a maquinas con valores nulos.
            scheduleRow[jobs+3+i] = -1;
        }
        
        /*Ordenamos el tiempo restante de proceso, guardando el numero del trabajo*/
        
        int[][] sortedJobsMatrix = sortJobs(startTime, p);
        //printJobsMatrix(sortedJobsMatrix);
        
        /*Inicializando la tabla de programación*/
        
        for(int i = 1; i <= jobs; i++){
            scheduleRow[i] = p[i-1]; //Trabajos
        }
        scheduleRow[jobs+1] = 0; //tiempo inicio
        scheduleRow[jobs+2] = 1; //tiempo fin
        
        schedule.add(scheduleRow);
                
        jobsToMachines(sortedJobsMatrix, startTime);
        startTime = startTime+1;
               
        int zeroedRPTs = 0; //numero de trabajos que tienen 0 tiempo restante. RPT: Remaining Process Time
        for(int i =0; i<jobs; i++){
            if(schedule.get(startTime-1)[1+i] == 0) zeroedRPTs = zeroedRPTs + 1;
        }
        
        /*Mientras que todos los trabajos no tengan 0 tiempo restante*/
        
        while(zeroedRPTs < jobs){
            
            zeroedRPTs = 0;
            
            /*Agregamos la siguiente fila*/

            scheduleRow = new int[1+jobs+2+m*2];
            scheduleRow[0] = startTime;
            schedule.add(scheduleRow);

            /*Actualizamos los tiempos de proceso restante de los trabajos asignados a máquinas*/
            int l = 0;
            while(l<jobs){
                boolean assigned = false;
                for(int j=0;j<m;j++){
                    if(schedule.get(startTime-1)[jobs+3+j*2] == l){
                        schedule.get(startTime)[l+1] = schedule.get(startTime-1)[l+1] - 1;
                        assigned = true;
                        l++;
                    }
                }
                if(assigned == false) {
                    schedule.get(startTime)[l+1] = schedule.get(startTime-1)[l+1];
                    l++;
                }
            }


            /*Actualizamos los tiempos de inicio y fin del proceso*/

            schedule.get(startTime)[jobs+1] = startTime;
            schedule.get(startTime)[jobs+2] = startTime+1;

            /*Ordenamos el tiempo restante de proceso*/

            int[] rt = new int[jobs]; //rt: Remaining time
            for(int i=0;i<jobs;i++){
                rt[i] = schedule.get(startTime)[i+1];
            }
            int[][] sortedRT = sortJobs(startTime,rt);

            /*Actualizamos la asignación de trabajos a máquinas*/

            jobsToMachines(sortedRT, startTime);
            
            for(int i =0; i<jobs; i++){
                if(schedule.get(startTime)[1+i] <= 0) zeroedRPTs = zeroedRPTs + 1;
            }
            
            if(zeroedRPTs == jobs){
                int t = startTime;
                schedule.remove(t);
            }           
            
            startTime = startTime + 1;
            
        }
        
        System.out.println("\nSchedule");
        printSchedule(schedule);
        
    }
    
    public void printJobsMatrix(int[][] s){
        
        int height = s.length;
        int width = s[0].length;
        System.out.print("pos:\t");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(j != width-1){
                    System.out.print("| "+s[i][j]+" ");
                }else{
                    System.out.print("| "+s[i][j]+" |\n");
                }
            }
            if(i==0) System.out.print("val:\t");
        }
    }
    
    public void printSchedule(List<int[]> s){
        
        int height = s.size();
        int width = s.get(0).length;
        System.out.print("|  t  ");
        for(int i=0;i<jobs;i++){
            System.out.print("|  j"+i+" ");
        }
        System.out.print("|  D  |  H  ");
        for(int i=0;i<m;i++){
            System.out.print("|  M"+i+" |  e  ");
        }
        System.out.print("|\n");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(j != width-1){
                    System.out.print("|  "+s.get(i)[j]+"  ");
                }else{
                    System.out.print("|  "+s.get(i)[j]+"  |\n");
                }
            }
        }
    }
    
    public int[][] getDoableJobs(int time, int[] u){
        
        int cont = 0;
        
        for(int i=0;i<jobs;i++){
            //Verificamos que el trabajo i pueda ser ejecutado en el tiempo actual
            if(time >= r[i]) cont++;
        }
        
        if(cont == 0){
            return null;
        }
        int[][] doableJobs = new int[2][cont];
        
        
        int k=0;
        for(int i=0;i<jobs;i++){
            if(time >= r[i]) {
                doableJobs[0][k] = i; //numero del trabajo realizable
                doableJobs[1][k] = u[i]; //tiempo restante del trabajo
                k++;
            }
        }
        
        return doableJobs;        
    }
    
    public int[][] sortJobs(int t, int[] u){
        
        if(getDoableJobs(t, u) == null) return null;
        int[][] jobsMatrix = getDoableJobs(t, u);
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
    
    public void jobsToMachines(int[][] sortedRT, int startTime){
        
        int doables = sortedRT[0].length; //numero de trabajos que se pueden ejecutar
        /*Confirmamos la asignación de trabajos a máquinas*/
        
        int aux = 0;
        int max = sortedRT[1][aux]; //mayor tiempo restante
        int posMax = sortedRT[0][aux]; //numero del trabajo de mayor tiempo restante
        int[] machineAccomodation = new int[m*2];
        for(int i=0; i<m*2;i++) machineAccomodation[i] = -1; //inicializamos con valores nulos
        int sum = m*2;
        
        if(max == 0) sum = 0;
        
        while(sum != 0){
            
            int numMax = 0;            
            for(int i=0;i<doables;i++){
                if(sortedRT[1][i] == max) {
                    numMax = numMax+1;//numero de veces que esta repetido el numero mayor
                }
            }
            
            if(numMax == 1){
                
                int cont = 0;
                int mach = 0;
                
                if(startTime > 0){//nos aseguramos que no sea la primera vez
                    for(int j=0;j<m;j++){ //Averiguamos si ya estaba asignado a una maquina
                        if(posMax == schedule.get(startTime-1)[jobs+3+j*2]) {
                            cont++;
                            mach = j;
                        }
                    }
                }
                
                if(cont==1 && startTime > 0) { //si el trabajo de mayor tiempo restante ya estaba asignado a una maquina, cambiar status a: trabajando
                    machineAccomodation[mach*2] = posMax;
                    machineAccomodation[mach*2+1] = 2;
                    if(aux+1>=doables){
                        sum = 0;
                    }else{
                        aux=aux+1;
                        sum = sum - 2;
                        max = sortedRT[1][aux];
                        posMax = sortedRT[0][aux]; 
                    }
                }else{
                    /*Si el trabajo con mayor tiempo restante no está asignado a ninguna máquina,
                     *  asignar una que tenga un trabajo con menor tiempo restante. Revisar que las máquinas no estén
                     *  ocupadas con trabajos mayores de otras iteraciones.
                     */
                    int i = 0, cont2=0;
                    while(cont2 < 1 && i<m*2) {
                        if(machineAccomodation[i] == -1){
                            //Si todavia hay maquinas que no estan ocupadas con trabajos mayores de otras iteraciones
                            machineAccomodation[i] = posMax;
                            machineAccomodation[i+1] = 0;
                            if(aux+1>=doables){
                                sum = 0;
                            }else{
                                aux=aux+1;
                                sum = sum - 2;
                                max = sortedRT[1][aux];
                                posMax = sortedRT[0][aux]; 
                            }
                            cont2++;
                        }
                        i=i+2;
                    }
                }
            }else{ //si hay mas de un trabajo con el mismo tiempo restante
                
                int[] manyPosMax = new int[numMax];
                int k=0;
                for(int j=0;j<doables;j++){
                    if(sortedRT[1][j] == max) {
                        manyPosMax[k] = sortedRT[0][j];
                        k++;
                    }
                }
                
                int cont = 0;
                List<int[]> assigned = new ArrayList();
                List<Integer> unassigned = new ArrayList();
                
                /*
                 * Buscamos si algun trabajo de los que sigue, viene de una iteración previa
                 */
                
                for(int i=0;i<numMax;i++){
                    boolean as = false;
                    for(int j=0;j<m;j++){
                        if(manyPosMax[i] == schedule.get(startTime-1)[jobs+3+j*2] && machineAccomodation[j*2] == -1) {
                            as = true;
                            int[] a = {manyPosMax[i],j}; //job|machine
                            assigned.add(a);
                        }
                    }
                    if(as == false) {
                        int u = manyPosMax[i];
                        unassigned.add(u);
                    }else{
                        cont++;
                    }
                }
                /*
                 * Cambiamos el estado de las que vienen de la iteración anterior,
                 * y se quedarán en la que viene.
                 */
                if(cont*2 == sum){
                    for(int i=0; i<cont;i++){
                        int machine = assigned.get(i)[1];
                        if(machineAccomodation[machine*2] == -1){
                            machineAccomodation[machine*2] = assigned.get(i)[0];
                            machineAccomodation[machine*2+1] = 2;
                        }
                    }
                    sum = 0;//Si no es necesario quitar un proceso para colocar otro
                }else{
                    
                    for(int i=0; i<cont;i++){
                        int machine = assigned.get(i)[1];
                        if(machineAccomodation[machine*2] == -1){
                            machineAccomodation[machine*2] = assigned.get(i)[0];
                            machineAccomodation[machine*2+1] = 2;
                            sum = sum - 2;
                        }
                    }
                    
                    //Si es necesario quitar un proceso para colocar otro
                    
                    int i = 0;
                    while(unassigned.size() > 0 && i < m){
                        if(machineAccomodation[i*2] == -1){
                            machineAccomodation[i*2] = unassigned.get(0);
                            machineAccomodation[i*2+1] = 0;
                            sum = sum - 2;
                            unassigned.remove(0);
                        }
                        i++;
                    }
                }
            }
        }
        for(int i=0;i<m*2;i++){
            schedule.get(startTime)[jobs+3+i] = machineAccomodation[i];
        }
    }
    
}
