package com.example.project1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Tab1_DisplayContact extends AppCompatActivity {
    private com.example.project1.DBHelper mydb;

    TextView name;
    TextView phone;
    TextView email;
    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_display_contact);
        name = findViewById(R.id.editTextName);
        phone = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.editTextEmail);

        mydb = new com.example.project1.DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");

            if (Value > 0) {
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();

                String nam = rs.getString(rs.getColumnIndex(com.example.project1.DBHelper.CONTACTS_COLUMN_NAME));
                String phon = rs.getString(rs.getColumnIndex(com.example.project1.DBHelper.CONTACTS_COLUMN_PHONE));
                String emai = rs.getString(rs.getColumnIndex(com.example.project1.DBHelper.CONTACTS_COLUMN_EMAIL));


                if (!rs.isClosed()) {
                    rs.close();
                }
                Button b = findViewById(R.id.button1);
                b.setVisibility(View.INVISIBLE);

                name.setText(nam);
                name.setFocusable(false);
                name.setClickable(false);

                phone.setText(phon);
                phone.setFocusable(false);
                phone.setClickable(false);

                email.setText(emai);
                email.setFocusable(false);
                email.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                getMenuInflater().inflate(R.menu.display_contact, menu);
            } else {
                getMenuInflater().inflate(R.menu.main_menu, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Edit_Contact:
                Button b = findViewById(R.id.button1);
                b.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                phone.setEnabled(true);
                phone.setFocusableInTouchMode(true);
                phone.setClickable(true);

                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);

                return true;
            case R.id.Delete_Contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mydb.deleteContact(id_To_Update);

                        Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //cancelled
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Delete Contact");
                d.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int Value = extras.getInt("id");
            if(name.length() != 0) {
                if (Value > 0) {
                    if (mydb.updateContact(id_To_Update, name.getText().toString(), phone.getText().toString(), email.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (mydb.insertContact(name.getText().toString(), phone.getText().toString(), email.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Not added", Toast.LENGTH_SHORT).show();
                    }
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Must enter name", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


