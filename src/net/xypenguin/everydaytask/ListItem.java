package net.xypenguin.everydaytask;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ListItem {
	private String task;
	private long totalTime;
	private int count;
	private String date;

	public ListItem(String task, long totalTime, int count, String date) {
		this.task = task;
		this.totalTime = totalTime;
		this.count = count;
		this.date = date;
		
	}

	public String getTask() {
		return task;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public int getCount() {
		return count;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
