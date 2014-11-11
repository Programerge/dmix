/*
 * Copyright (C) 2004 Felipe Gustavo de Almeida
 * Copyright (C) 2010-2014 The MPDroid Project
 *
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice,this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.a0z.mpd.item;

import org.a0z.mpd.Log;
import org.a0z.mpd.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.a0z.mpd.Tools.KEY;
import static org.a0z.mpd.Tools.VALUE;

/**
 * Class representing a generic file/music entry in playlist, base for the Genre items, abstracted
 * for backend.
 *
 * @author Felipe Gustavo de Almeida
 */
public abstract class AbstractMusic extends Item implements FilesystemTreeEntry {

    /** The media server response key returned for a {@link #mAlbumName} value. */
    public static final String CMD_KEY_ALBUM = "Album";

    /** The media server response key returned for a {@link #mAlbumArtistName} value. */
    public static final String CMD_KEY_ALBUM_ARTIST = "AlbumArtist";

    /** The media server response key returned for a {@link #mArtistName} value. */
    public static final String CMD_KEY_ARTIST = "Artist";

    /** The media server response key returned for a {@link #mComposerName} value. */
    public static final String CMD_KEY_COMPOSER = "Composer";

    /** The media server response key returned for a {@link #mDate} value. */
    public static final String CMD_KEY_DATE = "Date";

    /** The media server response key returned for a {@link #mDisc} value. */
    public static final String CMD_KEY_DISC = "Disc";

    /** The media server response key returned for a {@code #mFullPath} value. */
    public static final String CMD_KEY_FILE = "file";

    /** The media server response key returned for a {@link #mGenreName} value. */
    public static final String CMD_KEY_GENRE = "Genre";

    /** The media server response key returned for a {@link #mName} value. */
    public static final String CMD_KEY_NAME = "Name";

    /** The media server response key returned for a {@link #mSongId} value. */
    public static final String CMD_KEY_SONG_ID = "Id";

    /** The media server response key returned for a {@link #mSongPos} value. */
    public static final String CMD_KEY_SONG_POS = "Pos";

    /** The media server response key returned for a {@link #mTime} value. */
    public static final String CMD_KEY_TIME = "Time";

    /** The media server response key returned for a {@link #mTitle} value. */
    public static final String CMD_KEY_TITLE = "Title";

    /** The media server response key returned for a {@link #mTrack} value. */
    public static final String CMD_KEY_TRACK = "Track";

    /**
     * This is like the default {@code Comparable} for the Music class, but it compares without
     * taking disc and track numbers into account.
     */
    public static final Comparator<AbstractMusic> COMPARE_WITHOUT_TRACK_NUMBER =
            new Comparator<AbstractMusic>() {
                /**
                 * Compares the two specified objects to determine their relative ordering. The ordering
                 * implied by the return value of this method for all possible pairs of
                 * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
                 * This means that
                 * <ul>
                 * <li>{@code compare(a, a)} returns zero for all {@code a}</li>
                 * <li>the sign of {@code compare(a, b)} must be the opposite of the sign of {@code
                 * compare(b, a)} for all pairs of (a,b)</li>
                 * <li>From {@code compare(a, b) > 0} and {@code compare(b, c) > 0} it must
                 * follow {@code compare(a, c) > 0} for all possible combinations of {@code
                 * (a, b, c)}</li>
                 * </ul>
                 *
                 * @param lhs an {@code Object}.
                 * @param rhs a second {@code Object} to compare with {@code lhs}.
                 * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
                 * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
                 * @throws ClassCastException if objects are not of the correct type.
                 */
                @Override
                public int compare(final AbstractMusic lhs, final AbstractMusic rhs) {
                    int compare = 0;

                    if (lhs != null) {
                        compare = lhs.compareTo(rhs, false);
                    }

                    return compare;
                }
            };

    /** The date response has it's own delimiter. */
    private static final Pattern DATE_DELIMITER = Pattern.compile("\\D+");

    /** The maximum number of key/value pairs for a music item response. */
    private static final int MUSIC_ATTRIBUTES = 30;

    private static final String TAG = "Music";

    private static final int UNDEFINED_INT = -1;

    final String mAlbumArtistName;

    final String mAlbumName;

    final String mArtistName;

    final String mComposerName;

    final long mDate;

    final int mDisc;

    final String mFullPath;

    final String mGenreName;

    final String mName;

