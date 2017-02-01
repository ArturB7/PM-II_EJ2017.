package com.example.lenovo.students_contenproviderfoundation;

import android.content.ContentResolver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mNoControlEditText, mNombreEditText, mPuntosExtraEditText;
    Button mAddButton, mDeleteButton, mUpdateButton, mViewButton, mViewAllButton, mShowInfoButton;
    SQLiteDatabase db;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoControlEditText =(EditText)findViewById(R.id.et_numero_de_control);
        mNombreEditText =(EditText)findViewById(R.id.et_nombre);
        mPuntosExtraEditText =(EditText)findViewById(R.id.et_puntos_extras);

        mAddButton =(Button)findViewById(R.id.bt_add);
        mDeleteButton =(Button)findViewById(R.id.bt_delete);
        mUpdateButton =(Button)findViewById(R.id.bt_update);
        mViewButton =(Button)findViewById(R.id.bt_view);
        mViewAllButton =(Button)findViewById(R.id.bt_view_all);
        mShowInfoButton =(Button)findViewById(R.id.bt_show_info);

        mAddButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mViewButton.setOnClickListener(this);
        mViewAllButton.setOnClickListener(this);
        mShowInfoButton.setOnClickListener(this);

        db=openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(num_control VARCHAR, nombre VARCHAR,puntos_extra VARCHAR);");
    }

    public void onClick(View view)
    {
        if(view== mAddButton)
        {
            if(mNoControlEditText.getText().toString().trim().length()==0||
                    mNombreEditText.getText().toString().trim().length()==0||
                    mPuntosExtraEditText.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Introduzca todos los valores por favor");
                return;
            }
            db.execSQL("INSERT INTO student VALUES('"+ mNoControlEditText.getText()+"','"+ mNombreEditText.getText()+
                    "','"+ mPuntosExtraEditText.getText()+"');");
            showMessage("Exito", "Registro Agregado");
            clearText();
        }
        if(view== mDeleteButton)
        {
            if(mNoControlEditText.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Introduzca No. de Control por favor");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM student WHERE num_control='"+ mNoControlEditText.getText()+"'", null);
            if(c.moveToFirst())
            {
                db.execSQL("DELETE FROM student WHERE num_control='"+ mNoControlEditText.getText()+"'");
                showMessage("Exito", "Registro Eliminado");
            }
            else
            {
                showMessage("Error", "Numero de control invalido");
            }
            clearText();
            c.close();
        }
        if(view== mUpdateButton)
        {
            if(mNoControlEditText.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Introduzca No. de Control por favor");
                return;
            }
            Cursor c=db.rawQuery("SELECT * FROM student WHERE num_control='"+ mNoControlEditText.getText()+"'", null);
            if(c.moveToFirst())
            {
                db.execSQL("UPDATE student SET nombre='"+ mNombreEditText.getText()+"',puntos_extra='"+ mPuntosExtraEditText.getText()+
                        "' WHERE num_control='"+ mNoControlEditText.getText()+"'");
                showMessage("Exito", "Registro Modificado");
            }
            else
            {
                showMessage("Error", "Numero de control invalido");
            }
            clearText();
            c.close();
        }
        if(view== mViewButton)
        {
            if(mNoControlEditText.getText().toString().trim().length()==0)
            {
                showMessage("Error", "Introduzca No. de Control por favor");
                return;
            }

            Cursor c=db.rawQuery("SELECT * FROM student WHERE num_control='"+ mNoControlEditText.getText()+"'", null);

            if(c.moveToFirst())
            {
                mNombreEditText.setText(c.getString(1));
                mPuntosExtraEditText.setText(c.getString(2));
            }
            else
            {
                showMessage("Error", "Numero de control invalido");
                clearText();
            }
            c.close();
        }
        if(view== mViewAllButton)
        {
            Cursor c=db.rawQuery("SELECT * FROM student", null);

            if(c.getCount()==0)
            {
                showMessage("Error", "No hay registros");
                return;
            }
            StringBuilder builder=new StringBuilder();
            while(c.moveToNext())
            {
                builder.append("No.Control: ")
                        .append(c.getString(0))
                        .append("\n");
                builder.append("Nombre: ")
                        .append(c.getString(1))
                        .append("\n");
                builder.append("Puntos: ")
                        .append(c.getString(2))
                        .append("\n\n");
            }
            showMessage("Detalles del estudiante", builder.toString());
            c.close();
        }
        if(view== mShowInfoButton)
        {
            showMessage("Students Extra Point Management Application", "Developed By FJML based on Azim");
        }
    }

    public void showMessage(String title,String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearText()
    {
        mNoControlEditText.setText("");
        mNombreEditText.setText("");
        mPuntosExtraEditText.setText("");
        mNoControlEditText.requestFocus();
    }
}