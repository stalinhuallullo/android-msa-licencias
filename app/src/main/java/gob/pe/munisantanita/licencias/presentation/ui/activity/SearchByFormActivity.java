package gob.pe.munisantanita.licencias.presentation.ui.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gob.pe.munisantanita.licencias.BuildConfig;
import gob.pe.munisantanita.licencias.R;
import gob.pe.munisantanita.licencias.presentation.presenter.view_model.ResultViewModel;
import gob.pe.munisantanita.licencias.presentation.utils.Tools;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchByFormActivity extends AppCompatActivity implements OnClickListener {
    public String url = BuildConfig.base_url + BuildConfig._api;

    private EditText etCodigo;
    private EditText etAnio;
    private Button btn_search_license;
    public Context ctx;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_form);
        initToolbar();
        ctx = this;
        etCodigo = findViewById(R.id.etCodigo);
        etAnio = findViewById(R.id.etAnio);
        btn_search_license = findViewById(R.id.btn_search_license);

        btn_search_license.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        etCodigo.setText("");
        etAnio.setText("");
        hiddenProgress();
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
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }
        });
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
        getSupportActionBar().setTitle("Buscar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.green_900);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_license:
                validateData();
                break;
        }
    }

    private void validateData(){

        String codigo = etCodigo.getText().toString();
        String anio = etAnio.getText().toString();
        String msj = "";

        if(codigo.length() <= 0) msj = "Ingresa el codigo de la licencia";
        else if(anio.length() <= 0) msj = "Ingresa el año de la licencia";

        if(msj.length() <= 0) showResults(anio, codigo);
        else mostrarDialogo(msj, false);

    }


    public void parse(String response) {
        try {
            JSONObject Jobject = new JSONObject(response);

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

    private void showResults(String anio, String code){
        String urlStatic = url + anio+ "/" + code;
        showProgress();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlStatic)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mostrarDialogo("onFailure", false);
                hiddenProgress();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException { //
                String data = response.body().string();
                parse(data);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
