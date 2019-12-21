package agh.cs.visualizer.gui;

import agh.cs.engine.World;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatorVisualiser extends JPanel {
    private World world;
    public MapVisualisation mapVisualisation;
    private ScheduledExecutorService executor;
    private JLabel animalCountLabel = new JLabel();
    private JLabel grassCountLabel = new JLabel();

    public SimulatorVisualiser(World world, int ticks, float maxEnergy) {
        this.world = world;
        mapVisualisation = new MapVisualisation(world.getMap(), maxEnergy);

        Box hBox = Box.createHorizontalBox();
        Box sidePanel = Box.createVerticalBox();

        hBox.add(sidePanel);
        sidePanel.add(animalCountLabel);
        sidePanel.add(grassCountLabel);


        hBox.add(mapVisualisation);
        add(hBox);

        animalCountLabel.setPreferredSize(new Dimension(100,50));
        grassCountLabel.setPreferredSize(new Dimension(100,50));


        Runnable cycle = new Runnable() {
            @Override
            public void run() {
                System.out.println("test");

                world.runEpoch();
                int animalsCount = world.getAnimalCount();
                int grassCount = world.getGrassCount();

                animalCountLabel.setText("Animals: " + animalsCount);
                grassCountLabel.setText("Grass: " + grassCount);
                System.out.println(world.getAnimalCount());
                repaint();
            }
        };
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(cycle, 0, ticks, TimeUnit.MILLISECONDS);
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
