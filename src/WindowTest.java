import club.minnced.discord.rpc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class WindowTest {

    public static void main(String[] args) throws IOException {
        File configFile = new File("config.properties");
        FileReader reader = new FileReader(configFile);
        Properties props = new Properties();
        props.load(reader);

        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordRichPresence presence = new DiscordRichPresence();
        String applicationId = props.getProperty("appID");
        String steamId       = args.length < 2 ? "" : args[1];

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");

        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        presence.startTimestamp = 1580545800;
        presence.details   = "details";
        presence.state     = "state";

        presence.largeImageKey = "largeImageKey";
        presence.largeImageText = "largeImageText";

        presence.smallImageKey = "smallImageKey";
        presence.smallImageText = "smallImageText";
        lib.Discord_UpdatePresence(presence);

        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    lib.Discord_Shutdown();
                    break;
                }
            }
        }, "RPC-Callback-Handler");
        t.start();

        JFrame frame = new JFrame("SkyRichPresence");
        GridLayout frameLayout = new GridLayout(2, 1);
        frame.setLayout(frameLayout);

        JPanel top = new JPanel();
        GridLayout topLayout = new GridLayout(2, 2);
        top.setLayout(topLayout);

        JPanel bottom = new JPanel();
        GridLayout botLayout = new GridLayout(2, 3);
        bottom.setLayout(botLayout);


        // Details
        JLabel appKey = new JLabel("App-ID " + applicationId);
        appKey.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel detailsLabel = new JLabel("Details: ");
        detailsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField detailsText = new JTextField(presence.details);

        JLabel stateLabel = new JLabel("State");
        stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField stateText = new JTextField(presence.state);

        JLabel lImageLabel = new JLabel("L-Name");
        lImageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField lImageKey = new JTextField(presence.largeImageKey);

        JLabel lImageLabelT = new JLabel("L-Text");
        lImageLabelT.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField lImageText = new JTextField(presence.largeImageText);

        JLabel sImageLabel = new JLabel("S-Name");
        sImageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField sImageKey = new JTextField(presence.smallImageKey);

        JLabel sImageLabelT = new JLabel("S-Text");
        sImageLabelT.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextField sImageText = new JTextField(presence.smallImageText);


        JButton submit = new JButton("Update RPC");
        submit.addActionListener(e -> {
            presence.details = detailsText.getText();
            presence.state = stateText.getText();
            presence.largeImageKey = lImageKey.getText();
            presence.largeImageText = lImageText.getText();
            presence.smallImageKey = sImageKey.getText();
            presence.smallImageText = sImageText.getText();

            lib.Discord_UpdatePresence(presence);

        });

        top.add(detailsLabel);
        top.add(detailsText);
        top.add(stateLabel);
        top.add(stateText);
        top.add(lImageLabel);
        top.add(lImageKey);
        top.add(lImageLabelT);
        top.add(lImageText);

        top.add(sImageLabel);
        top.add(sImageKey);
        top.add(sImageLabelT);
        top.add(sImageText);

        bottom.add(new JPanel());
        bottom.add(submit);
        bottom.add(new JPanel()); // dummy components to center the update button

        bottom.add(appKey);

        frame.add(top);
        frame.add(bottom);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                t.interrupt();
            }
        });
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
