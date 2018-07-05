package com.example.project1;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Tab1_Fragment extends Fragment {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView lv;
    DBHelper mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);
        mydb = new DBHelper(getActivity());
        ArrayList array_list = mydb.getAllContacts();
        ContactListAdapter contactListAdapter = new ContactListAdapter(getActivity(), R.layout.tab1_listitem, array_list);

        lv = view.findViewById(R.id.listView1);
        lv.setAdapter(contactListAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contact contact = (Contact) adapterView.getItemAtPosition(position);
                int id_To_Search = contact.getId();

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getActivity(), Tab1_DisplayContact.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.item1:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getActivity(), Tab1_DisplayContact.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

//------------------------------------
class Contact {
    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact(String name, int id) {
        this.name = name;
        this.id = id;
    }
}

class ContactListAdapter extends ArrayAdapter<Contact>{
    private Context mcontext;
    int mresource;

    public ContactListAdapter(Context context, int resource, ArrayList<Contact> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        String name = getItem(position).getName();
        int id = getItem(position).getId();

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertview = inflater.inflate(mresource, parent, false);

        TextView tv1 = convertview.findViewById(R.id.tv1);
        TextView id1 = convertview.findViewById(R.id.id1);

        tv1.setText(name);
        id1.setText(String.valueOf(id));

        return convertview;
    }
}

class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBNAME.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table contacts " + "(id integer primary key, name text,phone text,email text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact(String name, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id =" +id+ ";", null);
        return res;
    }

    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + CONTACTS_COLUMN_ID + " FROM contacts WHERE " + CONTACTS_COLUMN_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean updateContact(Integer id, String name, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts", "id = ? ", new String[]{Integer.toString(id)});
    }

    public ArrayList<Contact> getAllContacts(){
        ArrayList<Contact> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String nameContact = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
            int nameId = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID));
            Contact contact = new Contact(nameContact, nameId);
            array_list.add(contact);
            res.moveToNext();
        }
        return array_list;
    }
}