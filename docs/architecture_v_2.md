# アーキテクチャ設計 v2

## 概要
JavaFX版太陽系シミュレーターのアーキテクチャ設計ドキュメントです。

## システム構成

### 1. コアコンポーネント
- `MainFX`: アプリケーションのエントリーポイント
- `SolarSystemManager`: 太陽系の管理
- `CelestialBody`: 天体の基本クラス
- `Planet`: 惑星クラス
- `Moon`: 衛星クラス

### 2. 表示コンポーネント
- `OrbitRenderer`: 軌道の描画
- `DebugOverlay`: デバッグ情報の表示
- `Camera`: カメラ制御

### 3. 座標系と軌道計算
- 黄道面を基準とした座標系
- ケプラーの法則に基づく軌道計算
- 2次元表示に最適化された軌道傾斜の計算

## クラス図
```
MainFX
  ├── SolarSystemManager
  │     ├── CelestialBody
  │     │     ├── Planet
  │     │     └── Moon
  │     └── OrbitRenderer
  ├── Camera
  └── DebugOverlay
```

## 主要な設計決定

### 1. 軌道計算の最適化
- 位置計算→角度更新の順序
- 2次元表示用の簡略化
- 数値安定性の向上

### 2. モジュール化
- 各コンポーネントの責務を明確に分離
- インターフェースの適切な設計
- 拡張性を考慮した構造

### 3. パフォーマンス
- 効率的な描画処理
- メモリ使用量の最適化
- スムーズなアニメーション

---

## 🎯 目的

本ドキュメントは、太陽系シミュレーション ver2.0 における内部構造、責務分離、補助クラス活用、そして描画・更新フローの全体設計を明示することを目的とする。

v2では以下の観点で設計を強化した：

- 状態変数の集中管理（`SolarSystemManager`）
- 視点制御の分離（`Camera`）
- 軌道・ラベル描画の専用化（`OrbitRenderer`）
- 状態可視化UI（`DebugOverlay`）

これにより、**インタラクティブ性・可読性・拡張性**の高い構成を実現する。

---

## 📦 クラス構成と責務

| クラス名                 | 責務概要                                 |
| -------------------- | ------------------------------------ |
| `Main`               | draw()/setup()/keyPressed() 等の統合ポイント |
| `Star`               | 恒星の描画・自転・惑星リストの管理                    |
| `Planet`             | 惑星の描画・公転・衛星リストの管理                    |
| `Moon`               | 衛星の描画・公転・潮汐ロックの処理                    |
| `SolarSystemManager` | 表示/速度/一時停止/切替などの状態変数と更新・描画集中管理       |
| `Camera`             | ズーム・パン・画面変換の一元管理                     |
| `OrbitRenderer`      | 軌道とラベルの描画、表示制御                       |
| `DebugOverlay`       | デバッグHUDとキーガイドの描画                     |

---

## 🔄 更新・描画フロー

```
Main.draw()
├── camera.apply(this)                       // 視点を適用
├── manager.update()                         // 状態に応じて更新（rotationFactorなど）
├── manager.display(this, orbitRenderer)     // 天体と軌道の描画（可視性・状態付き）
├── debugOverlay.display(this, manager, camera) // 情報UIの描画
```

---

## 🧭 ファイルの読み込み順・役割構成

### 実行時の主要ファイル読み込み順（Main起動時）

| ステージ    | ファイル                   | 内容・責務               |
| ------- | ---------------------- | ------------------- |
| 初期化     | `SolarSystemManager`   | 惑星・衛星データの定義、状態変数初期化 |
| 描画変換の適用 | `Camera.apply()`       | ズーム・パン処理適用          |
| 表示処理    | `OrbitRenderer`        | 軌道とラベルの描画           |
| 表示処理    | `Star → Planet → Moon` | 天体の位置・形状・色の描画       |
| 状態UI処理  | `DebugOverlay`         | 回転倍率・ズーム・デバッグUIの描画  |

---

## 💡 主な設計方針（v2の哲学）

- **関心の分離**：視点、状態、描画、UI を明確に役割分担
- **状態の外部化**：全てのフラグ・設定値は `Main` に集約しない
- **階層性の明示**：Star→Planet→Moon という構造がコードから読み取れる
- **描画性能配慮**：`visible`, `showOrbits`, `showLabels` で無駄な描画を抑制
- **初期表示と拡張性**：将来の天体追加やファイル読み込みに耐える柔軟性

---

## 📂 ファイル構成（重要ファイルのみ）

```
project2/
├── src/main/java/solar/
│   ├── Camera.java
│   ├── OrbitRenderer.java
│   ├── SolarSystemManager.java
│   ├── DebugOverlay.java
│   ├── Star.java
│   ├── Planet.java
│   ├── Moon.java
├── Main.java
├── docs/
│   ├── requirements_v2.md
│   ├── architecture_v2.md
│   ├── helpers_overview.md
```

---

## 🔧 設計時の注意点と反省（ver1との比較）

| 課題       | ver1の問題点                  | ver2での解決策                 |
| -------- | ------------------------- | ------------------------- |
| 状態の分散    | 各クラスが勝手に状態を持っていた          | `SolarSystemManager`に集中管理 |
| キーイベント混在 | `Main`にすべて詰め込まれていた        | 状態変更用メソッドで管理              |
| 軌道描画の分岐  | `Planet`や`Moon`が自身で描画していた | `OrbitRenderer`が専任で一括管理   |
| 拡張性不足    | 惑星を増やすたび手作業               | 配列やリスト、状態変数で対応可能に         |
| UIの混在    | 描画とガイドが一体化していた            | `DebugOverlay`を分離して責務を明確化 |

---

## 🚀 今後の拡張（ver3に向けて）

- `JSON/YAML` による天体構成データの外部化
- 軌道傾斜・軌道楕円化の数式適用（傾斜角 + 離心率）
- `Camera` の自動追尾・天体選択UIの導入
- クリック選択・ハイライト表示・ミニマップ構造の実装

---

