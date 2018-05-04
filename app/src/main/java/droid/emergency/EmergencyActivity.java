package droid.emergency;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Emergency設定画面
 * @author yasupong
 */
public class EmergencyActivity extends Activity implements OnClickListener, LocationListener {

	LocationManager locman = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // セーブボタンイベント関連付け
        Button btnSave = (Button)findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);
        
        // テストボタンイベント関連付け
        Button btnTest = (Button)findViewById(R.id.buttonTest);
        btnTest.setOnClickListener(this);
        
        // データのロード
        loadData();
        
        //AdView初期化
        AdView adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
        Button btnSave = (Button)findViewById(R.id.buttonSave);
        if (arg0 == btnSave) {
        	execBtnSave();
        }
        
        Button btnTest = (Button)findViewById(R.id.buttonTest);
        if (arg0 == btnTest) {
    		String gs = Secure.getString(getContentResolver(),Secure.LOCATION_PROVIDERS_ALLOWED);
    		// GPS利用判定
    		if (!(gs.indexOf("gps", 0) < 0)) {
    			// GPSデータ取得 
    			locman = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		
    			List<String> providers = locman.getProviders(true);
    			
    			for (String provider : providers) {
    				locman.requestLocationUpdates(provider,5000,1,this);
    			}
    		}
    		// GPS利用無し
    		else {
    			execBtnTest(false, 0, 0);
    		}
        }
	}
	
	@Override
	public void onLocationChanged(Location location) {
		double lon = location.getLongitude();
		double lat = location.getLatitude();
		
		execBtnTest(true,lat,lon);
		
        if (locman != null) {
	      	locman.removeUpdates(this);
        }
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this,"Provider disabled by the user. GPS turned off",Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this,"Provider enabled by the user. GPS turned on",Toast.LENGTH_LONG).show();	
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this,"Provider status changed",Toast.LENGTH_LONG).show();
	}
	
	/**
	 * SAVEボタン処理
	 */
	private void execBtnSave() {
		// DB更新
		EmergencyDBHelper dbHelper = new EmergencyDBHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put("emergency", "emergency");
			
			EditText textGAddr = (EditText)findViewById(R.id.editTextGmailAddr);
			if (textGAddr.getText().toString() == null || textGAddr.getText().toString().length() == 0) {
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setMessage(getString(R.string.conf_err_gaddr_msg));
				dlg.setPositiveButton("OK", null);
				dlg.show();
				return;
			}
			values.put("gmail", textGAddr.getText().toString());
			
			EditText textGpass = (EditText)findViewById(R.id.editTextGmailPass);
			if (textGpass.getText().toString() == null || textGpass.getText().toString().length() == 0) {
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setMessage(getString(R.string.conf_err_gpass_msg));
				dlg.setPositiveButton("OK", null);
				dlg.show();
				return;
			}
			values.put("gmailpass", textGpass.getText().toString());
			
			EditText textName = (EditText)findViewById(R.id.editTextName);
			if (textName.getText().toString() == null || textName.getText().toString().length() == 0) {
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setMessage(getString(R.string.conf_err_name_msg));
				dlg.setPositiveButton("OK", null);
				dlg.show();
				return;
			}
			values.put("name", textName.getText().toString());
			
			EditText textMail1 = (EditText)findViewById(R.id.editTextMail1);
			if (textMail1.getText().toString() == null || textMail1.getText().toString().length() == 0) {
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setMessage(getString(R.string.conf_err_mail1_msg));
				dlg.setPositiveButton("OK", null);
				dlg.show();
				return;
			}
			values.put("mail1", textMail1.getText().toString());
			
			EditText textMail2 = (EditText)findViewById(R.id.editTextMail2);
			values.put("mail2", textMail2.getText().toString());
			
			EditText textMail3 = (EditText)findViewById(R.id.editTextMail3);
			values.put("mail3", textMail3.getText().toString());
			
			EditText textMail4 = (EditText)findViewById(R.id.editTextMail4);
			values.put("mail4", textMail4.getText().toString());
			
			EditText textMail5 = (EditText)findViewById(R.id.editTextMail5);
			values.put("mail5", textMail5.getText().toString());
			
			EditText textCusMsg = (EditText)findViewById(R.id.editTextCusMsg);
			values.put("cusmessage", textCusMsg.getText().toString());
			
			int colNum = db.update(EmergencyDBHelper.DB_TABLE_EMERGENCY, values, null, null);
			if (colNum == 0) db.insert(EmergencyDBHelper.DB_TABLE_EMERGENCY, null, values);
		}
		finally {
			db.close();
		}

		// 正常保存
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setMessage(getString(R.string.conf_act_save_msg));
		dlg.setPositiveButton("OK", null);
		dlg.show();
	}

	/**
	 * テストボタン処理
	 * @param isGPSUse
	 * @param lon
	 * @param lat
	 */
	private void execBtnTest(boolean isGPSUse, double lat, double lon) {
		
		Notification notification = new Notification();
		notification.vibrate = new long[]{0, 100, 100, 100};
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(R.string.app_name, notification);
		
		// ネットワーク接続チェック
		if (!EmergencyUtil.networkIsConnected(this.getApplicationContext())) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setMessage(getString(R.string.cmn_msg_no_network));
			dlg.setPositiveButton("OK", null);
			dlg.show();
			return;
		}
		
		// データ検索
		EmergencyDBHelper dbHelper = new EmergencyDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cEmergency = db.query(EmergencyDBHelper.DB_TABLE_EMERGENCY, new String[]{"emergency","gmail","gmailpass","name","mail1","mail2","mail3","mail4","mail5", "cusmessage"}, null, null, null, null, null);
		if (cEmergency.getCount() > 0) {
			cEmergency.moveToFirst();
		}
		else {
			Toast.makeText(this, getString(R.string.widget_info_needed), Toast.LENGTH_LONG).show();
			cEmergency.close();
	        db.close();
			return;
		}
		
		Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		 
		Session sess = Session.getInstance(props);
		MimeMessage mimeMsg = new MimeMessage(sess);
		
		try {
			// データ展開
			String mUser = cEmergency.getString(1);
			String mPassword = cEmergency.getString(2);
					
			mimeMsg.setFrom(new InternetAddress(mUser));
			
			String mail1 = cEmergency.getString(4);
			if (mail1 != null && mail1.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail1));
			}
			
			String mail2 = cEmergency.getString(5);
			if (mail2 != null && mail2.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail2));
			}
			
			String mail3 = cEmergency.getString(6);
			if (mail3 != null && mail3.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail3));
			}
			
			String mail4 = cEmergency.getString(7);
			if (mail4 != null && mail4.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail4));
			}
			
			String mail5 = cEmergency.getString(8);
			if (mail5 != null && mail5.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail5));
			}
			
			mimeMsg.setContent("body", "text/plain; utf-8");
			mimeMsg.setHeader("Content-Transfer-Encoding", "7bit");
			mimeMsg.setSubject(cEmergency.getString(3) + " " + getString(R.string.mail_subject) + " TEST MAIL");
	 
			// related
			Multipart relatedPart = new MimeMultipart("related");
			
			// alternative
			MimeBodyPart alternativeBodyPart = new MimeBodyPart();
			Multipart alternativePart = new MimeMultipart("alternative");
			alternativeBodyPart.setContent(alternativePart);
			relatedPart.addBodyPart(alternativeBodyPart);
			
			// メッセージ
			String textMessage = "";
			textMessage = textMessage + getString(R.string.mail_text) + "\n";
			textMessage = textMessage + cEmergency.getString(9) + "\n";
			
			// GPSが有効な場合
			if (isGPSUse) {
				// GOOGLE MAP API URL
				String apiURL = "http://maps.google.com/maps/api/staticmap?center=LATITUDE,LONGITUDE&zoom=12&size=400x400&sensor=true&markers=LATITUDE,LONGITUDE";
				// GPSデータ取得
				apiURL = apiURL.replaceAll( EmergencyConst.KEY_LATITUDE, String.valueOf(lat));
				apiURL = apiURL.replaceAll( EmergencyConst.KEY_LONGITUDE, String.valueOf(lon));

				textMessage = textMessage + apiURL;
			}
			else {
				textMessage = textMessage + getString(R.string.mail_no_gps);	
			}
			
			// text mail
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setDataHandler(new DataHandler(textMessage, "text/plain;charset=UTF-8"));
			alternativePart.addBodyPart(textBodyPart);
			
			// HTMLメッセージ
			String htmlMessage = "";
			htmlMessage = htmlMessage + "<div>" + getString(R.string.mail_text) + "</div>";
			htmlMessage = htmlMessage + "<div>" + cEmergency.getString(9) + "</div>";
			
			// GPSが有効な場合
			if (isGPSUse) {
				
				String apiURL = "http://maps.google.com/maps/api/staticmap?center=LATITUDE,LONGITUDE&zoom=12&size=400x400&sensor=true&markers=LATITUDE,LONGITUDE";
				
				apiURL = apiURL.replaceAll( EmergencyConst.KEY_LATITUDE, String.valueOf(lat));
				apiURL = apiURL.replaceAll( EmergencyConst.KEY_LONGITUDE, String.valueOf(lon));

				htmlMessage = htmlMessage + "<div><a href=\"" + apiURL + "\">" + apiURL + "</a></div>";
				htmlMessage = htmlMessage + "<div><img src=\"" + apiURL + "\"/></div>";
			}
			else {
				htmlMessage = htmlMessage + "<div>"+ getString(R.string.mail_no_gps) + "</div>";	
			}
			
			// html mail
			MimeBodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setDataHandler(new DataHandler(htmlMessage, "text/html;charset=UTF-8"));
			alternativePart.addBodyPart(htmlBodyPart);
			
			mimeMsg.setContent(alternativePart);
			
			// 通信開始
			Transport transport = sess.getTransport("smtp");
	      	transport.connect(mUser, mPassword);		
	      	transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
	      	transport.close();
	      	
		} catch (Exception e) {
		    // 送信エラー
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setMessage(getString(R.string.widget_mail_error) + e.getMessage());
			dlg.setPositiveButton("OK", null);
			dlg.show();
			return;
		}
		finally {
			cEmergency.close();
	        db.close();
		}
		
		// 正常送信
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setMessage(getString(R.string.widget_mail_sent));
		dlg.setPositiveButton("OK", null);
		dlg.show();
	}
	
	/**
	 * データをロードし画面に展開
	 */
	private void loadData() {
		EmergencyDBHelper dbHelper = new EmergencyDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cEmergency = db.query(EmergencyDBHelper.DB_TABLE_EMERGENCY, new String[]{ "emergency","gmail","gmailpass","name","mail1","mail2","mail3","mail4","mail5","cusmessage" }, null, null, null, null, null);
		if (cEmergency.getCount() > 0) {
			cEmergency.moveToFirst();
			
			// GMAILアドレス
			EditText textGAddr = (EditText)findViewById(R.id.editTextGmailAddr);
			textGAddr.setText(cEmergency.getString(1));
			
			// GMAILパスワード
			EditText textGpass = (EditText)findViewById(R.id.editTextGmailPass);
			textGpass.setText(cEmergency.getString(2));
			
			// 名前
			EditText textName = (EditText)findViewById(R.id.editTextName);
			textName.setText(cEmergency.getString(3));
			
			// メール1
			EditText textMail1 = (EditText)findViewById(R.id.editTextMail1);
			textMail1.setText(cEmergency.getString(4));
			
			// メール2
			EditText textMail2 = (EditText)findViewById(R.id.editTextMail2);
			textMail2.setText(cEmergency.getString(5));
			
			// メール3
			EditText textMail3 = (EditText)findViewById(R.id.editTextMail3);
			textMail3.setText(cEmergency.getString(6));
			
			// メール4
			EditText textMail4 = (EditText)findViewById(R.id.editTextMail4);
			textMail4.setText(cEmergency.getString(7));
			
			// メール5
			EditText textMail5 = (EditText)findViewById(R.id.editTextMail5);
			textMail5.setText(cEmergency.getString(8));
			
			// カスタムメッセージ
			EditText textCusMsg = (EditText)findViewById(R.id.editTextCusMsg);
			textCusMsg.setText(cEmergency.getString(9));
		}
		
		cEmergency.close();
        db.close();
	}
}
