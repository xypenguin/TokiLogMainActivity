package net.xypenguin.everydaytask;

import net.xypenguin.everydaytask.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnChronometerTickListener, OnGestureListener {
	// �g�p�R���|�[�l���g
	private RelativeLayout layout; // �w�i��ς��邽��
	private ImageView sing; // �t���b�N����K�C�h
	private TextView textTask; // �^�X�N��
	private TextView textCount; // �^�X�N���s��
	private TextView textMeanTime; // ���ώ���
	private Chronometer chronoRunTime; // ���s����
	private TextView textTotalTime; // �݌v����
	private Button buttonRun; // �J�n�A�x�e�{�^��

	private int number; // �ۑ�����v���t�@�����X�̃t�@�C���ԍ��i0�`9�܂Łj
	private String date; // �^�X�N���쐬���������iID�̂悤�Ȃ��́j
	private long startTime; // �N���m���[�^�[���X�^�[�g����������
	private long stopTime; // �N���m���[�^�[���X�g�b�v����������
	private long elapsedTime; // stopTime - startTime
	private long totalTime; // �݌v����
	private long lo; // �ۑ�����ϓ������݌v����

	private boolean buttonOn; // true�Ȃ�^�X�N���s��
	private boolean firstTime; // �N�����Ă����x�ł��{�^����������false�ɂȂ�

	private Bundle bundle; // �o���h�� ListActivity����Ԃ����f�[�^���󂯎��
	private GestureDetector gd; // �W�F�X�`���[�f�B�e�N�^�[
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// �W�F�X�`���[�f�B�e�N�^�[�̐���
		gd = new GestureDetector(this, this);

		// �R���|�[�l���g�̊֘A�t��
		layout = (RelativeLayout)this.findViewById(R.id.main_layout);
		sing = (ImageView) this.findViewById(R.id.imageSign);
		sing.setAlpha(200); // �����x ������Ɠ����Ă�
		textTask = (TextView) this.findViewById(R.id.text_task);
		textCount = (TextView) this.findViewById(R.id.text_count);
		textMeanTime = (TextView) this.findViewById(R.id.text_meantime);
		chronoRunTime = (Chronometer) this.findViewById(R.id.chrono_runtime);
		textTotalTime = (TextView) this.findViewById(R.id.text_totaltime);
		buttonRun = (Button) this.findViewById(R.id.button_run);

		// ���X�i�[�̐ݒ�
		buttonRun.setOnClickListener(this);
		chronoRunTime.setOnChronometerTickListener(this);

		// �t�B�[���h������
		clearField();

		// �v���t�@�����X�ŕۑ����Ă���O��N�����̃v���t�@�����X�̃i���o�[���擾
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		// �v���t�@�����X�̃i���o�[������ꍇ
		if (0 <= pref.getInt("number", -1)) {
			number = pref.getInt("number", -1);
			// �Ή�����i���o�[�̃v���t�@�����X����^�X�N�f�[�^���擾����
			setField(number);
			// �v���t�@�����X�̃i���o�[�͑��݂��邪�Atask���������ꍇ�i�ʏ�͔������Ȃ��Ǝv����j
		} else if (textTask.getText().toString().length() < 1) {
			openList();
			// �v���t�@�����X�̃i���o�[�������ꍇ�i����N���j
		} else {
			openList();
		}
	}

	@Override
	protected void onResume() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onResume();
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		num2bg(pref.getInt("bgNum", 0));
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		switch (item.getItemId()) {
		// �^�X�N���ύX
		case R.id.item_rename:
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			editText.setText(textTask.getText());
			showRenameDialog(this, "�^�X�N���ύX", "�^�X�N������͂��Ă�������", editText,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO �����������ꂽ���\�b�h�E�X�^�u
							if (1 <= editText.getText().toString().length()) {
								textTask.setText(editText.getText().toString());
								SharedPreferences pref = getSharedPreferences(
										"task" + number, MODE_PRIVATE);
								SharedPreferences.Editor editor = pref.edit();
								editor.putString("task", editText.getText()
										.toString());
								editor.commit();
							}
						}
					});
			break;
		// �^�X�N�폜
		case R.id.item_delete:
			showYesDialog(this, "�폜�m�F", "���̃^�X�N���폜���܂����H",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO �����������ꂽ���\�b�h�E�X�^�u
							clearPreferences(number);
							clearField();
							openList();
						}
					});

			break;
		// CreditActivity�Ăяo��
		case R.id.item_credit:
			// �����I�ȃC���e���g�̐���
			Intent intent = new Intent(this,
					net.xypenguin.everydaytask.CreditActivity.class);
			// �A�N�e�B�r�e�B�̌Ăяo��
			startActivity(intent);
			break;
		// ListActivity�Ăяo��
		case R.id.item_list:
			openList();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		// Chronometer���~�܂��Ă���Ƃ�
		if (buttonOn == false) {
			// �^�X�N�I�����Ă��珉��N��
			if (firstTime) {
				// �J�E���g��1���₵�ĕ\��
				int count = Integer.parseInt(textCount.getText().toString());
				count += 1;
				textCount.setText(Integer.toString(count));
				// �N���������Ԃ��擾����Chronometer���N��
				startTime = SystemClock.elapsedRealtime();
				chronoRunTime.setBase(startTime);
				chronoRunTime.start();
				// onCreate��onActivityResult�ŕʂ̃A�C�e����I�����Ȃ����菉��N���ł͂Ȃ��Ȃ�
				firstTime = false;
				// ����N������Ȃ��ꍇ
			} else {
				// �x�[�X�Ɍo�ߎ��Ԃ����Z����Chronometer���X�^�[�g������
				startTime = SystemClock.elapsedRealtime();
				chronoRunTime.setBase(startTime - elapsedTime);
				chronoRunTime.start();
			}
			buttonRun.setText("�ꎞ��~");
			// Chronometer�𓮂����̂�true��
			buttonOn = true;
			// Chronometer�������Ă���Ƃ�
		} else {
			// Chronometer���~����ċN�����Ԃ��擾���Ă���
			stopTime = SystemClock.elapsedRealtime();
			chronoRunTime.stop();
			elapsedTime += stopTime - startTime;
			buttonRun.setText("�ĊJ");
			buttonOn = false;

			saveText2Preferences();
		}
	}

	// ��b����Chronometer�ɌĂ΂��R�[���o�b�N���\�b�h
	@Override
	public void onChronometerTick(Chronometer chrono) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		// �݌v���Ԃ��o���B�ۑ����Ă�����totalTime�ƌo�߂������Ԃ�elapsedTime�����킹��
		lo = (SystemClock.elapsedRealtime() - (startTime - (totalTime + elapsedTime)));
		textTotalTime.setText(long2Time(lo));

		// �݌v���Ԃ��񐔂Ŋ����ĕ��ώ��Ԃ��o��
		// Chronometer���N�����Ă���Ƃ���count�͍Œ�ł�1�͂���̂�0���Z�͋C�ɂ��Ȃ�
		int count = Integer.parseInt(textCount.getText().toString());
		textMeanTime.setText(long2Time(lo / count));
	}

	// ListActivity����A���Ă����Ƃ��̏���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onActivityResult(requestCode, resultCode, data);
		// List��I�����ċA���Ă����ꍇ
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 1:
				bundle = data.getExtras();
				if (bundle != null) {
					// �����^�X�N��I�����Ă����ꍇ�A���ɉ������Ȃ�
					if (date.equals(bundle.getString("date"))) {
						return;
					}
					// �ʂ̃^�X�N�����s���������ꍇ
					if (buttonOn) {
						showYesNoDialog(this, "�^�X�N�I���m�F",
								"���ݎ��s���̃^�X�N������܂��B���s���̃^�X�N���I�������܂����H",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO �����������ꂽ���\�b�h�E�X�^�u
										if (which == DialogInterface.BUTTON_POSITIVE) {
											chronoRunTime.stop();
											showYesNoDialog(
													MainActivity.this,
													"�^�X�N�ۑ��m�F",
													"�I���������^�X�N�̃f�[�^��ۑ����܂����H",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// TODO
															// �����������ꂽ���\�b�h�E�X�^�u
															if (which == DialogInterface.BUTTON_POSITIVE) {
																saveText2Preferences();
															}
															clearField();
															setField(bundle);
														}
													});

										}
									}
								});
						// �^�X�N�����s���łȂ������ꍇ
					} else {
						clearField();
						setField(bundle);
					}
				}
				break;
			default:
				break;
			}
			// �߂�{�^���ŋA���Ă����ꍇ
		} else if (resultCode == Activity.RESULT_CANCELED) {
			// �^�X�N���ɉ����\������Ă��Ȃ�������AListActivity�ɖ߂����
			if (textTask.getText().toString().length() < 1) {
				showOkDialog(this, "�^�X�N������܂���", "���s����^�X�N��I��ł�������",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO �����������ꂽ���\�b�h�E�X�^�u
								openList();
							}
						});
			}
		}
	}

	// �A�v�����I�����悤�Ƃ���ƌĂ΂��B
	@Override
	public void finish() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		showYesDialog(this, "�I���m�F", "�A�v�����I�����܂����H",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO �����������ꂽ���\�b�h�E�X�^�u
						if (buttonOn) {
							chronoRunTime.stop();
							showYesNoDialog(MainActivity.this, "�^�X�N�ۑ��m�F",
									"���s���̃^�X�N������܂��B�I������O�ɕۑ����܂����H",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// TODO �����������ꂽ���\�b�h�E�X�^�u
											if (arg1 == DialogInterface.BUTTON_POSITIVE) {
												saveText2Preferences();
											}
											endActivity();

										}
									});
						} else {
							endActivity();
						}
					}
				});
	}

	// ���ۂɏI��点��ꍇ�ɌĂ�
	private void endActivity() {
		super.finish();
	}

	// Destroy�����O�Ƀ��C���A�N�e�B�r�e�B�ɕ\������Ă���^�X�N�̃v���t�@�����X�i���o�[��ۑ�����
	@Override
	protected void onDestroy() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("number", number);
		editor.commit();
		super.onDestroy();
	}

	// �v���t�@�����X����t�B�[���h�ɒl���Z�b�g����i���d��`�j
	private void setField(int number) {
		SharedPreferences pref = getSharedPreferences("task" + number,
				MODE_PRIVATE);
		date = pref.getString("date", "");
		totalTime = pref.getLong("totalTime", 0);
		textTask.setText(pref.getString("task", ""));
		int count = pref.getInt("count", 0);
		textCount.setText(Integer.toString(count));
		textTotalTime.setText(long2Time(totalTime));
		// �[�����Z�����
		if (count == 0) {
			textMeanTime.setText("00:00:00");
		} else {
			textMeanTime.setText(long2Time(totalTime / count));
		}
	}

	// �o���h������t�B�[���h�ɒl���Z�b�g����i���d��`�j
	private void setField(Bundle bundle) {
		number = bundle.getInt("number");
		date = bundle.getString("date");
		totalTime = bundle.getLong("totalTime");
		textTask.setText(bundle.getString("task"));
		int count = bundle.getInt("count");
		textCount.setText(Integer.toString(count));
		textTotalTime.setText(long2Time(totalTime));
		// �[�����Z�����
		if (count == 0) {
			textMeanTime.setText("00:00:00");
		} else {
			textMeanTime.setText(long2Time(totalTime / count));
		}
	}

	// TextView�̒l���v���t�@�����X�ɕۑ����郁�\�b�h
	private void saveText2Preferences() {
		for (int i = 0; i < 10; i++) {
			SharedPreferences pref = getSharedPreferences("task" + i,
					MODE_PRIVATE);
			if (date.equals(pref.getString("date", ""))) {
				number = i;
				break;
			}
		}
		SharedPreferences pref = getSharedPreferences("task" + number,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong("totalTime", lo);
		editor.putInt("count", Integer.parseInt(textCount.getText().toString()));
		editor.commit();
	}

	// �w�肵���i���o�[�̃v���t�@�����X�̃f�[�^��S��������iclearField�̌ゾ��number��������̂Ŏg���Ȃ��j
	private void clearPreferences(int number) {
		SharedPreferences pref = getSharedPreferences("task" + number,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("task", "");
		editor.putLong("totalTime", 0);
		editor.putInt("count", 0);
		editor.putString("date", "");
		editor.commit();
	}

	// mainActivity�̒l��S��������i�l���Z�b�g����O�ɂ��̃��\�b�h�����s����Ƃ����j
	private void clearField() {
		chronoRunTime.stop();
		chronoRunTime.setText("0:00");
		textTask.setText("");
		textTotalTime.setText("00:00:00");
		textMeanTime.setText("00:00:00");
		textCount.setText("0");
		buttonRun.setText("�J�n");
		elapsedTime = 0;
		totalTime = 0;
		number = -1;
		date = "";
		firstTime = true;
		buttonOn = false;
	}

	// �~���b�����ԕ\�L�ɂ��郁�\�b�h
	private static String long2Time(long time) {
		int s = (int) ((time % (1000 * 60)) / 1000);
		int m = (int) (time % (1000 * 60 * 60) / (1000 * 60));
		int h = (int) (time / (1000 * 60 * 60));

		return String.format("%02d:%02d:%02d", h, m, s);
	}

	// ListActivity���Ăяo�����\�b�h
	private void openList() {
		// �����I�ȃC���e���g�̐���
		Intent intent = new Intent(this,
				net.xypenguin.everydaytask.ListActivity.class);
		// �A�N�e�B�r�e�B�̌Ăяo��
		int requestCode = 1;
		startActivityForResult(intent, requestCode);
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

	// ���l�[���_�C�A���O
	// ���򂠂� OK�Ń��X�i�[���ĂԁAcancel�ŉ������Ȃ�
	private static void showRenameDialog(Context context, String title,
			String message, EditText editText,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setView(editText);
		ad.setPositiveButton("OK", listener);
		ad.setNegativeButton("cancel", null);
		ad.show();
	}

	// �m�FYesNo�_�C�A���O
	// ���򂠂�@Yes��No���ꂼ��Ń��X�i�[���Ă�
	private static void showYesNoDialog(Context context, String title,
			String message, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("Yes", listener);
		ad.setNegativeButton("No", listener);
		ad.show();
	}

	// �m�F�_�C�A���O
	// ���򂠂�@Yes�Ń��X�i�[���ĂԁAcancel�ŉ������Ȃ�
	private static void showYesDialog(Context context, String title,
			String message, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("Yes", listener);
		ad.setNegativeButton("cancel", null);
		ad.show();
	}

	// �x���_�C�A���O
	// ���򖳂��@OK�Ń��X�i�[���ĂԁB�R�[���o�b�N�ɂ��̌�̏���������
	private static void showOkDialog(Context context, String title,
			String message, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK", listener);
		ad.show();
	}

	// �ȉ��^�b�`�C�x���g
	// �t���b�N����K�C�h�̃C���[�W�r���[��\���A��\��
	// ���X�g�A�N�e�B�r�e�B�̌Ăяo���A�A�v���I���̌Ăяo��
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		// �^�b�`�̍��W���p�f�B���O�ɓ���āA�t���b�N����K�C�h��\��
		int left = (int) event.getX() - 100;
		int top = (int) event.getY() - 270;
		sing.setPadding(left, top, 0, 0);
		sing.setVisibility(View.VISIBLE);
		// �t���b�N�����̂��߁A�W�F�X�`���[�f�B�e�N�^�[�ɃC�x���g��n��
		gd.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		// �w�𗣂����Ƃ��̃C�x���g�@�t���b�N����K�C�h���\���ɂ���
		case MotionEvent.ACTION_UP:
			sing.setVisibility(View.GONE);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	// �t���b�N����������B
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		// 400(�s�N�Z��/�b)�̂�葁���A�p�x���}40�x�ȓ�
		// �E�����ւ̃t���b�N�@���X�g�A�N�e�B�r�e�B���J��
		if (400 <= velocityX
				&& (1 / 0.83 <= velocityX / velocityY || velocityX / velocityY <= -1 / 0.83)) {
			openList();
			// �������ւ̃t���b�N�@�A�v���I��
		} else if (velocityX <= -400
				&& (1 / 0.83 <= velocityX / velocityY || velocityX / velocityY <= -1 / 0.83)) {
			finish();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

}
