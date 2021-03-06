package com.example.myapplication.DB.DBR;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ebtesam on 19/12/2018 AD.
 */

public class DbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the  table
        String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + ContractRecipe.Recipe.TABLE_NAME + " ("
                + ContractRecipe.Recipe._ID_Recipe + " INTEGER PRIMARY KEY, "
                + ContractRecipe.Recipe.NAME + " TEXT, "
                + ContractRecipe.Recipe.SERVINGS + " TEXT );";


        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContractRecipe.Recipe.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
