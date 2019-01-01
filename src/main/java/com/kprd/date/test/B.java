package com.kprd.date.test;

public class B {
	public static void main(String[] args) {
		TestThread t = new TestThread("a");
		TestThread t2 = new TestThread("b");
		TestThread t3 = new TestThread("c");
		TestThread t4 = new TestThread("d");
		
		t.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
