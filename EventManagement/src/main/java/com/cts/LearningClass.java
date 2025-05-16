package com.cts;
import org.springframework.data.domain.Sort;

interface A{
	void show();
}

class B implements A{
	@Override
	public void show() {
		System.out.println("Implementation of inteface A");
	}
}

public class LearningClass {
	
	public static void main(String[] args) {
		
		A obj = () -> System.out.println("Implementation of interface A");
		
	}
	
	
}
