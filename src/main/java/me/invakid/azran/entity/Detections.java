package me.invakid.azran.entity;

public class Detections {

    public String time, ign, ip, detectionName, detectionLine, detectedString, pinUsed, guild, channel, generatedBy, proccess;

    public Detections(String time, String ign, String ip, String detectionName, String detectionLine, String detectedString, String pinUsed, String guild, String channel, String generatedBy, String proccess) {
        this.time = time;
        this.ign = ign;
        this.ip = ip;
        this.detectionName = detectionName;
        this.detectionLine = detectionLine;
        this.detectedString = detectedString;
        this.pinUsed = pinUsed;
        this.guild = guild;
        this.channel = channel;
        this.generatedBy = generatedBy;
        this.proccess = proccess;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s): %s (\"%s\" in \"%s\") in %s, pin used: %s, generated in %s, %s by %s", time, ip, ign, detectionName, detectedString, detectionLine, proccess, pinUsed, channel, guild, generatedBy);
    }
}
