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
Simply run ukulele as a Docker Container

In the  simplest form just run, this will start ukulele with the default prefix `::`
```shell script
docker run --name ukulele -e CONFIG_TOKEN=PASTE_YOUR_TOKEN ukulele
```

You can customize the prefix
```shell script
docker run --name ukulele -e CONFIG_TOKEN=PASTE_YOUR_TOKEN -e CONFIG_PREFIX='!' ukulele
```

You can set any config defined in ukulele.example.yml as environment variable.

#### Run as Docker container with persistent database
In the examples above ukulele will start and continue to work as long as the container itself is not re-created or deleted.
You will most likely want to persist the database across re-creations & updates.
```shell script
# Create DB directory and own it to 999
mkdir db && chown -R 999 db/

# Now run ukulele with volume mounting
docker run --name ukulele -e CONFIG_TOKEN='PUT_YOUR_TOKEN_HERE' -e CONFIG_DATABASE='/opt/ukulele/db/database' -v $PWD/db:/opt/ukulele/db ukulele
```

To run the container in detached mode simply add `-d` to the arguments of the run command.

## Contributing
Pull requests are welcome! Look through the issues and/or create one if you have an idea.

Please read [CONTRIBUTING.md](CONTRIBUTING.md)

## Make your own changes (More info soon)
- Change code
- `./gradlew clean build`
