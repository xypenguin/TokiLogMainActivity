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
	// 使用コンポーネント
	private RelativeLayout layout; // 背景を変えるため
	private ImageView sing; // フリック操作ガイド
	private TextView textTask; // タスク名
	private TextView textCount; // タスク実行回数
	private TextView textMeanTime; // 平均時間
	private Chronometer chronoRunTime; // 実行時間
	private TextView textTotalTime; // 累計時間
	private Button buttonRun; // 開始、休憩ボタン

	private int number; // 保存するプリファレンスのファイル番号（0～9まで）
	private String date; // タスクを作成した日時（IDのようなもの）
	private long startTime; // クロノメーターをスタートさせた時間
	private long stopTime; // クロノメーターをストップさせた時間
	private long elapsedTime; // stopTime - startTime
	private long totalTime; // 累計時間
	private long lo; // 保存する変動した累計時間

	private boolean buttonOn; // trueならタスク実行中
	private boolean firstTime; // 起動してから一度でもボタンを押すとfalseになる

	private Bundle bundle; // バンドル ListActivityから返されるデータを受け取る
	private GestureDetector gd; // ジェスチャーディテクター
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ジェスチャーディテクターの生成
		gd = new GestureDetector(this, this);

		// コンポーネントの関連付け
		layout = (RelativeLayout)this.findViewById(R.id.main_layout);
		sing = (ImageView) this.findViewById(R.id.imageSign);
		sing.setAlpha(200); // 透明度 ちょっと透けてる
		textTask = (TextView) this.findViewById(R.id.text_task);
		textCount = (TextView) this.findViewById(R.id.text_count);
		textMeanTime = (TextView) this.findViewById(R.id.text_meantime);
		chronoRunTime = (Chronometer) this.findViewById(R.id.chrono_runtime);
		textTotalTime = (TextView) this.findViewById(R.id.text_totaltime);
		buttonRun = (Button) this.findViewById(R.id.button_run);

		// リスナーの設定
		buttonRun.setOnClickListener(this);
		chronoRunTime.setOnChronometerTickListener(this);

		// フィールド初期化
		clearField();

		// プリファレンスで保存してある前回起動時のプリファレンスのナンバーを取得
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		// プリファレンスのナンバーがある場合
		if (0 <= pref.getInt("number", -1)) {
			number = pref.getInt("number", -1);
			// 対応するナンバーのプリファレンスからタスクデータを取得する
			setField(number);
			// プリファレンスのナンバーは存在するが、task名が無い場合（通常は発生しないと思われる）
		} else if (textTask.getText().toString().length() < 1) {
			openList();
			// プリファレンスのナンバーが無い場合（初回起動）
		} else {
			openList();
		}
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
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
		// TODO 自動生成されたメソッド・スタブ
		switch (item.getItemId()) {
		// タスク名変更
		case R.id.item_rename:
			editText = new EditText(this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			editText.setText(textTask.getText());
			showRenameDialog(this, "タスク名変更", "タスク名を入力してください", editText,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 自動生成されたメソッド・スタブ
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
		// タスク削除
		case R.id.item_delete:
			showYesDialog(this, "削除確認", "このタスクを削除しますか？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 自動生成されたメソッド・スタブ
							clearPreferences(number);
							clearField();
							openList();
						}
					});

			break;
		// CreditActivity呼び出し
		case R.id.item_credit:
			// 明示的なインテントの生成
			Intent intent = new Intent(this,
					net.xypenguin.everydaytask.CreditActivity.class);
			// アクティビティの呼び出し
			startActivity(intent);
			break;
		// ListActivity呼び出し
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
		// TODO 自動生成されたメソッド・スタブ
		// Chronometerが止まっているとき
		if (buttonOn == false) {
			// タスク選択してから初回起動
			if (firstTime) {
				// カウントを1増やして表示
				int count = Integer.parseInt(textCount.getText().toString());
				count += 1;
				textCount.setText(Integer.toString(count));
				// 起動した時間を取得してChronometerを起動
				startTime = SystemClock.elapsedRealtime();
				chronoRunTime.setBase(startTime);
				chronoRunTime.start();
				// onCreateかonActivityResultで別のアイテムを選択しない限り初回起動ではなくなる
				firstTime = false;
				// 初回起動じゃない場合
			} else {
				// ベースに経過時間も合算してChronometerをスタートさせる
				startTime = SystemClock.elapsedRealtime();
				chronoRunTime.setBase(startTime - elapsedTime);
				chronoRunTime.start();
			}
			buttonRun.setText("一時停止");
			// Chronometerを動かすのでtrueに
			buttonOn = true;
			// Chronometerが動いているとき
		} else {
			// Chronometerを停止されて起動時間を取得しておく
			stopTime = SystemClock.elapsedRealtime();
			chronoRunTime.stop();
			elapsedTime += stopTime - startTime;
			buttonRun.setText("再開");
			buttonOn = false;

			saveText2Preferences();
		}
	}

	// 一秒毎にChronometerに呼ばれるコールバックメソッド
	@Override
	public void onChronometerTick(Chronometer chrono) {
		// TODO 自動生成されたメソッド・スタブ
		// 累計時間を出す。保存してあったtotalTimeと経過した時間のelapsedTimeをあわせる
		lo = (SystemClock.elapsedRealtime() - (startTime - (totalTime + elapsedTime)));
		textTotalTime.setText(long2Time(lo));

		// 累計時間を回数で割って平均時間を出す
		// Chronometerが起動しているときはcountは最低でも1はあるので0除算は気にしない
		int count = Integer.parseInt(textCount.getText().toString());
		textMeanTime.setText(long2Time(lo / count));
	}

	// ListActivityから帰ってきたときの処理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);
		// Listを選択して帰ってきた場合
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 1:
				bundle = data.getExtras();
				if (bundle != null) {
					// 同じタスクを選択していた場合、特に何もしない
					if (date.equals(bundle.getString("date"))) {
						return;
					}
					// 別のタスクが実行中だった場合
					if (buttonOn) {
						showYesNoDialog(this, "タスク終了確認",
								"現在実行中のタスクがあります。実行中のタスクを終了させますか？",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO 自動生成されたメソッド・スタブ
										if (which == DialogInterface.BUTTON_POSITIVE) {
											chronoRunTime.stop();
											showYesNoDialog(
													MainActivity.this,
													"タスク保存確認",
													"終了させたタスクのデータを保存しますか？",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// TODO
															// 自動生成されたメソッド・スタブ
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
						// タスクが実行中でなかった場合
					} else {
						clearField();
						setField(bundle);
					}
				}
				break;
			default:
				break;
			}
			// 戻るボタンで帰ってきた場合
		} else if (resultCode == Activity.RESULT_CANCELED) {
			// タスク名に何も表示されていなかったら、ListActivityに戻される
			if (textTask.getText().toString().length() < 1) {
				showOkDialog(this, "タスクがありません", "実行するタスクを選んでください",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO 自動生成されたメソッド・スタブ
								openList();
							}
						});
			}
		}
	}

	// アプリを終了しようとすると呼ばれる。
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ
		showYesDialog(this, "終了確認", "アプリを終了しますか？",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ
						if (buttonOn) {
							chronoRunTime.stop();
							showYesNoDialog(MainActivity.this, "タスク保存確認",
									"実行中のタスクがあります。終了する前に保存しますか？",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// TODO 自動生成されたメソッド・スタブ
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

	// 実際に終わらせる場合に呼ぶ
	private void endActivity() {
		super.finish();
	}

	// Destroyされる前にメインアクティビティに表示されているタスクのプリファレンスナンバーを保存する
	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		SharedPreferences pref = getSharedPreferences("preceding", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("number", number);
		editor.commit();
		super.onDestroy();
	}

	// プリファレンスからフィールドに値をセットする（多重定義）
	private void setField(int number) {
		SharedPreferences pref = getSharedPreferences("task" + number,
				MODE_PRIVATE);
		date = pref.getString("date", "");
		totalTime = pref.getLong("totalTime", 0);
		textTask.setText(pref.getString("task", ""));
		int count = pref.getInt("count", 0);
		textCount.setText(Integer.toString(count));
		textTotalTime.setText(long2Time(totalTime));
		// ゼロ除算を回避
		if (count == 0) {
			textMeanTime.setText("00:00:00");
		} else {
			textMeanTime.setText(long2Time(totalTime / count));
		}
	}

	// バンドルからフィールドに値をセットする（多重定義）
	private void setField(Bundle bundle) {
		number = bundle.getInt("number");
		date = bundle.getString("date");
		totalTime = bundle.getLong("totalTime");
		textTask.setText(bundle.getString("task"));
		int count = bundle.getInt("count");
		textCount.setText(Integer.toString(count));
		textTotalTime.setText(long2Time(totalTime));
		// ゼロ除算を回避
		if (count == 0) {
			textMeanTime.setText("00:00:00");
		} else {
			textMeanTime.setText(long2Time(totalTime / count));
		}
	}

	// TextViewの値をプリファレンスに保存するメソッド
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

	// 指定したナンバーのプリファレンスのデータを全消去する（clearFieldの後だとnumberが消えるので使えない）
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

	// mainActivityの値を全消去する（値をセットする前にこのメソッドを実行するといい）
	private void clearField() {
		chronoRunTime.stop();
		chronoRunTime.setText("0:00");
		textTask.setText("");
		textTotalTime.setText("00:00:00");
		textMeanTime.setText("00:00:00");
		textCount.setText("0");
		buttonRun.setText("開始");
		elapsedTime = 0;
		totalTime = 0;
		number = -1;
		date = "";
		firstTime = true;
		buttonOn = false;
	}

	// ミリ秒を時間表記にするメソッド
	private static String long2Time(long time) {
		int s = (int) ((time % (1000 * 60)) / 1000);
		int m = (int) (time % (1000 * 60 * 60) / (1000 * 60));
		int h = (int) (time / (1000 * 60 * 60));

		return String.format("%02d:%02d:%02d", h, m, s);
	}

	// ListActivityを呼び出すメソッド
	private void openList() {
		// 明示的なインテントの生成
		Intent intent = new Intent(this,
				net.xypenguin.everydaytask.ListActivity.class);
		// アクティビティの呼び出し
		int requestCode = 1;
		startActivityForResult(intent, requestCode);
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

	// リネームダイアログ
	// 分岐あり OKでリスナーを呼ぶ、cancelで何もしない
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

	// 確認YesNoダイアログ
	// 分岐あり　YesとNoそれぞれでリスナーを呼ぶ
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

	// 確認ダイアログ
	// 分岐あり　Yesでリスナーを呼ぶ、cancelで何もしない
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

	// 警告ダイアログ
	// 分岐無し　OKでリスナーを呼ぶ。コールバックにその後の処理を書く
	private static void showOkDialog(Context context, String title,
			String message, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK", listener);
		ad.show();
	}

	// 以下タッチイベント
	// フリック操作ガイドのイメージビューを表示、非表示
	// リストアクティビティの呼び出し、アプリ終了の呼び出し
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		// タッチの座標をパディングに入れて、フリック操作ガイドを表示
		int left = (int) event.getX() - 100;
		int top = (int) event.getY() - 270;
		sing.setPadding(left, top, 0, 0);
		sing.setVisibility(View.VISIBLE);
		// フリック実装のため、ジェスチャーディテクターにイベントを渡す
		gd.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		// 指を離したときのイベント　フリック操作ガイドを非表示にする
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
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	// フリックを実装する。
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO 自動生成されたメソッド・スタブ
		// 400(ピクセル/秒)のより早く、角度が±40度以内
		// 右方向へのフリック　リストアクティビティを開く
		if (400 <= velocityX
				&& (1 / 0.83 <= velocityX / velocityY || velocityX / velocityY <= -1 / 0.83)) {
			openList();
			// 左方向へのフリック　アプリ終了
		} else if (velocityX <= -400
				&& (1 / 0.83 <= velocityX / velocityY || velocityX / velocityY <= -1 / 0.83)) {
			finish();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
