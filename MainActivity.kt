package com.iptv.mv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iptv.mv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: M3URepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = M3URepository(this)
        binding.tvHeaderTitle.text = "IPTV MV TESTE"
        binding.tvStatus.text = "Carregando canais..."
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadChannels()
    }

    private fun loadChannels() {
        binding.progressBar.visibility = View.VISIBLE
        repository.loadPlaylist(object : M3URepository.PlaylistCallback {
            override fun onSuccess(channels: List<Channel>, isFromCache: Boolean) {
                binding.progressBar.visibility = View.GONE
                if (isFromCache) Toast.makeText(applicationContext, "Usando cache local.", Toast.LENGTH_SHORT).show()
                binding.tvStatus.text = "Total de canais: ${channels.size}"
                binding.recyclerView.adapter = ChannelAdapter(channels) { channel ->
                    startActivity(Intent(applicationContext, PlayerActivity::class.java).putExtra("CHANNEL_OBJECT", channel))
                }
            }
            override fun onError(error: String) {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = "Erro: $error"
            }
        })
    }
}
