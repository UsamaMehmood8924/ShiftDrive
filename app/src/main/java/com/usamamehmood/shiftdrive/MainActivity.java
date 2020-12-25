package com.usamamehmood.shiftdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText _LoginEmail, _LoginPassword;
    Button _loginBtn;
    TextView _ForgetPassword, _NavToReg;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _LoginEmail = findViewById(R.id.loginEmail);
        _LoginPassword = findViewById(R.id.loginPassword);
        _loginBtn = findViewById(R.id.loginButton);
        _ForgetPassword = findViewById(R.id.forgetPasswordbBtn);
        _NavToReg = findViewById(R.id.navToReg);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("UserProfiles");

        _ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,DetectCarDamage.class);
                startActivity(i);
            }
        });

        _NavToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin()
    {
        String email = _LoginEmail.getText().toString();
        String password = _LoginPassword.getText().toString();

        if (email.isEmpty())
            if (email.equals("") || password.equals("")) return;
        //Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Login in progress...");
        pd.show();

        //Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (!task.isSuccessful()) {
                    pd.dismiss();
                    showErrorDialog("There was a problem signing in");
                }
                else {

                    //Here User is authenticated. Now Check Whether this user is also a admin or not. If admin, then show LoginAs Screen, otherwise manually logged Home activity.
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = user.getUid();
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot snapshot1 : snapshot.getChildren() )
                            {

                                User _user = snapshot1.getValue(User.class);

                                if(_user.getId().equals(uid))
                                {
                                    UserCredentials.setPhone(_user.getPhone());
                                    UserCredentials.setPassword(_user.getPassword());
                                    UserCredentials.setEmail(_user.getEmail());
                                    UserCredentials.setName(_user.getName());
                                    UserCredentials.setId(uid);

                                    pd.dismiss();

                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    finish();
                                    startActivity(i);
                                    break;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }

    //Show error on screen with an alert dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}