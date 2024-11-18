package com.sdrt.myhall;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView logo;
    private EditText fullName, studentTeacherStaffId, department, email, phone, password, confirmPassword;
    private Button registerButton;
    private TextView loginLink;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        logo = findViewById(R.id.logo);
        fullName = findViewById(R.id.full_name);
        studentTeacherStaffId = findViewById(R.id.student_teacher_staff_id);
        department = findViewById(R.id.department);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        // Set onClickListener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set onClickListener for the login link
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the LoginActivity (implement this)
                finish();
            }
        });
    }

    private void registerUser() {
        String name = fullName.getText().toString().trim();
        String id = studentTeacherStaffId.getText().toString().trim();
        String dept = department.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id) || TextUtils.isEmpty(dept) ||
                TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPhone) ||
                TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user in Firebase Auth
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, save additional user info in Firebase Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user != null ? user.getUid() : "";

                        User userInfo = new User(name, id, dept, userEmail, userPhone);
                        databaseReference.child(userId).setValue(userInfo)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Clear all text fields
                                        fullName.setText("");
                                        studentTeacherStaffId.setText("");
                                        department.setText("");
                                        email.setText("");
                                        phone.setText("");
                                        password.setText("");
                                        confirmPassword.setText("");

                                        // Show a toast message and suggest to log in
                                        Toast.makeText(RegistrationActivity.this, "Registration successful! Please log in.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // User class to store user info in Firebase Database
    public static class User {
        public String fullName;
        public String id;
        public String department;
        public String email;
        public String phone;

        public User(String fullName, String id, String department, String email, String phone) {
            this.fullName = fullName;
            this.id = id;
            this.department = department;
            this.email = email;
            this.phone = phone;
        }
    }
}
