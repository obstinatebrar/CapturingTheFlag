package com.example.surinderkahlon.mapdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Spinner spinner;
    Button button;
    FirebaseDatabase database;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);
        String[] items = new String[]{"Select Team", "A", "B"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        root = database.getReference();
    }
    public void RegiterPlayer(View v){
        String playerName = editText.getText().toString();
        String playerTeam = spinner.getSelectedItem().toString();;
     if(playerName.isEmpty()!=true&&playerTeam.isEmpty()!=true){
         Player player = new Player(playerName,playerTeam);
         String key = root.push().getKey();
         if (playerTeam == "A"){
             root.child("Players").child("A").child(key).setValue(player);
             Intent intent = new Intent(this, MapsActivity.class);
             intent.putExtra("name",player.playerName);
             intent.putExtra("team",player.playerTeam);
             intent.putExtra("key",key);
             startActivity(intent);
         }
         else if (playerTeam == "B"){
             root.child("Players").child("B").child(key).setValue(player);
             Intent intent = new Intent(this, MapsActivity.class);
             intent.putExtra("name",player.playerName);
             intent.putExtra("team",player.playerTeam);
             intent.putExtra("key",key);
             startActivity(intent);
         }
         else{
             AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
             alertDialog.setTitle("Alert");
             alertDialog.setMessage("Please Seelect a team");
             alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                         }
                     });
             alertDialog.show();
         }

    }
    }
}