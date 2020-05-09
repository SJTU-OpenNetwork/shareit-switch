package com.sjtuopennetwork.shareit;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.huawei.hilink.openapi.nativelauncher.LaunchedProcess;
import com.huawei.hilink.openapi.nativelauncher.NativeLauncher;
import com.huawei.hilink.openapi.usbstorage.USBStorage;
import com.huawei.hilink.rest.AbstractRESTResource;
import com.huawei.hilink.rest.Response;

public class ShareService extends AbstractRESTResource {
    public static String TAG="==================== ";

    // services
    private USBStorage usbStorage = null;
    private NativeLauncher nativeLauncher = null;

    // Textile process
    LaunchedProcess textile = null;

    // global vars
    String usbPath;
    String homeDir;
    LinkedList<String> testWhiteList=new LinkedList<>((Collection<String>)Arrays.asList(new String[]{"p111","p222"}));
    boolean nodeStart=false;

    public ShareService(){
        super("shareit");
        System.out.println(TAG+"restservice start");
        buildDescriptor("/shareit/getWhiteList", HTTP_GET, null, "getWhiteList");
        buildDescriptor("/shareit/postCmd", HTTP_POST, PostData.class, "postCmd");
    }

    class ResponseData{
        String responseType;
        String[] peerIdList;
        public ResponseData(String r, String[] p){
            responseType=r;
            peerIdList=p;
        }
    }

    public Response getWhiteList(){
        String responseType="whiteList";
        String[] w=(String[])testWhiteList.toArray(new String[0]);
        for(String s:w){
            System.out.println(TAG+"res: "+s);
        }
        ResponseData responseData=new ResponseData(responseType, w);
        return Response.ok(responseData).build();
    }

    public Response postCmd(PostData postData){
        switch(postData.cmdType){
            case "startNode":
                if(!nodeStart){
                    nativeLauncher.launch("shadow",new String[]{"start",homeDir+"/test1"});
                    System.out.println(TAG+"start textile");
                    nodeStart=true;
                }else{
                    System.out.println(TAG+"node already started");
                }
                break;
            case "addWhiteList":
                // add a peerid to whiteList
                System.out.println(TAG+"add:"+postData.cmd);
                testWhiteList.add(postData.cmd);
                nativeLauncher.launch("shadow", new String[]{"whitelist","add",postData.cmd});
                break;
            case "delWhiteList":
                System.out.println(TAG+"del"+postData.cmd);
                for(int i=0;i<testWhiteList.size();i++){
                    if(testWhiteList.get(i).equals(postData.cmd)){
                        testWhiteList.remove(i);
                        break;
                    }
                }
            default:
                System.out.println(TAG+"get Cmd:"+postData.toString());
        }

        return Response.ok().build();
    }

    protected void activate() {
        // create shareit directory, and test the write permission
        usbPath=usbStorage.getDiskVolumes().get(0).path;
        homeDir=usbPath+"/shareit";
        File homeDirFile=new File(homeDir);
        if(!homeDirFile.exists()){
            homeDirFile.mkdir();
            System.out.println(TAG+"shareit dir created");
        }else{
            System.out.println(TAG+"shareit dir exists");
        }

        // launch the native progress
        textile=nativeLauncher.launch("shadow", new String[]{"init",homeDir+"/test1"});
        System.out.println(TAG+"init textile");
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