    final int mSongId;

    final int mSongPos;

    final long mTime;

    final String mTitle;

    final int mTotalTracks;

    final int mTrack;

    AbstractMusic() {
        this(null, /** AlbumName */
                null, /** AlbumArtistName */
                null, /** ArtistName */
                null, /** ComposerName */
                -1L, /** Date */
                UNDEFINED_INT, /** Disc */
                null, /** FullPath */
                null, /** GenreName */
                null, /** Name */
                UNDEFINED_INT, /** SongID */
                UNDEFINED_INT, /** SongPos */
                -1L, /** Time */
                null, /** Title */
                UNDEFINED_INT, /** TotalTracks*/
                UNDEFINED_INT /** Track */
        );

    }

    AbstractMusic(final AbstractMusic music) {
        this(music.mAlbumName, music.mAlbumArtistName, music.mArtistName, music.mComposerName,
                music.mDate, music.mDisc, music.mFullPath, music.mGenreName, music.mName,
                music.mSongId, music.mSongPos, music.mTime, music.mTitle, music.mTotalTracks,
                music.mTrack);
    }

    AbstractMusic(final String albumName, final String albumArtistName, final String artistName,
            final String composerName, final long date, final int disc, final String fullPath,
            final String genreName, final String name, final int songId, final int songPos,
            final long time, final String title, final int totalTracks, final int track) {
        super();

        mAlbumName = albumName;
        mArtistName = artistName;
        mAlbumArtistName = albumArtistName;
        mComposerName = composerName;
        mFullPath = fullPath;
        mDisc = disc;
        mDate = date;
        mGenreName = genreName;
        mTime = time;
        mTitle = title;
        mTotalTracks = totalTracks;
        mTrack = track;
        mSongId = songId;
        mSongPos = songPos;
        mName = name;
    }

    static Music build(final Collection<String> response) {
        String albumName = null;
        String artistName = null;
        String albumArtistName = null;
        String composerName = null;
        String fullPath = null;
        int disc = UNDEFINED_INT;
        long date = -1L;
        String genreName = null;
        long time = -1L;
        String title = null;
        int totalTracks = UNDEFINED_INT;
        int track = UNDEFINED_INT;
        int songId = UNDEFINED_INT;
        int songPos = UNDEFINED_INT;
        String name = null;

        for (final String[] pair : Tools.splitResponse(response)) {

            switch (pair[KEY]) {
                case CMD_KEY_ALBUM:
                    albumName = pair[VALUE];
                    break;
                case CMD_KEY_ALBUM_ARTIST:
                    albumArtistName = pair[VALUE];
                    break;
                case CMD_KEY_ARTIST:
                    artistName = pair[VALUE];
                    break;
                case CMD_KEY_COMPOSER:
                    composerName = pair[VALUE];
                    break;
                case CMD_KEY_DATE:
                    try {
                        final Matcher matcher = DATE_DELIMITER.matcher(pair[VALUE]);
                        date = Long.parseLong(matcher.replaceAll(""));
                    } catch (final NumberFormatException e) {
                        Log.warning(TAG, "Not a valid date.", e);
                    }
                    break;
                case CMD_KEY_DISC:
                    final int discIndex = pair[VALUE].indexOf('/');

                    try {
                        if (discIndex == -1) {
                            disc = Integer.parseInt(pair[VALUE]);
                        } else {
                            disc = Integer.parseInt(pair[VALUE].substring(0, discIndex));
                        }
                    } catch (final NumberFormatException e) {
                        Log.warning(TAG, "Not a valid disc number.", e);
                    }
                    break;
                case CMD_KEY_FILE:
                    fullPath = pair[VALUE];
                    if (!fullPath.isEmpty() && fullPath.contains("://")) {
                        final int pos = fullPath.indexOf('#');
                        if (pos > 1) {
                            name = fullPath.substring(pos + 1, fullPath.length());
                            fullPath = fullPath.substring(0, pos);
                        }
                    }
                    break;
                case CMD_KEY_GENRE:
                    genreName = pair[VALUE];
                    break;
                case CMD_KEY_NAME:
                    /**
                     * name may already be assigned to the stream name in file conditional
                     */
                    if (name == null) {
                        name = pair[VALUE];
                    }
                    break;
                case CMD_KEY_SONG_ID:
                    try {
                        songId = Integer.parseInt(pair[VALUE]);
                    } catch (final NumberFormatException e) {
                        Log.error(TAG, "Not a valid song ID.", e);
                    }
                    break;
                case CMD_KEY_SONG_POS:
                    try {
                        songPos = Integer.parseInt(pair[VALUE]);
                    } catch (final NumberFormatException e) {
                        Log.error(TAG, "Not a valid song position.", e);
                    }
                    break;
                case CMD_KEY_TIME:
                    try {
                        time = Long.parseLong(pair[VALUE]);
                    } catch (final NumberFormatException e) {
                        Log.error(TAG, "Not a valid time number.", e);
                    }
                    break;
                case CMD_KEY_TITLE:
                    title = pair[VALUE];
                    break;
                case CMD_KEY_TRACK:
                    final int trackIndex = pair[VALUE].indexOf('/');

                    try {
                        if (trackIndex == -1) {
                            track = Integer.parseInt(pair[VALUE]);
                        } else {
                            track = Integer.parseInt(pair[VALUE].substring(0, trackIndex));
                            totalTracks = Integer.parseInt(pair[VALUE].substring(trackIndex + 1));
                        }
                    } catch (final NumberFormatException e) {
                        Log.warning(TAG, "Not a valid track number.", e);
                    }
                    break;
                default:
                    /**
                     * Ignore everything else, there are a lot of
                     * uninteresting blocks the server might send.
                     */
                    break;
            }
        }

        return new Music(albumName, albumArtistName, artistName, composerName, date, disc, fullPath, genreName, name,
                songId, songPos, time, title, totalTracks, track);
    }

