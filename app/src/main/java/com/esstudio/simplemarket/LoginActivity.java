package com.esstudio.simplemarket;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esstudio.simplemarket.data.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity
{
	private static final int SIGN_IN = 20000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button login = findViewById(R.id.login);

		login.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO : https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
				Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
						.setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
						.setIsSmartLockEnabled(false)
						.build();

				startActivityForResult(intent, SIGN_IN);
			}
		});

		if (FirebaseAuth.getInstance().getCurrentUser() != null)
		{
			startActivity(new Intent(this, ItemListActivity.class));
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SIGN_IN && resultCode == RESULT_OK)
		{
			FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
			if (fuser != null)
			{
				Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();

				DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

				User user = new User(null, fuser.getDisplayName(), fuser.getEmail());
				users.child(fuser.getUid()).setValue(user);

				startActivity(new Intent(this, ItemListActivity.class));
				finish();
			}
		}
	}
}
