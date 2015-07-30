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