    /**
     * This method extends Integer.compare() by adding a undefined integer comparison.
     *
     * @param compUndefined If true, will compare by {@code UNDEFINED_INT} value.
     * @param lhs           The first integer to compare.
     * @param rhs           The second integer to compare.
     * @return A negative integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     */
    private static int compareIntegers(final boolean compUndefined, final int lhs, final int rhs) {
        int result = 0;

        if (lhs != rhs) {

            /** Compare the two integers against the primitive undefined integer for this class. */
            if (compUndefined) {
                if (lhs == UNDEFINED_INT) {
                    result = -1;
                } else if (rhs == UNDEFINED_INT) {
                    result = 1;
                }
            }
        }

        if (result == 0) {
            result = Integer.compare(lhs, rhs);
        }

        return result;
    }

    private static int compareString(final String lhs, final String rhs) {
        final int result;

        if (lhs == null && rhs == null) {
            result = 0;
        } else if (lhs == null) {
            result = -1; // lhs < rhs
        } else if (rhs == null) {
            result = 1;  // lhs > rhs
        } else {
            result = lhs.compareToIgnoreCase(rhs);
        }

        return result;
    }

    public static List<Music> getMusicFromList(final Collection<String> response,
            final boolean sort) {
        final Collection<String> lineCache = new ArrayList<>(MUSIC_ATTRIBUTES);
        final int size = response.size();
        final List<Music> result;

        /** This list can be pretty sizable, it's good to give a low estimate of it's size. */
        if (size > MUSIC_ATTRIBUTES) {
            result = new ArrayList<>(size / MUSIC_ATTRIBUTES);
        } else {
            result = new ArrayList<>(0);
        }

        for (final String line : response) {
            if (line.startsWith("file: ")) {
                if (!lineCache.isEmpty()) {
                    result.add(build(lineCache));
                    lineCache.clear();
                }
            }
            lineCache.add(line);
        }

        if (!lineCache.isEmpty()) {
            result.add(build(lineCache));
        }

        if (sort) {
            Collections.sort(result);
        }

        return result;
    }

    private static boolean isEmpty(final String s) {
        return null == s || s.isEmpty();
    }

