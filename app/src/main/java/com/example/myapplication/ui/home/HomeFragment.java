package com.example.myapplication.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        final TextInputEditText Msg = root.findViewById(R.id.editText_dashboard);
        final Button Send = root.findViewById(R.id.button3);
        String myString = "Jó napot kivánunk!      \n" + "MAXimális Kínálat      \n" + "MAXimális Kedvezmények         \n" + "minimális árak -csak NÁLUNK !!!!  \n\n\n\n\n\n\n\nAmíg a készlet tart.      \n" + "Velünk MAXimálisan Jól Jár !!! SUPERMARKET Kovács MAX\n" + "      \"Otthon\" a JÓ árakban!";
        Msg.setText((CharSequence) myString);
        List<String> input = new ArrayList<>();
        File myFile = new File("MyCache");
        try {
            FileInputStream fileInputStream = getActivity().getBaseContext().openFileInput(myFile.getName());
            ObjectInputStream objectInputStream = null;
            objectInputStream = new ObjectInputStream(fileInputStream);
            input = (List<String>) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        final List<String> list = input;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Megerősítés");
        builder.setMessage("Biztosan el akarod küldeni?");
        builder.setPositiveButton("Igen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager smsManager = SmsManager.getDefault();

                        String message = Msg.getText().toString();
                        ArrayList<String> parts = smsManager.divideMessage(message);
                        String phoneNo;
                        phoneNo = list.get(0).trim();
                        for (int i = 1; i < list.size(); i++) {
                            phoneNo = list.get(i).trim();

                            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);

                            try {
                                TimeUnit.MILLISECONDS.sleep(1000);
                                System.out.println("Megvan!");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }


                        Toast.makeText(getActivity().getBaseContext(), "SMS-ek átmásolva sikeresen.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(),
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }
}