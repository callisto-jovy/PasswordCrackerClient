package net.bplaced.abzzezz.util.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Packet {

    private final String signature;
    private final List<String> arguments;

    public Packet(final String signature) {
        this.signature = signature;
        this.arguments = new ArrayList<>();
    }

    public Packet(final String signature, final List<String> arguments) {
        this.signature = signature;
        this.arguments = arguments;
    }

    public Packet(final String signature, final String... arguments) {
        this.signature = signature;
        this.arguments = Arrays.stream(arguments).toList();
    }

    @Override
    public String toString() {
        return PacketUtil.packetToString(this);
    }

    public String getSignature() {
        return signature;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
