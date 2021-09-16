package config;

import java.util.Scanner;

public class Config {
    public static final int loginPORT = 9007;
    public static final int chatPORT = 9008;
    public static final int filePORT = 9003;
    public static final String HOST="localhost";

    //9001仅为登录验证端口号(用于初始化与后续的列表更新)
    //9002为接收传输聊天消息的端口
    //9003为接收传输文件的端口

    public final static String filePath="D:\\IM_chat\\Client\\";


}
