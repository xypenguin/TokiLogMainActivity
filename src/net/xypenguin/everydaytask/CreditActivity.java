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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
		
		layout = (RelativeLayout)this.findViewById(R.id.main_layout);
		ImageButton imageButton = (ImageButton)this.findViewById(R.id.imageButton1);
		imageButton.setOnClickListener(this);

		// �v���t�@�����X�ŕۑ����Ă���O��N�����̃v���t�@�����X��bgNum���擾
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		bgNum = pref.getInt("bgNum", 0);
		num2bg(bgNum);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		getMenuInflater().inflate(R.menu.menu_credit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		// bgNum�̒l��ύX����B���N���b�N������Ⴄ�w�i��\�����邽��
		if (bgNum == 3) {
			bgNum = 0;
		}else{
			bgNum += 1;
		}
		num2bg(bgNum);
		// �\�����Ă���bgNum��ۑ�
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("bgNum", bgNum);
		editor.commit();
	}
	
	// bgNum���󂯎���Ĕw�i��ύX���郁�\�b�h
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
