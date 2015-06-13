package programacionproduccion;

public class Reglas {


    //Programacion de n Trabajos en 1 Maquinas
    //Jaime Acevedo Chedid y Holman Ospina Mateus
	 
    public static void main(String[] args) {
        System.out. println("         **********************             ");
        System.out. println(" Bienvenido a la Programacion de n Trabajos en 1 Maquinas     ");
        System.out. println("         **********************             ");
        System.out. println();

        //Inicio de Captura de Datos del para Programar

         int TotalTrabajos = 20; //miScanner.nextInt();
         int OrdenOriginal[]= {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}; 

         //int TimeOperacion[]= new int[TotalTrabajos];
         int TimeOperacion[]= {54,83,15,71,77,36,53,38,27,87,76,91,14,29,12,77,32,87,68,94};

         //int Disponibilidad[]= new int[TotalTrabajos];
         int Disponibilidad[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

         //int Entrega[]= new int[TotalTrabajos];
         int Entrega[]={70,130,50,110,140,98,77,99,87,150,190,200,40,56,67,180,94,250,230,300};

         //int Ponderacion[]= new int[TotalTrabajos];
         int Ponderacion[]={1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};


         System.out.println("La Informacion Registrada para Programar Fue "+ "\n ");

         for (int i=0; i<TotalTrabajos; i++){
                   System.out.println("|Job "+OrdenOriginal[i]+"|TP "+TimeOperacion[i]+"|r "+Disponibilidad[i]+"|d "+Entrega[i]+"|w "+ Ponderacion[i]);
         } 

         //Fin de Captura de Datos del para Programar

         System.out.println();

         //Inicio de Programacion de Regla de Despacho FIFO

         System.out.println("___________REGLA DE DESPACHO FIFO______________"+ "\n ");

         int SecuenciaFIFO[]= new int[TotalTrabajos];
         int TiempoInicioFIFO[]= new int[TotalTrabajos];
         int TiempoFinFIFO[]= new int[TotalTrabajos];
         int F_JobFIFO[]= new int[TotalTrabajos];
         int L_JobFIFO[]= new int[TotalTrabajos];
         int T_JobFIFO[]= new int[TotalTrabajos];
         int E_JobFIFO[]= new int[TotalTrabajos];
         int U_JobFIFO[]= new int[TotalTrabajos];

         //Calculo de la Secuencia

         System.out. println("La secuencia de Trabajos es: "+ "\n " );
         for (int i=0; i<TotalTrabajos; i++){
                   SecuenciaFIFO[i]= OrdenOriginal[i];
             } 

         //Imprime Secuencia

         for (int i=0; i<TotalTrabajos; i++){
                   System.out. print(SecuenciaFIFO[i]+ ", ");
             } 
         System.out.println("\n ");

         //Calculo de Indicadores FIFO	 

         int TiempoMaqFIFO =0;
         float SumaU_JobFIFO = 0;
         float SumaT_JobFIFO =0;
         float SumaF_JobFIFO =0;
         float SumaE_JobFIFO =0;
         int TmaxFIFO=0; 
         int CmaxFIFO;

        for (int i=0; i<TotalTrabajos; i++){
            int r = SecuenciaFIFO[i];
            if(TiempoMaqFIFO>Disponibilidad[r-1]){
                TiempoInicioFIFO[r-1]=TiempoMaqFIFO;
                TiempoFinFIFO[r-1] = TiempoInicioFIFO[r-1] + TimeOperacion[r-1];
                TiempoMaqFIFO= TiempoFinFIFO[r-1];
            }
            else{
                TiempoInicioFIFO[r-1]=Disponibilidad[r-1];
                TiempoFinFIFO[r-1] = Disponibilidad[r-1] + TimeOperacion[r-1];
                TiempoMaqFIFO= TiempoFinFIFO[r-1];
            }
            F_JobFIFO[r-1]=TiempoFinFIFO[r-1]-Disponibilidad[r-1];
            SumaF_JobFIFO= SumaF_JobFIFO +F_JobFIFO[r-1];
            L_JobFIFO[r-1] = TiempoFinFIFO[r-1] - Entrega[r-1];
            if (L_JobFIFO[r-1]<0){
                E_JobFIFO[r-1]= -L_JobFIFO[r-1];
                U_JobFIFO[r-1]= 0;
                SumaE_JobFIFO = SumaE_JobFIFO + 1;
            }
            else{
                T_JobFIFO[r-1] = L_JobFIFO[r-1];
                U_JobFIFO[r-1] = 1;
                SumaU_JobFIFO = SumaU_JobFIFO + U_JobFIFO[r-1];
                SumaT_JobFIFO = SumaT_JobFIFO + T_JobFIFO[r-1];
                if(T_JobFIFO[r-1]>TmaxFIFO){
                    TmaxFIFO = T_JobFIFO[r-1];
                }
            }
        }
         CmaxFIFO = TiempoMaqFIFO;

         //Imprimir Secuencia FIFO

         System.out.println(" El Esquema de Secuencia FIFO es: "+ "\n ");
         for (int i=0; i<TotalTrabajos; i++){
         int r = SecuenciaFIFO[i]; 
         System.out.println(" "+TiempoInicioFIFO[r-1]+"  "+r+"  "+TiempoFinFIFO[r-1]+" ");
         }
         System.out. println();

         //Imprimir Calculo de Indicadores FIFO

         System.out.println(" Los Indicadores de la Secuencia FIFO es: "+ "\n ");
         System.out.println(" El Makespan es: "+CmaxFIFO);
         System.out.println(" La Suma de los Tiempos de Flujo es: "+SumaF_JobFIFO);
         System.out.println(" La Suma de la Tardanza es: "+SumaT_JobFIFO);
         System.out.println(" La Suma de la Trabajos Tardios es: "+SumaU_JobFIFO);
         System.out.println(" La Suma de la Trabajos Adelantados es: "+SumaE_JobFIFO);
         System.out.println(" La Tardanza Maxima es: "+TmaxFIFO);
         System.out.println();
         System.out.println();

     }	
	
}