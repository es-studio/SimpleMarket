package com.esstudio.simplemarket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.esstudio.simplemarket.data.Item;
import com.esstudio.simplemarket.data.User;
import com.esstudio.simplemarket.utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemDetailActivity extends AppCompatActivity
{
	private Item mItem;

	private ImageView mImage;
	private TextView mTitle;
	private TextView mPrice;
	private TextView mContent;
	private TextView mBuyer;
	private Button mTrade;

	private View mBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);

		mImage = findViewById(R.id.image);
		mTitle = findViewById(R.id.title);
		mPrice = findViewById(R.id.price);
		mContent = findViewById(R.id.content);
		mBottom = findViewById(R.id.bottom);
		mTrade = findViewById(R.id.trade);
		mBuyer = findViewById(R.id.buyer);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("상품 설명");
		actionBar.setDisplayHomeAsUpEnabled(true);

		String itemId = getIntent().getStringExtra("ITEM_ID");

		final DatabaseReference itemRef = FirebaseDatabase.getInstance()
				.getReference("market")
				.child(itemId);

		itemRef.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				mItem = Item.from(dataSnapshot);
				bindItem(itemRef);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private void bindItem(DatabaseReference itemRef)
	{
		StorageReference imageRef = FirebaseStorage.getInstance().getReference(mItem.getImagePath());

		GlideApp.with(this)
				.load(imageRef)
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(mImage);

		mTitle.setText(mItem.getTitle());
		mContent.setText(mItem.getContent());
		mPrice.setText("₩ " + mItem.getPrice());

		boolean isMine = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mItem.getUserId());

		switch (mItem.getState())
		{
			case READY:
				mTrade.setText("거래하기");

				if (isMine)
				{
					mTrade.setOnClickListener(createCantBuyListener());
				}
				else
				{
					mTrade.setOnClickListener(createReadyListener(itemRef));
				}

				break;

			case TRADING:
			{
				showBuyerInfo();

				if (isMine)
				{
					mTrade.setText("구매완료");
					mTrade.setOnClickListener(createCompleteListener(itemRef));
				}
				else
				{
					mTrade.setText("거래중...");
					mTrade.setOnClickListener(createTradingListener());
				}
				break;
			}

			case COMPLETE:
			{
				showBuyerInfo();
				mTrade.setText("구매완료됨");
				mTrade.setOnClickListener(createAlreadyCompleteListener());
				break;
			}
		}
	}

	private void showBuyerInfo()
	{
		DatabaseReference buyerRef = FirebaseDatabase.getInstance()
				.getReference("users")
				.child(mItem.getBuyerId());

		buyerRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				User buyer = User.from(dataSnapshot);
				mBuyer.setText("구매자 : " + buyer.getName() + " (" + buyer.getEmail() + ")");
				mBuyer.setVisibility(View.VISIBLE);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{

			}
		});
	}

	private View.OnClickListener createCantBuyListener()
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(ItemDetailActivity.this, "내가 올린 물건은 구매할 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
		};
	}

	private View.OnClickListener createCompleteListener(final DatabaseReference itemRef)
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(ItemDetailActivity.this)
						.setTitle("구매완료")
						.setMessage("구매를 완료할까요?")
						.setPositiveButton("구매하기", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

								mItem.setState(Item.State.COMPLETE);
								mItem.setBuyerId(uid);
								itemRef.setValue(mItem);
								Toast.makeText(ItemDetailActivity.this, "구매가 완료되었습니다.", Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("필요없음", null)
						.show();
			}
		};
	}

	private View.OnClickListener createAlreadyCompleteListener()
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(ItemDetailActivity.this, "이미 구매가 완료되었습니다.", Toast.LENGTH_SHORT).show();
			}
		};
	}

	private View.OnClickListener createTradingListener()
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(ItemDetailActivity.this, "이미 거래중입니다.", Toast.LENGTH_SHORT).show();
			}
		};
	}

	private View.OnClickListener createReadyListener(final DatabaseReference itemRef)
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(ItemDetailActivity.this)
						.setTitle("거래하기")
						.setMessage("구매할까요?")
						.setPositiveButton("구매하기", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

								mItem.setState(Item.State.TRADING);
								mItem.setBuyerId(uid);
								itemRef.setValue(mItem);
								Toast.makeText(ItemDetailActivity.this, "구매되었습니다.", Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("필요없음", null)
						.show();
			}
		};
	}
}
