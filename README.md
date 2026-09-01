# Oneblock-Skript-Core

Native Java replacements for a pile of Skript scripts that were dragging down main thread performance on my Oneblock server. Each feature is small, config driven and toggleable.

Features:

- GG waves in chat, restyling the message and paying out rewards on a cooldown
- Drop locking
- Island resize blocking in configurable worlds
- Scheduled broadcasts
- Weekly reward and booster claims driven by permission groups
- A stack of misc commands the server needed (apply, votes, pinata, next reboot and so on)

Built against Paper using the Cloud command framework, Adventure components and PlaceholderAPI.
