
public class Mutex {
    private boolean signal = true;
    
    public synchronized void take() throws InterruptedException {
        while (!signal) {
            wait();
        }
        signal = false;
    }
    
    public synchronized void release() {
        signal = true;
        notify();
    }
}
