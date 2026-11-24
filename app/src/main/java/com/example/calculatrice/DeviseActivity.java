package com.example.calculatrice;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class DeviseActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder currentInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvDisplay.setText("0 $");

        Button btnEquals = findViewById(R.id.btnEquals);
        btnEquals.setText("Exchange");

        int[] numericButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button button = (Button) v;
            currentInput.append(button.getText().toString());
            tvDisplay.setText(currentInput.toString() + " $");
        };

        for (int id : numericButtonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentInput.setLength(0);
            tvDisplay.setText("0 $");
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
                tvDisplay.setText(currentInput.length() > 0 ? currentInput.toString() + " $" : "0 $");
            }
        });

        btnEquals.setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                try {
                    double usd = Double.parseDouble(currentInput.toString());
                    double rate = 10.0; // 1 USD = 10.0 MAD (Dirham)
                    double mad = usd * rate;
                    tvDisplay.setText(String.format(Locale.getDefault(), "%.2f MAD", mad));
                    currentInput.setLength(0);
                    currentInput.append(mad);
                } catch (Exception e) {
                    tvDisplay.setText("Error");
                    currentInput.setLength(0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.calc_standard) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.calc_scientifique) {
            startActivity(new Intent(this, ScientificActivity.class));
            finish();
            return true;
        } else if (id == R.id.devise) {
            // Already here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
