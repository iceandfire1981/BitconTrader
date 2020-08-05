package com.frt.BitconTrader;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Trade app main begin===========================");
		TraderEntry entry = new TraderEntry();
		entry.startTrade();
		
//		TestEntry entry = new TestEntry();
//		entry.startTest();
		System.out.println("Trade app main end===========================");
	}
}
