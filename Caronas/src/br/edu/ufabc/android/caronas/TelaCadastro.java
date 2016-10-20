package br.edu.ufabc.android.caronas;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TelaCadastro extends Activity{
	
	// variavel que ira armazenar dado de RA para passar de View para outra
	String RA = "";
	int notificationID = 1;
	NotificationManager nm = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_usuario);
	}
	
	public void onClickCadastroMotorista(View view) {
		setContentView(R.layout.cadastro_carro);
	}
	
	public void onClickCadastroUsuario(View view) {
		setContentView(R.layout.cadastro_escolha);
	}
	
	public void onClickCadastroPassageiro(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void onClickBackLogin(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void onClickCadastrarUsuario(View view){
		// pegar valores de cadastro
		EditText fieldNome;
		EditText fieldNasc;
		EditText fieldIngresso;
		EditText fieldRA;
		EditText fieldEmail;
		EditText fieldSenha;
		String nome;
		String nasc;
		String ingresso;
		String ra;
		String email;
		String senha;
		int resultadoCadastro;
		
		fieldNome 		= (EditText)findViewById(R.id.cadastro_field_nome);
		fieldNasc 		= (EditText)findViewById(R.id.cadastro_field_nasc);
		fieldIngresso 	= (EditText)findViewById(R.id.cadastro_field_ingresso);
		fieldRA 		= (EditText)findViewById(R.id.cadastro_field_ra);
		fieldEmail 		= (EditText)findViewById(R.id.cadastro_field_email);
		fieldSenha 		= (EditText)findViewById(R.id.cadastro_field_senha);
		
		nome 		= fieldNome.getText().toString();
		nasc		= fieldNasc.getText().toString();
		ingresso 	= fieldIngresso.getText().toString();
		ra 			= fieldRA.getText().toString();
		email 		= fieldEmail.getText().toString();
		senha 		= fieldSenha.getText().toString();
		
		// transfere RA para variavel da activity - sera usado no cadastro de veiculo
		this.RA = ra;
		
		// enviar dados para o servidor cadastrar
		resultadoCadastro = enviaCadastroUsuario(nome, nasc, ingresso, ra, email, senha);
		
		// verificar se o cadastro foi efetuado com sucesso
		if(resultadoCadastro == -1){
			Toast toast = Toast.makeText(getApplicationContext(), "Falha ao cadastrar!", Toast.LENGTH_LONG);
			toast.show();
			Intent intent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification.Builder nb = new Notification.Builder(this);
			nb.setContentTitle("Falha no cadastro!");
			nb.setContentText("Seu cadastro falhou...");
			nb.setSmallIcon(R.drawable.ic_launcher);
			nb.setTicker("Falha ao cadastrar");
			nb.setContentIntent(pendingIntent);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationID, nb.build());
			notificationID++;
		}
		else{
			// redirecionar para escolha entre motorista e passageiro
			Intent intent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification.Builder nb = new Notification.Builder(this);
			nb.setContentTitle("Sucesso!");
			nb.setContentText("Seu cadastro foi realizado com sucesso!");
			nb.setSmallIcon(R.drawable.ic_launcher);
			nb.setTicker("Sucesso no cadastro");
			nb.setContentIntent(pendingIntent);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationID, nb.build());
			notificationID++;
			setContentView(R.layout.cadastro_escolha);
		}
	}
	
	public void onClickCadastrarVeiculo(View view) {
		// pegar valores para cadastro do veiculo
		EditText fieldFabricante;
		EditText fieldModelo;
		EditText fieldCor;
		EditText fieldPlaca;
		String fabricante;
		String modelo;
		String cor;
		String placa;
		int resultadoCadastro;
		
		fieldFabricante = (EditText)findViewById(R.id.cadastro_field_fabricante);
		fieldModelo 	= (EditText)findViewById(R.id.cadastro_field_modelo);
		fieldCor 		= (EditText)findViewById(R.id.cadastro_field_cor);
		fieldPlaca 		= (EditText)findViewById(R.id.cadastro_field_placa);
		
		fabricante 	= fieldFabricante.getText().toString();
		modelo 		= fieldModelo.getText().toString();
		cor 		= fieldCor.getText().toString();
		placa 		= fieldPlaca.getText().toString();
		
		// enviar dados do cadastro para o servidor
		resultadoCadastro = enviaCadastroVeiculo(fabricante, modelo, cor, placa);
		
		// verificar se cadastro foi realizado com sucesso
		if(resultadoCadastro == -1){
			Toast toast = Toast.makeText(getApplicationContext(), "Falha ao cadastrar!", Toast.LENGTH_LONG);
			toast.show();
			Intent intent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification.Builder nb = new Notification.Builder(this);
			nb.setContentTitle("Falha no cadastro!");
			nb.setContentText("Seu cadastro falhou...");
			nb.setSmallIcon(R.drawable.ic_launcher);
			nb.setTicker("Falha ao cadastrar");
			nb.setContentIntent(pendingIntent);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationID, nb.build());
			notificationID++;
		}
		else{
			// voltar para tela de login
			Intent intent = new Intent(this, TelaMotorista.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification.Builder nb = new Notification.Builder(this);
			nb.setContentTitle("Sucesso!");
			nb.setContentText("Seu cadastro foi realizado com sucesso!");
			nb.setSmallIcon(R.drawable.ic_launcher);
			nb.setTicker("Sucesso no cadastro");
			nb.setContentIntent(pendingIntent);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationID, nb.build());
			notificationID++;
			startActivity(intent);
		}
	}
	
	public int enviaCadastroUsuario(String nome, String nasc, String ingresso, String ra, String email, String senha) {
		// Funcao responsavel por enviar cadastro de usuario para o servidor
		// Retorna o valor correspondente a cadastro efetuado com sucesso ou nao
		Conector con = new Conector();
		String params[] = new String[7];
						
		params[0] = "op=2";
		params[1] = "nome=" + nome;
		params[2] = "nasc=" + nasc;
		params[3] = "ingresso=" + ingresso;
		params[4] = "ra=" + ra;
		params[5] = "email=" + email;
		params[6] = "senha=" + senha;
						
		con.sendHTTP(params);
		
		return 0;
	}
	
	public int enviaCadastroVeiculo(String fabricante, String modelo, String cor, String placa) {
		// Funcao responsavel por enviar cadastro de veiculo para o servidor
		// Retorna o valor correspondente a cadastro efetuado com sucesso ou nao
		Conector con = new Conector();
		String params[] = new String[6];
				
		params[0] = "op=3";
		params[1] = "ra=" + this.RA;	// recebe RA para manter controle nas tabelas do banco de dados
		params[2] = "fabricante=" + fabricante;
		params[3] = "modelo=" + modelo;
		params[4] = "cor=" + cor;
		params[5] = "placa=" + placa;
				
		con.sendHTTP(params);
			
		return 0;
	}
	
}
