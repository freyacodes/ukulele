package dev.arbjerg.ukulele.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

var AudioTrack.meta: TrackMeta
    get() = this.userData as TrackMeta
    set(data) { this.userData = data }

class TrackMeta
