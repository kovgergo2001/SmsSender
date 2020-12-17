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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
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

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                textView.setText((CharSequence) message.obj);
                // Toast.makeText(getActivity().getBaseContext(), (CharSequence) message.obj,Toast.LENGTH_LONG).show();
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Megerősítés");
        builder.setMessage("Biztosan el akarod küldeni?");
        builder.setPositiveButton("Igen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context mContext = getActivity().getBaseContext();
                        Thread sender = new Thread(() -> {
                            SmsManager smsManager = SmsManager.getDefault();

                            String message = Msg.getText().toString();
                            ArrayList<String> parts = smsManager.divideMessage(message);
                            String phoneNo;
                            final int[] sentMsg = {parts.size()};
                            final boolean[] failed = {false};
                            for (int i = 0; i < list.size(); i++) {
                                failed[0] = false;
                                phoneNo = list.get(i).trim();
                                String SENT = "SMS_SENT";
                                ArrayList<PendingIntent> sentPI = new ArrayList<>();
                                final Boolean[] sent = {false};
                                sentMsg[0] = parts.size();
                                BroadcastReceiver myBroadcast = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context arg0, Intent arg1) {
                                        switch (getResultCode()) {
                                            case Activity.RESULT_OK:
                                                sentMsg[0]--;
                                                System.out.println("elment:" + sentMsg[0]);
                                                break;
                                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                                failed[0] = true;
                                                System.out.println("hiba");
                                                break;
                                        }
                                        if (sentMsg[0] == 0) {
                                            sent[0] = true;
                                        }
                                    }
                                };
                                mContext.registerReceiver(myBroadcast, new IntentFilter(SENT));


                                for (int j = 0; j < parts.size(); j++) {
                                    sentPI.add(PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0));
                                    //pendingIntents.add(PendingIntent.getBroadcast(mContext, 0, sentIntent, 0));
                                }

                                System.out.println(sentPI.size());

                                smsManager.sendMultipartTextMessage(phoneNo, null, parts, sentPI, null);
                                System.out.println("elkell menjen:" + parts.size());
                                try {
                                    while (!sent[0]) {
                                        Thread.sleep(100);
                                        if (failed[0]) {
                                            i--;
                                            break;
                                        }
                                    }
                                    //CharSequence mess = (i + 1) + ". SMS elkuldve!";
                                    //textView.setText(mess);
                                    //Toast.makeText(getActivity().getBaseContext(),"asd", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }

                                mContext.unregisterReceiver(myBroadcast);
                                System.out.printf("");
                            }
                            //Toast.makeText(mContext, "SMS-ek kuldese elkezdve.", Toast.LENGTH_LONG).show();
                            Message ms = mHandler.obtainMessage(0, "Uzenetek elkuldve");
                            ms.sendToTarget();
                        });
                        sender.start();
                        Toast.makeText(mContext, "SMS-ek kuldese elkezdve.", Toast.LENGTH_LONG).show();
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