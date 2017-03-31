
public class Mutex {
    private boolean signal = true;
    
    public synchronized void take() {
        signal = true;
        notify();
    }
    
    public synchronized void release() throws InterruptedException {
        while (!signal) {
            wait();
        }
        signal = false;
    }
}
