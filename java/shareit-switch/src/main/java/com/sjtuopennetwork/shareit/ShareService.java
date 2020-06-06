package com.sjtuopennetwork.shareit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import com.huawei.hilink.openapi.nativelauncher.LaunchedProcess;
import com.huawei.hilink.openapi.nativelauncher.NativeLauncher;
import com.huawei.hilink.openapi.usbstorage.USBStorage;
import com.huawei.hilink.rest.AbstractRESTResource;
import com.huawei.hilink.rest.Response;

public class ShareService extends AbstractRESTResource {
    public static String TAG = "==================== ";

    // services
    private USBStorage usbStorage = null;
    private NativeLauncher nativeLauncher = null;

    // Textile process
    LaunchedProcess textile = null;

    // global vars
    String usbPath;
    String homeDir;
    ResponseAddr responseAddr;
    // LinkedList<String> testWhiteList=new
    // LinkedList<>((Collection<String>)Arrays.asList(new String[]{"p111","p222"}));
    LinkedList<String> testWhiteList = new LinkedList<>();
    boolean nodeStart = false;

    public ShareService() {
        super("shareit");
        System.out.println(TAG + "restservice start");
        buildDescriptor("/shareit/getWhiteList", HTTP_GET, null, "getWhiteList");
        buildDescriptor("/shareit/startNode", HTTP_GET, null, "startNode");
        buildDescriptor("/shareit/postCmd", HTTP_POST, PostData.class, "postCmd");
    }

    class ResponseData {
        String responseType;
        String[] peerIdList;

        public ResponseData(String r, String[] p) {
            responseType = r;
            peerIdList = p;
        }
    }

    class ResponseAddr{
        String responseType;
        String addr;
        public ResponseAddr(String s1,String s2){
            responseType=s1;
            addr=s2;
        }
    }

    public Response startNode() {
        // 从文件中读取，第二行
        if(!nodeStart){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(homeDir + "/shadow/address"));
                bufferedReader.readLine();
                String swarmAddr = bufferedReader.readLine();
                responseAddr=new ResponseAddr("swarmAddr", swarmAddr);
                bufferedReader.close();
                nodeStart=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(TAG + responseAddr.addr);
        return Response.ok(responseAddr).build();
    }

    public Response getWhiteList() {
        String responseType = "whiteList";
        String[] w = (String[]) testWhiteList.toArray(new String[0]);
        for (String s : w) {
            System.out.println(TAG + "res: " + s);
        }
        ResponseData responseData = new ResponseData(responseType, w);
        return Response.ok(responseData).build();
    }

    public Response postCmd(PostData postData) {
        switch (postData.cmdType) {
            case "addWhiteList":
                // add a peerid to whiteList
                System.out.println(TAG + "add:" + postData.cmd);
                testWhiteList.add(postData.cmd);
                nativeLauncher.launch("shadow-arm", new String[] { "whitelist", "add", postData.cmd });
                break;
            case "delWhiteList":
                System.out.println(TAG + "del" + postData.cmd);
                for (int i = 0; i < testWhiteList.size(); i++) {
                    if (testWhiteList.get(i).equals(postData.cmd)) {
                        testWhiteList.remove(i);
                        break;
                    }
                }
            default:
                System.out.println(TAG + "get Cmd:" + postData.toString());
        }

        return Response.ok().build();
    }

    protected void activate() {
        // create shareit directory, and test the write permission
        usbPath = usbStorage.getDiskVolumes().get(0).path;
        homeDir = usbPath + "/shareit";
        System.out.println(TAG + homeDir);
        File homeDirFile = new File(homeDir);
        if (!homeDirFile.exists()) {
            homeDirFile.mkdir();
            System.out.println(TAG + "shareit dir created");
        } else {
            System.out.println(TAG + "shareit dir exists");
        }

        // launch the native progress
        textile = nativeLauncher.launch("shadow-arm", new String[] { "init", homeDir + "/shadow" });
        System.out.println(TAG + "init textile");

        // if(!nodeStart){
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nativeLauncher.launch("shadow-arm",new String[]{"start",homeDir+"/shadow"});
                System.out.println(TAG+"start textile");
            }
        }.start();
        // }else{
        //     System.out.println(TAG+"node already started");
        // }
    }

    protected void deactivate() {
        nativeLauncher.stop(textile);
        System.out.println(TAG+"stop textile");
    }

    //=== services binder ================================================
    protected void setUSBStorage(USBStorage usbStorage) {
        System.out.println(TAG+"usbStorage set");
        this.usbStorage = usbStorage;
    }
    protected void unsetUSBStorage(USBStorage usbStorage) {
        this.usbStorage = null;
    }
    protected void setNativeLauncher(NativeLauncher nativeLauncher) {
        this.nativeLauncher = nativeLauncher;
    }
    protected void unsetNativeLauncher(NativeLauncher nativeLauncher) {
        this.nativeLauncher = null;
    }
}