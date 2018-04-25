package com.esstudio.simplemarket.data;

import com.google.firebase.database.DataSnapshot;

public class User
{
	String uid;
	String name;
	String email;

	public User(String uid, String name, String email)
	{
		this.uid = uid;
		this.name = name;
		this.email = email;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public static User from(DataSnapshot snapshot)
	{
		return new User(snapshot.getKey(),
				snapshot.child("name").getValue(String.class),
				snapshot.child("email").getValue(String.class)
		);
	}
}
