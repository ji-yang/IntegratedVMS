/*
 * =========================================================================
 * Copyright (c) 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 * =========================================================================
 * @file: DrawView.java
 */

package com.example.shayanmirjafari.integratedvms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.shayanmirjafari.integratedvms.MainActivity;
import com.example.shayanmirjafari.integratedvms.database.Person;
import com.qualcomm.snapdragon.sdk.face.FaceData;

import java.util.HashMap;


public class DrawView extends SurfaceView {
	
	private Paint paintForTextBackground = new Paint(); // Draw the black background
	// behind the text
	private Paint paintForText = new Paint(); // Draw the text
	private FaceData[] mFaceArray;
	private boolean _inFrame; // Boolean to see if there is any faces in the frame
	private HashMap<String, String> hash;
	private Person person = null;
	private MainActivity faceRecog;
	private Context context;
	
	public DrawView(Context context, FaceData[] faceArray, boolean inFrame) {
		super(context);
		setWillNotDraw(false); // This call is necessary, or else the draw
								// method will not be called.
		mFaceArray = faceArray;
		_inFrame = inFrame;
		faceRecog = new MainActivity();
		this.context = context;
//		hash = faceRecog.retrieveHash(getContext());

	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if (_inFrame) // If the face detected is in frame.
		{
			for (int i = 0; i < mFaceArray.length; i++) {
//				String selectedPersonId = Integer.toString(mFaceArray[i]
//						.getPersonId());
				int personID = mFaceArray[i].getPersonId();
				String personName = null;
				String location = null;
				String organization = null;

				person = faceRecog.retrievePerson(mFaceArray[i].getPersonId());
				if(person != null) {
					personName = person.getName();
					location = person.getLocation();
					organization = person.getOrganization();
				}

				if(mFaceArray[i].getRecognitionConfidence() >= 90){
					//recognize
					Rect rect = mFaceArray[i].rect;
					paintForTextBackground.setStyle(Paint.Style.STROKE);
					paintForTextBackground.setColor(Color.RED);
					canvas.drawRect(rect, paintForTextBackground);

//				}



					float pixelDensity = getResources().getDisplayMetrics().density;
					int textSize = (int) (rect.width() / 25 * pixelDensity);

					paintForText.setColor(Color.WHITE);
					paintForText.setTextSize(textSize);
					Typeface tp = Typeface.SERIF;
					Rect nameRect = new Rect(rect.left, rect.bottom,
							rect.right, (rect.bottom + textSize));
					Rect locRect = new Rect(rect.left, (rect.bottom + textSize),
							rect.right, (rect.bottom + 2*textSize));
					Rect orgRect = new Rect(rect.left, (rect.bottom + 2*textSize),
							rect.right, (rect.bottom + 3*textSize));

					paintForTextBackground.setStyle(Paint.Style.FILL);
					paintForTextBackground.setColor(Color.BLACK);
					paintForText.setTypeface(tp);
					paintForTextBackground.setAlpha(80);
					if (personName != null && !personName.equalsIgnoreCase("no info")) {
						canvas.drawRect(nameRect, paintForTextBackground);
						canvas.drawText("Name: "+personName, rect.left, rect.bottom
								+ (textSize), paintForText);
					}
					if(location != null && !location.equalsIgnoreCase("no info")){
						canvas.drawRect(locRect, paintForTextBackground);
						canvas.drawText("Location: "+ location, rect.left, rect.bottom
								+ 2*(textSize), paintForText);
					}
					if(organization != null && !organization.equalsIgnoreCase("no info")){
						canvas.drawRect(orgRect, paintForTextBackground);
						canvas.drawText("Organization: "+organization, rect.left, rect.bottom
								+ 3*(textSize), paintForText);

					}

				}
				else if(mFaceArray[i].getRecognitionConfidence() >= 70 && mFaceArray[i].getRecognitionConfidence() < 90 && !personName.equalsIgnoreCase("no info")){
					//update
					int result1 = MainActivity.faceObj.updatePerson(personID,
							i);


//                            Iterator<HashMap.Entry<String, String>> iter = retrieveHash(this).entrySet()
//                                    .iterator();
//                            while (iter.hasNext()) {
//                                HashMap.Entry<String, String> entry = iter.next();
//                                if (entry.getValue().equals(Integer.toString(personId))) {
//                                    userName = entry.getKey();
//                                }
//                            }
					if (result1 == 0) {
						Toast.makeText(
								context,
								"'"
										+ personName
										+ "' updated successfully ",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(
								context,
								"Maximum face limit for " + "'"
										+ personName
										+ "' reached.",
								Toast.LENGTH_SHORT).show();
					}
//					saveAlbum();
					faceRecog.saveAlbum();
				}

			}
		} else {
			canvas.drawColor(0, Mode.CLEAR);
		}
	}
	
}
