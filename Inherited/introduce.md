1-方法继承里面属性不具有多态表现。
2-父类构造器调用了父类某方法，而该方法被子类重写了，子类对象构建时，父类对象创建过程中调用父类自身构造器时调用的方法是<子类>的!
public class Divide {

	
	public static void main(String[] args) {
		N n = new N();
	}
	
}

abstract class Node {
	static int i = 10;
	
	Node() {
		change();
	}
	
	static void change() {
		System.out.println("Node is nice to meet you");
	}
}

class N extends Node{

	void m() {
		System.out.println(super.i);
	}
	
	static void change() {
		System.out.println("meet you");
	}
}
