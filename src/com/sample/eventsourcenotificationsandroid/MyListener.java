/**
* Copyright 2015 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.sample.eventsourcenotificationsandroid;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLEventSourceListener;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLOnReadyToSubscribeListener;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

/** This class handles all callback functions for various actions such as 
 * a. onReadyToSubscribe
 * b. callback from connect
 * c. callback from subscribe
 * d. callback from unsubscribe
 * e. callback when a notification is received
 */
public class MyListener implements WLOnReadyToSubscribeListener, WLResponseListener, WLEventSourceListener{
	
	/* The mode defines what action the MyListener object will do */
	public static final int MODE_CONNECT = 0; 
	public static final int MODE_SUBSCRIBE = 1;
	public static final int MODE_UNSUBSCRIBE =2;
	
	private int mode ; 
	
	public MyListener(int mode){
		this.mode = mode; 
	}

	/* This function is called when the registration with GCM is successful. 
	 * We are now ready to subscribe and unsubscribe 
	 */
	@Override
	public void onReadyToSubscribe() {
	
		/* Register the event source callback for the alias of myAndroid. 
		 * This must be performed before we can subscribe or unsubscribe on an alias 
		 */
		WLClient.getInstance().getPush().registerEventSourceCallback("myAndroid", "PushAdapter","PushEventSource", this );
		
		EventSourceNotificationsAndroid.updateTextView("Ready to subscribe");
		EventSourceNotificationsAndroid.enableSubscribeButtons();
	}

	/* onFailure - Update the UI with the error message 
	 * 
	 */
	@Override
	public void onFailure(WLFailResponse arg0) {
		switch (mode){
		case MODE_CONNECT:
			EventSourceNotificationsAndroid.updateTextView("Unable to connect : " + arg0.getErrorMsg());
			break;
		
		case MODE_SUBSCRIBE:
			EventSourceNotificationsAndroid.updateTextView("Failure to subscribe : " + arg0.getErrorMsg());
			break;
			
		case MODE_UNSUBSCRIBE:
			EventSourceNotificationsAndroid.updateTextView("Failure to unsubscribe : " + arg0.getErrorMsg());
			break;
			
		}
	}

	
	@Override
	public void onSuccess(WLResponse arg0) {
		switch (mode){
		case MODE_CONNECT:
			EventSourceNotificationsAndroid.updateTextView("Connected successfully ");
			break;
		
		case MODE_SUBSCRIBE:
			EventSourceNotificationsAndroid.updateTextView("Subscribed successfully to push notifications");
			break;
			
		case MODE_UNSUBSCRIBE:
			EventSourceNotificationsAndroid.updateTextView("Unsubscribed successfully from push notifications");
			break;
			
		}				
	}

	/* Update the UI with the notification received */
	@Override
	public void onReceive(String arg0, String arg1) {
		EventSourceNotificationsAndroid.updateTextView("Notification received  " + arg0);	
	}

}
