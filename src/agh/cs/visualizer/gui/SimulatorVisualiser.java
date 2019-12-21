package agh.cs.visualizer.gui;

import agh.cs.engine.World;
import agh.cs.engine.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SimulatorVisualiser extends JPanel {
    private JLabel animalCountLabel = new JLabel();
    private JLabel grassCountLabel = new JLabel();
    private JLabel averageAgeLabel = new JLabel();
    private JLabel epochLabel = new JLabel();
    private JLabel averageDeathAgeLabel = new JLabel();
    private JLabel deathCounterLabel = new JLabel();

    private Semaphore mapLock = new Semaphore(1);

    private boolean pauseFlag = false;

    private SimulatorVisualiser(World world, WorldMap map, int tickPeriod) {
        MapVisualisation mapVisualisation = new MapVisualisation(map, world.getMaxAnimalEnergy(), mapLock);

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

        Runnable cycle = () -> {
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
                grassCount = map.getGrassCount();
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

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(cycle, 0, tickPeriod, TimeUnit.MILLISECONDS);
    }

    private void togglePause() {
        if(pauseFlag){
            resume();
        } else {
            pause();
        }
    }

    private void pause() {
        pauseFlag = true;
    }

    private void resume() {
        pauseFlag = false;
    }

    private static void show(World world, WorldMap map, int tickPeriod) {
        JFrame.setDefaultLookAndFeelDecorated(false);

        JFrame frame = new JFrame("DarwinGame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new SimulatorVisualiser(world, map, tickPeriod));

        frame.pack();
        frame.setVisible(true);
    }

    static public void start(World world, WorldMap map,int tickPeriod) {
        SwingUtilities.invokeLater(() -> show(world, map, tickPeriod));
    }
}
