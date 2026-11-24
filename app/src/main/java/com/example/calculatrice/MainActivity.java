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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder currentInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDisplay = findViewById(R.id.tvDisplay);
        Button btnEquals = findViewById(R.id.btnEquals);
        btnEquals.setText("=");

        int[] numericButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button button = (Button) v;
            currentInput.append(button.getText().toString());
            tvDisplay.setText(currentInput.toString());
        };

        for (int id : numericButtonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        int[] operatorButtonIds = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
        View.OnClickListener operatorListener = v -> {
            Button button = (Button) v;
            if (currentInput.length() > 0) {
                char lastChar = currentInput.charAt(currentInput.length() - 1);
                if (isOperator(lastChar)) {
                    currentInput.setCharAt(currentInput.length() - 1, button.getText().charAt(0));
                } else {
                    currentInput.append(button.getText().toString());
                }
                tvDisplay.setText(currentInput.toString());
            }
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(operatorListener);
        }

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentInput.setLength(0);
            tvDisplay.setText("0");
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
                tvDisplay.setText(currentInput.length() > 0 ? currentInput.toString() : "0");
            }
        });

        btnEquals.setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                double result = evaluateExpression(currentInput.toString());
                if (Double.isNaN(result)) {
                    tvDisplay.setText("Error");
                    currentInput.setLength(0);
                } else {
                    tvDisplay.setText(String.valueOf(result));
                    currentInput.setLength(0);
                    currentInput.append(result);
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
            return true;
        } else if (id == R.id.calc_scientifique) {
            startActivity(new Intent(this, ScientificActivity.class));
            finish();
            return true;
        } else if (id == R.id.devise) {
            startActivity(new Intent(this, DeviseActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private double evaluateExpression(String expression) {
        try {
            List<Double> numbers = new ArrayList<>();
            List<Character> operators = new ArrayList<>();

            StringBuilder numberBuffer = new StringBuilder();
            for (int i = 0; i < expression.length(); i++) {
                char ch = expression.charAt(i);

                if (isOperator(ch)) {
                    if (i == 0 && ch == '-') {
                        numberBuffer.append(ch);
                    } else if (i > 0 && isOperator(expression.charAt(i - 1)) && ch == '-') {
                        numberBuffer.append(ch);
                    } else if (i > 0) {
                        if (numberBuffer.length() > 0) {
                            numbers.add(Double.parseDouble(numberBuffer.toString()));
                            numberBuffer.setLength(0);
                        }
                        operators.add(ch);
                    }
                } else {
                    numberBuffer.append(ch);
                }
            }
            if (numberBuffer.length() > 0) {
                numbers.add(Double.parseDouble(numberBuffer.toString()));
            }

            if (numbers.isEmpty()) {
                return 0.0;
            }

            for (int i = 0; i < operators.size(); i++) {
                char op = operators.get(i);
                if (op == '*' || op == '/') {
                    if (i + 1 >= numbers.size()) break;
                    double num1 = numbers.get(i);
                    double num2 = numbers.get(i + 1);
                    double res;
                    if (op == '*') {
                        res = num1 * num2;
                    } else {
                        if (num2 == 0.0) {
                            return Double.NaN;
                        }
                        res = num1 / num2;
                    }

                    numbers.set(i, res);
                    numbers.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }

            double total = numbers.get(0);
            for (int i = 0; i < operators.size(); i++) {
                char op = operators.get(i);
                if (i + 1 >= numbers.size()) break;
                double nextNum = numbers.get(i + 1);
                if (op == '+') total += nextNum;
                if (op == '-') total -= nextNum;
            }

            return total;
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
