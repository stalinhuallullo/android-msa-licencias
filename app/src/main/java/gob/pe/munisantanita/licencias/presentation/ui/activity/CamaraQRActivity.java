package gob.pe.munisantanita.licencias.presentation.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.TextView;

import gob.pe.munisantanita.licencias.BuildConfig;
import gob.pe.munisantanita.licencias.R;
import gob.pe.munisantanita.licencias.presentation.presenter.view_model.ResultViewModel;
import gob.pe.munisantanita.licencias.presentation.utils.CameraSourcePreview;
import gob.pe.munisantanita.licencias.presentation.utils.Tools;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CamaraQRActivity extends AppCompatActivity {

    public String url = BuildConfig.base_url + BuildConfig._api;


    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private TextRecognizer textRecognizer;
    private String code_qr = "";
    private Boolean stoped = false;

    AlertDialog.Builder builder;
    ProgressDialog pd;
    public Context ctx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_qr);

        ctx = this;
        barcodeInfo = findViewById(R.id.code_info);
        preview = findViewById(R.id.cameraSourcePreview);

        initToolbar();
        setupBarcodeDetector();
        setupCameraSource();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hiddenProgress();
        stoped = false;
    }

    private void showProgress(){
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando...");
        pd.setCancelable(false);
        pd.show();
    }
    public void hiddenProgress(){
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            pd = null;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Escanear el código");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.green_900);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }


    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void setupBarcodeDetector() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                //.setBarcodeFormats(Barcode.QR_CODE)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    if(stoped == false){
                        stoped = true;
                        barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                            public void run() {
                                barcodeInfo.setText(    // Update the TextView
                                        barcodes.valueAt(0).displayValue
                                );
                                showCodeQR(barcodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }
            }
        });

        if (!barcodeDetector.isOperational()) {
            Log.w("TAG_QR", "Detector dependencies are not yet available.");
        }

        textRecognizer = new TextRecognizer.Builder(this).build();

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available.");
        }
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections)
            {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0)
                {
                    Log.d("ReceiveDetections", items.size()+"");
                    /*barcodeInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i=0 ; i < items.size(); i++)
                            {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            barcodeInfo.setText(stringBuilder.toString());
                        }
                    });*/
                }
            }
        });
    }

    public void mostrarDialogo(final String msj, final boolean type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("¡Mensaje!");
                    builder.setMessage(msj);
                    if (type){
                        builder.setPositiveButton(
                                "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                    }
                    else builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() { // define the 'Cancel' button
                        public void onClick(DialogInterface dialog, int which) {
                            stoped = false;
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });
    }


    private void showCodeQR(String qr)
    {

        String[] split = qr.split("/");
        String codigo = split[split.length - 1];
        String anio = split[split.length - 2];

        showProgress();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + anio+ "/" + codigo)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mostrarDialogo("onFailure", false);
                hiddenProgress();
                stoped = false;
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parse(response.body().string());
            }
        });

        /*URL miUrl = new URL(qr);
            String url_get = miUrl.getQuery();
            String[] parameters = url_get.split("&");

            String[] anio = parameters[0].split("anioLic=");
            String[] code = parameters[1].split("nroLic=");
            */
    }

    private void parse(String response) {
        JSONObject Jobject = null;

        try {
            Jobject = new JSONObject(response);

            JSONArray objArray = Jobject.getJSONArray("data");
            if(objArray.length() > 0){

                JSONObject obj = objArray.getJSONObject(0);
                ResultViewModel result = new ResultViewModel();

                result.setLicNum(obj.getString("licNum"));
                result.setLicAnio(obj.getString("licAnio"));
                result.setNomSolicitante(obj.getString("nomSolicitante"));
                result.setDirPredio(obj.getString("dirPredio"));
                result.setNomContri(obj.getString("nomContri"));
                result.setAreaM2(obj.getString("areaM2"));
                result.setGiro(obj.getString("giro"));
                result.setLicEstado(obj.getString("licEstado"));
                result.setTipo(obj.getString("licTipo"));
                result.setFechaVencimiento(obj.getString("licFechaVence"));
                result.setFechaEmision(obj.getString("licFechaEmision"));
                result.setRuc(obj.getString("rucSolicitante"));

                stoped = true;
                Intent intentView = new Intent(getApplicationContext(), ViewActivity.class);
                intentView.putExtra("obj", result);
                startActivity(intentView);
            }
            else {
                mostrarDialogo("No se encontro Licencia", false);
                hiddenProgress();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            mostrarDialogo("Ocurrio un erro, por favor intente nuevamente.", false);
            hiddenProgress();
        }
    }

    private void setupCameraSource() {
        //DisplayMetrics metrics = new DisplayMetrics();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setRequestedPreviewSize(1024, 1024)
                //.setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
