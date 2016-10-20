package br.edu.ufabc.android.caronas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class ServiceMotorista extends Service {
	ResultReceiver resultReceiver;
	Intent intent;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		resultReceiver = intent.getParcelableExtra("receiver");
		this.intent = intent;

		synchronized (this) {
			try {
				// handler que inicia com delay para permitir UI continuar funcionando
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						updatePUSH();
					}
				}, 20 * 1000);

			} catch (Exception e) {
				Log.d("SERVICEMOTORISTA",e.getMessage());
			}

			return START_STICKY;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public void updatePUSH() {
		resultReceiver = intent.getParcelableExtra("receiver");
		NotificationManager nm = null;
		int notificationID = 1;
		String passageiros = requisicaoPassageiros();

		if (!passageiros.equals("0")) {

			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			Notification.Builder nb = new Notification.Builder(this);
			nb.setContentTitle("Há passageiros no terminal");
			nb.setContentText("Há " + passageiros
					+ " passageiros esperando por socorro no terminal!");
			nb.setSmallIcon(R.drawable.ic_launcher);
			nb.setTicker("Há passageiros!");
			nb.setContentIntent(pendingIntent);
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationID, nb.build());
			notificationID++;

			Bundle bundle = new Bundle();
			bundle.putString("passageiros", passageiros);
			resultReceiver.send(100, bundle);
		}
	}

	public String requisicaoPassageiros() {
		// enviar requisicao por quantidade de passageiro para o servidor
		Conector con = new Conector();
		String params[] = new String[1];
		String passageiros;

		params[0] = "op=13";

		passageiros = con.sendHTTP(params);

		return passageiros;
	}

}
