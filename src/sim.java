import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class sim {
    //static final int N =8;
    //static final int ts=50; //ms
    //static final int td=240*ts;
    //static double P;
    //static int tp;
    //static final int tdifs=30*ts;
    //static final int tifs=10*ts;
    //double M;
    static Semaphore sem=new Semaphore(1);
    //static medium me=new medium();
    public static void main(String[] args){
        double M,P;
        int tp,N=8;
        int ts=50; //ms
        int td=240*ts;
        Scanner s=new Scanner(System.in);
        System.out.print("P probability: ");
        P=s.nextDouble();
        System.out.print("Single packet transmission time (integer tp) where "+.3*td+" < tp < "+.6*td+": ");
        tp=s.nextInt();
        System.out.print("Number of packets (M) 1-6 inclusive: ");
        M=s.nextDouble();
        s.close();
        for(int i=0;i<N;i++){
            new Thread(new station(P,tp,M,i)).start();
        }
    }

    public static class medium{
        private boolean med;
    }

    public static class station implements Runnable{
        static final int N =8;
        static final int ts=50; //ms
        static final int td=240*ts;
        static final int tdifs=30*ts;
        static final int tifs=10*ts;
        static int k=1;
        static long ttot=0;
        double P,M;
        int i,tp;
        int tcw;
        int W;
        public station(double P,int tp,double M,int i){
            this.P=P;
            this.tp=tp;
            this.M=M;
            this.i=i;
        }

        @Override
        public void run() {
            System.out.println("Station "+i+" created");
            s1();
        }

        public void s1(){
            try {
                Thread.sleep(tp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s2();
        }

        public void s2(){
            int p=(int)Math.round(100*P);
            int t=new Random().nextInt(100);
            if(t>=p) s1();
            else s4();
        }

        public void s4(){
            if(sem.availablePermits()==0) s5();
            else s6();
        }

        public void s5(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot+=ts;
            s4();
        }

        public void s6(){
            tcw=k*W;
            s7();
        }

        public void s7(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcw-=ts;
            ttot=ttot+ts;
            s9();
        }

        public void s9(){
            if(sem.availablePermits()==0) s10();
            else s16();
        }

        public void s10(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot+=ts;
            s12();
        }

        public void s12(){
            if(sem.availablePermits()==0) s10();
            else s13();
        }

        public void s13(){
            try {
                Thread.sleep(tdifs);
                ttot+=tdifs;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s14();
        }

        public void s14(){
            //System.out.println("S14 {P{}{{}}{}{}{}{}{}{}{}{][][]");
            if(tcw>0) s7();
            else s15();
        }

        public void s15(){
            k=2*k;
            if(k>16) k=16;
            s6();
        }

        public void s16(){
            if(tcw>0) s7();
            else s17();
        }

        public void s17(){
            try {
                sem.acquire();
                System.out.println("Station "+i+" is transmitting");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s18();
        }

        public void s18(){
            try {
                Thread.sleep((long) tp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot+=tp;
            s19();
        }

        public void s19(){
            try {
                Thread.sleep(tifs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot+=tifs;
            s20();
        }

        public void s20(){
            System.out.println("Station "+i+" releasing medium");
            sem.release();
            s21();
        }

        public void s21(){
            try {
                Thread.sleep(tdifs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot+=tdifs;
            s22();
        }

        public void s22(){
            M--;
            if(M>0){
                s4();
            }else System.out.println("ttot for station "+i+": "+ttot);
        }

    }
}