package useful;

import java.util.Arrays;

public class TestNine {
	/**
	 * 用来表示九连环的成员数组，每一位代表一个环，该位置上只可能是'0'或是'1'，数值'0'代表已经拆下，'1'代表未拆下；
	 */
	static int[] arr;
	
	/**
	 * 用来记录拆下九连环总共所需操作数，初始位0步
	 */
	static int step = 0;
	public static void main(String[] args) {
		int n = 9; //九连环
		arr = new int[n];
		for(int i=0; i<n; i++) //初始化九连环数组，都置为1代表初始每个环都在横梁上
			arr[i] = 1;
		System.out.println("初始化九连环状态：" + Arrays.toString(reverse(arr))); //初始打印九连环数组状态
		solve(n);
	}

	/**
	 * 解开所有前1~n个环的方法
	 * @param n
	 */
	private static void solve(int n) {
		for(int i=n-1; i>=0; i--) //从高位到低位逐个解开
			remove(i);
	}

	/**
	 * 仅装上第k位环的方法
	 * @param k
	 */
	private static void install(int k) {
		if(k < 0 || arr[k] == 1) return; //若该环本来就在，则直接返回
		if(k == 0) {setOne(k); return;} //若该环位为第一环，直接装上
		install(k-1); //先装上前一个环
		clear(k-1); //再拆掉前一环之前的所有环
		setOne(k); //最后将该环装上
	}
	
	/**
	 * 仅拆除第k位环方法
	 * @param k
	 */
	private static void remove(int k) {
		if(k == -1 || arr[k] == 0) return; //若该环本来就不在，则直接返回
		if(k == 0) {setZero(k); return;} //若该环位为第一环，直接拆除
		install(k-1); //先装上前一个环
		clear(k-1); //再拆掉前一环之前的所有环
		setZero(k); //最后将该环拆掉
	}
	
	/**
	 * 拆除第k位下面的所有环（不包括第k个环）
	 * @param k
	 */
	private static void clear(int k) {
		if(isClear(k)) return; //若全部拆除，则返回
		for(int i=k-1; i>=0; i--) { //从高位到低位逐个拆除
			remove(i);
		}
	}

	/**
	 * 将九连环数组上第i位置为'0'方法，代表此时拆下该环
	 * @param i
	 */
	private static void setZero(int i) {
		if(i >= 0 && i < 9) {
			if(arr[i] == 0) return; //若本身就是0，函数即刻返回
			arr[i] = 0;
			/*若执行到这，说明这里含有置0操作，那么一定操作了九连环，需要即刻打印出新状态下的九连环如下*/
			System.out.println("第 " + ++step + "步--置‘0’操作：拆下第" + (i+1) + "环-此刻九连环状态：" + Arrays.toString(reverse(arr)));
		}
	}
	
	/**
	 * 将九连环数组上第i位置为'1'方法，代表此时装上该环
	 * @param i
	 */
	private static void setOne(int i) {
		if(i >= 0 && i < 9) {
			if(arr[i] == 1) return; //若本身就是1，函数即刻返回
			arr[i] = 1;
			/*若执行到这，说明这里含有置1操作，那么一定操作了九连环，需要即刻打印出新状态下的九连环如下*/
			System.out.println("第 " + ++step + "步--置‘1’操作：装上第" + (i+1) + "环-此刻九连环状态：" + Arrays.toString(reverse(arr)));
		}
	}
	
	/**
	 * 判断九连环上第k环以下所有环是否都已经拆下
	 * @param k 第k环
	 * @return 若全已经拆下，或者该k环为第1环，则返回true，否，返回false
	 */
	private static boolean isClear(int k) {
		if(k == 0)
			return true;
		for(int i=0; i<k; i++)
			if(arr[i] == 1)
				return false;
		return true;
	}

	/**
	 * 实际中的九连环是最右手边是第1环，逐渐向左边一环解一环，这里为了更好说明问题，
	 * 也将数组翻转了下，数组从左向右依次为9~1环，这样将从右往左依次解开
	 * @param arr 九连环数组，函数里不改变该数组的数值
	 * @return 返回只为了打印显示用的新数组
	 */
	private static int[] reverse(int[] arr) {
		int[] c = new int[arr.length];
		for(int i=arr.length-1; i>-1; i--)
			c[i] = arr[arr.length - 1 - i];
		return c;
	}
}
