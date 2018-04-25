package com.esstudio.simplemarket.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

public class Item
{
	public enum State
	{
		READY,
		TRADING,
		COMPLETE
	}

	private String uid;

	private String title;
	private String content;
	private long price;
	private String imagePath;
	private long time;
	private String userId;
	private State state;
	private String buyerId;

	public Item()
	{
		//
	}

	public Item(String uid, String title, String content, long price, String imagePath, String userId, long time, String buyerId, State state)
	{
		this.uid = uid;
		this.title = title;
		this.content = content;
		this.price = price;
		this.imagePath = imagePath;
		this.userId = userId;
		this.time = time;
		this.buyerId = buyerId;
		this.state = state;
	}

	public String getBuyerId()
	{
		return buyerId;
	}

	public void setBuyerId(String buyerId)
	{
		this.buyerId = buyerId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public Long getPrice()
	{
		return price;
	}

	public void setPrice(long price)
	{
		this.price = price;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public static Item from(DataSnapshot snapshot)
	{
		return new Item(snapshot.getKey(),
				snapshot.child("title").getValue(String.class),
				snapshot.child("content").getValue(String.class),
				snapshot.child("price").getValue(Long.class),
				snapshot.child("imagePath").getValue(String.class),
				snapshot.child("userId").getValue(String.class),
				snapshot.child("time").getValue(Long.class),
				snapshot.child("buyerId").getValue(String.class),
				snapshot.child("state").getValue(State.class)
		);
	}
}
