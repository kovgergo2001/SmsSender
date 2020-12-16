package com.example.myapplication.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private TextInputEditText soonToAdd;
    private Spinner spinner;
    private TextView count;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        textView.setText("Itt csinálod meg a csoportot!");
        TextInputEditText editText = root.findViewById(R.id.editText_dashboard);
        Button addButton = root.findViewById(R.id.button);
        Button removeButton = root.findViewById(R.id.button2);
        soonToAdd = root.findViewById(R.id.myTextInputLayout);
        spinner = root.findViewById(R.id.spinner2);
        count = root.findViewById(R.id.textView);
        List<String> input = new ArrayList<>();
        File myFile = new File("MyCache");
        try {
            FileInputStream fileInputStream = getActivity().getBaseContext().openFileInput(myFile.getName());
            ObjectInputStream objectInputStream = null;
            objectInputStream = new ObjectInputStream(fileInputStream);
            input = (List<String>) objectInputStream.readObject();
            objectInputStream.close();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, input);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        final List<String> list = input;

        count.setText((CharSequence) (list.size() + " darab telefonszám van."));

        addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (!list.contains(Objects.requireNonNull(soonToAdd.getText()).toString()) && soonToAdd.getText().length() == 10 && soonToAdd.getText().charAt(0) == '0' && containsOnlyNumbers(soonToAdd.getText().toString())) {
                    list.add(Objects.requireNonNull(soonToAdd.getText()).toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    CharSequence s = "Sikeresen hozzáadva: ";
                    s = (CharSequence) (s.toString() + soonToAdd.getText().toString());
                    textView.setText(s);
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = getActivity().getBaseContext().openFileOutput(myFile.getName(), Context.MODE_PRIVATE);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        objectOutputStream.writeObject(list);
                        objectOutputStream.close();
                        //System.out.println("Kiirva");
                        soonToAdd.setText((CharSequence) "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CharSequence s = "Hiba!";
                    if (list.contains(Objects.requireNonNull(soonToAdd.getText()).toString()))
                        s = "Ezt a telefonszámot már hozzáadtad!";
                    else if (soonToAdd.getText().length() != 10)
                        s = "Nem 10 számbol áll ez a telefonszám!";
                    else if (soonToAdd.getText().charAt(0) != '0')
                        s = "0 val kell kezdődjön ez a telefonszám!";
                    else if (!containsOnlyNumbers(soonToAdd.getText().toString()))
                        s = "Nem csak számokat tartalmaz ez a telefonszám!";
                    textView.setText(s);
                }
                count.setText((CharSequence) (list.size() + " darab telefonszám van."));
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() != 0) {
                    String name = spinner.getSelectedItem().toString();
                    list.remove(spinner.getSelectedItem().toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    CharSequence s = "Sikeresen törölve: ";
                    s = (CharSequence) (s.toString() + name);
                    textView.setText(s);
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = getActivity().getBaseContext().openFileOutput(myFile.getName(), Context.MODE_PRIVATE);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        objectOutputStream.writeObject(list);
                        objectOutputStream.close();
                        //System.out.println("Kiirva");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count.setText((CharSequence) (list.size() + " darab telefonszám van."));
            }
        });

        return root;
    }

    public boolean containsOnlyNumbers(String num) {
        for (int i = 0; i < num.length(); i++) {
            if (num.charAt(i) > '9' || num.charAt(i) < '0') return false;
        }
        return true;
    }
}