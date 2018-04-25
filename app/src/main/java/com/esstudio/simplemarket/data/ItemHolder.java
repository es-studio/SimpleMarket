package com.esstudio.simplemarket.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.esstudio.simplemarket.R;
import com.esstudio.simplemarket.utils.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemHolder extends RecyclerView.ViewHolder
{
	private TextView title;
	private TextView content;
	private TextView price;
	private ImageView icon;
	private TextView state;

	public ItemHolder(View itemView)
	{
		super(itemView);
		title = itemView.findViewById(R.id.title);
		icon = itemView.findViewById(R.id.icon);
		content = itemView.findViewById(R.id.content);
		price = itemView.findViewById(R.id.price);
		state = itemView.findViewById(R.id.state);
	}

	public void bind(Context context, Item item)
	{
		title.setText(item.getTitle());
		content.setText(item.getContent());
		price.setText("₩ " + item.getPrice());
		state.setText(getPrettyState(item.getState()));

		StorageReference imageRef = FirebaseStorage.getInstance().getReference(item.getImagePath());

		GlideApp.with(context)
				.load(imageRef)
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(icon);
	}

	public String getPrettyState(Item.State state)
	{
		if (state != null)
		{
			switch (state)
			{
				case READY:
					return "";
				case TRADING:
					return "거래중";
				case COMPLETE:
					return "거래완료";
			}
		}

		return "";
	}
}
