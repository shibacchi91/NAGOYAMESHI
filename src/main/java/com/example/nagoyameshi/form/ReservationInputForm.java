package com.example.nagoyameshi.form;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class ReservationInputForm {

	@NotBlank(message = "来店日を選択してください。")
	private String fromCheckinDate;

	@NotBlank(message = "来店時間を選択してください。")
	private String fromCheckinTime;

	@NotNull(message = "利用人数を入力してください。")
	@Min(value = 1, message = "利用人数は1人以上に設定してください。")
	private Integer numberOfPeople;

	// チェックイン日を取得する
	public LocalDate getCheckinDate() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 日付の形式が"yyyy-MM-dd"の場合
			return LocalDate.parse(getFromCheckinDate().trim(), formatter);
		} catch (DateTimeParseException e) {
			// エラーハンドリング
			throw new IllegalArgumentException("日付の形式が正しくありません。", e);
		}

	}

	// チェックイン時間を取得する
	public LocalTime getCheckinTime() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return LocalTime.parse(getFromCheckinTime().trim(), formatter);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("時間の形式が正しくありません。", e);
		}
	}
}
