package com.Mohammad.ac.test3g;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;

public final class myDualSim {

    private static myDualSim dualSimInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    public String phoneNumber1;
    public String phoneNumber2;
    public String imsi1;
    public String imsi2;
    public int netType1;
    public int netType2;
    public String netOperator1;
    public String netOperator2;
    public String netName1;
    public String netName2;

    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImeiSIM1() {
        return imeiSIM1;
    }

/*public static void setImeiSIM1(String imeiSIM1) {
    myDualSim.imeiSIM1 = imeiSIM1;
}*/

    public String getImeiSIM2() {
        return imeiSIM2;
    }

/*public static void setImeiSIM2(String imeiSIM2) {
    myDualSim.imeiSIM2 = imeiSIM2;
}*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

/*public static void setSIM1Ready(boolean isSIM1Ready) {
    myDualSim.isSIM1Ready = isSIM1Ready;
}*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

/*public static void setSIM2Ready(boolean isSIM2Ready) {
    myDualSim.isSIM2Ready = isSIM2Ready;
}*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private myDualSim() {
    }

    public static myDualSim getInstance(Context context) {
        if (dualSimInfo == null) {
            dualSimInfo = new myDualSim();
        }
        {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            dualSimInfo.imeiSIM1 = telephonyManager.getDeviceId();
            dualSimInfo.imeiSIM2 = null;
            try {
                dualSimInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                dualSimInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    dualSimInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.phoneNumber1 = telephonyManager.getLine1Number();
            dualSimInfo.phoneNumber2 = null;
            try {
                dualSimInfo.phoneNumber1 = getDeviceIdBySlot(context, "getLine1NumberGemini", 0);
                dualSimInfo.phoneNumber2 = getDeviceIdBySlot(context, "getLine1NumberGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.phoneNumber1 = getDeviceIdBySlot(context, "getLine1Number", 0);
                    dualSimInfo.phoneNumber2 = getDeviceIdBySlot(context, "getLine1Number", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.imsi1 = telephonyManager.getSubscriberId();
            dualSimInfo.imsi2 = null;
            try {
                dualSimInfo.imsi1 = getDeviceIdBySlot(context, "getSubscriberIdGemini", 0);
                dualSimInfo.imsi2 = getDeviceIdBySlot(context, "getSubscriberIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.imsi1 = getDeviceIdBySlot(context, "getSubscriberId", 0);
                    dualSimInfo.imsi2 = getDeviceIdBySlot(context, "getSubscriberId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.netType1 = telephonyManager.getNetworkType();
            dualSimInfo.netType2 = TelephonyManager.NETWORK_TYPE_UNKNOWN;
            try {
                dualSimInfo.netType1 = getValueBySlot(context, "getNetworkTypeGemini", 0);
                dualSimInfo.netType2 = getValueBySlot(context, "getNetworkTypeGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.netType1 = getValueBySlot(context, "getNetworkType", 0);
                    dualSimInfo.netType2 = getValueBySlot(context, "getNetworkType", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.netOperator1 = telephonyManager.getSimOperator();
            dualSimInfo.netOperator2 = null;
            try {
                dualSimInfo.netOperator1 = getDeviceIdBySlot(context, "getSimOperatorGemini", 0);
                dualSimInfo.netOperator2 = getDeviceIdBySlot(context, "getSimOperatorGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.netOperator1 = getDeviceIdBySlot(context, "getSimOperator", 0);
                    dualSimInfo.netOperator2 = getDeviceIdBySlot(context, "getSimOperator", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.netName1 = telephonyManager.getNetworkOperatorName();
            dualSimInfo.netName2 = null;
            try {
                dualSimInfo.netName1 = getDeviceIdBySlot(context, "getNetworkOperatorNameGemini", 0);
                dualSimInfo.netName2 = getDeviceIdBySlot(context, "getNetworkOperatorNameGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    dualSimInfo.netName1 = getDeviceIdBySlot(context, "getNetworkOperatorName", 0);
                    dualSimInfo.netName2 = getDeviceIdBySlot(context, "getNetworkOperatorName", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            dualSimInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            dualSimInfo.isSIM2Ready = false;

            try {
                dualSimInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                dualSimInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    dualSimInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    dualSimInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
        }

        return dualSimInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            /*{
                Class tClass = telephony.getClass();
                Method[] methods = tClass.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    System.out.println("public method: " + methods[i]);
                }
            }*/
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static int getValueBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        int imei = -1;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            /*{
                Class tClass = telephony.getClass();
                Method[] methods = tClass.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    System.out.println("public method: " + methods[i]);
                }
            }*/
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imei = Integer.parseInt(ob_phone.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}