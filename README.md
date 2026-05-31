# Ukulele
...and his music was electric.

Ukulele is a bot made by the creator and collaborators of FredBoat. The concept is to replicate FredBoat while keeping it simple. The original stack is engineered for serving millions of servers, and is thus too complex to selfhost.

The bot is self-contained and only requires Java 25 to run. This bot uses some of the underlying technology behind Lavalink without actually requiring you to host Lavalink.

> [!WARNING]
> This bot was newly revived on May 31st 2026 after being outdated for a couple of years.
> Testing has been minimal and part of it might be broken. 

## Features
- Basic player commands (::play, ::list, ::skip, ::shuffle)
- Volume command
- Zero-maintenance embedded database

## Host it yourself

### Manual
- Install Java 25
- Make a copy of `ukulele.example.yml` and rename it to `ukulele.yml`
- Input the bot token [(guide)](https://discordjs.guide/preparations/setting-up-a-bot-application.html)
- Make sure to enable 'Message Content Intent' in your bot settings on Discord
- Run `./ukulele` to build and run the application (Windows users use the .bat files via commandline)

> [!NOTE]
> If you get an error such as `IllegalArgumentException: Cannot open library:` mentioning jdave when trying to play music,
> you might be running on an old or unsupported system. Try using the Docker image instead.

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
docker-compose up
```

To run the container in detached mode simply add `-d` to the arguments of the run command.

Run `docker-compose pull && docker-compose up -d` to update the container.

## Contributing
Pull requests are welcome! Look through the issues and/or create one if you have an idea.

Please read [CONTRIBUTING.md](CONTRIBUTING.md)
