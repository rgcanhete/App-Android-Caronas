package br.edu.ufabc.android.caronas;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TelaPassageiro extends Activity{
	String usuario;
	
	int notificationID = 1;
	NotificationManager nm = null;
	MyResultReceiver resultReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tela_passageiro_inicial);
		
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		if(params != null)
			usuario = params.getString("usuario");
		
		Log.d("PASSAGEIROS", usuario);
	}
	
	public void onClickParticipar(View view) {
		enviaPassageiro();
		Intent intent = new Intent(this, TelaPassageiro.class);
		setContentView(R.layout.tela_passageiro);
		
		resultReceiver = new MyResultReceiver(null);
		Intent intent2 = new Intent(this, ServicePassageiro.class);
		intent2.putExtra("receiver", resultReceiver);
		startService(intent2);
		
		populaComMotoristas();
	}
	
	public void enviaPassageiro(){
		// envia para servidor que novo motorista esta disponivel
		Conector con = new Conector();
		String params[] = new String[2];
		
		params[0] = "op=5";
		params[1] = "email=" + usuario;
	
		con.sendHTTP(params);
	}
	
	public void populaComMotoristas(){
		ListView lista;
		TextView quantidade;
		String auxMotoristas[];
		ArrayList<String> passageiros = new ArrayList<String>();
		
	    lista = (ListView) findViewById(R.id.passageiro_lista_motoristas);
		quantidade = (TextView)findViewById(R.id.passageiro_txt_quantidade);
		auxMotoristas = requisicaoMotoristas();
		
		// atualiza a quantidade de passageiros que estao esperando na tela
		quantidade.setText(" " + auxMotoristas[0]);
		Log.d("PASSAGEIROS", auxMotoristas[0]);
		
		for(int i=1; i<auxMotoristas.length; i++){
			String aux[] = auxMotoristas[i].split(":");
			passageiros.add("Motorista: " + aux[0] + "\nFabricante: " + aux[1] + "\nCarro: " +  aux[2] + "\nCor: " + aux[3] + "\nPlaca: " + aux[4]);
		}
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, passageiros);
		
		lista.setAdapter(arrayAdapter);
	}
	
	public String[] requisicaoMotoristas(){
		// enviar requisicao por lista de passageiro para o servidor
		Conector con = new Conector();
		String params[] = new String[1];
		String resultado;
		String passageiros[];
		
		params[0] = "op=6";
		
		resultado = con.sendHTTP(params);
		Log.d("PASSAGEIRO", resultado);
		
		passageiros = resultado.split(";");
		
		return passageiros;
	}
	
	public void onClickChegou(View view){
		removePassageiro();
		setContentView(R.layout.tela_passageiro_inicial);
	}
	
	public void removePassageiro() {
		// envia para o servidor que esse passageiro nao esta mais disponivel
		Conector con = new Conector();
		String params[] = new String[2];
		
		params[0] = "op=9";
		params[1] = "email=" + usuario;
		
		con.sendHTTP(params);
	}
	
	public void atualiza() {
		resultReceiver = new MyResultReceiver(null);
		Intent intent2 = new Intent(this, ServicePassageiro.class);
		intent2.putExtra("receiver", resultReceiver);
		startService(intent2);
	}
	
	class MyResultReceiver extends ResultReceiver {
		public MyResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			if (resultCode == 100) {
				String resultado = resultData.getString("motoristas");
				populaComMotoristas();
				Log.d("MOTO2", resultado);
				atualiza();
			}
		}
	}

}
