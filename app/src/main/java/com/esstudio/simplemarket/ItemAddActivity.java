package com.esstudio.simplemarket;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esstudio.simplemarket.data.Item;
import com.firebase.ui.auth.ui.ProgressDialogHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ItemAddActivity extends AppCompatActivity
{
	private static final int PICK_FILE = 10000;

	private EditText mTitle;
	private EditText mContent;
	private EditText mPrice;
	private Button mSelect;
	private Button mUpload;
	private ImageView mImage;

	private Uri mSelectedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_add);

		mTitle = findViewById(R.id.title);
		mContent = findViewById(R.id.content);
		mPrice = findViewById(R.id.price);
		mSelect = findViewById(R.id.select_image);
		mUpload = findViewById(R.id.upload);
		mImage = findViewById(R.id.image);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("내 물건 올리기");
		actionBar.setDisplayHomeAsUpEnabled(true);

		mSelect.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO : https://developer.android.com/training/basics/intents/sending.html?hl=ko
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				startActivityForResult(intent, PICK_FILE);
			}
		});

		mUpload.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String title = mTitle.getText().toString();
				String content = mContent.getText().toString();
				int price = Integer.valueOf(mPrice.getText().toString());

				if (!emptyCheck(title, content, price, mSelectedImage))
				{
					return;
				}

				byte[] imageData = readImageData(mSelectedImage);

				String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
				String imagePath = "images/" + System.currentTimeMillis() + "_" + mSelectedImage.toString().hashCode();

				final Item item = new Item(null, title, content, price, imagePath, userId, System.currentTimeMillis(), "", Item.State.READY);

				uploadItem(item, imagePath, imageData);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_FILE && resultCode == RESULT_OK)
		{
			mSelectedImage = data.getData();

			Glide.with(this)
					.load(mSelectedImage)
					.into(mImage);
		}
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

	private void uploadItem(Item item, String imagePath, byte[] imageData)
	{
		final ProgressDialogHolder progress = new ProgressDialogHolder(this);
		progress.showLoadingDialog(R.string.please_wait);

		FirebaseStorage storage = FirebaseStorage.getInstance();
		StorageReference imageRef = storage.getReference().child(imagePath);
		imageRef.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
			{
				progress.dismissDialog();

				Toast.makeText(ItemAddActivity.this, "업로드 되었습니다.", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		DatabaseReference mItemRef = FirebaseDatabase.getInstance()
				.getReference()
				.child("/market");

		String key = mItemRef.push().getKey();
		mItemRef.child(key).setValue(item);
	}

	private byte[] readImageData(Uri selectedImage)
	{
		byte[] data = null;

		try
		{
			if (mSelectedImage == null)
			{
				return null;
			}

			InputStream in = getContentResolver().openInputStream(selectedImage);

			if (in == null)
			{
				return null;
			}

			data = IOUtils.toByteArray(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return data;
	}

	private boolean emptyCheck(String title, String content, int price, Uri selectedImage)
	{
		if (TextUtils.isEmpty(title))
		{
			Toast.makeText(ItemAddActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (TextUtils.isEmpty(content))
		{
			Toast.makeText(ItemAddActivity.this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (price == 0)
		{
			Toast.makeText(ItemAddActivity.this, "가격을 입력하세요", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (selectedImage == null)
		{
			Toast.makeText(ItemAddActivity.this, "이미지를 선택하세요", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
}
