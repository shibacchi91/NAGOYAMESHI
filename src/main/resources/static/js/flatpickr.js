let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr('#fromCheckinDate', {
	mode: "single",//選択日付は1日のみ
	locale: 'ja',
	minDate: 'today',
	maxDate: maxDate
});

flatpickr("#fromCheckinTime", {
	enableTime: true, // 時間の選択を有効にする
	noCalendar: true, // カレンダーを非表示にする
	dateFormat: "H:i", // 時間のフォーマット
	time_24hr: true, // 24時間形式を使用
	minTime: "10:00", // 最小時間を午前10時に設定
	maxTime: "22:00", // 最大時間を午後10時に設定
	minuteIncrement: 60, // 分の選択を無効にするために60分ごとに設定
	enableSeconds: false, // 秒の選択を無効にする
	
	onClose: function(selectedDates, dateStr, instance) {
		// フォームフィールドに選択された時間を設定する
		document.getElementById("fromCheckinTime").value = dateStr;
	}
});