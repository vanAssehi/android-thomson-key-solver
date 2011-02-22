package org.exobel.routerkeygen;

import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;


public class WifiNetwork implements Comparable<WifiNetwork>, Serializable{
	
	private static final long serialVersionUID = 1L;
	String ssid;
	String mac;
	String ssidSubpart;
	String encryption;
	boolean supported;
	boolean newThomson;
	int level;
	List <AliceMagicInfo> supportedAlice;
	TYPE type;
	static enum TYPE {
		THOMSON , DLINK , DISCUS , VERIZON ,
		EIRCOM , PIRELLI , TELSEY , ALICE ,
		WLAN4 , HUAWEI, WLAN2 , ONO_WEP};
	public WifiNetwork(String ssid, String mac, int level , String enc , Context con ){
		this.ssid = ssid;
		this.mac = mac.toUpperCase();
		this.level  = level;
		this.encryption = enc;
		if ( this.encryption.equals(""))
			this.encryption = "Open";
		this.newThomson = false;
		this.supported =  essidFilter(con);
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getEssid(){
		return ssidSubpart;
	}
	
	public String getMacEnd(){
		if ( mac.replace(":", "").length() < 12 )
			return mac.replace(":", "");
		return mac.replace(":", "").substring(6);
	}
	
	public String getMac(){
		return  mac.replace(":", "");
	}
	
	private boolean essidFilter(Context con) {
		if ( ( ssid.startsWith("Thomson") && ssid.length() == 13 )    ||
		     ( ssid.startsWith("SpeedTouch") && ssid.length() == 16 ) ||
		     ( ssid.startsWith("O2Wireless") && ssid.length() == 16 ) ||
		     ( ssid.startsWith("Orange-") && ssid.length() == 13 ) || 
		     ( ssid.startsWith("INFINITUM") && ssid.length() == 15 )  ||
		     ( ssid.startsWith("BigPond") && ssid.length() == 13 )  ||
		     ( ssid.startsWith("Otenet") && ssid.length() == 12 ) ||
		     ( ssid.startsWith("Bbox-") && ssid.length() == 11 ) ||
		     ( ssid.startsWith("DMAX") && ssid.length() == 10 )  || 
		     ( ssid.startsWith("privat") && ssid.length() == 12 ) )
		{
			ssidSubpart = ssid.substring(ssid.length()-6);
			if ( !mac.equals("") )
				if ( ssidSubpart.equals(getMacEnd()) )
					newThomson = true;
			type = TYPE.THOMSON;
			return true;
		}
		if (  ssid.startsWith("DLink-") && ssid.length() == 12 )
		{
			ssidSubpart = new String ( ssid.substring(ssid.length()-6));
			type = TYPE.DLINK;
			return true;
		}
		if ( ( ssid.startsWith("Discus-") && ssid.length() == 13 ) ||
			( ssid.startsWith("Discus--") && ssid.length() == 14 )	)
		{
			ssidSubpart = ssid.substring(ssid.length()-6);
			type = TYPE.DISCUS;
			return true;
		}
		if ( ( ssid.startsWith("Eircom") && ssid.length() >= 14 ) ||
		     ( ssid.startsWith("eircom") && ssid.length() >= 14 )	)
		{
			ssidSubpart = ssid.substring(ssid.length()-8);
			if ( mac.equals("") )
				calcEircomMAC();
			type = TYPE.EIRCOM;
			return true;
		}
		if ( ssid.length() == 5  && ( mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44") ||
				mac.startsWith("00:18:01") || mac.startsWith("00:20:E0") ||
				mac.startsWith("00:0F:B3") || mac.startsWith("00:1E:A7") ||
				mac.startsWith("00:15:05") || mac.startsWith("00:24:7B") ||
				mac.startsWith("00:26:62") || mac.startsWith("00:26:B8") ) )
		{
			ssidSubpart = ssid;
			type = TYPE.VERIZON;
			return true;
		}
		if ( ( ssid.startsWith("FASTWEB-1-000827") && ssid.length() == 22 ) ||
		     ( ssid.startsWith("FASTWEB-1-0013C8") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-0017C2") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-00193E") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-001CA2") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-001D8B") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-002233") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-00238E") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-002553") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-00A02F") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-080018") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-3039F2") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-38229D") && ssid.length() == 22 )	||
		     ( ssid.startsWith("FASTWEB-1-6487D7") && ssid.length() == 22 ))
			{
				ssidSubpart = ssid.substring(ssid.length()-12);
				if ( mac.equals("") )
					calcFastwebMAC();
				type = TYPE.PIRELLI;
				return true;
			}
		if ( ssid.matches("FASTWEB-[1-2]-002196[0-9A-F]{6}|FASTWEB-[1-2]-00036F[0-9A-F]{6}") )
		{
			ssidSubpart = new String ( ssid.substring(ssid.length()-12));
			if ( mac.equals("") )
				calcFastwebMAC();
			type = TYPE.TELSEY;
			return true;
		}
		if ( ssid.matches("Alice-[0-9]{8}") )
		{
			AliceHandle aliceReader = new AliceHandle(ssid.substring(0,9));
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser;
		    try {
		    	saxParser = factory.newSAXParser();
				saxParser.parse(con.getResources().openRawResource(R.raw.alice), aliceReader);
			} 
		    catch (Exception e) {}
			ssidSubpart = ssid.substring(ssid.length()-8);
			type = TYPE.ALICE;
			if( aliceReader.supportedAlice.isEmpty() )
				return false;
			supportedAlice = aliceReader.supportedAlice;
			return true;
		}
		if (  ( ssid.startsWith("WLAN_") && ssid.length() == 9 ) ||
			  ( ssid.startsWith("JAZZTEL_") && ssid.length() == 12 ))
		{
			ssidSubpart = ssid.substring(ssid.length()-4);
			type = TYPE.WLAN4;
			return true;
		}
		if ( ( ssid.startsWith("INFINITUM") && ssid.length() == 13 ) ||
				mac.startsWith("00:25:9E") || mac.startsWith("00:25:68") ||
				mac.startsWith("00:22:A1") || mac.startsWith("00:1E:10") ||
				mac.startsWith("00:18:82") || mac.startsWith("00:0F:F2") ||
				mac.startsWith("00:E0:FC") || mac.startsWith("28:6E:D4") ||
				mac.startsWith("54:A5:1B") || mac.startsWith("F4:C7:14") ||
				mac.startsWith("28:5F:DB") || mac.startsWith("30:87:30") ||
				mac.startsWith("4C:54:99") || mac.startsWith("40:4D:8E") ||
				mac.startsWith("64:16:F0") || mac.startsWith("78:1D:BA") ||
				mac.startsWith("84:A8:E4") || mac.startsWith("04:C0:6F") ||
				mac.startsWith("5C:4C:A9") || mac.startsWith("1C:1D:67") ||
				mac.startsWith("CC:96:A0") || mac.startsWith("20:2B:C1"))
		{
			if ( ssid.startsWith("INFINITUM")  )
				ssidSubpart = ssid.substring(ssid.length()-4);
			else
				ssidSubpart = "";
			type = TYPE.HUAWEI;
			return true;
		}
		if ( ssid.startsWith("WLAN_") && ssid.length() == 7 &&
			( mac.startsWith("00:01:38") || mac.startsWith("00:16:38") || 
			  mac.startsWith("00:01:13") || mac.startsWith("00:01:1B") || 
			  mac.startsWith("00:19:5B") ) )
		{
			ssidSubpart = ssid.substring(ssid.length()-2);
			type = TYPE.WLAN2;
			return true;
		}
		/*ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X*/
		if ( ssid.matches("[Pp]1[0-9]{6}0{4}[0-9]") )
		{
			ssidSubpart = "";
			type = TYPE.ONO_WEP;
			return true;
		}
		return false;
	}
	
	public void calcFastwebMAC(){
		this.mac = ssidSubpart.substring(0,2) + ":" + ssidSubpart.substring(2,4) + ":" + 
				   ssidSubpart.substring(4,6) + ":" + ssidSubpart.substring(6,8) + ":" +
				   ssidSubpart.substring(8,10) + ":" + ssidSubpart.substring(10,12);
	}
	
	public void calcEircomMAC(){
		String end = Integer.toHexString( Integer.parseInt(ssidSubpart, 8) ^ 0x000fcc );
		this.mac = "00:0F:CC" +  ":" + end.substring(0,2)+ ":" +
					end.substring(2,4)+ ":" + end.substring(4,6);
	}

	public int compareTo(WifiNetwork another) {
		if ( another.level == this.level && this.ssid.equals(another.ssid) && this.mac.equals(another.mac) )
			return 0;
		if ( this.supported && !this.newThomson )
			return -1;
		return 1;
	}
	
}