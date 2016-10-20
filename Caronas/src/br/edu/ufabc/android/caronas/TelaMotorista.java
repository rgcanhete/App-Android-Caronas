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

public class TelaMotorista extends Activity {
	// armazena o usuario atual logado no app
	String usuario;
	int notificationID = 1;
	NotificationManager nm = null;
	MyResultReceiver resultReceiver;

	@Override
	public void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
		setContentView(R.layout.tela_motorista_inicial);
		Intent intent = getIntent();
		Bundle params = intent.getExtras();
		if (params != null)
			usuario = params.getString("usuario");

	}

	public void onClickParticipar(View view) {
		enviaMotorista();
		Intent intent = new Intent(this, TelaMotorista.class);
		setContentView(R.layout.tela_motorista);

		resultReceiver = new MyResultReceiver(null);
		Intent intent2 = new Intent(this, ServiceMotorista.class);
		intent2.putExtra("receiver", resultReceiver);
		startService(intent2);

		populaComPassageiros();
	}

	public void atualiza() {
		resultReceiver = new MyResultReceiver(null);
		Intent intent2 = new Intent(this, ServiceMotorista.class);
		intent2.putExtra("receiver", resultReceiver);
		startService(intent2);
	}

	public void enviaMotorista() {
		// envia para servidor que novo motorista esta disponivel
		Conector con = new Conector();
		String params[] = new String[2];

		params[0] = "op=4";
		params[1] = "email=" + usuario;

		con.sendHTTP(params);
	}

	public void onClickChegou(View view) {
		removeMotorista();
		Intent intent = new Intent(this, TelaMotorista.class);
		setContentView(R.layout.tela_motorista_inicial);
	}

	public void removeMotorista() {
		// envia para o servidor que esse motorista nao esta mais disponivel
		Conector con = new Conector();
		String params[] = new String[2];

		params[0] = "op=8";
		params[1] = "email=" + usuario;

		con.sendHTTP(params);
	}

	public void populaComPassageiros() {
		ListView lista;
		TextView quantidade;
		String auxPassageiros[];
		ArrayList<String> passageiros = new ArrayList<String>();

		lista = (ListView) findViewById(R.id.motorista_lista_passageiros);
		quantidade = (TextView) findViewById(R.id.motorista_txt_quantidade);
		auxPassageiros = requisicaoPassageiros();

		// atualiza a quantidade de passageiros que estao esperando na tela
		quantidade.setText(" " + auxPassageiros[0]);

		for (int i = 1; i < auxPassageiros.length; i++) {
			String aux[] = auxPassageiros[i].split(":");
			passageiros.add("Aluno: " + aux[1] + "\nRA: " + aux[0]);
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, passageiros);

		lista.setAdapter(arrayAdapter);

	}

	public String[] requisicaoPassageiros() {
		// enviar requisicao por lista de passageiro para o servidor
		Conector con = new Conector();
		String params[] = new String[1];
		String resultado;
		String passageiros[];

		params[0] = "op=7";

		resultado = con.sendHTTP(params);

		passageiros = resultado.split(";");

		return passageiros;
	}

	class MyResultReceiver extends ResultReceiver {
		public MyResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			if (resultCode == 100) {
				String resultado = resultData.getString("passageiros");
				populaComPassageiros();
				Log.d("MOTO2", resultado);
				atualiza();
			}
		}
	}

}
