package gob.pe.munisantanita.licencias.presentation.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import gob.pe.munisantanita.licencias.R;
import gob.pe.munisantanita.licencias.presentation.presenter.view_model.ResultViewModel;
import gob.pe.munisantanita.licencias.presentation.utils.Tools;

public class ViewActivity extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvDireccion;
    private TextView tvNombreContri;
    private TextView tvLicNum;
    private TextView tvLicAnio;
    private TextView tvRuc;
    private TextView tvGiro;
    private TextView tvAreaM2;
    private TextView tvTipo;
    private TextView tvEstado;

    private TextView tvFechaVencimiento;
    private TextView tvFechaEmision;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        initToolbar();

        tvNombre = findViewById(R.id.tvNombre);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvNombreContri = findViewById(R.id.tvNombreContri);
        tvLicNum = findViewById(R.id.tvLicNum);
        tvLicAnio = findViewById(R.id.tvLicAnio);
        tvRuc = findViewById(R.id.tvRuc);
        tvGiro = findViewById(R.id.tvGiro);
        tvAreaM2 = findViewById(R.id.tvAreaM2);
        tvTipo = findViewById(R.id.tvTipo);
        tvEstado = findViewById(R.id.tvEstado);
        tvFechaVencimiento = findViewById(R.id.tvFechaVencimiento);
        tvFechaEmision = findViewById(R.id.tvFechaEmision);


        getData();
    }



    private void getData(){
        Bundle parametros = this.getIntent().getExtras();
        if(parametros != null){
            ResultViewModel resultViewModel = (ResultViewModel)parametros.getSerializable("obj");

            tvNombre.setText(resultViewModel.getNomSolicitante());
            tvDireccion.setText(resultViewModel.getDirPredio());
            tvNombreContri.setText(resultViewModel.getNomContri());
            tvLicNum.setText(resultViewModel.getLicNum());
            tvLicAnio.setText(resultViewModel.getLicAnio());
            tvRuc.setText(resultViewModel.getRuc());
            tvGiro.setText(resultViewModel.getGiro());
            tvAreaM2.setText(resultViewModel.getAreaM2());
            tvTipo.setText(resultViewModel.getTipo());
            tvEstado.setText(resultViewModel.getLicEstado());
            tvFechaVencimiento.setText(resultViewModel.getFechaVencimiento());
            tvFechaEmision.setText(resultViewModel.getFechaEmision());

        }
        else{
            Toast.makeText(this, "No se encontro resultados", Toast.LENGTH_SHORT).show();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Resultado de busqueda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.green_900);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
