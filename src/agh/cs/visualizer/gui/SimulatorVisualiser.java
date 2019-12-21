package agh.cs.visualizer.gui;

import agh.cs.engine.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SimulatorVisualiser extends JPanel {
    private MapVisualisation mapVisualisation;
    private ScheduledExecutorService executor;
    private JLabel animalCountLabel = new JLabel();
    private JLabel grassCountLabel = new JLabel();
    private JLabel averageAgeLabel = new JLabel();
    private JLabel epochLabel = new JLabel();
    private JLabel averageDeathAgeLabel = new JLabel();
    private JLabel deathCounterLabel = new JLabel();

    private Semaphore mapLock = new Semaphore(1);

    private boolean pauseFlag = false;

    public SimulatorVisualiser(World world, int ticks, float maxEnergy) {
        mapVisualisation = new MapVisualisation(world.getMap(), maxEnergy, mapLock);

        Box hBox = Box.createHorizontalBox();
        Box sidePanel = Box.createVerticalBox();

        hBox.add(sidePanel);
        sidePanel.add(animalCountLabel);
        sidePanel.add(grassCountLabel);
        sidePanel.add(averageAgeLabel);
        sidePanel.add(epochLabel);
        sidePanel.add(deathCounterLabel);
        sidePanel.add(averageDeathAgeLabel);

        JLabel infoLabel = new JLabel("Press P to pause");
        sidePanel.add(infoLabel);


        hBox.add(mapVisualisation);
        add(hBox);

        animalCountLabel.setPreferredSize(new Dimension(150,50));
        grassCountLabel.setPreferredSize(new Dimension(150,50));
        averageAgeLabel.setPreferredSize(new Dimension(300, 50));

        Runnable cycle = new Runnable() {
            @Override
            public void run() {
                int animalsCount;
                int grassCount;
                long epoch;
                float averageAge;
                if (pauseFlag) {
                    return;
                }
                try {
                    mapLock.acquire();
                    world.runEpoch();
                    epoch = world.getEpoch();
                    animalsCount = world.getAnimalCount();
                    grassCount = world.getGrassCount();
                    averageAge = world.getAverageLivingAnimalsAge();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    mapLock.release();
                }

                animalCountLabel.setText("Animals: " + animalsCount);
                grassCountLabel.setText("Grass: " + grassCount);
                averageAgeLabel.setText("Average animal age: " + averageAge);
                epochLabel.setText("Epoch: " + epoch);
                deathCounterLabel.setText("Dead animals: " + world.getDeathCounter());
                averageDeathAgeLabel.setText("Avg death age: " + world.getAverageDeathAge());
                repaint();
            }
        };

        InputMap im = getInputMap(WHEN_FOCUSED);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "onPause");

        am.put("onPause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(cycle, 0, ticks, TimeUnit.MILLISECONDS);
    }

    public void togglePause() {
        if(pauseFlag){
            resume();
        } else {
            pause();
        }
    }

    public void pause() {
        pauseFlag = true;
    }

    public void resume() {
        pauseFlag = false;
    }

    private static void createAndShowGUI(World world, int tickCount, float maxEnergy) {
        JFrame.setDefaultLookAndFeelDecorated(false);

        JFrame frame = new JFrame("DarwinGame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setContentPane(new SimulatorVisualiser(world, tickCount, maxEnergy));
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        frame.pack();
        frame.setVisible(true);
    }

    static public void start(World world, int tickCount, float maxEnergy) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() { createAndShowGUI(world, tickCount, maxEnergy);}
        });

    }
}
