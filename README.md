# Ukulele
...and his music was electric.

Ukulele is a bot made by the creator and collaborators of FredBoat. The concept is to replicate FredBoat while keeping it simple. The original stack is engineered for serving millions of servers, and is thus too complex to selfhost.

The bot is self-contained and only requires Java 11 to run.

This is currently work-in-progress.

## Features
- Basic player commands (::play, ::list, ::skip, ::shuffle)
- Volume command
- Zero-maintenance embedded database

## Host it yourself

### Manual
- Install Java 11
- Make a copy of `ukulele.example.yml` and rename it to `ukulele.yml`
- Input the bot token [(guide)](https://discordjs.guide/preparations/setting-up-a-bot-application.html)
- Run `./ukulele` to build and run the application (Windows users use the .bat files via commandline)

### Using Docker
#### Requirements
- Docker (Engine: 18.06.0+)
- Docker-Compose

#### Running
```shell script
# Create DB directory and own it to 999
mkdir db && chown -R 999 db/

# Copy ukulele config file
cp ukulele.example.yml ukulele.yml
# Open ukulele.yml and make config changes

# Now simply run run docker-compose 
docker-compose up -d
```

To run the container in detached mode simply add `-d` to the arguments of the run command.

### AUR Package ![AUR version](https://img.shields.io/aur/version/ukulele-git)
https://aur.archlinux.org/packages/ukulele-git/

This Arch package provides a systemd service for ukulele, and places the files for ukulele in the correct places according to the [Arch Package Guidelines](https://wiki.archlinux.org/title/Arch_package_guidelines#Directories). This installation method is only relevant if you have an arch-based system.

- Install the package either using an AUR helper (paru, yay, etc), or following the [guide](https://wiki.archlinux.org/title/Arch_User_Repository#Installing_and_upgrading_packages) on the Arch Wiki.
- Edit the config file (`/etc/ukulele/ukulele.yml`) as required.
    - As noted when installing the package, the discord bot token must be set in the config file ([guide](https://discordjs.guide/preparations/setting-up-a-bot-application.html))
- Start/enable the `ukulele.service` as required ([wiki](https://wiki.archlinux.org/title/Systemd#Using_units))

## Contributing
Pull requests are welcome! Look through the issues and/or create one if you have an idea.

Please read [CONTRIBUTING.md](CONTRIBUTING.md)

## Make your own changes (More info soon)
- Change code
- `./gradlew clean build`
