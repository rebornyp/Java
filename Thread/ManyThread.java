package useful;

import java.util.Stack;

public class ManyThread {

	public static void main(String[] args) {
		Bucket b = new Bucket();
		Producer p1 = new Producer(b);
		Consumer c1 = new Consumer(b);
		Consumer c2 = new Consumer(b);
		new Thread(p1).start();
		new Thread(c1).start();
//		new Thread(c2).start();
	}

}

class Bucket {
	static Stack<Toufu> s = new Stack<Toufu>();
	static int Max = 50;
	
	public synchronized void push() {
		while(s.size() > Max) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.notifyAll();
		s.add(new Toufu());
	}
	
	public synchronized Toufu pop() {
		while(s.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.notifyAll();
		return s.pop();
	}
}

class Toufu {
	static int id = 0;
	String name;
	Toufu() {
		id ++;
		name = id + "号豆腐";
	}
	public String toString() {
		return name;
	}
}

class Producer implements Runnable{
	static int id = 0;
	String name;
	Bucket b = null;
	Producer(Bucket b) {
		this.b = b;
		id ++;
		name = "生产者" + id + "号";
	}

	@Override
	public void run() {
		for(int i=0; i<10; i++) {
			synchronized(b) {
				System.out.println(name + ": 生产了第" + (i+1) + "个豆腐");
				b.push();
			}
			try {
				Thread.sleep((int)Math.random() * 10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}

class Consumer implements Runnable{
	static int id = 0;
	String name;
	Bucket b = null;
	Consumer(Bucket b) {
		this.b = b;
		id ++;
		name = "消费者" + id + "号";
	}

	@Override
	public void run() {
		for(int i=0; i<10; i++) {
			synchronized(b) {
				Toufu t = b.pop();
				System.out.println(name + ": 消费了第" + (i+1) + "个豆腐" + "(" + t + ")");
			}
			try {
				Thread.sleep((int)Math.random() * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
