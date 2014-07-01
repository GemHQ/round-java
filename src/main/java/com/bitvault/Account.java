package com.bitvault;


import java.io.*;

import com.github.kevinsawicki.http.HttpRequest;

public class Account {
	public static final String accept="application/vnd.bitvault.account+json;version=1.0";
	public static final String content_type="application/vnd.bitvault.account_create+json;version=1.0";
	
public static void main(String args[])throws IOException
{
	String name;
	System.out.println("Enter name");
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
	name=br.readLine();
	create(name);
}
public static void create(String name)
{
	
	 String response = HttpRequest.post(name)
	    		.accept(accept)
	    		.contentType();
	 System.out.println("hii");
	 System.out.println(response);
	    		
	
	
}

}
