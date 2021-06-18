package com.example.musiccompose.data.network

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.musiccompose.R
import com.example.musiccompose.extensions.*
import com.example.musiccompose.util.contansts.FAKE_ALBUM_ART_URL
import com.example.musiccompose.util.contansts.MEDIA_ALBUMS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_ARTISTS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_BROWSABLE_ROOT
import com.example.musiccompose.util.contansts.MEDIA_RECENT_ROOT
import com.example.musiccompose.util.contansts.MEDIA_RECOMMENDED_ROOT
import com.google.android.exoplayer2.MediaItem

// code from UAMP
// I have added some extra functions and made some modifications
// Note that the implementation of recommendation is not finished yet
/**
 * Represents a tree of media that's used by [MusicService.onLoadChildren].
 *
 * [BrowseTree] maps a media id (see: [MediaMetadataCompat.METADATA_KEY_MEDIA_ID]) to one (or
 * more) [MediaMetadataCompat] objects, which are children of that media id.
 *
 * For example, given the following conceptual tree:
 * root
 *  +-- Albums
 *  |    +-- Album_A
 *  |    |    +-- Song_1
 *  |    |    +-- Song_2
 *  ...
 *  +-- Artists
 *  ...
 *
 *  Requesting `browseTree["root"]` would return a list that included "Albums", "Artists", and
 *  any other direct children. Taking the media ID of "Albums" ("Albums" in this example),
 *  `browseTree["Albums"]` would return a single item list "Album_A", and, finally,
 *  `browseTree["Album_A"]` would return "Song_1" and "Song_2". Since those are leaf nodes,
 *  requesting `browseTree["Song_1"]` would return null (there aren't any children of it).
 */

class BrowseTree(
    val context: Context,
    musicSource: MusicSource,
    val recentMediaId: String? = null
) {
    // Todo recommendation + recent
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootList = mediaIdToChildren[MEDIA_BROWSABLE_ROOT] ?: mutableListOf()

        val recommendedMetadata = MediaMetadataCompat.Builder().apply {
            id = MEDIA_RECOMMENDED_ROOT
            title = context.getString(R.string.recommended_title)
            albumArtUri = FAKE_ALBUM_ART_URL
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            id = MEDIA_ALBUMS_ROOT
            title = context.getString(R.string.albums_title)
            albumArtUri = FAKE_ALBUM_ART_URL
            flag = FLAG_BROWSABLE
        }.build()

        val artistsMetadata = MediaMetadataCompat.Builder().apply {
            id = MEDIA_ARTISTS_ROOT
            title = context.getString(R.string.artists_title)
            albumArtUri = FAKE_ALBUM_ART_URL
            flag = FLAG_BROWSABLE
        }.build()

        rootList += recommendedMetadata
        rootList += albumsMetadata
        rootList += artistsMetadata
        mediaIdToChildren[MEDIA_BROWSABLE_ROOT] = rootList

        musicSource.forEach { mediaItem ->
            val albumMediaId = mediaItem.album.urlEncoded
            val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(mediaItem)
            albumChildren += mediaItem

            val artistMediaId = mediaItem.artist
            val artistChildren = mediaIdToChildren[artistMediaId] ?: buildArtistsRoot(mediaItem)
            artistChildren += mediaItem

            // If this was recently played, add it to the recent root.
            if (mediaItem.id == recentMediaId) {
                mediaIdToChildren[MEDIA_RECENT_ROOT] = mutableListOf(mediaItem)
            }
        }
    }


    operator fun get(mediaId: String) = mediaIdToChildren[mediaId]

    /**
     * Builds a node, under the root, that represents an album, given
     * a [MediaMetadataCompat] object that's one of the songs on that album,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildAlbumRoot(mediaItem: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.album.urlEncoded
            title = mediaItem.album
            artist = mediaItem.artist
            //albumArt = mediaItem.albumArt
            albumArtUri = mediaItem.albumArtUri.toString()
            flag = FLAG_BROWSABLE
        }.build()

        // Adds this album to the 'Albums' category.
        val rootList = mediaIdToChildren[MEDIA_ALBUMS_ROOT] ?: mutableListOf()
        rootList += albumMetadata
        mediaIdToChildren[MEDIA_ALBUMS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[albumMetadata.id!!] = it
        }
    }

    private fun buildArtistsRoot(mediaItem: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.artist ?: ""
            title = mediaItem.artist // for simplicity
            artist = mediaItem.artist
            albumArt = mediaItem.albumArt
            albumArtUri = mediaItem.albumArtUri.toString()
            flag = FLAG_BROWSABLE
        }.build()

        val rootList = mediaIdToChildren[MEDIA_ARTISTS_ROOT] ?: mutableListOf()
        rootList += artistMetadata
        mediaIdToChildren[MEDIA_ARTISTS_ROOT] = rootList

        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[artistMetadata.id!!] = it
        }
    }
}

