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
import com.worklight.wlclient.api.WLPush;
import com.worklight.wlclient.api.WLPushOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	private static Button isPushSupportedBtn, isSubscribedBtn, subscribeBtn, unsubscribeBtn;

	private static MainActivity _this;
	private EventSourceChallengeHandler challengeHandler;
	private String realm = "PushAppRealm";
	private MyListener listener;

	private WLClient client = null;
	private WLPush push = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (push != null)
			push.setForeground(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (push != null)
			push.setForeground(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (push != null)
		push.unregisterReceivers();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_this = this;

		// Create a WLClient instance
		client = WLClient.createInstance(this);
		push = client.getPush();

		/* Before making the connect call:
		 * a. Set the onReadyToSubscribe listener
		 * b. Add a challenge handler.
		 */
		listener = new MyListener(MyListener.MODE_CONNECT);
		client.getPush().setOnReadyToSubscribeListener(listener);
		challengeHandler = new EventSourceChallengeHandler(realm, this);
		client.registerChallengeHandler(challengeHandler);
		client.connect(listener);

		isPushSupportedBtn = (Button) findViewById(R.id.isPushSupported);
		isPushSupportedBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					if (client.getPush().isPushSupported()){
						alertMsg("isPushSupported?", "true");
					} else {
						alertMsg("isPushSupported?", "false");
					}
			}

		});

		isSubscribedBtn = (Button) findViewById(R.id.isSubscribed);
		isSubscribedBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (client.getPush().isSubscribed("myAndroid")){
					alertMsg("isSubscribed?", "true");
				} else {
					alertMsg("isSubscribed?", "false");
				}
			}
		});

		subscribeBtn = (Button) findViewById(R.id.subscribe);
		subscribeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/* Subscribe with an alias of myAndroid
				 * This alias must already be created by the registerEventSourceCallback() function
				 */
				client.getPush().subscribe("myAndroid",new WLPushOptions(), new MyListener(MyListener.MODE_SUBSCRIBE));
			}
		});

		unsubscribeBtn = (Button) findViewById(R.id.unsubscribe);
		unsubscribeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/* Unsubscribe from the myAndroid alias */
				client.getPush().unsubscribe("myAndroid",new MyListener(MyListener.MODE_UNSUBSCRIBE));
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean back = data.getBooleanExtra(LoginActivity.Back, true);
		String username = data.getStringExtra(LoginActivity.UserNameExtra);
		String password = data.getStringExtra(LoginActivity.PasswordExtra);
		challengeHandler.submitLogin(resultCode, username, password, back);
	}

	public static void alertMsg(final String title, final String msg) {
		Runnable run = new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(_this);
				builder.setMessage(msg)
			       .setTitle(title);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			           }
			       });
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		};

		_this.runOnUiThread(run);

	}

	public static void enableSubscribeButtons(){
		Runnable run = new Runnable() {
			public void run() {
				subscribeBtn.setEnabled(true);
				unsubscribeBtn.setEnabled(true);
			}
		};
		_this.runOnUiThread(run);

	}

}
