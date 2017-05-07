package com.peterjosling.scroball;

import android.graphics.Bitmap;
import android.media.MediaMetadata;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.peterjosling.scroball.transforms.TitleExtractor;

@AutoValue
public abstract class Track {

  public abstract String track();
  public abstract String artist();
  public abstract Optional<String> album();
  public abstract Optional<String> albumArtist();
  public abstract Optional<Long> duration();
  public abstract Optional<Bitmap> art();
  public abstract Builder toBuilder();

  public boolean isValid() {
    return !track().equals("") && !artist().equals("");
  }

  public static Track fromMediaMetadata(MediaMetadata metadata) {
    String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
    String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
    String album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
    String albumArtist = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
    Bitmap art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
    long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);

    if (title == null) {
      title = metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);

      if (title  == null) {
        title = "";
      }
    }

    if (art == null) {
      art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
    }

    Track.Builder builder = Track.builder().track(title);

    if (duration > 0) {
      builder.duration(duration);
    }
    if (album != null && !album.isEmpty()) {
      builder.album(album);
    }
    if (albumArtist != null && !albumArtist.isEmpty()) {
      builder.albumArtist(albumArtist);
    }
    if (art != null) {
      builder.art(art);
    }
    if (artist != null) {
      builder.artist(artist);
    } else {
      return new TitleExtractor().transform(builder.build());
    }
    return builder.build();
  }

  public boolean isSameTrack(Track track) {
    return track != null && track.track().equals(track()) && track.artist().equals(artist());
  }

  public static Track empty() {
    return Track.builder().track("").artist("").build();
  }

  public static Builder builder() {
    return new AutoValue_Track.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder track(String track);
    public abstract Builder artist(String artist);
    public abstract Builder album(String album);
    public abstract Builder albumArtist(String albumArtist);
    public abstract Builder duration(long duration);
    public abstract Builder art(Bitmap art);
    public abstract Track build();
  }
}
