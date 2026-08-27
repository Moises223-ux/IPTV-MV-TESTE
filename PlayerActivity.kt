package com.iptv.mv

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.iptv.mv.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), Player.Listener {
    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var currentChannel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentChannel = intent.getSerializableExtra("CHANNEL_OBJECT") as? Channel
        binding.tvPlayerTitle.text = currentChannel?.name ?: "Player"

        currentChannel?.streamUrl?.let { url ->
            player = ExoPlayer.Builder(this).build().also {
                binding.playerView.player = it
                it.addListener(this)
                it.setMediaItem(MediaItem.fromUri(url))
                it.prepare()
                it.playWhenReady = true
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        binding.tvErrorMessage.text = "Canal: ${currentChannel?.name}\nErro: ${error.localizedMessage}\nURL indisponível/incompatível."
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
