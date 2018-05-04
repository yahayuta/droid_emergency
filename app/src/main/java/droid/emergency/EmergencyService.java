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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Emergencyサービス
 * @author yasupong
 */
public class EmergencyService extends Service implements LocationListener {

	/** イベント識別子 */
	private static final String ACTION_BTNCLICK = "droid.emergency.EmergencyService.ACTION_BTNCLICK";

	LocationManager locman = null;
	
	EmergencyEntity emergency = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		RemoteViews view = new RemoteViews(getPackageName(), droid.emergency.R.layout.widgetview);

		if (ACTION_BTNCLICK.equals(intent.getAction())) {
			btnClicked(view, intent);
		}
		
		Intent newintent = new Intent();
		newintent.setAction(ACTION_BTNCLICK);
		PendingIntent pending = PendingIntent.getService(this, 0, newintent, 0);
		view.setOnClickPendingIntent(droid.emergency.R.id.btnImageEmergency, pending);
		
		ComponentName widget = new ComponentName(this, EmergencyAppWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(widget, view);
		
	    return START_STICKY_COMPATIBILITY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * ボタンクリック時に呼ばれる
	 * @param view
	 * @param intent
	 */
	private void btnClicked(RemoteViews view, Intent intent) {
		
		Notification notification = new Notification();
		notification.vibrate = new long[]{0, 100, 100, 100};
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(R.string.app_name, notification);
		
		// ネットワーク接続チェック
		if (!EmergencyUtil.networkIsConnected(this.getApplicationContext())) {
			Toast.makeText(this, getString(R.string.cmn_msg_no_network), Toast.LENGTH_LONG).show();
			return;
		}
		
		// データ取得
		if (!getDataFromDB(intent)){
			Toast.makeText(this, getString(R.string.widget_info_needed), Toast.LENGTH_LONG).show();
			return;
		}
		
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
			sendMail(false, 0, 0);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		double lon = location.getLongitude();
		double lat = location.getLatitude();
		
		sendMail(true,lat,lon);
		
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
	 * メール送信
	 * @param isGPSUse
	 * @param lat
	 * @param lon
	 */
	private void sendMail(boolean isGPSUse, double lat, double lon ) {
		
		Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		 
		Session sess = Session.getInstance(props);
		MimeMessage mimeMsg = new MimeMessage(sess);
		
		try {
			// データ展開
			String mUser = emergency.getGmail();
			String mPassword = emergency.getGmailpass();
					
			mimeMsg.setFrom(new InternetAddress(mUser));
			
			String mail1 = emergency.getMail1();
			if (mail1 != null && mail1.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail1));
			}
			
			String mail2 = emergency.getMail2();
			if (mail2 != null && mail2.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail2));
			}
			
			String mail3 = emergency.getMail3();
			if (mail3 != null && mail3.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail3));
			}
			
			String mail4 = emergency.getMail4();
			if (mail4 != null && mail4.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail4));
			}
			
			String mail5 = emergency.getMail5();
			if (mail5 != null && mail5.length() > 0) {
				mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail5));
			}
			
			mimeMsg.setContent("body", "text/plain; utf-8");
			mimeMsg.setHeader("Content-Transfer-Encoding", "7bit");
			mimeMsg.setSubject(emergency.getName() + " " + getString(R.string.mail_subject));
	 
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
			textMessage = textMessage + emergency.getCusmessage() + "\n";
			
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
			htmlMessage = htmlMessage + "<div>" + emergency.getCusmessage() + "</div>";
			
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
			Toast.makeText(this,getString(R.string.widget_mail_error) + e.getMessage(),Toast.LENGTH_LONG).show();
			return;
		}
		
		// 正常送信
		Toast.makeText(this, getString(R.string.widget_mail_sent),Toast.LENGTH_LONG).show();
	}
	
	/**
	 * データ取得
	 * @param intent
	 * @return
	 */
	private boolean getDataFromDB(Intent intent) {
		intent.setData(EmergencyContentProvider.CONTENT_URI);
		// 再検索実行
        Cursor c = getContentResolver().query(intent.getData(), null, null, null, null);

        if (c.getCount() > 0) {
        	c.moveToNext();
        	
        	emergency = new EmergencyEntity();
        	
        	emergency.setGmail(c.getString(1));
        	emergency.setGmailpass(c.getString(2));
        	emergency.setName(c.getString(3));
        	emergency.setMail1(c.getString(4));
        	emergency.setMail2(c.getString(5));
        	emergency.setMail3(c.getString(6));
        	emergency.setMail4(c.getString(7));
        	emergency.setMail5(c.getString(8));
        	emergency.setCusmessage(c.getString(9));
        }
        else {
        	return false;
        }

        c.close();
        return true;
	}
}
