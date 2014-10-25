package es.gdfcordoba.jorider;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author jorge
 *
 */
public class MainActivity extends Activity {
	
	private final String SENDER_ID = "504217290391";
	
	private Context context = null;
	private SharedPreferences preferences = null;
	private SharedPreferences.Editor prefEdit = null;
	
	private EditText reg_id_text = null;
	private TextView messageReceived = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		
		reg_id_text = (EditText) findViewById(R.id.reg_id);
		messageReceived = (TextView) findViewById(R.id.message_received);

		preferences = getSharedPreferences("gcmpreferences", MODE_PRIVATE);
		prefEdit = preferences.edit();

		if(preferences.getString("reg_id", null) == null) {
			new GCMRegister().execute();
		} else {
			reg_id_text.setText(preferences.getString("reg_id", ""));
		}

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			messageReceived.setText(extras.getString("message"));
		}
	}

	/**
	 * 
	 * @author jorge
	 *
	 */
	private class GCMRegister extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialogo = null;
		private String registrationId = null;

		@Override
		protected Void doInBackground(Void... params) {
			GoogleCloudMessaging gcmRegister = GoogleCloudMessaging.getInstance(context);
			try {
				registrationId = gcmRegister.register(SENDER_ID);
			} catch (IOException e) {
				Log.e("RegistroGCM", "Error!!!->" + (e!= null && e.getMessage()!=null?e.getMessage():"Exception"));
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			dialogo = ProgressDialog.show(context, "Registrando en GCM", "Espere por favor...");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			dialogo.dismiss();
			if(registrationId != null) {
				prefEdit.putString("reg_id", registrationId);
				prefEdit.commit();
				reg_id_text.setText(registrationId);
				Toast.makeText(context, "Registro finalizado!!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "ERRROR EN EL REGISTRO!!", Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}
}
