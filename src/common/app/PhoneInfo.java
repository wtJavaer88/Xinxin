package common.app;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfo
{
    private TelephonyManager telephonemanager;
    private String IMSI;
    private Context ctx;

    /**
     * 获取手机国际识别码IMEI
     * */
    public PhoneInfo(Context context)
    {
        ctx = context;
        telephonemanager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取手机信息
     * */
    public Map<String, Object> getPhoneInfo()
    {

        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("DeviceID(IMEI)", tm.getDeviceId());
        map.put("DeviceSoftwareVersion:", tm.getDeviceSoftwareVersion());
        map.put("getLine1Number:", tm.getLine1Number());
        map.put("NetworkCountryIso:", tm.getNetworkCountryIso());
        map.put("NetworkOperator:", tm.getNetworkOperator());
        map.put("NetworkOperatorName:", tm.getNetworkOperatorName());
        map.put("NetworkType:", tm.getNetworkType());
        map.put("PhoneType:", tm.getPhoneType());
        map.put("SimCountryIso:", tm.getSimCountryIso());
        map.put("SimOperator:", tm.getSimOperator());
        map.put("SimOperatorName:", tm.getSimOperatorName());
        map.put("SimSerialNumber:", tm.getSimSerialNumber());
        map.put("getSimState:", tm.getSimState());
        map.put("SubscriberId:", tm.getSubscriberId());
        map.put("VoiceMailNumber:", tm.getVoiceMailNumber());

        return map;

    }
}