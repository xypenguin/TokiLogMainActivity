package net.xypenguin.everydaytask;

import net.xypenguin.everydaytask.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CreditActivity extends Activity implements OnClickListener{
	RelativeLayout layout;
	private int bgNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
		
		layout = (RelativeLayout)this.findViewById(R.id.main_layout);
		ImageButton imageButton = (ImageButton)this.findViewById(R.id.imageButton1);
		imageButton.setOnClickListener(this);

		// プリファレンスで保存してある前回起動時のプリファレンスのbgNumを取得
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		bgNum = pref.getInt("bgNum", 0);
		num2bg(bgNum);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO 自動生成されたメソッド・スタブ
		getMenuInflater().inflate(R.menu.menu_credit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		switch (item.getItemId()) {
		case R.id.item_return:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		// bgNumの値を変更する。次クリックしたら違う背景を表示するため
		if (bgNum == 3) {
			bgNum = 0;
		}else{
			bgNum += 1;
		}
		num2bg(bgNum);
		// 表示しているbgNumを保存
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("bgNum", bgNum);
		editor.commit();
	}
	
	// bgNumを受け取って背景を変更するメソッド
	private void num2bg(int bgNum){
		switch (bgNum) {
		case 0:
			layout.setBackgroundResource(R.drawable.bg_01);
			break;
		case 1:
			layout.setBackgroundResource(R.drawable.bg_02);
			break;
		case 2:
			layout.setBackgroundResource(R.drawable.bg_03);
			break;
		case 3:
			layout.setBackgroundResource(R.drawable.bg_04);
			break;
		default:
			break;
		}
	}


}
