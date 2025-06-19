package com.example.solarsystem.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.converter.NumberStringConverter;
import java.text.DecimalFormat;

public class TimeScaleControl extends VBox {
    private static final double[] PRESET_SCALES = {0.1, 1.0, 10.0, 100.0};
    private static final String[] PRESET_LABELS = {"0.1x", "1x", "10x", "100x"};

    private final Slider timeScaleSlider;
    private final TextField timeScaleInput;
    private final Label timeScaleLabel;
    private final ToggleButton pauseButton;
    private final Label statusLabel;
    private final HBox presetButtons;

    public TimeScaleControl(
            DoubleProperty timeScale,
            BooleanProperty isPaused,
            StringProperty statusMessage) {

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        // スライダーの設定
        timeScaleSlider = new Slider(0.1, 100.0, timeScale.get());
        timeScaleSlider.setShowTickMarks(true);
        timeScaleSlider.setShowTickLabels(true);
        timeScaleSlider.setMajorTickUnit(10);
        timeScaleSlider.setMinorTickCount(9);
        timeScaleSlider.setSnapToTicks(true);
        timeScaleSlider.setPrefWidth(300);

        // 対数スケールの適用
        timeScaleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double logValue = Math.log10(newVal.doubleValue());
            timeScale.set(Math.pow(10, logValue));
        });

        // 入力フィールドの設定
        timeScaleInput = new TextField();
        timeScaleInput.setPrefWidth(80);
        DecimalFormat format = new DecimalFormat("#0.0");
        Bindings.bindBidirectional(
            timeScaleInput.textProperty(),
            timeScale,
            new NumberStringConverter(format)
        );

        // ラベルの設定
        timeScaleLabel = new Label("時間スケール:");
        timeScaleLabel.setStyle("-fx-font-weight: bold;");

        // 一時停止ボタンの設定
        pauseButton = new ToggleButton("一時停止");
        pauseButton.selectedProperty().bindBidirectional(isPaused);
        pauseButton.textProperty().bind(
            Bindings.when(isPaused)
                .then("再開")
                .otherwise("一時停止")
        );

        // ステータスラベルの設定
        statusLabel = new Label();
        statusLabel.textProperty().bind(statusMessage);
        statusLabel.setStyle("-fx-text-fill: #666666;");

        // プリセットボタンの設定
        presetButtons = new HBox(5);
        presetButtons.setAlignment(Pos.CENTER);
        for (int i = 0; i < PRESET_SCALES.length; i++) {
            final double scale = PRESET_SCALES[i];
            Button button = new Button(PRESET_LABELS[i]);
            button.setOnAction(e -> timeScale.set(scale));
            presetButtons.getChildren().add(button);
        }

        // レイアウトの構築
        HBox sliderBox = new HBox(10, timeScaleLabel, timeScaleSlider, timeScaleInput);
        sliderBox.setAlignment(Pos.CENTER);

        HBox controlBox = new HBox(10, pauseButton);
        controlBox.setAlignment(Pos.CENTER);

        getChildren().addAll(
            sliderBox,
            presetButtons,
            controlBox,
            statusLabel
        );

        // スタイルの設定
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" +
                 "-fx-background-radius: 5;" +
                 "-fx-border-color: #CCCCCC;" +
                 "-fx-border-radius: 5;");
    }

    // スライダーの値を対数スケールに変換
    private double toLogScale(double value) {
        return Math.log10(value);
    }

    // 対数スケールの値を通常のスケールに変換
    private double fromLogScale(double value) {
        return Math.pow(10, value);
    }
} 