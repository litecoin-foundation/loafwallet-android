package com.breadwallet.tools.sqlite;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 9/25/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.breadwallet.presenter.entities.BRBusinessEntity;
import com.breadwallet.tools.manager.BRReportsManager;
import com.breadwallet.tools.util.BRConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.breadwallet.presenter.entities.BRBusinessEntity.*;

public class BusinessesDataSource implements BRDataSourceInterface {
    private static final String TAG = BusinessesDataSource.class.getName();

    private AtomicInteger mOpenCounter = new AtomicInteger();

    // Database fields
    private SQLiteDatabase database;
    private final BRSQLiteHelper dbHelper;
    private final String[] allColumns = {
            BRSQLiteHelper.BUSINESS_COLUMN_ID,
            BRSQLiteHelper.BUSINESS_BUSINESSNAME,
            BRSQLiteHelper.BUSINESS_BUSINESSPRODUCTS,
            BRSQLiteHelper.BUSINESS_LAT,
            BRSQLiteHelper.BUSINESS_LNG,
            BRSQLiteHelper.BUSINESS_DATESTART,
            BRSQLiteHelper.BUSINESS_REGLENGTH
    };

    private static BusinessesDataSource instance;

    public static BusinessesDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new BusinessesDataSource(context);
        }
        return instance;
    }

    public BusinessesDataSource(Context context) {
        dbHelper = BRSQLiteHelper.getInstance(context);
    }

    public void putBusinesses(Collection<BRBusinessEntity> businessEntities) {
        if (businessEntities == null) return;

        try {

            database = openDatabase();
            database.beginTransaction();
            for (BRBusinessEntity b : businessEntities) {
                ContentValues values = new ContentValues();
                values.put(BRSQLiteHelper.BUSINESS_BUSINESSNAME, b.businessname);
                values.put(BRSQLiteHelper.BUSINESS_BUSINESSPRODUCTS, b.businessproducts);
                values.put(BRSQLiteHelper.BUSINESS_LAT, b.lat);
                values.put(BRSQLiteHelper.BUSINESS_LNG, b.lng);
                values.put(BRSQLiteHelper.BUSINESS_DATESTART, b.dateStart);
                values.put(BRSQLiteHelper.BUSINESS_REGLENGTH, b.regLength);
                database.insertWithOnConflict(BRSQLiteHelper.BUSINESS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            database.setTransactionSuccessful();
        } catch (Exception ex) {
            BRReportsManager.reportBug(ex);
            //Error in between database transaction
        } finally {
            database.endTransaction();
            closeDatabase();
        }

    }

    public void deleteAllBusinesses() {
        try {
            database = openDatabase();
            database.delete(BRSQLiteHelper.BUSINESS_TABLE_NAME, BRSQLiteHelper.BUSINESS_COLUMN_ID + " <> -1", null);
        } finally {
            closeDatabase();
        }
    }

    public List<BRBusinessEntity> getAllBusinesses() {

        List<BRBusinessEntity> businesses = new ArrayList<>();
        Cursor cursor = null;
        try {
            database = openDatabase();

            cursor = database.query(BRSQLiteHelper.BUSINESS_TABLE_NAME,
                    allColumns, null, null, null, null, "\'" + BRSQLiteHelper.BUSINESS_BUSINESSNAME + "\'");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                BRBusinessEntity businessEntity = cursorToBusiness(cursor);
                businesses.add(businessEntity);
                cursor.moveToNext();
            }
            // make sure to close the cursor
        } finally {
            if (cursor != null)
                cursor.close();
            closeDatabase();
        }

        return businesses;
    }

    private BRBusinessEntity cursorToBusiness(Cursor cursor) {
        BRBusinessEntity businessEntity = new BRBusinessEntity(cursor.getString(1), cursor.getString(2), cursor.getDouble(3),cursor.getDouble(4),cursor.getString(5),cursor.getInt(6));
        businessEntity.setId(cursor.getInt(0));
        return businessEntity;
    }

    @Override
    public SQLiteDatabase openDatabase() {
//        if (mOpenCounter.incrementAndGet() == 1) {
        // Opening new database
        if (database == null || !database.isOpen())
            database = dbHelper.getWritableDatabase();
        dbHelper.setWriteAheadLoggingEnabled(BRConstants.WAL);
//        }
//        Log.d("Database open counter: ",  String.valueOf(mOpenCounter.get()));
        return database;
    }

    @Override
    public void closeDatabase() {
//        if (mOpenCounter.decrementAndGet() == 0) {
//            // Closing database
//        database.close();
//
//        }
//        Log.d("Database open counter: " , String.valueOf(mOpenCounter.get()));
    }
}