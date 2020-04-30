package com.sjtuopennetwork.shareit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator  implements BundleActivator{
    public static String TAG="======================= ";
	public static BundleContext bundleContext  = null;
	
    public void start(BundleContext context) throws Exception {
    	this.bundleContext = context;
        System.out.println(TAG+"shareit-switch-1.0.2 start..................");         
    }
    

    public void stop(BundleContext context) throws Exception {
        System.out.println(TAG+"shareit-switch stop....................");
    }
    
}
