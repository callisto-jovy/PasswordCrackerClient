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
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

            case "RANGE":
                //Receive rage of characters to try for a certain length
                final int lowerBound = Integer.parseInt(packet.getArguments().get(0));
                final int upperBound = Integer.parseInt(packet.getArguments().get(1));
                final int length = Integer.parseInt(packet.getArguments().get(2));


                //Start cracking
                System.out.println("New range given");
                executorService.submit(() -> {
                    final Cracker cracker = new Cracker(lowerBound, upperBound, chars -> ZipUtil.tryReadByteStream(chars, localZip));
                    final String pw = cracker.bruteForcePassword(length, new char[length], 0);
                    if (pw != null)
                        send(new Packet("SUCCESS", pw).toString());
                    else
                        sendUnsuccessfulPacket();

                });
                break;
            case "FOUND":
                //Close the cracking thread
                executorService.shutdown();
                JOptionPane.showMessageDialog(null, "The password has been found. Thank you very much.");
                this.close();
                System.exit(0);
                break;
        }
    }


    private void sendUnsuccessfulPacket() {
        send(new Packet("UNSUCCESSFUL").toString());
    }
}
