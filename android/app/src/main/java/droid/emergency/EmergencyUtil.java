package droid.emergency;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 共通部品
 * @author yasupong
 */
public class EmergencyUtil {

	/**
	 * ネットワーク接続されているかチェックする
	 * @param context
	 * @return
	 */
	public static boolean networkIsConnected(Context context){  
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo ni = cm.getActiveNetworkInfo();  
	    if( ni != null ){  
	        return cm.getActiveNetworkInfo().isConnected();  
	    }  
	    return false;  
	}  
}
