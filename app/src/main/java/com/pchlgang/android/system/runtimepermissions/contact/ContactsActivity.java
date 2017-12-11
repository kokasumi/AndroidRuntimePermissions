package com.pchlgang.android.system.runtimepermissions.contact;

import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.pchlgang.android.system.runtimepermissions.R;

import java.util.ArrayList;

/**
 * Created by lart-02 on 2017/12/11.
 */

public class ContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "Contacts";
    /**
     * 查询PROJECTION
     */
    private static final String[] PROJECTION = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    /**
     * 查询结果排序条件，按显示名升序排列
     */
    private static final String ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";
    private static String DUMMY_CONTACT_NAME = "__DUMMY CONTACT from runtime permissions sample";
    private AppCompatTextView mMessageText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mMessageText = findViewById(R.id.tv_contact_message);
    }

    public void addContact(View view) {
        //添加联系人需要2步
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        //第一步，建立一行新的联系人数据
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null);
        operations.add(builder.build());
        //第二步，设置联系人信息
        builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,DUMMY_CONTACT_NAME);
        operations.add(builder.build());

        ContentResolver resolver = getContentResolver();
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY,operations);
        } catch (RemoteException | OperationApplicationException e) {
            Snackbar.make(mMessageText.getRootView(), "Could not add a new contact: " +
                    e.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    public void showContact(View view) {
        //重新启动loader来查询第一个联系人的信息
        getLoaderManager().restartLoader(0,null,this);
    }

    /**
     * 初始化一个查询{@link ContactsContract}的{@link CursorLoader}
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI,PROJECTION,null,null,ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null) {
            int totalCount = data.getCount();
            if(totalCount > 0) {
                data.moveToFirst();
                String name = data.getString(data.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mMessageText.setText(getString(R.string.contacts_string,totalCount,name));
            }else {
                mMessageText.setText(R.string.contacts_empty);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMessageText.setText(R.string.contacts_empty);
    }

    public void onBackClick(View view) {
        finish();
    }
}
