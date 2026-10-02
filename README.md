# Cockroach

[![Build](https://github.com/MrBuggI/CockMod/actions/workflows/build.yml/badge.svg)](https://github.com/MrBuggI/CockMod/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/MrBuggI/CockMod)](https://github.com/MrBuggI/CockMod/releases/latest)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A)
![Loader](https://img.shields.io/badge/loader-Fabric-DBD0B4)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Мод для Minecraft, добавляющий таракана, который живёт прямо в интерфейсе игры.

> **English:** a client-side Fabric 1.20.1 mod: a cockroach lives in the player inventory screen, visits containers that hold rotten flesh, flees from the mouse cursor and hunts down diamonds to eat them. The diamond is removed by the server, so the mod is needed on both sides. Localized in English, Russian and Ukrainian.

## Возможности

Таракан это **клиентское** визуальное существо. Он:

- всегда живёт в окне инвентаря игрока (кроме креативного) и не выходит за границы панели;
- приходит в сундук или другой контейнер, если там лежит **гнилая плоть**;
- сам блуждает по экрану и **убегает от курсора**, поймать его мышкой нельзя;
- если в открытом окне есть **алмаз**, выслеживает его и съедает.

Рисуется таракан только на клиенте, но алмаз удаляет сервер, поэтому мод должен стоять и на сервере. На сервере без мода таракан бегает, но алмазы не ест.

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
| Версия мода      | 1.0.1                         |

## Архитектура

Пакет `io.github.mrbuggi.cockroach`. Один миксин-аксессор, один сетевой пакет.

| Файл | Роль |
|---|---|
| `CockroachMod` | точка входа, регистрация серверного приёмника пакета |
| `CockroachNetworking` | пакет `eat_diamond`: клиент называет слот, сервер проверяет его и убирает один алмаз |
| `client/CockroachClient` | подписка на отрисовку экранов контейнеров, поиск ближайшего алмаза |
| `client/Cockroach` | движение и отрисовка: блуждание, бегство от курсора, охота |
| `mixin/client/HandledScreenAccessor` | доступ к положению и размеру панели экрана |

Миксин здесь только аксессор: он читает четыре поля `HandledScreen` и не меняет код игры. Сервер не доверяет клиенту: номер слота проверяется на границы, а алмаз убирается, только если он действительно лежит в этом слоте открытого игроком окна.

## Сборка из исходников

Нужен JDK 17 или новее.

```bash
./gradlew build       # готовый .jar появится в build/libs/
./gradlew runClient   # запустить клиент Minecraft с модом
```

Каждый push проверяется сборкой в GitHub Actions, а при публикации тега `v*` собранный `.jar` автоматически прикладывается к релизу.

## Лицензия

[MIT](LICENSE).
