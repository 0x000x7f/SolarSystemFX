package com.example.solarsystem.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import com.example.solarsystem.model.CelestialBody;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.DecimalFormat;

public class CelestialInfoPanel extends VBox {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.00E0");

    private final Label nameLabel;
    private final Label typeLabel;
    private final Label massLabel;
    private final Label radiusLabel;
    private final Label positionLabel;
    private final Label velocityLabel;
    private final Label orbitLabel;
    private final Label rotationLabel;
    private final ComboBox<CelestialBody> bodySelector;
    private final VBox infoBox;

    public CelestialInfoPanel(ObservableList<CelestialBody> bodies) {
        setSpacing(10);
        setPadding(new Insets(15));
        setPrefWidth(300);
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);" +
                 "-fx-background-radius: 5;" +
                 "-fx-border-color: #CCCCCC;" +
                 "-fx-border-radius: 5;");

        // タイトル
        Label titleLabel = new Label("天体情報");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        // 天体選択コンボボックス
        bodySelector = new ComboBox<>(bodies);
        bodySelector.setPromptText("天体を選択");
        bodySelector.setMaxWidth(Double.MAX_VALUE);
        bodySelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateInfo(newVal);
            }
        });

        // 情報ラベルの初期化
        nameLabel = createInfoLabel("名前: ");
        typeLabel = createInfoLabel("種類: ");
        massLabel = createInfoLabel("質量: ");
        radiusLabel = createInfoLabel("半径: ");
        positionLabel = createInfoLabel("位置: ");
        velocityLabel = createInfoLabel("速度: ");
        orbitLabel = createInfoLabel("軌道: ");
        rotationLabel = createInfoLabel("自転: ");

        // 情報ボックス
        infoBox = new VBox(5);
        infoBox.getChildren().addAll(
            nameLabel, typeLabel, massLabel, radiusLabel,
            positionLabel, velocityLabel, orbitLabel, rotationLabel
        );

        // レイアウトの構築
        getChildren().addAll(titleLabel, bodySelector, infoBox);

        // 初期状態では情報を非表示
        infoBox.setVisible(false);
    }

    private Label createInfoLabel(String prefix) {
        Label label = new Label(prefix);
        label.setWrapText(true);
        return label;
    }

    private void updateInfo(CelestialBody body) {
        infoBox.setVisible(true);

        // 基本情報
        nameLabel.setText("名前: " + body.nameProperty().get());
        typeLabel.setText("種類: " + body.typeProperty().get());

        // 物理量
        double mass = body.massProperty().get();
        double radius = body.radiusProperty().get();
        massLabel.setText("質量: " + formatScientific(mass) + " M⊕");
        radiusLabel.setText("半径: " + formatDecimal(radius) + " R⊕");

        // 位置と速度
        var position = body.positionProperty().get();
        var velocity = body.velocityProperty().get();
        positionLabel.setText(String.format("位置: (%.2f, %.2f, %.2f) AU",
            position.getX(), position.getY(), position.getZ()));
        velocityLabel.setText(String.format("速度: (%.2f, %.2f, %.2f) km/s",
            velocity.getX(), velocity.getY(), velocity.getZ()));

        // 軌道情報
        var orbitData = body.getOrbitData();
        if (orbitData != null) {
            orbitLabel.setText(String.format("軌道: a=%.2f AU, e=%.3f, i=%.1f°",
                orbitData.semiMajorAxis,
                orbitData.eccentricity,
                orbitData.inclination));
        } else {
            orbitLabel.setText("軌道: なし");
        }

        // 自転情報
        var rotationData = body.getRotationData();
        if (rotationData != null) {
            rotationLabel.setText(String.format("自転: %.2f 日/回転",
                rotationData.period));
        } else {
            rotationLabel.setText("自転: なし");
        }
    }

    private String formatDecimal(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    private String formatScientific(double value) {
        return SCIENTIFIC_FORMAT.format(value);
    }

    // 既存の setVisible(boolean) のオーバーライドを削除
    // 代わりに独自の可視制御メソッドを追加
    public void showPanel(boolean visible) {
        super.setVisible(visible);
    }

    // 選択されている天体を取得
    public CelestialBody getSelectedBody() {
        return bodySelector.getValue();
    }

    // 天体を選択
    public void selectBody(CelestialBody body) {
        bodySelector.setValue(body);
    }

    // 既存の selectBody メソッドの下に追加
    public void selectCelestialBody(CelestialBody body) {
        bodySelector.setValue(body);
    }
} 