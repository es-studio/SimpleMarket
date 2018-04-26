package com.esstudio.simplemarket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esstudio.simplemarket.data.Item;
import com.esstudio.simplemarket.data.ItemHolder;
import com.esstudio.simplemarket.utils.SimpleLineDecorator;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemListActivity extends AppCompatActivity
{
	private RecyclerView mList;
	private RecyclerView.Adapter mAdapter;
	private View mEmpty;

	private DatabaseReference mMarketRef;
	private ValueEventListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mEmpty = findViewById(R.id.empty);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("심플마켓");

		// TODO : https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
		mMarketRef = FirebaseDatabase.getInstance()
				.getReference()
				.child("/market");

		mListener = new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				initAdapter(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		};
		mMarketRef.addValueEventListener(mListener);

		mList = findViewById(R.id.list);
		mList.setLayoutManager(new LinearLayoutManager(this));
		mList.addItemDecoration(new SimpleLineDecorator(Color.parseColor("#888888"), 1));
		mList.setItemAnimator(null);
	}

	@Override
	protected void onDestroy()
	{
		mMarketRef.removeEventListener(mListener);

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.add)
		{
			startActivity(new Intent(this, ItemAddActivity.class));
		}

		if (item.getItemId() == R.id.logout)
		{
			AuthUI.getInstance().signOut(this)
					.addOnCompleteListener(new OnCompleteListener<Void>()
					{
						@Override
						public void onComplete(@NonNull Task<Void> task)
						{
							startActivity(new Intent(ItemListActivity.this, LoginActivity.class));
							finish();
						}
					});
		}

		return super.onOptionsItemSelected(item);
	}

	private void initAdapter(final DataSnapshot snapshot)
	{
		final List<DataSnapshot> snapshotList = new ArrayList<>();
		for (DataSnapshot dataSnapshot : snapshot.getChildren())
		{
			snapshotList.add(dataSnapshot);
		}

		Collections.sort(snapshotList, new Comparator<DataSnapshot>()
		{
			@Override
			public int compare(DataSnapshot o1, DataSnapshot o2)
			{
				long t1 = o1.child("time").getValue(Long.class);
				long t2 = o2.child("time").getValue(Long.class);

				if (t1 > t2) return -1;
				if (t1 < t2) return +1;

				return 0;
			}
		});

		mAdapter = new RecyclerView.Adapter<ItemHolder>()
		{
			@NonNull
			@Override
			public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
			{
				return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
			}

			@Override
			public void onBindViewHolder(@NonNull ItemHolder holder, int position)
			{
				final Item item = Item.from(snapshotList.get(position));
				holder.bind(ItemListActivity.this, item);
				holder.itemView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
						intent.putExtra("ITEM_ID", item.getUid());
						startActivity(intent);
					}
				});
			}

			@Override
			public int getItemCount()
			{
				return snapshotList.size();
			}
		};

		if (snapshotList.size() == 0)
		{
			mEmpty.setVisibility(View.VISIBLE);
		}
		else
		{
			mEmpty.setVisibility(View.GONE);
		}

		mList.setAdapter(mAdapter);
	}
}

