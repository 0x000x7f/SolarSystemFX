package com.example.solarsystem.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SolarSystemManagerTest {
    private SolarSystemManager manager;

    @BeforeAll
    static void initJavaFX() {
        // JavaFXスレッドを初期化
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("JavaFXスレッドの初期化に失敗しました", e);
        }
    }

    @BeforeEach
    void setUp() {
        // JavaFXスレッドで初期化
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                manager = new SolarSystemManager();
                latch.countDown();
            });
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("JavaFXスレッドの初期化に失敗しました", e);
        }
    }

    @Test
    void testPausedProperty() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            BooleanProperty pausedProperty = manager.isPausedProperty();
            
            // 初期状態の確認
            assertFalse(pausedProperty.get(), "初期状態は一時停止していないはず");
            
            // 一時停止の切り替え
            manager.pause();
            assertTrue(pausedProperty.get(), "一時停止後はtrueになるはず");
            
            // 再度切り替え
            manager.resume();
            assertFalse(pausedProperty.get(), "再開後はfalseになるはず");
            
            // リスナーの動作確認
            final boolean[] listenerCalled = {false};
            pausedProperty.addListener((obs, oldVal, newVal) -> {
                listenerCalled[0] = true;
                assertEquals(!oldVal, newVal, "値が反転しているはず");
            });
            
            manager.pause();
            assertTrue(listenerCalled[0], "リスナーが呼び出されるはず");
        });
    }

    @Test
    void testTimeScaleProperty() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            DoubleProperty timeScaleProperty = manager.timeScaleProperty();
            
            // 初期値の確認
            assertEquals(1.0, timeScaleProperty.get(), 0.001, "初期値は1.0のはず");
            
            // 時間スケールの変更
            manager.setTimeScale(0.5);
            assertEquals(0.5, timeScaleProperty.get(), 0.001, "時間スケールが0.5に変更されるはず");
            
            // リスナーの動作確認
            final boolean[] listenerCalled = {false};
            timeScaleProperty.addListener((obs, oldVal, newVal) -> {
                listenerCalled[0] = true;
                assertEquals(0.5, newVal.doubleValue(), 0.001, "新しい値は0.5のはず");
            });
            
            manager.setTimeScale(0.5); // 同じ値でもリスナーは呼ばれる
            assertTrue(listenerCalled[0], "リスナーが呼び出されるはず");
        });
    }

    @Test
    void testPropertyBindings() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            // 時間スケールの双方向バインディング
            DoubleProperty externalTimeScale = manager.timeScaleProperty();
            manager.setTimeScale(2.0);
            assertEquals(2.0, externalTimeScale.get(), 0.001, "外部プロパティが更新されるはず");
            
            externalTimeScale.set(0.25);
            assertEquals(0.25, manager.timeScaleProperty().get(), 0.001, "内部プロパティが更新されるはず");
            
            // 一時停止状態の双方向バインディング
            BooleanProperty externalPaused = manager.isPausedProperty();
            manager.pause();
            assertTrue(externalPaused.get(), "外部プロパティが更新されるはず");
            
            externalPaused.set(false);
            assertFalse(manager.isPausedProperty().get(), "内部プロパティが更新されるはず");
        });
    }

    @Test
    void testInitialUpdateSetsLastUpdateTime() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long initialTime = System.nanoTime();
            manager.update(initialTime);
            
            // 2回目の更新で時間差が正しく計算されることを確認
            long secondTime = initialTime + 1_000_000_000; // 1秒後
            manager.update(secondTime);
            
            // currentTimeが約1秒進んでいることを確認
            assertTrue(Math.abs(manager.getCurrentTime() - 1.0) < 0.01,
                "currentTimeは約1秒進むはず");
        });
    }

    @Test
    void testTimeScaleAffectsUpdate() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);
            
            // 時間スケールを2倍に設定
            manager.setTimeScale(2.0);
            
            // 1秒後の時間で更新
            long t2 = t1 + 1_000_000_000;
            manager.update(t2);
            
            // currentTimeが約2秒進んでいることを確認
            assertTrue(Math.abs(manager.getCurrentTime() - 2.0) < 0.01,
                "timeScale=2.0の場合、currentTimeは約2秒進むはず");
        });
    }

    @Test
    void testPauseSkipsTimeUpdate() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);
            
            // 一時停止
            manager.pause();
            double timeBeforePause = manager.getCurrentTime();
            
            // 1秒後の時間で更新
            long t2 = t1 + 1_000_000_000;
            manager.update(t2);
            
            // currentTimeが更新されていないことを確認
            assertEquals(timeBeforePause, manager.getCurrentTime(),
                "一時停止中はcurrentTimeが更新されないはず");
            
            // 再開
            manager.resume();
            long t3 = t2 + 1_000_000_000;
            manager.update(t3);
            
            // 再開後の更新で時間が進むことを確認
            assertTrue(manager.getCurrentTime() > timeBeforePause,
                "再開後はcurrentTimeが更新されるはず");
        });
    }

    @Test
    void testResetClearsTime() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);
            
            // 時間を進める
            long t2 = t1 + 1_000_000_000;
            manager.update(t2);
            
            // リセット
            manager.reset();
            
            // currentTimeが0にリセットされていることを確認
            assertEquals(0.0, manager.getCurrentTime(),
                "リセット後はcurrentTimeが0になるはず");
            
            // リセット後の更新で時間が正しく進むことを確認
            long t3 = t2 + 1_000_000_000;
            manager.update(t3);
            assertTrue(manager.getCurrentTime() > 0.0,
                "リセット後は時間が正しく進むはず");
        });
    }

    @Test
    void testConsecutiveUpdates() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);
            
            // 連続した更新
            for (int i = 1; i <= 5; i++) {
                long t = t1 + (i * 1_000_000_000L);
                manager.update(t);
                
                // 各更新で約1秒ずつ進むことを確認
                assertTrue(Math.abs(manager.getCurrentTime() - i) < 0.01,
                    String.format("%d回目の更新で約%d秒進むはず", i, i));
            }
        });
    }

    @Test
    void testUpdateAppliesTimeScaleCorrectly() throws Exception {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1); // 初回呼び出しで lastUpdateTime が設定される

            try {
                Thread.sleep(100); // 100ms 待機
            } catch (InterruptedException e) {
                fail("スリープが中断されました", e);
            }

            long t2 = System.nanoTime();
            manager.timeScaleProperty().set(2.0);
            manager.update(t2);

            double expected = (t2 - t1) / 1_000_000_000.0 * 2.0;
            double actual = manager.getCurrentTime();

            assertTrue(Math.abs(actual - expected) < 0.01,
                String.format("期待値: %.3f, 実際の値: %.3f", expected, actual));
        });
    }

    @Test
    void testDeltaTimeCalculation() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);

            // 0.5秒後の更新
            long t2 = t1 + 500_000_000; // 0.5秒
            manager.update(t2);

            // currentTimeが約0.5秒進んでいることを確認
            assertTrue(Math.abs(manager.getCurrentTime() - 0.5) < 0.01,
                "0.5秒後の更新でcurrentTimeは約0.5秒進むはず");

            // さらに0.5秒後の更新
            long t3 = t2 + 500_000_000; // 0.5秒
            manager.update(t3);

            // currentTimeが約1.0秒進んでいることを確認
            assertTrue(Math.abs(manager.getCurrentTime() - 1.0) < 0.01,
                "1.0秒後の更新でcurrentTimeは約1.0秒進むはず");
        });
    }

    @Test
    void testTimeScaleBoundaries() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);

            // 最小値（0.1）のテスト
            manager.setTimeScale(0.1);
            long t2 = t1 + 1_000_000_000; // 1秒
            manager.update(t2);
            assertTrue(Math.abs(manager.getCurrentTime() - 0.1) < 0.01,
                "timeScale=0.1の場合、currentTimeは約0.1秒進むはず");

            // 最大値（100.0）のテスト
            manager.setTimeScale(100.0);
            long t3 = t2 + 1_000_000_000; // 1秒
            manager.update(t3);
            assertTrue(Math.abs(manager.getCurrentTime() - 100.1) < 0.01,
                "timeScale=100.0の場合、currentTimeは約100秒進むはず");

            // 範囲外の値のテスト
            manager.setTimeScale(0.05); // 最小値未満
            long t4 = t3 + 1_000_000_000; // 1秒
            manager.update(t4);
            assertTrue(Math.abs(manager.getCurrentTime() - 100.2) < 0.01,
                "最小値未満のtimeScaleは0.1として扱われるはず");

            manager.setTimeScale(200.0); // 最大値超過
            long t5 = t4 + 1_000_000_000; // 1秒
            manager.update(t5);
            assertTrue(Math.abs(manager.getCurrentTime() - 100.3) < 0.01,
                "最大値超過のtimeScaleは100.0として扱われるはず");
        });
    }

    @Test
    void testPauseResumeTimeContinuity() {
        // JavaFXスレッドでテストを実行
        Platform.runLater(() -> {
            long t1 = System.nanoTime();
            manager.update(t1);

            // 1秒進める
            long t2 = t1 + 1_000_000_000;
            manager.update(t2);
            double timeBeforePause = manager.getCurrentTime();

            // 一時停止
            manager.pause();
            long t3 = t2 + 2_000_000_000; // 2秒待機
            manager.update(t3);

            // 再開
            manager.resume();
            long t4 = t3 + 1_000_000_000; // 1秒進める
            manager.update(t4);

            // 一時停止前の時間 + 1秒 になっていることを確認
            assertTrue(Math.abs(manager.getCurrentTime() - (timeBeforePause + 1.0)) < 0.01,
                "再開後は一時停止前の時間から正しく進むはず");
        });
    }
} 