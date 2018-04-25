import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class sim {
    static boolean debug;
    static Semaphore sem=new Semaphore(1);
    static int N=8;
    static int[] ttot = new int[N];
    public static void main(String[] args){
        debug=args.length>0;
        double M,P;
        int tp,ts=50,td=240*ts;
        Scanner s=new Scanner(System.in);
        System.out.print("P probability: ");
        P=s.nextDouble();
        System.out.print("Single packet transmission time (integer tp) where "+.3*td+" < tp < "+.6*td+": ");
        tp=s.nextInt();
        System.out.print("Number of packets (M) 1-6 inclusive: ");
        M=s.nextDouble();
        s.close();
        for(int i=0;i<N;i++){//Have to start counting at 1 to make sure that ts<W<2Nts is possible
            new Thread(new station(P,tp,M,i)).start();
        }
    }

    public static class station implements Runnable{
        static final int ts=50; //ms
        static final int td=240*ts;
        static final int tdifs=30*ts;
        static final int tifs=10*ts;
        static int k=1;
        double P,M;
        int i,tp,o=0,tcw,W;
        public station(double P,int tp,double M,int i){
            this.P=P;
            this.tp=tp;
            this.M=M;
            this.i=i;
        }

        @Override
        public void run() {
            if(debug)System.out.println("Station "+i+" created");
            s1();
        }

        //All of the s# methods match to the numbered blocks in the flowchart in the project description
        //with the exception of the "Check medium status" and "Medium busy?" blocks which are combined.
        //If debug is not enabled then the only output will be the ttot value at the end of each thread
        //ending a cycle.

        void s1(){
            try {
                Thread.sleep(td);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s2();
        }

        void s2(){
            int p=(int)Math.round(100*P);
            int t=new Random().nextInt(100);
            if(t>=p){  //Assuming the distribution of Random.nextInt(int bound) is even/close to random this'll work
                if(debug)System.out.println("Station "+i+" has no data ready, sleeping");
                s1();
            }
            else s4();
        }

        void s4(){
            if(debug) System.out.println("Station "+i+" has data ready, checking medium");
            if(sem.availablePermits()==0) s5();
            else s6a();
        }

        void s5(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=ts;
            s4();
        }

        //There are two 6s on the diagram so s6a is the first chronologically

        void s6a(){
            try {
                Thread.sleep(tdifs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=tdifs;
            k=1;
            W=new Random().nextInt((2*N*ts-ts)-1)+ts+1;
            s6b();
        }

        void s6b(){
            tcw=k*W;
            s7();
        }

        void s7(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcw-=ts;
            ttot[i]+=ts;
            s9();
        }

        void s9(){
            if(!sem.tryAcquire()){
                if(debug) System.out.println("Station "+i+" failed to acquire: waiting");
                s10();
            }else{
                if(debug) System.out.println("Station "+i+" acquired medium");
                s16();
            }
        }

        void s10(){
            try {
                Thread.sleep(ts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=ts;
            s12();
        }

        void s12(){
            if(sem.availablePermits()==0) s10();
            else s13();
        }

        void s13(){
            try {
                Thread.sleep(tdifs);
                ttot[i]+=tdifs;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s14();
        }

        void s14(){
            if(tcw>0) s7();
            else s15();
        }

        void s15(){
            k=2*k;
            if(k>16) k=16;
            s6b();
        }

        void s16(){
            if(tcw>0){
                sem.release();
                s7();
            }
            else s17();
        }

        void s17(){
            if(debug)System.out.println("Station "+i+" is transmitting"); //Nothing is done to mark the medium as busy because the semaphore is acquired in s9
            s18();
        }

        void s18(){
            try {
                Thread.sleep((long) tp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=tp;
            s19();
        }

        void s19(){
            try {
                Thread.sleep(tifs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=tifs;
            s20();
        }

        void s20(){
            if(debug)System.out.println("Station "+i+" releasing medium");
            sem.release();
            s21();
        }

        void s21(){
            try {
                Thread.sleep(tdifs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ttot[i]+=tdifs;
            s22();
        }

        void s22(){
            M--;
            o++;
            if(M>0){
                System.out.println("ttot for station "+i+" after cycle "+o+": "+ttot[i]);
                s4();
            }else System.out.println("total ttot for station "+i+" for "+o+" cycle(s): "+ttot[i]);
        }

    }
}