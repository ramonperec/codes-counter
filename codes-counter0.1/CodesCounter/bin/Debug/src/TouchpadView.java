package rudp.controller;

import ahelpers.motionevent.MotionEventHelper;
import ahelpers.motionevent.TouchPointer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 13.08.11
 * Time: 1:37
 * To change this template use File | Settings | File Templates.
 */
public class TouchpadView extends View{

	private Rect _leftButtonRect;
	private Rect _rightButtonRect;
	private Rect _touchPadRect;
	private Rect _scrollRect;
	private Rect _backgroundRect;

	private List<Rect> rects;

	private Paint _leftButtonPaint = new Paint();
	private Paint _rightButtonPaint = new Paint();
	private Paint _scrollButtonPaint = new Paint();
	private TouchPointer[] pointers;

	private TouchPadDrawer drawer;

	private MouseModel mouseModel;
	private Map<Integer, Rect> pointersToRect = new Hashtable<Integer, Rect>();
	private String testString="";

	public TouchpadView(Context context) {
		super(context);    //To change body of overridden methods use File | Settings | File Templates.
		//initRectangles();
		init();
	}

	public TouchpadView(Context context, AttributeSet attrs) {
		super(context, attrs);	//To change body of overridden methods use File | Settings | File Templates.
		init();
	}

