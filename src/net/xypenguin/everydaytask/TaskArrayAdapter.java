package net.xypenguin.everydaytask;

import java.util.List;

import net.xypenguin.everydaytask.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
/*
 * リストビューに接続するアダプタ（独自クラス）
 */
public class TaskArrayAdapter extends ArrayAdapter<ListItem> {
	private List<ListItem> list;
	LinearLayout layoutDate;
	
	public TaskArrayAdapter(Context context,List<ListItem> list) {
		super(context, -1, list);
		// TODO 自動生成されたコンストラクター・スタブ
		this.list = list;
	}
	
	//要素ビューが生成される時に呼ばれる
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		//要素ビューの情報がnullの時はレイアウトを生成
		if(convertView == null){
			//コンテキストからレイアウトインフレーターを取得
			LayoutInflater inflater = LayoutInflater.from(getContext());
			
			//要素ビューにXMLレイアウト定義をセット
			convertView = inflater.inflate(R.layout.list, null);
			
			//レイアウトよりビューを取得
			TextView task = (TextView)convertView.findViewById(R.id.text_task);
			TextView totalTime = (TextView)convertView.findViewById(R.id.text_totaltime);
			TextView count = (TextView)convertView.findViewById(R.id.text_count);
			TextView date = (TextView)convertView.findViewById(R.id.text_date);
			layoutDate = (LinearLayout)convertView.findViewById(R.id.layout_date);
			
			//再利用のため、ビューのインスタンスをホルダーへ格納
			holder = new ViewHolder();
			holder.task = task;
			holder.totalTime = totalTime;
			holder.count = count;
			holder.date = date;
			
			//タグとして登録
			convertView.setTag(holder);
		}else{
			//ビューを再利用
			holder = (ViewHolder)convertView.getTag();
		}
		//リストポジションに該当するデータを取得
		ListItem item = list.get(position);
		
		//データ設定
		holder.task.setText(item.getTask());
		holder.totalTime.setText(long2Time(item.getTotalTime()));
		holder.count.setText(Integer.toString(item.getCount()));
		holder.date.setText(item.getDate());
		
		if (item.getDate().equals("") == false) {
			layoutDate.setVisibility(View.VISIBLE);
		}else{
			layoutDate.setVisibility(View.GONE);
		}
		
		//要素ビューを返す
		return convertView;
	}
	
	public List<ListItem> getList(){
		return list;
	}
	
	public void setList(List<ListItem> list){
		this.list = list;
	}
	
	private static class ViewHolder{
		TextView task;
		TextView totalTime;
		TextView count;
		TextView date;
	}
	
	//ミリ秒を時間表記にするメソッド
	private static String long2Time(long time){
		int s = (int) ((time % (1000 * 60)) / 1000);
		int m = (int) (time % (1000 * 60 * 60) / (1000 * 60));
		int h = (int) (time / (1000 * 60 * 60));
		
		return String.format("%02d:%02d:%02d",h,m,s);
	}
}
