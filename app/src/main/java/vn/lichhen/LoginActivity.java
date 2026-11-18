package vn.lichhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public final class  LoginActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.login);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        TextView error = findViewById(R.id.error);
        findViewById(R.id.login).setOnClickListener(view -> {
            String name = username.getText().toString().trim();
            String secret = password.getText().toString();
            if (name.isEmpty() || secret.trim().isEmpty()) {
                error.setText("Nhập tên đăng nhập và mật khẩu.");
                error.setVisibility(View.VISIBLE);
                return;
            }
            try (DatabaseHelper database = new DatabaseHelper(this)) {
                if (database.checkLogin(name, secret)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return;
                }
            }
            error.setText("Sai tên đăng nhập hoặc mật khẩu");
            error.setVisibility(View.VISIBLE);
        });
    }
}
