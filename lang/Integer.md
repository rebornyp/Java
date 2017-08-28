# Integer类关键

**主要是理解一下一段代码含义就可**
- 自动装箱策略
- 缓存策略（cache）
- 以及Integer类和基本数据类型的关系

-----------------------------

```java
	public static void main(String[] args) {
		Integer a1 = new Integer(2);
		Integer a2 = new Integer(2);
		Integer b1 = Integer.valueOf(2);
		Integer b2 = Integer.valueOf(2);
		Integer c1 = 2;
		Integer c2 = 2;
		int d1 = 2;
		int d2 = 2;
		/*----------------a1--------------------*/
		System.out.println(a1 == a2); //false
		System.out.println(a1 == b1); //false
		System.out.println(a1 == c1); //false
		System.out.println(a1 == d1); //true
		
		/*----------------b1--------------------*/
		System.out.println(b1 == b2); //true
		System.out.println(b1 == c1); //true
		System.out.println(b1 == d1); //true
		
		/*----------------b1--------------------*/
		System.out.println(c1 == c2); //true
		System.out.println(c1 == d1); //true
	}
  ```
  
  ------------------
  