	public TouchpadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);	//To change body of overridden methods use File | Settings | File Templates.
	    init();
	}

	private  void init(){
		//initPaints();
		mouseModel = new MouseModel();
		//setTouchDelegate();
		setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {
				onTouchList(motionEvent);
				return true;  //To change body of implemented methods use File | Settings | File Templates.
			}
		});



	}

	private  void onTouchList(MotionEvent event){

		pointers = MotionEventHelper.getTouchPointers(event);
		TouchPointer actionedPointer = MotionEventHelper.getActionedPointer(pointers);

		if(actionedPointer==null)return;
		Rect rect = getRect(actionedPointer.getX(), actionedPointer.getY());
		if(rect==null)return;

		if(event.getAction()==MotionEvent.ACTION_DOWN || event.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN){

			pointersToRect.put(actionedPointer.getRID(), rect);
			if(rect==_touchPadRect){
				Log.i("motion", "ok");
				mouseModel.setMouseLastCoord(actionedPointer.getX(), actionedPointer.getY());
			}

			if(rect==_leftButtonRect){
				onLeftDown();
			}

			if(rect == _scrollRect){
				onScrollDown(actionedPointer);
			}

			if(rect == _rightButtonRect){
				onRightDown();
			}

			if(rect == _touchPadRect){
				onTouchPadDown();
			}
		}

		if(event.getAction()==MotionEvent.ACTION_MOVE ){

			if(pointersToRect.containsValue(_touchPadRect)){
				//TODO Тут страшенное говно, в плане решения, нужно избавится, но пока не знаю как без ведения второй map
				// а вторую мне лень заводить

				//TODO проверить на ноль ответ
				onTouchPadMove(getTouchPointerByRect(_touchPadRect));
			}

			if(pointersToRect.containsValue(_scrollRect)){

				onScrollMove(getTouchPointerByRect(_scrollRect));

			}

		}

		if(event.getAction()==MotionEvent.ACTION_UP || event.getActionMasked()==MotionEvent.ACTION_POINTER_UP){
			if(pointersToRect.get(actionedPointer.getRID()) == _leftButtonRect){
				onLeftUp();
			}

			if(pointersToRect.get(actionedPointer.getRID()) == _rightButtonRect){
				onRightUp();
			}

			if(pointersToRect.get(actionedPointer.getRID()) == _touchPadRect){
				mouseModel.setMouseLastCoord(actionedPointer.getX(), actionedPointer.getY());
				onTouchPadUp();
			}

			pointersToRect.remove(actionedPointer.getRID());

		}


	}

	private void onScrollDown(TouchPointer pointer) {

		mouseModel.setStartWheelPosition(pointer.getY());

		//mouseModel.getWheelHandler().init(1);
	}

	private void onScrollMove(TouchPointer pointer) {
		Log.i("motion", "scroll");
		mouseModel.setWheelPosition(pointer.getY());
	}

	private TouchPointer getTouchPointerByRect(Rect rect){
		Rect p;
		int id=-1;
		for(int i=0; i<10; i++){
			p=pointersToRect.get(i);
			if(p==rect){
				id=i;
				break;
			}
		}
		for (int i=0; i<pointers.length; i++){
			if(pointers[i].getId()==id){
				return  pointers[i];
				//onTouchPadMove(pointers[i]);
			}
		}
		return null;
	}

	private void onTouchPadMove(TouchPointer pointer) {
		Log.i("motion", "move");
		mouseModel.setMouseCoord(pointer.getX(), pointer.getY());
	}

	private void onLeftUp() {
		mouseModel.setMouseLeftState(MouseModel.MouseButtonState.RELEASED);
		Log.i("motion", "lu");
		//setButtonOutPaints(_leftButtonPaint);
	}

	private void onLeftDown() {
		Log.i("motion", "ld");
		mouseModel.setMouseLeftState(MouseModel.MouseButtonState.PRESSED);
		//setButtonOverPaints(_leftButtonPaint);
	}

	private void onRightUp() {
		Log.i("motion", "ru");
		mouseModel.setMouseRightState(MouseModel.MouseButtonState.RELEASED);
		//setButtonOutPaints(_rightButtonPaint);
	}

	private void onRightDown() {
		Log.i("motion", "rd");
		mouseModel.setMouseRightState(MouseModel.MouseButtonState.PRESSED);
		//setButtonOverPaints(_rightButtonPaint);
	}

	private long timeOfLastOnTouchPadDown = 0;//Date.parse()

	private void onTouchPadUp() {
		Date date=new Date();
		if(date.getTime() - timeOfLastOnTouchPadDown<=SettingsSource.getSelf().getClick_delay()){
			mouseModel.doClick();
		}
	}

	private void onTouchPadDown() {
		Date date=new Date();
		timeOfLastOnTouchPadDown = date.getTime();
	}

	private  Rect getRect(float cx, float cy){
		for(int i = 0; i<rects.size(); i++){
			//Rect rect = rects
			if(((Rect)rects.get(i)).contains((int)cx, (int)cy)){
				return (Rect)rects.get(i);//[i];
			}

		}
		return null;
	}

	private boolean checkPointForRect(float cx, float cy, Rect rect){
		return cx > rect.left &&
				cx < rect.right &&
				cy > rect.top &&
				cy < rect.bottom;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initRectangles();

		if(drawer!=null){
			drawer.draw(canvas);
		}
		invalidate();
	}


	private void initRectangles(){

		if(_leftButtonRect==null){
			int w = this.getWidth();
			int h = this.getHeight();
			//Log.e("draw", String.valueOf(w));
			if(w==0)return;



			double  buttonWidth = 0.1;
			double  buttonHeight = 0.5;

			int bRealWidth = (int)(buttonWidth*w);
			int bRealHeight = (int)(buttonHeight*h);
			_leftButtonRect = new Rect(0, 0, bRealWidth, bRealHeight);
			_rightButtonRect = new Rect(0, bRealHeight, bRealWidth, bRealHeight * 2);

			_scrollRect = new Rect(w - bRealWidth, 0, w, h);
			_backgroundRect = new Rect(0, 0, w, h);
			_touchPadRect = new Rect(_leftButtonRect.right, 0, _scrollRect.left, h);

			drawer= new TouchPadDrawer(getResources(), mouseModel, _leftButtonRect, _rightButtonRect, _touchPadRect, _scrollRect, _backgroundRect);

			rects = new LinkedList<Rect>();



			rects.add(_leftButtonRect);
			rects.add(_rightButtonRect);
			rects.add(_scrollRect);
			rects.add(_touchPadRect);
			//initPaints();
		}


	}

	public MouseModel getMouseModel() {
		return mouseModel;
	}
}
