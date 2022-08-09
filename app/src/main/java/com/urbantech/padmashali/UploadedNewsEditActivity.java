package com.urbantech.padmashali;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadCat;
import com.urbantech.AsyncTask.LoadUpload;
import com.urbantech.adapter.AdapterGalleryEdit;
import com.urbantech.interfaces.CatListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemCat;
import com.urbantech.item.ItemGallery;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MultiSelectSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class UploadedNewsEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Methods methods;
    private ProgressDialog progressDialog, progress;
    private LinearLayout ll_url;
    private RecyclerView rv_upload_gallery;
    private AdapterGalleryEdit adapterGallery;
    private Spinner spinner_type;
    private CardView cv_gallery,cv_upload_video_image;
    private MultiSelectSpinner spinner_cat;
    private AppCompatButton button_submit, button_upload_gallery,button_upload_video;
    private AppCompatEditText et_title, et_url, et_description;
    //private TextView tv_date;
    private ImageView iv_image;
    private RoundedImageView iv_upload_gallery;
    private ArrayList<String> arrayList_cat, arrayList_catid, arrayListType;
    private ArrayList<File> arrayList_file;
    private ArrayList<Uri> arrayList_file_gallery;
    private ArrayList<ItemGallery> arrayList_item_gallery;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_MULTIPLE_IMAGE_REQUEST = 2;
    private Bitmap bitmap_upload;
    private Uri imageUri,selectedVideoUri=null;
    private String cat_id = "", type = "";
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    private ItemNews itemNews;

    private AppCompatTextView uploaded_txt;
    private static final int SELECT_VIDEO = 3;
    private String selectedPath;
    private Uri contentUri;
    private TextView text_or,video_limit;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);

        Toolbar toolbar = findViewById(R.id.toolbar_upload);
        toolbar.setTitle(getString(R.string.edit_news));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);

        itemNews = Constant.itemNewsCurrent;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setCancelable(false);

        cv_upload_video_image=findViewById(R.id.cv_upload_video_image);

        cv_gallery = findViewById(R.id.cv_upload_gallery);
        rv_upload_gallery = findViewById(R.id.rv_upload_gallery);
        ll_url = findViewById(R.id.ll_upload_video_url);
        button_submit = findViewById(R.id.button_upload_wall_submit);
        button_upload_gallery = findViewById(R.id.btn_upload_add_gallery);
        et_title = findViewById(R.id.et_upload_video_title);
        et_url = findViewById(R.id.et_upload_video_url);
        et_description = findViewById(R.id.et_upload_description);
        //tv_date = findViewById(R.id.tv_upload_video_date);

        iv_image = findViewById(R.id.iv_upload_wall_submit);
        iv_upload_gallery = findViewById(R.id.iv_upload_gallery);

        button_upload_video=findViewById(R.id.upload_video_btn);
        uploaded_txt=findViewById(R.id.et_uploaded_txt);
        text_or=findViewById(R.id.text_or);
        video_limit=findViewById(R.id.video_limit);



        arrayList_cat = new ArrayList<>();
        arrayList_catid = new ArrayList<>();
        arrayListType = new ArrayList<>();
        arrayList_file_gallery = new ArrayList<>();
        arrayList_item_gallery = new ArrayList<>();
        arrayList_file = new ArrayList<>();

        arrayListType.add("Image");
        arrayListType.add("Video");
        arrayListType.add("Text");

        rv_upload_gallery.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        spinner_cat = findViewById(R.id.spinner_upload_wallcat);
        spinner_type = findViewById(R.id.spinner_upload_video_type);
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, R.layout.layout_spinner, arrayListType);
        spinner_type.setAdapter(adapterType);

        spinner_cat.hasNoneOption(false);

        if (methods.isNetworkAvailable()) {
            loadAllCat();
        } else {
            Toast.makeText(this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickImage();
                }
            }
        });

        iv_upload_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickMultipleImage();
                }
            }
        });

        button_upload_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.checkPer()) {
                    pickMultipleImage();
                }
            }
        });

        button_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (methods.checkPer()) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
                }
            }
        });

        /*tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        UploadedNewsEditActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
// If you're calling this from a support Fragment
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });*/

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isLogged) {
                    if (methods.isNetworkAvailable()) {
                        if (et_title.getText().toString().trim().equals("")) {
                            Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.enter_news_title), Toast.LENGTH_SHORT).show();
                        } else if (cat_id.equals("")) {
                            Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.select_1_cat), Toast.LENGTH_SHORT).show();
                        } /*else if (tv_date.getText().toString().trim().equals("")) {
                            Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.enter_date), Toast.LENGTH_SHORT).show();
                        }*/ else if (spinner_type.getSelectedItemPosition() == 1 && et_url.getText().toString().trim().equals("")) {
                            Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.enter_url), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                uploadNews();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    methods.clickLogin();
                }
            }
        });


        spinner_cat.setListener(new MultiSelectSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {
                if (indices.size() > 0) {
                    cat_id = arrayList_catid.get(indices.get(0));
                    for (int i = 1; i < indices.size(); i++) {
                        cat_id = cat_id + "," + arrayList_catid.get(indices.get(i));
                    }
                } else {
                    cat_id = "";
                }
            }

            @Override
            public void selectedStrings(List<String> strings) {

            }
        });

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    type = "image";
                    ll_url.setVisibility(View.GONE);
                    cv_gallery.setVisibility(View.VISIBLE);
                    cv_upload_video_image.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    type = "video";
                    ll_url.setVisibility(View.VISIBLE);
                    cv_gallery.setVisibility(View.GONE);
                    cv_upload_video_image.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    type="Text";
                    ll_url.setVisibility(View.GONE);
                    cv_gallery.setVisibility(View.GONE);
                    cv_upload_video_image.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAllCat() {
        LoadCat loadAllCat = new LoadCat(new CatListener() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                progress.dismiss();
                if (success.equals("1")) {
                    switch (verifyStatus) {
                        case "1":

                            String[] selectedCat = itemNews.getCatId().split(",");
                            int[] arrayListSelected = new int[selectedCat.length];

                            if (selectedCat.length > 0) {
                                cat_id = selectedCat[0];
                            }

                            int size = 0;
                            for (int i = 0; i < arrayListCat.size(); i++) {
                                arrayList_cat.add(arrayListCat.get(i).getName());
                                arrayList_catid.add(arrayListCat.get(i).getId());

                                for (int j = 0; j < selectedCat.length; j++) {
                                    String s = selectedCat[j];
                                    if (arrayListCat.get(i).getId().equals(s)) {
                                        arrayListSelected[size] = i;
                                        size = size + 1;
                                        if(j > 0) {
                                            cat_id = cat_id + "," + selectedCat[j];
                                        }

                                        break;
                                    }
                                }
                            }

                            String[] mStringArray = new String[arrayList_cat.size()];
                            mStringArray = arrayList_cat.toArray(mStringArray);

                            spinner_cat.setItems(mStringArray);

                            spinner_cat.setSelection(arrayListSelected);

                            setNewsData();
                            break;
                        case "-2":
                            methods.getInvalidUserDialog(message);
                            break;
                        case "-1":
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            break;
                    }
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_CATEGORY, 0, "", "", "", "", "", "", "", "", "", "", "", et_url.getText().toString(),  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadAllCat.execute();
    }

    private void setNewsData() {
        et_title.setText(itemNews.getHeading());
        et_description.setText(itemNews.getDesc());
        //tv_date.setText(itemNews.getDate());

        if (itemNews.getType().equals("image")) {
            spinner_type.setSelection(0, true);
        } else if(itemNews.getType().equals("video")){
            spinner_type.setSelection(1, true);
            et_url.setText(itemNews.getVideoUrl());
        }else {
            spinner_type.setSelection(2,true);
        }

        Picasso.get().load(itemNews.getImage()).into(iv_image);

//        for (int i = 0; i < itemNews.getGalleryList().size(); i++) {
//            arrayList_item_gallery.add(new ItemGallery(itemNews.getGalleryList().get(i).getImage()));
//        }

        arrayList_item_gallery.addAll(itemNews.getGalleryList());

        if (arrayList_item_gallery.size() > 0) {
            adapterGallery = new AdapterGalleryEdit(UploadedNewsEditActivity.this, itemNews.getId(), arrayList_item_gallery, arrayList_file_gallery);
            rv_upload_gallery.setAdapter(adapterGallery);

            iv_upload_gallery.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadNews() {
        File file = null;
        try {
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }

            for (int i = 0; i < arrayList_file_gallery.size(); i++) {
                String path = methods.getTempUploadPath(arrayList_file_gallery.get(i));
                if(!path.equals("")) {
                    arrayList_file.add(new File(path));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoadUpload loadUpload = new LoadUpload(new SuccessListener() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                if (success.equals("1")) {
                    if (registerSuccess.equals("1")) {

                        Constant.isNewsUpdated = true;

                        UploadedNewsEditActivity.this.finish();
                    } else {
                        Toast.makeText(UploadedNewsEditActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UploadedNewsEditActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }, methods.getAPIRequest(Constant.METHOD_EDIT_UPLOAD_NEWS, 0, et_title.getText().toString(), itemNews.getId(), et_description.getText().toString(), type, cat_id, "", "", "", "", "", Constant.itemUser.getId(), et_url.getText().toString(),  "","","","","","","","","","","","","","","","","","","","","",file, arrayList_file));
        loadUpload.execute();

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void pickMultipleImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_MULTIPLE_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            iv_image.setImageURI(imageUri);

        } else if (requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null && data.getClipData().getItemCount() > 0) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    arrayList_file_gallery.add(data.getClipData().getItemAt(i).getUri());
                    arrayList_item_gallery.add(new ItemGallery(data.getClipData().getItemAt(i).getUri()));
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                arrayList_file_gallery.add(uri);
                arrayList_item_gallery.add(new ItemGallery(uri));
            }

            if (adapterGallery == null) {
                adapterGallery = new AdapterGalleryEdit(UploadedNewsEditActivity.this, itemNews.getId(), arrayList_item_gallery, arrayList_file_gallery);
                rv_upload_gallery.setAdapter(adapterGallery);
            } else {
                adapterGallery.notifyDataSetChanged();
            }
            iv_upload_gallery.setVisibility(View.INVISIBLE);
        }else if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK && data != null) {

            if(data.getData()!=null) {
                selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                uploadVideo();
            }
        }
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadedNewsEditActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                if (s.equals("Error")) {
                    et_url.setText("");
                    Toast.makeText(UploadedNewsEditActivity.this, "Error uploading data", Toast.LENGTH_SHORT).show();
                } else{
                    uploaded_txt.setVisibility(View.VISIBLE);
                    uploaded_txt.setText("Video Uploaded Successfully");

                    et_url.setText(Html.fromHtml("<b>http://padmashaly.com/home/videos/<a href='" + s + "'>" + s + "</a></b>"));
                    et_url.setMovementMethod(LinkMovementMethod.getInstance());
                    et_url.setVisibility(View.GONE);
//                    et_url.setText("Video Uploades Successfully");
                    text_or.setVisibility(View.GONE);
                    button_upload_video.setVisibility(View.GONE);
                    video_limit.setVisibility(View.GONE);

                }
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                et_url.setText(msg);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
    
    public  String getPath( final Uri uri) {
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider

        if (isKitKat ) {
            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "") {
                    return fullPath;
                } else {
                    return null;
                }
            }


            // DownloadsProvider

            if (isDownloadsDocument(uri)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = UploadedNewsEditActivity.this.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    }
                    finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));


                                return getDataColumn(UploadedNewsEditActivity.this, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }


                    }
                }
                else {
                    final String id = DocumentsContract.getDocumentId(uri);

                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {

                        return getDataColumn(UploadedNewsEditActivity.this, contentUri, null, null);
                    }
                }
            }


            // MediaProvider
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};


                return getDataColumn(UploadedNewsEditActivity.this, contentUri, selection,
                        selectionArgs);
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri);
            }

            if(isWhatsAppFile(uri)){
                return getFilePathForWhatsApp(uri);
            }


            if ("content".equalsIgnoreCase(uri.getScheme())) {

                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri);
                }
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {

                    // return getFilePathFromURI(UploadsNewsActivity.this,uri);
                    return copyFileToInternalStorage(uri,"userfiles");
                    // return getRealPathFromURI(UploadsNewsActivity.this,uri);
                }
                else
                {
                    return getDataColumn(UploadedNewsEditActivity.this, uri, null, null);
                }

            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        else {

            if(isWhatsAppFile(uri)){
                return getFilePathForWhatsApp(uri);
            }

            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = UploadedNewsEditActivity.this.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private  boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }

    private String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }

    private String getDriveFilePath(Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = UploadedNewsEditActivity.this.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(UploadedNewsEditActivity.this.getCacheDir(), name);
        try {
            InputStream inputStream = UploadedNewsEditActivity.this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    /***
     * Used for Android Q+
     * @param uri
     * @param newDirName if you want to create a directory, you can set this variable
     * @return
     */
    private String copyFileToInternalStorage(Uri uri,String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = UploadedNewsEditActivity.this.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if(!newDirName.equals("")) {
            File dir = new File(UploadedNewsEditActivity.this.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(UploadedNewsEditActivity.this.getFilesDir() + "/" + newDirName + "/" + name);
        }
        else{
            output = new File(UploadedNewsEditActivity.this.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = UploadedNewsEditActivity.this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        }
        catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }

    private String getFilePathForWhatsApp(Uri uri){
        return  copyFileToInternalStorage(uri,"whatsapp");
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = UploadedNewsEditActivity.this.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    private  boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private  boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private  boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private  boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public boolean isWhatsAppFile(Uri uri){
        return "com.whatsapp.provider.media".equals(uri.getAuthority());
    }

    private  boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //tv_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }
}