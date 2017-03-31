
public class CountingSemaphore {

    private int count;
    
    public CountingSemaphore(int initialCount) {
        count = initialCount;
    }
    
    public int getCount() {
        return count;
    }
    
    public synchronized void take() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        count--;
    }
    
    public synchronized void release() {
        count++;
        notify();
    }
}
