package epos.girlsday.calculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import epos.girlsday.calculator.databinding.ActivityMainBinding;

import android.widget.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.button0.setOnClickListener(handleClickDefault);
        binding.button1.setOnClickListener(handleClickDefault);
        binding.button2.setOnClickListener(handleClickDefault);
        binding.button3.setOnClickListener(handleClickDefault);
        binding.button4.setOnClickListener(handleClickDefault);
        binding.button5.setOnClickListener(handleClickDefault);
        binding.button6.setOnClickListener(handleClickDefault);
        binding.button7.setOnClickListener(handleClickDefault);
        binding.button8.setOnClickListener(handleClickDefault);
        binding.button9.setOnClickListener(handleClickDefault);
        binding.buttonDecimal.setOnClickListener(handleClickDefault);
        binding.buttonAdd.setOnClickListener(handleClickDefault);
        binding.buttonSubtract.setOnClickListener(handleClickDefault);
        binding.buttonMultiply.setOnClickListener(handleClickDefault);
        binding.buttonDivide.setOnClickListener(handleClickDefault);

        binding.buttonEqual.setOnClickListener(handleClickResult);

        binding.buttonClear.setOnClickListener(handleClickClear);
    }

    private final View.OnClickListener handleClickDefault = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();
                String currentText = binding.tvResult.getText().toString();
                List<String> orderedNumberList = Arrays.asList(currentText.split("[-+×÷]"));
                String lastPart = orderedNumberList.get(orderedNumberList.size() - 1);

                if (currentText.isEmpty() && "+-×÷.".contains(buttonText)) {
                    binding.tvResult.setText("0");
                }

                Pattern pattern = Pattern.compile(".$");
                Matcher matcher = pattern.matcher(currentText);

                if (matcher.find() && "+-×÷.".contains(matcher.group()) && "+-×÷.".contains(buttonText)) {
                    binding.tvResult.setText(currentText.substring(0, currentText.length() - 1));
                }

                if (buttonText.equals(".")) {
                    if (!lastPart.contains(".")) {
                        binding.tvResult.append(buttonText);
                    }
                } else {
                    binding.tvResult.append(buttonText);
                }
            }
        }
    };

    private final View.OnClickListener handleClickResult = v -> {
        if (v.getId() != binding.buttonEqual.getId()) {
            return;
        }
        String text = binding.tvResult.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        try {
            char firstCharacter = text.charAt(0);
            Integer.parseInt(String.valueOf(firstCharacter));
        } catch (Exception e) {
            binding.tvResult.setText("ERROR");
            return;
        }

        //Aufsplitten des Textes ab Rechenzeichen
        List<Float> orderedNumberList = Arrays.stream(text.split("[-+×÷]")).map(Float::parseFloat).collect(Collectors.toList());
        List<Character> orderedOperatorList = new ArrayList<>();

        //String Rechenzeile wird in ein Array umgewandelt und durch iteriert, wobei Rechenzeichen in der Reihenfolge in die Liste orderedOperatorList hinzugefügt werden
        for (char c : text.toCharArray()) {
            if (c == '+' || c == '-' || c == '×' || c == '÷') {
                orderedOperatorList.add(c);
            }
        }

        //Wenn ein Rechenzeichen am Ende steht, löschen
        if (orderedNumberList.size() == orderedOperatorList.size()) {
            orderedOperatorList.remove(orderedOperatorList.size() - 1);
        }

        boolean error = false;
        // Punkt-Run (Mal, Geteilt)
        ListIterator<Character> iterator = orderedOperatorList.listIterator();
        while (iterator.hasNext()) {
            Character operator = iterator.next();
            float firstOperand = orderedNumberList.get(iterator.previousIndex());
            float secondOperand = orderedNumberList.get(iterator.nextIndex());
            float result;
            switch (operator) {
                // Multiplication
                case '×':
                    result = firstOperand * secondOperand;
                    orderedNumberList.set(iterator.previousIndex(), result);
                    orderedNumberList.remove(iterator.nextIndex());
                    iterator.remove();
                    break;
                // Division
                case '÷':
                    if (secondOperand == 0) {
                        error = true;
                    } else {
                        result = firstOperand / secondOperand;
                        orderedNumberList.set(iterator.previousIndex(), result);
                        orderedNumberList.remove(iterator.nextIndex());
                        iterator.remove();
                    }
                    break;
                // Addition and Subtraction (do nothing at first)
                default:
                    break;
            }
        }

        float endResult = orderedNumberList.get(0);
        // Strich-Run (Plus, Minus)
        for (int i = 0; i < orderedOperatorList.size(); i++) {
            if (orderedOperatorList.get(i) == '+') {
                endResult = endResult + orderedNumberList.get(i + 1);
            } else {
                endResult = endResult - orderedNumberList.get(i + 1);
            }
        }

        if (error) {
            binding.tvResult.setText("ERROR");
        } else {
            String stringEndResult = String.valueOf(Formatter(endResult));
            binding.tvResult.setText(stringEndResult.replace(',', '.'));
        }
    };

    private final View.OnClickListener handleClickClear = v -> {
        if (v.getId() == binding.buttonClear.getId()) {
            binding.tvResult.setText("");
        }
    };

    private String Formatter(float value) {
        DecimalFormat formatter = new DecimalFormat("0.##");
        return formatter.format(value);
    }
}