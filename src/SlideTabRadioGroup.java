import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SlideTabRadioGroup extends RadioGroup implements  RadioGroup.OnCheckedChangeListener {
	private final Transformation mTransformation = new Transformation();
	private Animation mAnimation;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private int mLastCheckedId = -1;
	private Drawable mDummy;
	private Drawable mDrawableNormal,mDrawableChecked;
	private boolean mAminDoing=false;

	public SlideTabRadioGroup(Context context) {
		super(context);
		init();
	}

	public SlideTabRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		super.setOnCheckedChangeListener(this);
		mLastCheckedId = super.getCheckedRadioButtonId();
		mDummy = getResources().getDrawable(R.drawable.rb_checked);
		mDrawableNormal = getResources().getDrawable(android.R.color.transparent);
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (mLastCheckedId != -1) {
			doAmin(checkedId);
		}else{
			mLastCheckedId = checkedId;
		}
		if (mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(group, checkedId);
		}
	}
	
	private void doAmin( int checkedId){
		RadioButton rbCurChecked = (RadioButton) super.findViewById(checkedId), rbLastChecked = (RadioButton) super.findViewById(mLastCheckedId);
		if(rbCurChecked==null||rbLastChecked==null){
			mLastCheckedId=checkedId;
			return;
		}
		int X1=rbLastChecked.getLeft(),X2=rbCurChecked.getLeft();
		if (X1 <= 0 && X2 <= 0) {
			mLastCheckedId=checkedId;
			return;
		}

		if (mDrawableChecked == null) {
			mDrawableChecked = rbCurChecked.getBackground();
			mDummy.setBounds(0, 0, rbCurChecked.getWidth(), rbCurChecked.getHeight());
		}
		rbCurChecked.setBackgroundDrawable(mDrawableNormal);

		if(mAminDoing && mAnimation!=null){
			mAnimation.reset();
		}
		mAnimation = new TranslateAnimation(X1,X2, rbCurChecked.getTop(), rbCurChecked.getTop());
		mAnimation.setDuration(300);
		mAnimation.initialize(0, 0, 0, 0);
		mAminDoing=true;
		mAnimation.startNow();
		invalidate();
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	protected void onDraw(Canvas canvas) {
		if (mAnimation == null || !mAminDoing) {
			return;
		}
		if (!mAnimation.hasEnded()) {
			int sc = canvas.save();
			mAnimation.getTransformation(
					AnimationUtils.currentAnimationTimeMillis(),
					mTransformation);
			canvas.concat(mTransformation.getMatrix());
			mDummy.draw(canvas);
			canvas.restoreToCount(sc);
			invalidate();
		} else {
			mAminDoing=false;
			setReallyCheck();
		}
	}

	private void setReallyCheck() {
		if (mLastCheckedId != -1) {
			super.findViewById(mLastCheckedId).setBackgroundDrawable(mDrawableNormal);
		}
		
		mLastCheckedId = super.getCheckedRadioButtonId();
		if (mLastCheckedId != -1) {
			super.findViewById(mLastCheckedId).setBackgroundDrawable(mDrawableChecked);
		}
	}

}// end class NesRadioGroup