# Cursor向け移行指示: SolarSystemプロジェクト（JavaFX移行）

## 概要
本プロジェクトは、Processingで開発していた太陽系シミュレーションをJavaFXへ移行する作業を進行中です。主な理由は、Processingでは高度なUI構築・拡張が困難であるためです。

## 状況
- Processing版: `Main.java`（非推奨）
- JavaFX版: `MainFX.java`（移行先、基本描画まで完了）
- Maven構成済み、Java 21対応済み
- VSCode + Cursor対応環境にて管理

## 優先タスク
1. JavaFXによるUI部のFXML化
2. カメラの選択・フォーカス操作機能
3. 軌道楕円・傾斜軌道の描画
4. 天体構成をYAML外部定義に移行
5. マルチ言語対応（フォント切替含む）

## 実行方法
```bash
mvn javafx:run
```

## 依存環境

* Java 21.0.7+
* Maven 3.9.6
* JavaFX SDK 21.0.1 