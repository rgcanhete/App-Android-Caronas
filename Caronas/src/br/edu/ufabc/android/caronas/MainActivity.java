package br.edu.ufabc.android.caronas;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String SH_NAME = "CARONASUFABC";
	int notificationID = 1;
	NotificationManager nm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String emailLogado;
		String senhaLogado;
		int resultadoLogin;
		Intent intent;
		Bundle params;
		
		// devera verificar se as credenciais do usuario ja existem no proprio telefone
		
		// acessa as shredpref do aplicativo
		SharedPreferences shared = getSharedPreferences(SH_NAME, 0);
		// recupera informacoes do usuario logado no sistema
		emailLogado = shared.getString("logadoEmail", "-1");
		senhaLogado = shared.getString("logadoSenha", "-1");
		
		// compara o conteudo com o servidor
		if(emailLogado != "-1" && senhaLogado != "-1") {
			resultadoLogin = testaLogin(emailLogado, senhaLogado);
			
			switch(resultadoLogin) {
			case 0:
				Log.d("Login", "Login falhou!");
				Toast toast = Toast.makeText(getApplicationContext(), "Login falhou!", Toast.LENGTH_LONG);
				toast.show();
				break;
			case 1:
				Log.d("Login", "Login como passageiro!");
				intent = new Intent(this, TelaPassageiro.class);
				params = new Bundle();
				
				// envia dado de login para manter conexao coerente com servidor na proxima activity
				params.putString("usuario", emailLogado);
				intent.putExtras(params);
				
				startActivity(intent);
				break;
			case 2:
				Log.d("Login", "Login como motorista!");
				intent = new Intent(this, TelaMotorista.class);
				params = new Bundle();
				
				// envia dado de login para manter conexao coerente com servidor na proxima activity
				params.putString("usuario", emailLogado);
				intent.putExtras(params);
				
				startActivity(intent);
				break;
			default:
				Log.d("Login", "Login mal interpretado!");
				toast = Toast.makeText(getApplicationContext(), "Login falhou!", Toast.LENGTH_LONG);
				toast.show();
				break;
			}
		}
	}

	// metodo para evitar que a pessoa volte ao login ou outra activity ao pressionar back
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		finish();
	}
	
	public void onClickLogin(View view){
		String email;
		String senha;
		int resultadoLogin;
		Intent intent;
		Bundle params;
		
		// pegar informacoes e enviar como requisicao de login para o servidor
		EditText fieldEmail = (EditText)findViewById(R.id.login_field_email);
		EditText fieldSenha = (EditText)findViewById(R.id.login_field_senha);
		
		email = fieldEmail.getText().toString();
		senha = fieldSenha.getText().toString();
		
		// servidor devera comparar informacoes e verificar se batem
		resultadoLogin = testaLogin(email, senha);
		
		switch(resultadoLogin) {
		case 0:
			Intent intent0 = new Intent(this, MainActivity.class);
			Log.d("Login", "Login falhou!");
			Toast toast = Toast.makeText(getApplicationContext(), "Login falhou!", Toast.LENGTH_LONG);
			toast.show();
			break;
		case 1:
			Intent intent1 = new Intent(this, MainActivity.class);
			Log.d("Login", "Login como passageiro!");
			salvaShared(email, senha);
			intent = new Intent(this, TelaPassageiro.class);
			params = new Bundle();
			
			// envia dado de login para manter conexao coerente com servidor na proxima activity
			params.putString("usuario", email);
			intent.putExtras(params);

			startActivity(intent);
			break;
		case 2:
			Intent intent2 = new Intent(this, MainActivity.class);
			Log.d("Login", "Login como motorista!");
			salvaShared(email, senha);
			intent = new Intent(this, TelaMotorista.class);
			params = new Bundle();
			
			// envia dado de login para manter conexao coerente com servidor na proxima activity
			params.putString("usuario", email);
			intent.putExtras(params);
			
			startActivity(intent);
			break;
		default:
			Intent intent3 = new Intent(this, MainActivity.class);
			Log.d("Login", "Login mal interpretado!");
			toast = Toast.makeText(getApplicationContext(), "Login falhou!", Toast.LENGTH_LONG);
			toast.show();
			break;
		}
	}
	
	public void salvaShared(String email, String senha) {
		SharedPreferences.Editor editor;
		// acessa as shredpref do aplicativo
		SharedPreferences shared = getSharedPreferences(SH_NAME, 0);
		// armazena os valores do usuario
		editor = shared.edit();
		
		editor.putString("logadoEmail", email);
		editor.putString("logadoSenha", senha);
		editor.commit();
	}
	
	public void removeShared() {
		SharedPreferences.Editor editor;
		SharedPreferences shared = getSharedPreferences(SH_NAME, 0);
		
		editor = shared.edit();
		editor.remove("logadoEmail");
		editor.remove("logadoSenha");
		editor.commit();
	}
	
	public void onClickCadastrar(View view) {
		removeShared();
		Intent intent = new Intent(this, TelaCadastro.class);
		startActivity(intent);
	}
	
	public int testaLogin(String email, String senha) {
		// Funcao responsavel por testar o login no servidor
		// Retorna o valor correspondente a login com sucesso ou nao
		Conector con = new Conector();
		String params[] = new String[3];
		String respostaRequest;
		
		params[0] = "op=1";
		params[1] = "email=" + email;
		params[2] = "senha=" + senha;
		
		respostaRequest = con.sendHTTP(params);
		
		Log.d("LOGIN", respostaRequest);
		
		return Integer.parseInt(respostaRequest);
	}
}
