package com.peterjosling.scroball;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.Map;

public class PlaybackTracker {

  private final ScrobbleNotificationManager scrobbleNotificationManager;
  private final ScrobbleLog scrobbleLog;
  private final ConnectivityManager connectivityManager;
  private final Scrobbler scrobbler;
  private Map<String, PlayerState> playerStates = new HashMap<>();

  public PlaybackTracker(
      ScrobbleNotificationManager scrobbleNotificationManager,
      ScrobbleLog scrobbleLog,
      ConnectivityManager connectivityManager,
      Scrobbler scrobbler) {
    this.scrobbleNotificationManager = scrobbleNotificationManager;
    this.scrobbleLog = scrobbleLog;
    this.connectivityManager = connectivityManager;
    this.scrobbler = scrobbler;
  }

  public void handlePlaybackStateChange(String player, PlaybackState playbackState) {
    if (playbackState == null) {
      return;
    }

    PlayerState playerState = getOrCreatePlayerState(player);
    playerState.setPlaybackState(playbackState);
  }

  public void handleMetadataChange(String player, MediaMetadata metadata) {
    if (metadata == null) {
      return;
    }

    Track track = Track.fromMediaMetadata(metadata);

    if (!track.isValid()) {
      return;
    }

    PlayerState playerState = getOrCreatePlayerState(player);
    playerState.setTrack(track);
  }

  public void handleSessionTermination(String player) {
    PlayerState playerState = getOrCreatePlayerState(player);
    PlaybackState playbackState = new PlaybackState.Builder()
        .setState(PlaybackState.STATE_PAUSED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1)
        .build();
    playerState.setPlaybackState(playbackState);
  }

  private PlayerState getOrCreatePlayerState(String player) {
    PlayerState playerState = playerStates.get(player);

    if (!playerStates.containsKey(player)) {
      playerState = new PlayerState(scrobbler, scrobbleNotificationManager);
      playerStates.put(player, playerState);
    }

    return playerState;
  }
}