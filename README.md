# Ukulele
...and his music was electric.

Ukulele is a bot made by the creator and collaborators of FredBoat. The concept is to replicate FredBoat while keeping it simple. The original stack is engineered for serving millions of servers, and is thus too complex to selfhost.

The bot is self-contained and only requires Java 11 to run.

This is currently work-in-progress.

## Features
- Basic player commands (::play, ::list, ::skip)
- Volume command
- Zero-maintenance embedded database

## Host it yourself
- Make a copy of `ukulele.example.yml` and rename it to `ukulele.yml`
- Input the bot token [(guide)](https://discordjs.guide/preparations/setting-up-a-bot-application.html)
- Run `./ukulele` to build and run the application

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

## Contributing
Pull requests are welcome! Look through the issues and/or create one if you have an idea.

Please read [CONTRIBUTING.md](CONTRIBUTING.md)

## Make your own changes (More info soon)
- Change code
- `./gradlew clean build`
