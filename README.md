# Cockroach

[![Build](https://github.com/MrBuggI/CockMod/actions/workflows/build.yml/badge.svg)](https://github.com/MrBuggI/CockMod/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/MrBuggI/CockMod)](https://github.com/MrBuggI/CockMod/releases/latest)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A)
![Loader](https://img.shields.io/badge/loader-Fabric-DBD0B4)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Мод для Minecraft, добавляющий таракана, который живёт прямо в интерфейсе игры.

> **English:** a client-side Fabric 1.20.1 mod: a cockroach runs around inside inventory and container screens, flees from the mouse cursor and hunts down diamonds to eat them. In multiplayer the diamond removal is confirmed by the server. Localized in English, Russian and Ukrainian.

## Возможности

Таракан это **клиентское** визуальное существо. Он:

- бегает внутри окон инвентаря, сундуков и других контейнеров и не выходит за границы панели;
- сам блуждает по экрану и **убегает от курсора**, поймать его мышкой нельзя;
- если в открытом инвентаре есть **алмаз**, выслеживает его и съедает (в мультиплеере удаление алмаза подтверждается на сервере).

Локализация: английский (`en_us`), русский (`ru_ru`) и украинский (`uk_ua`).

## Установка

1. Установите [Fabric Loader](https://fabricmc.net/use/) для Minecraft 1.20.1.
2. Скачайте `.jar` из раздела [Releases](https://github.com/MrBuggI/CockMod/releases/latest).
3. Положите его вместе с [Fabric API](https://modrinth.com/mod/fabric-api) в папку `mods/`.
4. Запустите игру.

## Технические данные

| Параметр         | Значение                      |
|------------------|-------------------------------|
| Версия Minecraft | 1.20.1                        |
| Загрузчик        | Fabric (Fabric Loader 0.18.4) |
| Fabric API       | 0.88.1+1.20.1                 |
| Java             | 17+                           |
| Версия мода      | 1.0.0                         |

## Сборка из исходников

Нужен JDK 17 или новее.

```bash
./gradlew build       # готовый .jar появится в build/libs/
./gradlew runClient   # запустить клиент Minecraft с модом
```

Каждый push проверяется сборкой в GitHub Actions, а при публикации тега `v*` собранный `.jar` автоматически прикладывается к релизу.

## Лицензия

[MIT](LICENSE).
