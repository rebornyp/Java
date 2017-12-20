## 整齐的绘制有向无环图的算法

---

代码如下

```java
package useful;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class TPSort {

	static boolean[] marked;
	Stack<Integer> stack = new Stack<Integer>(); //拓扑排序顺序
	Queue<Integer> queue = new LinkedList<Integer>(); //队列，即深度优先遍历初次识别的顺序
	
	public static void main(String[] args) {
		TPSort t = new TPSort();
		File f = new File("C:\\Users\\Gastby\\Desktop\\c.txt");
		GraphTable g = new GraphTable(f);
		System.out.println(g);
		
		GraphTable gt = g.reverse();
//		System.out.println(gt);
		
		t.deepfirst(g, 0);
		
		/*System.out.print("拓扑排序如下：");
		while(!t.stack.isEmpty())
			System.out.print("-" + g.all[t.stack.pop()]);
		System.out.println();
		System.out.print("深度优先遍历如下：");
		while(!t.queue.isEmpty())
			System.out.print("-" + g.all[t.queue.poll()]);*/
		
//		GraphNode cur = g.all[t.stack.pop()];
		
		int[] arr = new int[t.stack.size()];
		for(int i=0; i<arr.length; i++)
			arr[i] = t.stack.pop(); //将拓扑排序的顺序放到数组中去了
		Stack<Integer> stack = new Stack<Integer>(); //保存中途的顶点信息的堆栈
		
		double x0 = 0, y0 = 0;
		double dx = 1, dy = -1;
		int pre = arr[0];
		g.all[pre].x = x0;
		g.all[pre].y = y0;
		for(int k=1; k<arr.length; k++) {
			int cur = arr[k];
			
			double yMin = Integer.MAX_VALUE;
			for(int i : gt.adj[cur]) {
				yMin = Math.min(yMin, g.all[i].y);
			}
			g.all[cur].y = yMin + dy;
			
			if(g.adj[arr[k-1]].size() > 1)
				stack.push(k-1); //存入在数组中的索引
			
			if(gt.adj[cur].size() > 1) {
				int index = stack.pop();
				double len = 0;
				for(int w : g.adj[arr[index]])
					len += (g.all[w].len);
				len += (g.adj[arr[index]].size() - 1) * dx;
				g.all[arr[index]].len = len;
				
				double mx = g.all[arr[index]].len / 2;
				int num = g.adj[arr[index]].size()-1;				
				for(int temp=index+1; temp<k; temp++) {
					if(g.linked(arr[index], arr[temp])) {
						if(num == g.adj[arr[index]].size()-1) mx -= g.all[g.adj[arr[index]].get(num)].len / 2;
						else mx -= g.all[g.adj[arr[index]].get(num)].len / 2 + g.all[g.adj[arr[index]].get(num+1)].len / 2 + dx;
					}
					g.all[arr[temp]].x += mx;
					if(g.linked(arr[temp], arr[k]))
						num --;
				}
				
				double avx = 0;
				for(int w : gt.adj[cur])
					avx += g.all[w].x;
				g.all[cur].x = avx / gt.adj[cur].size();
			}
			pre = cur;
		}
		System.out.println(g);
	}

	
	
	
	private void deepfirst(GraphTable g, int i) {
		marked = new boolean[g.V];
		stack.clear();
		queue.clear();
//		System.out.print("深度优先遍历如下：");
		dfs(g, i);
		System.out.println();
	}

	private void dfs(GraphTable g, int i) {
		marked[i] = true;
		queue.offer(i);
//		System.out.print(g.all[i] + "-");
		for(int w : g.adj[i])
			if(!marked[w]) dfs(g, w);
		stack.push(i);
	}

}

class GraphNode {
	
	private char c;
	double x, y;
	double len;
	GraphNode(char c) {
		this.c = c;
		x = 0;
		y = 0;
	}
	
	@Override
	public String toString() {
		return "顶点" + c + " 坐标：(" + x + "," + y + ") ";
	}
	
}

class GraphTable {
	int V, E;
	List<Integer>[] adj;
	GraphNode[] all;
	GraphTable(int n) {
		V = n;
		E = 0;
		adj = new ArrayList[V];
		for(int i=0; i<V; i++)
			adj[i] = new ArrayList<Integer>();
		all = new GraphNode[V];
		for(int i=0; i<V; i++)
			all[i] = new GraphNode((char)(i + 65));
		System.out.println("请依次输入有向无环图的每条边，欲结束输入‘-1’即可");
		Scanner s = new Scanner(System.in);
		while(s.hasNext()) {
			String temp = s.nextLine();
			String[] str = temp.split("\\s");
			if(temp.equals("-1")) break;
			int i = Integer.valueOf(str[0]);
			int j = Integer.valueOf(str[1]);
			adj[i].add(j);
			E ++;
		}
		s.close();
	}
	
	GraphTable(File f) {
		try {
			Scanner s = new Scanner(f);
			V = Integer.valueOf(s.nextLine());
			E = 0;
			adj = new ArrayList[V];
			for(int i=0; i<V; i++)
				adj[i] = new ArrayList<Integer>();
			all = new GraphNode[V];
			for(int i=0; i<V; i++)
				all[i] = new GraphNode((char)(i + 65));
			while(s.hasNext()) {
				String temp = s.nextLine();
				String[] str = temp.split("\\s");
				int i = Integer.valueOf(str[0]);
				int j = Integer.valueOf(str[1]);
				adj[i].add(j);
				E ++;
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	GraphTable(int n, boolean b) {
		if(!b) return;
		V = n;
		E = 0;
		adj = new ArrayList[V];
		for(int i=0; i<V; i++)
			adj[i] = new ArrayList<Integer>();
		all = new GraphNode[V];
		for(int i=0; i<V; i++)
			all[i] = new GraphNode((char)(i + 65));
	}
	
	GraphTable reverse() {
		GraphTable r = new GraphTable(V, true);
		for(int i=0; i<V; i++) 
			for(int w : adj[i])
				r.adj[w].add(i);
		return r;
	}
	
	boolean linked(int i, int j) {
		for(int w : adj[i])
			if(w == j)
				return true;
		return false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<V; i++) {
//			System.out.print(all[i] + ": ");
			sb.append(all[i] + ": ");
			for(int j : adj[i])
//				System.out.print("-" + all[j]);
				sb.append("-" + all[j]);
//			System.out.println();
			sb.append("\n");
		}
		return sb.toString();
	}
}
```
