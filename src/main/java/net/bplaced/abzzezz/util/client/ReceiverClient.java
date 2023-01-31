package net.bplaced.abzzezz.util.client;

import net.bplaced.abzzezz.util.Cracker;
import net.bplaced.abzzezz.util.ZipUtil;
import net.bplaced.abzzezz.util.packet.Packet;
import net.bplaced.abzzezz.util.packet.PacketUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiverClient extends Client {

    private File localZip;
    final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ReceiverClient(String pServerIP, int pServerPort) {
        super(pServerIP, pServerPort);
    }

    @Override
    public void processMessage(String pMessage) {
        final Packet packet = PacketUtil.packetFromString(pMessage);

        switch (packet.getSignature()) {
            case "START":
                final String base64Zip = packet.getArguments().get(0);
                try {
                    localZip = ZipUtil.createTempZip(base64Zip);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //Send "READY" to indicate the readiness to receive a range
                send(new Packet("READY").toString());
                break;

            case "WORDS":
                final String[] words = packet.getArguments().toArray(String[]::new);
                executorService.submit(() -> {
                    boolean success = false;
                    for (String word : words) {
                        //Try the initial word:
                        if (ZipUtil.tryReadByteStream(word.toCharArray(), localZip)) {
                            System.err.println("SUCCESS!");
                            send(new Packet("SUCCESS", word).toString());
                            success = true;
                            break;
                        }
                    }
                    if (!success) {
                        send(new Packet("UNSUCCESSFUL WORD").toString());
                    }

                });
                break;
            case "FOUND":
                //Close the cracking thread
                executorService.shutdown();
                JOptionPane.showMessageDialog(null, "The password has been found. Thank you very much.");
                this.close();
                System.exit(0);
                break;
            case "RANGE":
                final int length = Integer.parseInt(packet.getArguments().get(0));
                //Process bruteforce in the background (for a certain length, todo).
                executorService.submit(() -> {
                    System.out.println("Executing bruteforce for length " + length);
                    final Cracker cracker = new Cracker(chars -> ZipUtil.tryReadByteStream(chars, localZip));
                    final String pw = cracker.bruteForcePassword(length, new char[length], 0);
                    if (pw != null) {
                        send(new Packet("SUCCESS", pw).toString());
                    } else
                        send(new Packet("UNSUCCESSFUL RANGE", String.valueOf(length)).toString());
                });
                break;
        }
    }
}
