package utils;

import android.os.Build;

/**
 * Created by Super on 2020/4/16.
 */
public class CPUUtils {
//返回CPU架构
    public static String getCPUAbi() {
        return Build.CPU_ABI;
    }

}
