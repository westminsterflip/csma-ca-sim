public class sim {
    static final int N =8;
    static final int ts=50; //ms
    static final int td=240*ts;
    static long P;
    static long tp;
    static final int tdifs=30*ts;
    static final int tifs=10*ts;
    static final int W=N*ts;
    static long M;
    public static void main(String[] args){
        for(int i=0;i<N;i++){
            new Thread(new station(P,tp,M)).start();
        }
    }

    public static class station implements Runnable{
        static final int N =8;
        static final int ts=50; //ms
        static final int td=240*ts;
        static final int tdifs=30*ts;
        static final int tifs=10*ts;
        static final int W=N*ts;
        long P,tp,M;
        public station(long P,long tp,long M){
            //this.P=P;
            //this.tp=tp;
            //this.M=M;
        }

        @Override
        public void run() {

        }
    }
}
