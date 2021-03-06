package com.example.myapplication.DB.DBI;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.provider.BaseColumns._ID;
import static com.example.myapplication.DB.DBI.ContractIngredient.PATH;
import static com.example.myapplication.DB.DBR.ContractRecipe.CONTENT_AUTHORITY;

/**
 * Created by ebtesam on 19/12/2018 AD.
 */

public class IngredientProvider extends ContentProvider {

    public static final int INGREDIENT = 100;
    public static final int RECIPE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

//        sUriMatcher.addURI(CONTENT_AUTHORITY, "cte", INGREDIENT);
//        sUriMatcher.addURI(CONTENT_AUTHORITY, "cte" + "/*", NAME);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH, INGREDIENT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH + "/#", RECIPE_ID);

    }



    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor rCursor;
        switch (match) {
            case INGREDIENT:
                rCursor = db.query(
                        ContractIngredient.Recipe.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        orderBy
                );
                break;
            case RECIPE_ID:
                String id = uri.getPathSegments().get(1);
                rCursor = db.query(
                        ContractIngredient.Recipe.TABLE_NAME,
                        projection,
                        _ID + "=?",
                        new String[]{id},
                        null, null, null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (getContext() != null) rCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return rCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INGREDIENT:
                return ContractIngredient.Recipe.CONTENT_LIST_TYPE;
            case RECIPE_ID:
                return ContractIngredient.Recipe.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INGREDIENT:
                return insertRecipe(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }


    private Uri insertRecipe(Uri uri, ContentValues values) {

        // Check that the name is not null
        String idRecipe = values.getAsString(ContractIngredient.Recipe._ID_Recipe );
        String measure = values.getAsString(ContractIngredient.Recipe.MEASURE );
        String quantity = values.getAsString(ContractIngredient.Recipe.QUANTITY );

        String ingredient = values.getAsString(ContractIngredient.Recipe.INGREDIENT );
        /// Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Log.e("Recipe", idRecipe+measure+quantity+ingredient + uri);

        // Insert the new pet with the given values
        long id = database.insert(ContractIngredient.Recipe.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e("Recipe", "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INGREDIENT:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ContractIngredient.Recipe.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_ID:
                // Delete a single row given by the ID in the URI
                selection = ContractIngredient.Recipe._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ContractIngredient.Recipe.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;}

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
