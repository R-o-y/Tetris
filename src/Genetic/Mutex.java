package Genetic;

public class Mutex {
	private boolean signal = true;

	public synchronized void release() {
		this.signal = true;
		this.notify();
	}

	public synchronized void take() throws InterruptedException{
		while(!this.signal) wait();
		this.signal = false;
	}
}
