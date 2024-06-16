package gob.pe.munisantanita.licencias.presentation.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gob.pe.munisantanita.licencias.BuildConfig;
import gob.pe.munisantanita.licencias.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private String apiSesion = BuildConfig.base_url + "MicroservicioUsuarios/autentificacion";
    private TextInputEditText usuario;
    private TextInputEditText contrasena;
    private AppCompatCheckBox cbxSesion;
    private Button btn_iniciar_sesion;
    private Button btn_clear;
    private Context ctx;
    private ProgressDialog pd;
    private SharedPreferences sharedPref;
    private String STRING_PREFERENCES = "licencias_sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;

        usuario = findViewById(R.id.usuario);
        contrasena = findViewById(R.id.contrasena);
        cbxSesion = findViewById(R.id.cbxSesion);
        btn_iniciar_sesion = findViewById(R.id.btn_iniciar_sesion);
        btn_clear = findViewById(R.id.btn_iniciar_sesion);
        btn_iniciar_sesion.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        sharedPref = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        validarSesionAutomatica();
    }

    private void mostrarVistaMain(){
        Intent intentView = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intentView);
        finish();
    }

    private void validarSesionAutomatica(){
        String id = sharedPref.getString("id", "");
        String sesionActiva = sharedPref.getString("sesionActiva", "");

        if(id != null && sesionActiva.equals("1")) mostrarVistaMain();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_iniciar_sesion:
                validarIniciarSesion();
                break;
        }
    }

    private void showProgress() {
        pd = new ProgressDialog(this);
        pd.setMessage("Cargando...");
        pd.setCancelable(false);
        pd.show();
    }

    public void hiddenProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            pd = null;
        }
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

    private void parse(String response) {

        try {
            JSONObject Jobject = new JSONObject(response);

            String msj = Jobject.get("mensaje").toString();
            String estado = Jobject.get("estado").toString();

            if( estado.equals("ERR") ){
                mostrarDialogo(msj, false);
                hiddenProgress();
            } else {
                JSONObject object = Jobject.getJSONObject("usuario");

                sharedPref
                        .edit()
                        .putString("id", object.get("id").toString())
                        .putString("sesionActiva", (cbxSesion.isChecked()) ? "1" : "0" )
                        .putString("nombre", object.get("nombres").toString())
                        .putString("apellido", object.get("ape_paterno").toString() + " " + object.get("ape_materno").toString())
                        .apply();
                hiddenProgress();
                mostrarVistaMain();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            mostrarDialogo("Ocurrio un error, por favor intente nuevamente.", false);
            hiddenProgress();
        }
    }


    private void validarIniciarSesion(){

        String _usuario = usuario.getText().toString().trim();
        String _contrasena = contrasena.getText().toString().trim();
        String msj = "";

        if(_usuario.length() <= 0) msj = "Usuario incorrecto";
        else if(_contrasena.length() <= 0) msj = "Contraseña incorrecta";

        if(msj.length() <= 0) mostrarResultado(_usuario, _contrasena);
        else mostrarDialogo(msj, false);

    }

    private void mostrarResultado(String usuario, String contrasena) {
        showProgress();
        OkHttpClient client = new OkHttpClient();

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("cuenta", usuario);
            postdata.put("contrasena", contrasena);
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody formBody = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(apiSesion)
                .post(formBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mostrarDialogo("onFailure", false);
                hiddenProgress();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                parse(data);
            }
        });
    }

}
