package com.minesweeper.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

class TimerPanel extends JPanel {

    private final JLabel timerLabel;
    private final Timer swingTimer;
    private long startTimeMillis,elapsedMillis;
    private boolean timerStarted,timerRunning;

    TimerPanel() {
        setLayout(new BorderLayout());

        timerLabel = new JLabel("00:00");
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        add(timerLabel, BorderLayout.CENTER);

        swingTimer = new Timer(1000, e -> onTimerTick());
        swingTimer.setRepeats(true);

        resetTimer();
    }

    void startTimerIfNeeded() {
        if (timerStarted) {return;}

        timerStarted = true;
        timerRunning = true;
        startTimeMillis = System.currentTimeMillis();
        elapsedMillis = 0L;
        timerLabel.setText(formatElapsedTime(elapsedMillis));

        if (swingTimer.isRunning()) {
            swingTimer.restart();
        } else {
            swingTimer.start();
        }
    }

    void stopTimer() {
        if (!timerStarted || !timerRunning) {return;}

        updateElapsedMillis();
        timerRunning = false;
        swingTimer.stop();
        timerLabel.setText(formatElapsedTime(elapsedMillis));
    }

    void resetTimer() {
        timerStarted = false;
        timerRunning = false;
        elapsedMillis = 0L;
        startTimeMillis = 0L;

        if (swingTimer.isRunning()) {
            swingTimer.stop();
        }
        timerLabel.setText("00:00");
    }

    long getElapsedMillis() {
        if (timerStarted && timerRunning) {
            updateElapsedMillis();
        }
        return elapsedMillis;
    }

    String getFormattedElapsedTime() {
        return formatElapsedTime(getElapsedMillis());
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension base = super.getPreferredSize();
        if (base.width == 0 && base.height == 0) {
            return timerLabel.getPreferredSize();
        }
        return base;
    }

    private void onTimerTick() {
        if (!timerRunning) {return;}

        updateElapsedMillis();
        timerLabel.setText(formatElapsedTime(elapsedMillis));
    }

    private void updateElapsedMillis() {
        long now = System.currentTimeMillis();
        elapsedMillis = Math.max(0L, now - startTimeMillis);
    }

    private String formatElapsedTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }
}