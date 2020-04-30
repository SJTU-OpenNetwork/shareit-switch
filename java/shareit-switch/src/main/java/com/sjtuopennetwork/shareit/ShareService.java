package com.sjtuopennetwork.shareit;

import java.io.File;

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

    public ShareService(){
        super("shareit");
        System.out.println(TAG+"restservice start");
        buildDescriptor("/shareit/postCmd", HTTP_POST, PostData.class, "postCmd");
    }

    public Response postCmd(PostData postData){
        System.out.println(TAG+"get Cmd:"+postData.toString());
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
        }

        // launch the native progress
        // textile=nativeLauncher.launch("testbin.txt", new String[]{});

    }


    protected void deactivate() {
        // nativeLauncher.stop(textile);
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