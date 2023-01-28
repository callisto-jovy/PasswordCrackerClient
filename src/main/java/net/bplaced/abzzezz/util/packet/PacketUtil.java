package net.bplaced.abzzezz.util.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketUtil {


    public static String packetToString(final Packet packet) {
        return packet.getSignature() + ":" + packet.getArguments()
                .stream()
                .map(s -> s.replaceAll(":", "\\\\:"))
                .collect(Collectors.joining(":"));
    }

    public static Packet packetFromString(final String string) {
        final int firstColonIndex = string.indexOf(":");
        //Read till first colon
        final String signature = string.substring(0, firstColonIndex);

        final List<String> arguments = new ArrayList<>();

        int lastIndex = firstColonIndex + 1;
        char lastChar = ':';


        for (int i = lastIndex; i < string.length(); i++) {
            final char cAtIndex = string.charAt(i);
            //Check split and ignore if the char is escaped
            if (cAtIndex == ':' && lastChar != '\\') {
                arguments.add(string.substring(lastIndex, i));
                i++;
                lastIndex = i;
            } else if (i == string.length() - 1) {
                arguments.add(string.substring(lastIndex));
                break;
            }
            lastChar = cAtIndex;
        }
        //Not very efficient, works for now.
        return new Packet(signature, arguments.stream().map(s -> s.replaceAll("(\\\\)+:", ":")).collect(Collectors.toList()));
    }
}
