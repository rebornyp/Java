package com;

import java.io.Serializable;
import java.util.HashSet;

public class Test implements Serializable{



	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashSet<Person> h = new HashSet<Person>();
		Person p1 = new Person("li", 12, "female");
		Person p2 = new Person("li", 16, "female");
		Person p3 = new Person("lan", 32, "male");
		h.add(p1);
		h.add(p2);
//		h.add(p3);
		System.out.println(p1.equals(p2));
		System.out.println(h.size());
		for(Person o:h) {
			System.out.println(o.age);
		}
	}

}

class Person {
	String name;
	int age;
	String sex;
	
	Person(String name, int age, String sex) {
		this.name = name;
		this.age = age;
		this.sex = sex;
	}
	
		@Override
	public boolean equals(Object o) {
		if(!(o instanceof Person))
			return false;
		Person p = (Person)o;
		if(name.equals(p.name) && sex.equals(p.sex))
			return true;
		else
			return false;
	}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return this.name.hashCode() + this.sex.hashCode();
		}
}
