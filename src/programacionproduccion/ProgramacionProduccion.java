package programacionproduccion;

import java.util.Arrays;
import programacionproduccion.objetivos.Cmax;
import programacionproduccion.objetivos.LmaxWPreemption;
import programacionproduccion.reglas.Prec;


public class ProgramacionProduccion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*  ********  PUNTO 1  ********   */
        
        int[] p1 = {3,3,3,8};
        int[] d1 = {4,5,8,9};
        int jobs1 = 4;
        int m1 = 2;
        
        LmaxWPreemption lmax_1 = new LmaxWPreemption(jobs1, m1, p1, d1);
        System.out.println("\nPROBLEM 1 ...");
        lmax_1.schedule();
        
        /*  ********  PUNTO 9  ********   */
        
        int[] p9 = {4,5,7,9};
        int[] d9 = {5,6,9,10};
        int jobs9 = 4;
        int m9 = 2;
        
        LmaxWPreemption lmax_9 = new LmaxWPreemption(jobs9, m9, p9, d9);
        System.out.println("\n\nPROBLEM 9 ...");
        lmax_9.schedule();
        
        /*  ********  PUNTO 2  ********   */

        int[] p2 = {9,6,10,9,7,8,6,11,7,6,11,8,10};
        int jobs2 = 13;
        int m2 = 6;
        
        Cmax cmax2 = new Cmax(jobs2, m2, p2);
        System.out.println("\n\nPROBLEM 2 ...");
        cmax2.scheduleLPT();
        
        /*  ********  PUNTO 6  ********   */

        int[] p6 = {5,11,9,8,7,3,8,6,9,2,5,2,9};
        int jobs6 = 13;
        int m6 = 1000; //infinitas maquinas
        int[] ect = {5,16,25,8,15,11,24,22,20,2,5,7,16};
        int[] lct = {5,16,25,9,16,16,25,25,25,9,14,16,25};
        
        Cmax cmax6 = new Cmax(jobs6, m6, p6);
        System.out.println("\n\nPROBLEM 6 ...");
        cmax6.schedulePrecInfMach(ect,lct); //Programar para minimizar el Cmax con Precedencia e Infinitas máquinas.
        
        /*  ********  PUNTO 3  ********   */

        int[] p3 = {10,10,10,12,11,10,12,12,10,10,10,10};
        int m3 = 4;
        int[][] precedence = {
                    null,       //j1
                    {1},        //j2
                    {2,4,5},      //j3
                    null,       //j4
                    null,       //j5
                    {4,5},      //j6
                    null,       //j7
                    null,       //j8
                    null,       //j9
                    {7,8,9},    //j10
                    {7,8,9},    //j11
                    {8,9}       //j10
                };
        int jobs3 = precedence.length;
        
        Cmax cmax3 = new Cmax(jobs3, m3, p3);
        System.out.println("\n\nPROBLEM 3 ...");
        Prec prec = new Prec(precedence,jobs3,m3,p3);
        System.out.println("ECT: "+Arrays.toString(prec.ect));
        System.out.println("LCT: "+Arrays.toString(prec.lct));
        
    }
    
}
