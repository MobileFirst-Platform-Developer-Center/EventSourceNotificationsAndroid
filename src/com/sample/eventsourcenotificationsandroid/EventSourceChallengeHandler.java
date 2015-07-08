/**
*
    COPYRIGHT LICENSE: This information contains sample code provided in source code form. You may copy, modify, and distribute
    these sample programs in any form without payment to IBM® for the purposes of developing, using, marketing or distributing
    application programs conforming to the application programming interface for the operating platform for which the sample code is written.
    Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES,
    EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY,
    FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT,
    INDIRECT, INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.
    IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.

*/
package com.sample.eventsourcenotificationsandroid;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.challengehandler.ChallengeHandler;

public class EventSourceChallengeHandler extends ChallengeHandler {
	public EventSourceChallengeHandler(String realm, EventSourceNotificationsAndroid act) {
		super(realm);
	}

	@Override
	public boolean isCustomResponse(WLResponse response) {	
		Log.d("isCustomResponses", "isCustomResponse called");
		if (response == null || response.getResponseText() == null || 
				response.getResponseText().indexOf("j_security_check") == -1) {
			return false;
		}			
		return true;
	}

	@Override
	public void handleChallenge(WLResponse response) {
		Log.d("handleChallenge", "handleChallenge called");
		Map<String, String> params = new HashMap<String, String>();
		params.put("j_username", "mobilefirst");
		params.put("j_password", "password");
		submitLoginForm("j_security_check", params, null, 0, "post");
	}
	
	public void onSuccess(WLResponse response) {
		Log.d("ChallengeHandler onSuccess", "onSuccess called");
		if(isCustomResponse(response)){
			Log.d("ChallengeHandler onSuccess", "Failed - wrong credentials");
			handleChallenge(response);
		}
		else{
			Log.d("ChallengeHandler onSuccess", "submitSuccess");
//			mainActivity.showLoginForm(View.GONE);
			submitSuccess(response);
		}
	}

	public void onFailure(WLFailResponse response) {
		Log.d("ChallengeHandler onFailure", "onFailure called");
	}	
}