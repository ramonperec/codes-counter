package rudp.controller;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 17.08.11
 * Time: 0:46
 * To change this template use File | Settings | File Templates.
 */
public class TouchPadDrawer {

	private MouseModel mouseModel;
	private Rect leftButtonRect;
	private Rect rightButtonRect;
	private Rect touchPadRect;
	private Rect wheelRect;
	private Rect backgroundRect;

	private Resources resources;

	private Drawable leftButtonUp;
	private Drawable leftButtonDown;
	private Drawable rightButtonUp;
	private Drawable rightButtonDown;
	private Drawable wheelDrawable13;
	private Drawable wheelDrawable23;
	private Drawable wheelDrawable33;

	private Drawable backGround;

	public TouchPadDrawer(Resources resources, MouseModel model, Rect leftBRect, Rect rightBrect, Rect touchRect, Rect scrollRect, Rect backRect){
		mouseModel = model;
		leftButtonRect = leftBRect;
		rightButtonRect = rightBrect;
		touchPadRect = touchRect;
		wheelRect = scrollRect;
		backgroundRect = backRect;

		this.resources = resources;
		initDrawables();

	}

	private void initDrawables(){

		leftButtonDown = initDrawableAndBounds(R.drawable.button_down_p, leftButtonRect);

		leftButtonUp = initDrawableAndBounds(R.drawable.button_up_p, leftButtonRect);

		rightButtonDown = initDrawableAndBounds(R.drawable.button_down_p, rightButtonRect);

		rightButtonUp = initDrawableAndBounds(R.drawable.button_up_p, rightButtonRect);

		backGround = initDrawableAndBounds(R.drawable.back_png, backgroundRect);

		wheelDrawable13 = initDrawableAndBounds(R.drawable.scroll_13, wheelRect);
		wheelDrawable23 = initDrawableAndBounds(R.drawable.scroll_23, wheelRect);
		wheelDrawable33 = initDrawableAndBounds(R.drawable.scroll_33, wheelRect);

	}

	private Drawable initDrawableAndBounds(int resourceId, Rect rect){
		Drawable result = resources.getDrawable(resourceId);
		result.setBounds(rect);
		return result;

	}

	public void draw(Canvas canvas){

		Drawable leftDrawable;
		Drawable rightDrawable;

		if(mouseModel.getLeftButtonState() == MouseModel.MouseButtonState.PRESSED){
			leftDrawable = leftButtonDown;
		}else{
			leftDrawable = leftButtonUp;
		}

		if(mouseModel.getRightButtonState() == MouseModel.MouseButtonState.PRESSED){
			rightDrawable = rightButtonDown;
		}else{
			rightDrawable = rightButtonUp;
		}

		backGround.draw(canvas);

		drawLines(canvas);

		leftDrawable.draw(canvas);
		rightDrawable.draw(canvas);



		Drawable wDr=wheelDrawable13;

		switch (mouseModel.getWheelStatus()){

			case MouseModel.WheelStatus.ST_13:
				wDr = wheelDrawable13;
				break;
			case MouseModel.WheelStatus.ST_23:
				wDr = wheelDrawable23;
				break;
			case MouseModel.WheelStatus.ST_33:
				wDr = wheelDrawable33;
				break;
		}

		wDr.draw(canvas);


	}

	private final int SIZE = 25;
	private final int COLOR = 0xFFFFFF;
	private final int MAX_ALPHA = 64;
	private void drawLines(Canvas canvas){
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		Log.i("draw", w+" "+h);
		int alpha = 0;

		float startX=0;
		float startY = 0;

		for(int i =0; i<w; i+=SIZE){
			startY=0;
			for(int j =0; j<h; j+=SIZE){
				alpha = (int) ((1 -(Math.sqrt(Math.pow(mouseModel.getX() - i, 2) + Math.pow(mouseModel.getY() - j, 2))/70))*MAX_ALPHA);
				if(alpha<0)alpha=0;
				Paint p = new Paint();
				p.setColor((alpha<<24)|(0xFFFFFF));
				p.setStrokeWidth(1);
				canvas.drawLine(i, startY, (i), (j+SIZE), p);

				startY=j+SIZE;
			}
			startX=i+SIZE;
		}

		for(int i =0; i<h; i+=SIZE){
			startY=0;
			for(int j =0; j<w; j+=SIZE){
				alpha = (int) ((1 -(Math.sqrt(Math.pow(mouseModel.getX() - j, 2) + Math.pow(mouseModel.getY() - i, 2))/70))*MAX_ALPHA);
				if(alpha<0)alpha=0;
				Paint p = new Paint();
				p.setColor((alpha<<24)|(0xFFFFFF));
				p.setStrokeWidth(1);
				canvas.drawLine(startY, i, (j+SIZE), (i), p);

				startY=j+SIZE;
			}
			startX=i+SIZE;
		}


		//for(int i )
	}

}
