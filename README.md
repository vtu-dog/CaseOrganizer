# CaseOrganizer
Case organizer for a law office

- [Technologies](#technologies)
- [Features](#features)
- [Running the project](#running-the-project)
- [How to use](#how-to-use)
- [Additional info](#additional-info)
- [Contributing](#contributing)

## Technologies
- JAXB
- JavaFX (+ Gluon Scene Builder)
- Apache Commons Configuration
- Sauron Software ftp4j

## Features
- persistent data storage utilizing FTP protocol
- beautiful, accessible UI
- smart error handling

## Running the project
In order to run the program, download the latest binary release. If you'd rather build it yourself, clone the repository and type `$ gradle run`. Alternatively, you can create your own standalone JAR of the application by running `$ gradle jar` and retrieving it from `/build/libs`.

## How to use
![Login](./screenshots/login.png)

First you'll be presented with a login window - enter your FTP server credentials and click the Login button to proceed to the main screen.

![Example](./screenshots/example.png)

From here you are free to search for cases, add new ones, delete cases, link them to one another, inspect the links, edit case data and upload / download / delete relevant case files.

![Error](./screenshots/error.png)

Should the program enter an unrecoverable state (e.g. internet access was lost), the user will be presented with a developer-friendly error message, then further program execution will be halted to prevent data loss.

## Additional info
The project was tested using Java 8u221 on macOS 10.15 Catalina and Manjaro Linux 18.1.2 (GNOME flavour).

## Contributing
- Dawid Gałka
- Wojciech Jarząbek
