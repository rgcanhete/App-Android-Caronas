package br.edu.ufabc.android.caronas;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.util.Log;

public class Conector {
	public String sendHTTP(String[] params){
		String url = "http://54.207.15.90:8080//CaroneiroServidor/teste";
		
		// cria um vetor de strings que sera interpretado no request
		String param1[] = new String[params.length + 1];
		
		// junta todos os parametros recebidos do app com a url de requests do servidor
		param1[0] = url;
		for(int i=1; i<=params.length; i++)
			param1[i] = params[i-1];
		
		// recupera os valores de resposta do servidor
		Solicitacao sol = new Solicitacao();
		String retorno = "";
		
		try {
			retorno = sol.execute(param1).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		Log.d("SENDHTTP", "Retorno do execute" + retorno);
		
		return retorno;
	}

private class Solicitacao extends AsyncTask<String, Void, String>{
		
		protected void onPreExecute(){
			
		}

		protected void onPostExecute(String param){

		}

		protected String doInBackground(String... param1){
			String conteudo = "";
			String linha;
			
			try{
				String urlParameters = "";
				URL url = new URL(param1[0]);
				
				Log.d("HTTPCLIENT", "" + param1.length);
				Log.d("HTTPCLIENT", param1[0]);
				
				//concatena todos os parametros em uma string so
				if(param1[1] != null)
					urlParameters += param1[1];
				for(int i=2; i<param1.length; i++) {
					urlParameters += "&" + param1[i];
				}
				Log.d("HTTPCLIENT", urlParameters);
				
				byte[] postData 	  = urlParameters.getBytes( StandardCharsets.UTF_8 );
				int    postDataLength = postData.length;
				HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
				connection.setDoOutput( true );
				connection.setInstanceFollowRedirects( false );
				connection.setRequestMethod( "POST" );
				connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
				connection.setRequestProperty( "charset", "utf-8");
				connection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
				connection.setUseCaches( false );
				try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
				   wr.write( postData );
				}
				
				// realizo a conexao
				connection.connect();
				
				int response = connection.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK){
					// crio um leitor a partir da conexao obtida
					InputStreamReader isr = new InputStreamReader(connection.getInputStream());
					// crio um objeto para ler linha a linha meu resultado
					BufferedReader br = new BufferedReader(isr);
					// faço a leitura da pagina
					while ((linha = br.readLine()) != null){
						conteudo += linha;
					}
					Log.d("HTTPCLIENT", conteudo);
					br.close();
					isr.close();
				}
			}
			catch(Exception ex){
				Log.d("HTTPCLIENT","Erro ao conectar - " + ex.getMessage());
			}
			return conteudo;
		}
}
}
