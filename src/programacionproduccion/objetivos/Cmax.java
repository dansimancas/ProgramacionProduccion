package programacionproduccion.objetivos;

import java.util.ArrayList;
import java.util.List;
import programacionproduccion.reglas.LPT;
import programacionproduccion.reglas.Prec;

/**
 * Objective: Minimize Makespan
 */
public class Cmax {
    
    private final int jobs;
    private final int m;
    private final int[] p;
    private int[][] schedule;
    private List<Integer> scheduledJobs;
    private List<int[]> unscheduledJobs;
    
    public Cmax(int jobs, int m, int[] p){
        
        this.jobs = jobs;
        this.m = m;
        this.p = (p.length == jobs) ? p : null;
        schedule = new int[jobs][5];
        scheduledJobs = new ArrayList();
        unscheduledJobs = new ArrayList();
        
        for(int i=0;i<jobs;i++){
            int[] job = new int[2];
            job[0] = i+1;
            job[1] = p[i];
            unscheduledJobs.add(job);
        }
        
        if(p == null) throw new IndexOutOfBoundsException();
    }
    
    public void scheduleLPT(){
        
        LPT lpt = new LPT(jobs,m,p);        
        int[][] sortedPT = lpt.sortJobs(unscheduledJobs);
        
        /*Job | Machine | Start time | Process time | Completion time*/
        
        /*Por la primera vez*/
        for(int j=0;j<m;j++){ //para jobs > maquinas
            schedule[j][0] = sortedPT[0][j]; //numero del trabajo.. j
            schedule[j][1] = j+1; //numero de la maquina.. m
            schedule[j][2] = 0; //tiempo de inicio.. s[j]
            schedule[j][3] = sortedPT[1][j]; //tiempo de proceso del trabajo.. p[ij]
            schedule[j][4] = sortedPT[1][j]; //tiempo de finalizacion del trabajo.. c[j]


            int job = schedule[j][0];
            scheduledJobs.add(job);

            for(int k=0;k<unscheduledJobs.size();k++){
                if(unscheduledJobs.get(k)[0] == job) unscheduledJobs.remove(k);
            }
        }
        
        
        while(unscheduledJobs.size() > 0){
            sortedPT = lpt.sortJobs(unscheduledJobs);
            int j=0;
            int aux = scheduledJobs.size()-1;
            int f = scheduledJobs.size();
            while(j<m && f<jobs){
                schedule[f][0] = sortedPT[0][j]; //numero del trabajo.. j
                schedule[f][1] = aux; //numero de la maquina.. m
                schedule[f][2] = schedule[aux][4]; //tiempo de inicio.. s[j]
                schedule[f][3] = sortedPT[1][j]; //tiempo de proceso del trabajo.. p[ij]
                schedule[f][4] = schedule[f][2] + schedule[f][3]; //tiempo de finalizacion del trabajo.. c[j]

                int job = schedule[f][0];
                scheduledJobs.add(job);
                for(int k=0;k<unscheduledJobs.size();k++){
                    if(unscheduledJobs.get(k)[0] == job) unscheduledJobs.remove(k);
                }
                j++;f++;aux--;
            }
        }
        
        System.out.println("\nSchedule");
        printSchedule(schedule);
    }
    
    public void schedulePrecInfMach(int[] ect, int[] lct){
        
        List<int[]> schedule = new ArrayList();
        
        int startTime = 0;
        Prec prec = new Prec(ect,lct,jobs,p);
        
        //int j=0;
        while(unscheduledJobs.size() > 0){
            
            int[][] doableJobs = prec.getDoableJobs(startTime, unscheduledJobs);
            if(doableJobs != null){
                for(int j=0;j<doableJobs[0].length;j++){
                   
                    int[] scheduleRow = new int[4];
                    scheduleRow[0] = doableJobs[0][j]; //number of the job.. j
                    scheduleRow[1] = startTime; // start time.. S[j]
                    scheduleRow[2] = doableJobs[1][j]; //process time of job.. p[j]
                    scheduleRow[3] = scheduleRow[1] + scheduleRow[2]; //earliest completion time.. c[j]

                    int job = scheduleRow[0]; int k=0;
                    boolean found = false;
                    while(!found){
                        if(unscheduledJobs.get(k)[0] == job) {
                            unscheduledJobs.remove(k);
                            found = true;
                        }
                        k++;
                    }
                    schedule.add(scheduleRow);
                }
            }
            startTime++;
        }
        
        System.out.println("\nSchedule");
        printSchedule(schedule);
        
        System.out.print("\nCritical jobs: ");
        printJobs(prec.getCriticalJobs());
        
        System.out.print("Slack jobs: ");
        printJobs(prec.getSlackJobs());
        
        System.out.print("Optimal makespan: "+prec.getCmax()+"\n");
        
    }
    
    public void printSchedule(int[][] s){
        
        int height = s.length;
        int width = s[0].length;
        System.out.print("|\tj\t|\tM\t|\tSj\t|\tp_ij\t|\tc_j\t|\n");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(j != width-1){
                    System.out.print("|\t"+s[i][j]+"\t");
                }else{
                    System.out.print("|\t"+s[i][j]+"\t|\n");
                }
            }
        }
    }
    
    public void printSchedule(List<int[]> s){
        
        int height = s.size();
        int width = s.get(0).length;
        System.out.print("|\tj\t|\tSj\t|\tp_ij\t|\tc_j\t|\n");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(j != width-1){
                    System.out.print("|\t"+s.get(i)[j]+"\t");
                }else{
                    System.out.print("|\t"+s.get(i)[j]+"\t|\n");
                }
            }
        }
    }
    
    public void printJobs(int[] j){
        System.out.print("|");
        for(int i=0;i<j.length;i++){
            System.out.print("  "+j[i]+"  |");
        }
        System.out.print("\n");
    }
}