    /**
     * This method takes seconds and converts it into HH:MM:SS
     *
     * @param totalSeconds Seconds to convert to a string.
     * @return Returns time formatted from the {@code totalSeconds} in format HH:MM:SS.
     */
    public static String timeToString(final long totalSeconds) {
        final String result;
        final long secondsInHour = 3600L;
        final long secondsInMinute = 60L;
        final long hours;
        final long minutes;
        long seconds;

        if (totalSeconds < 0L) {
            seconds = 0L;
        } else {
            seconds = totalSeconds;
        }

        hours = seconds / secondsInHour;
        seconds -= secondsInHour * hours;

        minutes = seconds / secondsInMinute;
        seconds -= minutes * secondsInMinute;

        if (hours == 0) {
            result = String.format("%02d:%02d", minutes, seconds);
        } else {
            result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return result;
    }

    /**
     * Defines a natural order to this object and another.
     *
     * @param another         The other object to compare this to.
     * @param withTrackNumber If true, compare tracks by Disc and Track number first
     * @return A negative integer if this instance is less than {@code another};
     * A positive integer if this instance is greater than {@code another};
     * 0 if this instance has the same order as {@code another}.
     */
    private int compareTo(final Item another, final boolean withTrackNumber) {
        int compareResult = 0;

        if (another instanceof AbstractMusic) {
            final AbstractMusic om = (AbstractMusic) another;

            /** songId overrides every other sorting method. It's used for playlists/queue. */
            compareResult = compareIntegers(true, mSongId, om.mSongId);

            if (withTrackNumber) {
                if (compareResult == 0) {
                    /** Order by the disc number. */
                    compareResult = compareIntegers(true, mDisc, om.mDisc);
                }

                if (compareResult == 0) {
                    /** Order by track number. */
                    compareResult = compareIntegers(true, mTrack, om.mTrack);
                }
            }

            if (compareResult == 0) {
                /** Order by song title (getTitle() fallback on file names). */
                compareResult = compareString(getTitle(), om.getTitle());
            }

            if (compareResult == 0) {
                /** Order by name (this is helpful for streams). */
                compareResult = compareString(mName, om.mName);
            }

            if (compareResult == 0) {
                /** As a last resort, order by the full path. */
                compareResult = compareString(mFullPath, om.mFullPath);
            }
        } else {
            compareResult = super.compareTo(another);
        }

        return compareResult;
    }

    /**
     * Defines a natural order to this object and another.
     *
     * @param another The other object to compare this to.
     * @return A negative integer if this instance is less than {@code another};
     * A positive integer if this instance is greater than {@code another};
     * 0 if this instance has the same order as {@code another}.
     */
    @Override
    public int compareTo(final Item another) {
        return compareTo(another, true);
    }

    @Override
    public boolean equals(final Object o) {
        Boolean isEqual = null;

        if (this == o) {
            isEqual = Boolean.TRUE;
        } else if (o == null || getClass() != o.getClass()) {
            isEqual = Boolean.FALSE;
        }

        if (isEqual == null || isEqual.equals(Boolean.TRUE)) {
            final AbstractMusic music = (AbstractMusic) o;

            final Object[][] equalsObjects = {
                    {mAlbumName, music.mAlbumName},
                    {mAlbumArtistName, music.mAlbumArtistName},
                    {mArtistName, music.mArtistName},
                    {mComposerName, music.mComposerName},
                    {mGenreName, music.mGenreName},
                    {mName, music.mName},
                    {mTitle, music.mTitle}
            };

            final int[][] equalsInt = {
                    {mDisc, music.mDisc},
                    {mSongId, music.mSongId},
                    {mSongPos, music.mSongPos},
                    {mTotalTracks, music.mTotalTracks},
                    {mTrack, music.mTrack}
            };

            if (mDate != music.mDate || mTime != music.mTime || Tools.isNotEqual(equalsInt)) {
                isEqual = Boolean.FALSE;
            }

            if (!mFullPath.equals(music.mFullPath) || Tools.isNotEqual(equalsObjects)) {
                isEqual = Boolean.FALSE;
            }
        }

        if (isEqual == null) {
            isEqual = Boolean.TRUE;
        }

        return isEqual.booleanValue();
    }

    public Album getAlbum() {
        final boolean isAlbumArtist = !isEmpty(mAlbumArtistName);
        final AlbumBuilder albumBuilder = new AlbumBuilder();

        albumBuilder.setName(mAlbumName);
        if (isAlbumArtist) {
            albumBuilder.setAlbumArtist(mAlbumArtistName);
        } else {
            albumBuilder.setArtist(mArtistName);
        }

        albumBuilder.setSongDetails(mDate, mFullPath);

        return albumBuilder.build();
    }

    public Artist getAlbumArtist() {
        return new Artist(mAlbumArtistName);
    }

    /**
     * Retrieves the original album artist name.
     *
     * @return album artist name or null if it is not set.
     */
    public String getAlbumArtistName() {
        return mAlbumArtistName;
    }

    public String getAlbumArtistOrArtist() {
        final String result;

        if (mAlbumArtistName != null && !mAlbumArtistName.isEmpty()) {
            result = mAlbumArtistName;
        } else if (mArtistName != null && !mArtistName.isEmpty()) {
            result = mArtistName;
        } else {
            result = getArtist().mainText();
        }

        return result;
    }

    /**
     * Retrieves album name.
     *
     * @return album name.
     */
    public String getAlbumName() {
        return mAlbumName;
    }

    public Artist getArtist() {
        return new Artist(mArtistName);
    }

    /**
     * Retrieves artist name.
     *
     * @return artist name.
     */
    public String getArtistName() {
        return mArtistName;
    }

    public String getComposerName() {
        return mComposerName;
    }

    public long getDate() {
        return mDate;
    }

    public int getDisc() {
        return mDisc;
    }

    /**
     * TODO test this for streams Retrieves filename.
     *
     * @return filename.
     */
    public String getFilename() {
        String result = null;

        if (mFullPath != null) {
            final int pos = mFullPath.lastIndexOf('/');
            if (pos == -1 || pos == mFullPath.length() - 1) {
                result = mFullPath;
            } else {
                result = mFullPath.substring(pos + 1);
            }
        }

        return result;
    }

    /**
     * Retrieves mDate as string (##:##).
     *
     * @return mDate as string.
     */
    public CharSequence getFormattedTime() {
        return timeToString(mTime);
    }

    /**
     * Retrieves full path name.
     *
     * @return full path name.
     */
    @Override
    public String getFullPath() {
        return mFullPath;
    }

    public String getGenreName() {
        return mGenreName;
    }

    /**
     * Retrieves stream's name.
     *
     * @return stream's name.
     */
    @Override
    public String getName() {
        final String name;

        if (isEmpty(mName)) {
            name = getFilename();
        } else {
            name = mName;
        }

        return name;
    }

    /**
     * Retrieves file's parent directory
     *
     * @return file's parent directory
     */
    public String getParent() {
        String parent = null;

        if (mFullPath != null) {
            final int pos = mFullPath.lastIndexOf('/');

            if (pos != -1) {
                parent = mFullPath.substring(0, pos);
            }
        }

        return parent;
    }

    /**
     * Retrieves path of music file (does not start or end with /)
     *
     * @return path of music file.
     */
    public String getPath() {
        final String result;
        if (null != mFullPath && mFullPath.length() > getFilename().length()) {
            result = mFullPath.substring(0, mFullPath.length() - getFilename().length() - 1);
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Retrieves current song stopped on or playing, playlist song number.
     *
     * @return current song stopped on or playing, playlist song number.
     */
    public int getPos() {
        return mSongPos;
    }

    /**
     * Retrieves current song playlist id.
     *
     * @return current song playlist id.
     */
    public int getSongId() {
        return mSongId;
    }

    /**
     * Retrieves playing time.
     *
     * @return playing time.
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Retrieves title.
     *
     * @return title.
     */
    public String getTitle() {
        if (isEmpty(mTitle)) {
            return getFilename();
        } else {
            return mTitle;
        }
    }

    /**
     * Retrieves total number of tracks from this music's album when available.
     * This can contain letters!
     *
     * @return total number of tracks from this music's album when available.
     */
    public int getTotalTracks() {
        return mTotalTracks;
    }

    /**
     * Retrieves track number. This can contain letters!
     *
     * @return track number.
     */
    public int getTrack() {
        return mTrack;
    }

    public boolean hasTitle() {
        return null != mTitle && !mTitle.isEmpty();
    }

    @Override
    public int hashCode() {
        final Object[] objects = {mAlbumName, mArtistName, mAlbumArtistName, mGenreName, mName,
                mTitle};

        int result = 31 * mFullPath.hashCode();
        result = 31 * result + mDisc;
        result = 31 * result + (int) (mDate ^ (mDate >>> 32));
        result = 31 * result + mSongId;
        result = 31 * result + mSongPos;
        result = 31 * result + (int) (mTime ^ (mTime >>> 32));
        result = 31 * result + mTotalTracks;
        result = 31 * result + mTrack;

        return result + Arrays.hashCode(objects);
    }

    public boolean isStream() {
        return null != mFullPath && mFullPath.contains("://");
    }

    @Override
    public String mainText() {
        return getTitle();
    }
}
