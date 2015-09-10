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

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.challengehandler.ChallengeHandler;

public class EventSourceChallengeHandler extends ChallengeHandler {
	private Activity parentActivity;
	private WLResponse cachedResponse; 
	
	public EventSourceChallengeHandler(String realm, MainActivity activity) {
		super(realm);
		parentActivity = activity;
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
	public void handleChallenge(WLResponse response){
		Log.d("handleChallenge", "handleChallenge called");
		if (!isCustomResponse(response)) {
			submitSuccess(response);
		} else {
			cachedResponse = response;
			Intent login = new Intent(parentActivity, LoginActivity.class);
			parentActivity.startActivityForResult(login, 1);
		}
		
	}
	
	
	public void onSuccess(WLResponse response) {
		Log.d("ChallengeHandler onSuccess", "onSuccess called");
		if(isCustomResponse(response)){
			Log.d("ChallengeHandler onSuccess", "Failed - wrong credentials");
			handleChallenge(response);
		}
		else{
			Log.d("ChallengeHandler onSuccess", "submitSuccess");
			submitSuccess(response);
		}
	}

	public void onFailure(WLFailResponse response) {
		Log.d("ChallengeHandler onFailure", "onFailure called");
	}	
	
	public void submitLogin(int resultCode, String userName, String password, boolean back){
		if (resultCode != Activity.RESULT_OK || back) {
			submitFailure(cachedResponse);
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("j_username", userName);
			params.put("j_password", password);
			submitLoginForm("/j_security_check", params, null, 0, "post");
		}
	}
}
