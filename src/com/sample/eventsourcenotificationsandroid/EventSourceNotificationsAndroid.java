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

import com.sample.eventsourcenotificationsandroid.R;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLPush;
import com.worklight.wlclient.api.WLPushOptions;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EventSourceNotificationsAndroid extends Activity {


	private static TextView txtVResult = null;
	private static Button subscribe = null;
	private static Button unsubscribe = null;

	private static EventSourceNotificationsAndroid _this;
	private MyListener listener = new MyListener(MyListener.MODE_CONNECT) ;

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
		setContentView(R.layout.activity_android_native_push);
		_this = this;

		/* Create a WLClient instance to be used here after */
		client = WLClient.createInstance(this);
		push = client.getPush();

		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		Button isPushSupported = (Button)findViewById(R.id.isPushSupported);
		Button isSubscribed = (Button)findViewById(R.id.isSubscribed);
		subscribe = (Button)findViewById(R.id.subscribe);
		unsubscribe = (Button)findViewById(R.id.unsubscribe);

		txtVResult = (TextView)findViewById(R.id.txtVResult);

		/* Set and define the onClickListener for all the buttons */
		btnConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTextView("Connecting...");
				/* Before making the connect call,
				 * a. Set the onReadyToSubscribe listener so that the app knows what to do when it is ready to subscribe
				 * b. Also add a challenge handler before connecting.
				 */
				client.getPush().setOnReadyToSubscribeListener(listener);
				client.registerChallengeHandler(new EventSourceChallengeHandler("PushAppRealm",EventSourceNotificationsAndroid.this));
				client.connect(listener);
			}
		});


		isPushSupported.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					updateTextView("Checking if Push notifications are supported on this device...");

					WLClient client = WLClient.getInstance();
					boolean supported = client.getPush().isPushSupported();

					String updateText = supported ? "Push notifications are supported on this device" :
						"Sorry, this device does not support Push notifications";
					updateTextView(updateText);
				}catch(Exception ex){
					updateTextView(ex.getLocalizedMessage());
				}
			}
		});


		isSubscribed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				WLClient client = WLClient.getInstance();
				boolean bIsSubscribed = client.getPush().isSubscribed("myAndroid");

				String text = bIsSubscribed ? "You are subscribed to push notifications on this device" :
					"You are not subscribed to push notifications on this device";
				txtVResult.setText(text);
			}
		});

		subscribe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					updateTextView("Subscribing to Push notifications...");

					/* Subscribe with an alias of myAndroid
					 * This alias must already be created by the registerEventSourceCallback() function
					 */
					WLClient client = WLClient.getInstance();
					client.getPush().subscribe("myAndroid",new WLPushOptions(), new MyListener(MyListener.MODE_SUBSCRIBE));

					updateTextView("Subscribe request sent. Waiting for response...");
				}catch(Exception ex){
					updateTextView(ex.getLocalizedMessage());
				}
			}
		});


		unsubscribe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTextView("Unsubscribing from Push notifications...");

				/* Unsubscribe from the myAndroid alias */
				WLClient client = WLClient.getInstance();
				client.getPush().unsubscribe("myAndroid",new MyListener(MyListener.MODE_UNSUBSCRIBE));

				updateTextView("Unsubscribe request sent. Waiting for response...");
			}
		});
	}

	/* Functions to update the UI on the main screen */
	public static void updateTextView(final String str){
		Runnable run = new Runnable() {
			public void run() {
				txtVResult.setText(str);
			}
		};
		_this.runOnUiThread(run);
    }

	public static void enableSubscribeButtons(){
		Runnable run = new Runnable() {
			public void run() {
				subscribe.setEnabled(true);
				unsubscribe.setEnabled(true);
			}
		};
		_this.runOnUiThread(run);

	}

}
