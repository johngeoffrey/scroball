package com.peterjosling.scroball;

import android.graphics.Bitmap;
import android.media.MediaMetadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TrackTest {

  private static final Bitmap EMPTY_BITMAP = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);

  @Test
  public void fromMediaMetadata() {
    MediaMetadata input =
        new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "Title")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artist")
            .putString(MediaMetadata.METADATA_KEY_ALBUM, "Album")
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, "Album Artist")
            .putBitmap(MediaMetadata.METADATA_KEY_ART, EMPTY_BITMAP)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, 10)
            .build();

    Track output = Track.fromMediaMetadata(input);

    assertThat(output.track()).isEqualTo("Title");
    assertThat(output.artist()).isEqualTo("Artist");
    assertThat(output.albumArtist()).hasValue("Album Artist");
    assertThat(output.art()).isPresent();
    assertThat(output.duration()).hasValue((long) 10);
  }

  @Test
  public void fromMediaMetadata_generatesInvalidTrackWhenNoArtistOrTitle() {
    MediaMetadata empty = new MediaMetadata.Builder().build();
    MediaMetadata noTitle =
        new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_ARTIST, "Artist").build();
    MediaMetadata noArtist =
        new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE, "Title").build();

    Track output1 = Track.fromMediaMetadata(empty);
    Track output2 = Track.fromMediaMetadata(noTitle);
    Track output3 = Track.fromMediaMetadata(noArtist);

    assertThat(output1.isValid()).isFalse();
    assertThat(output2.isValid()).isFalse();
    assertThat(output3.isValid()).isFalse();
  }

  @Test
  public void fromMediaMetadata_runsTitleExtractorIfArtistMissing() {
    MediaMetadata input =
        new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "Test - Input")
            .build();

    Track output = Track.fromMediaMetadata(input);

    assertThat(output.artist()).isEqualTo("Test");
    assertThat(output.track()).isEqualTo("Input");
  }

  @Test
  public void fromMediaMetadata_usesDisplayTitleIfTitleMissing() {
    MediaMetadata input =
        new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, "Display Title")
            .build();

    Track output = Track.fromMediaMetadata(input);

    assertThat(output.track()).isEqualTo("Display Title");
  }

  @Test
  public void fromMediaMetadata_usesAlbumArtIfArtMissing() {
    MediaMetadata input =
        new MediaMetadata.Builder()
            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, EMPTY_BITMAP)
            .build();

    Track output = Track.fromMediaMetadata(input);

    assertThat(output.art()).isPresent();
  }
}
