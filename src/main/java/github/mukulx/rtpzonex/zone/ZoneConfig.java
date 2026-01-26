/*
 * RtpZoneX - https://github.com/mukulx/RtpZoneX
 * Copyright (C) 2025 Mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package github.mukulx.rtpzonex.zone;

import org.bukkit.Particle;
import org.bukkit.Sound;

public class ZoneConfig {

    private String targetWorld = "world";
    private int minDistance = 100;
    private int maxDistance = 5000;
    private int teleportCountdown = 5;
    private int minPlayers = 1;
    private int maxPlayers = 10;
    private boolean waitForPlayers = true;
    private int waitTime = 10;
    private int cooldown = 60;
    private boolean darknessEnabled = false;
    private boolean soundEnabled = true;
    private Sound teleportSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private float soundVolume = 1.0f;
    private float soundPitch = 1.0f;
    private boolean particlesEnabled = false;
    private Particle particleType = Particle.PORTAL;
    private int particleCount = 50;
    private boolean zoneParticlesEnabled = false;
    private boolean titlesEnabled = true;
    private String countdownTitle = "&5&lTeleporting...";
    private String countdownSubtitle = "&7You and &e%players% &7will teleport in &c%countdown%s";
    private String waitingTitle = "&6&lWaiting for players...";
    private String waitingSubtitle = "&7%count% players ready. Teleporting in &c%wait%s";
    private boolean showCoordinates = true;
    private int maxAttempts = 50;
    private int minY = -60;
    private int maxY = 320;
    private boolean checkLava = true;
    private boolean checkWater = true;

    public String getTargetWorld() {
        return targetWorld;
    }

    public void setTargetWorld(String targetWorld) {
        this.targetWorld = targetWorld;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getTeleportCountdown() {
        return teleportCountdown;
    }

    public void setTeleportCountdown(int teleportCountdown) {
        this.teleportCountdown = teleportCountdown;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isWaitForPlayers() {
        return waitForPlayers;
    }

    public void setWaitForPlayers(boolean waitForPlayers) {
        this.waitForPlayers = waitForPlayers;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isDarknessEnabled() {
        return darknessEnabled;
    }

    public void setDarknessEnabled(boolean darknessEnabled) {
        this.darknessEnabled = darknessEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public Sound getTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(Sound teleportSound) {
        this.teleportSound = teleportSound;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public void setSoundPitch(float soundPitch) {
        this.soundPitch = soundPitch;
    }

    public boolean isParticlesEnabled() {
        return particlesEnabled;
    }

    public void setParticlesEnabled(boolean particlesEnabled) {
        this.particlesEnabled = particlesEnabled;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public void setParticleType(Particle particleType) {
        this.particleType = particleType;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    public boolean isZoneParticlesEnabled() {
        return zoneParticlesEnabled;
    }

    public void setZoneParticlesEnabled(boolean zoneParticlesEnabled) {
        this.zoneParticlesEnabled = zoneParticlesEnabled;
    }

    public boolean isTitlesEnabled() {
        return titlesEnabled;
    }

    public void setTitlesEnabled(boolean titlesEnabled) {
        this.titlesEnabled = titlesEnabled;
    }

    public String getCountdownTitle() {
        return countdownTitle;
    }

    public void setCountdownTitle(String countdownTitle) {
        this.countdownTitle = countdownTitle;
    }

    public String getCountdownSubtitle() {
        return countdownSubtitle;
    }

    public void setCountdownSubtitle(String countdownSubtitle) {
        this.countdownSubtitle = countdownSubtitle;
    }

    public String getWaitingTitle() {
        return waitingTitle;
    }

    public void setWaitingTitle(String waitingTitle) {
        this.waitingTitle = waitingTitle;
    }

    public String getWaitingSubtitle() {
        return waitingSubtitle;
    }

    public void setWaitingSubtitle(String waitingSubtitle) {
        this.waitingSubtitle = waitingSubtitle;
    }

    public boolean isShowCoordinates() {
        return showCoordinates;
    }

    public void setShowCoordinates(boolean showCoordinates) {
        this.showCoordinates = showCoordinates;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public boolean isCheckLava() {
        return checkLava;
    }

    public void setCheckLava(boolean checkLava) {
        this.checkLava = checkLava;
    }

    public boolean isCheckWater() {
        return checkWater;
    }

    public void setCheckWater(boolean checkWater) {
        this.checkWater = checkWater;
    }
}
