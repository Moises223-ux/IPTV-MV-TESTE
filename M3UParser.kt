package com.iptv.mv

object M3UParser {
    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var currentName = ""
        var currentLogo = ""
        var currentGroup = "Geral"
        var currentTvgId = ""

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith("#EXTINF:")) {
                currentName = extractAttribute(trimmed, "tvg-name") ?: extractChannelName(trimmed)
                currentLogo = extractAttribute(trimmed, "tvg-logo") ?: ""
                currentGroup = extractAttribute(trimmed, "group-title") ?: "Geral"
                currentTvgId = extractAttribute(trimmed, "tvg-id") ?: ""
            } else if (!trimmed.startsWith("#")) {
                if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) {
                    val finalName = if (currentName.isNotEmpty()) currentName else "Canal Sem Nome"
                    channels.add(Channel(finalName, trimmed, currentLogo, currentGroup, currentTvgId))
                }
                currentName = ""; currentLogo = ""; currentGroup = "Geral"; currentTvgId = ""
            }
        }
        return channels
    }

    private fun extractAttribute(line: String, attribute: String): String? {
        val pattern = Regex("""$attribute="([^"]*)"""")
        return pattern.find(line)?.groups?.get(1)?.value
    }

    private fun extractChannelName(line: String): String {
        val lastComma = line.lastIndexOf(',')
        return if (lastComma != -1 && lastComma < line.length - 1) line.substring(lastComma + 1).trim() else "Canal IPTV"
    }
}
