package com.ghostcompany.hackfest.ghostcompany;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostcompany.hackfest.ghostcompany.models.Empresa;

public class EmpActivity extends AppCompatActivity {


    private TextView tvEndereco, tvCnpj, tvEmpresaTitulo;
    private Empresa empresa;
    public EmpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
          empresa = (Empresa) bundle.get("obj");
        }
        setContentView(R.layout.activity_emp);
        tvEmpresaTitulo = (TextView) findViewById(R.id.tvDetailEmpresaTitulo);
        tvEmpresaTitulo.setText(empresa.getTitle());
        //tvEmpresaTitulo.setVisibility(View.INVISIBLE);
        tvCnpj = (TextView) findViewById(R.id.tvDetailEmpresaCnpj);
        tvCnpj.setText("CNPJ: "+empresa.getEmpresaCode());
        //       tvCnpj.setVisibility(View.INVISIBLE);
        tvEndereco = (TextView) findViewById(R.id.tvDetailEmpresaEnderecoj);
        tvEndereco.setText("Endereço: "+empresa.getEndereco());
        if(!Util.isNetworkAvaiable(this)){
            Toast.makeText(getApplicationContext(), getText(R.string.no_network_avaible), Toast.LENGTH_LONG).show();
        }

    }


}
