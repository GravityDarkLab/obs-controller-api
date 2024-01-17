[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7effd0e67a70432eb40c85bdbabba728)](https://app.codacy.com/gh/GravityDarkLab/obs-controller-api?utm_source=github.com&utm_medium=referral&utm_content=GravityDarkLab/obs-controller-api&utm_campaign=Badge_Grade)
# OBS-Controller API

This is an API for controlling OBS Studio via HTTP requests.
It is written in Java with Spring Boot and uses
the [obs-websocket](https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md#startrecord)
plugin.

![OBS-Controller-API.png](assets/OBS-Controller-API.png)

## Installation

1. Clone this repository.
2. Run `mvn clean install` in the root directory.
3. Run `java -jar obs-controller-api-0.0.1-SNAPSHOT.jar` in the `target` directory.
4. The API is now running on port 8080.
5. You can access Documentation via Swagger UI at `http://localhost:8080/docs.html`.

## Usage

1. Start OBS Studio.
2. Enable the Websocket Server in OBS Studio.
3. Copy the ipAdresse, port and password of the Websocket Server.
4. Send `POST` requests to `http://localhost:8080/obs/connect` with the following required parameters to connect to the
   Websocket Server:
    - `ipAdresse`: The ipAdresse of the Websocket Server.
    - `port`: The port of the Websocket Server.
    - `password`: The password of the Websocket Server.
5. Send a `POST` request to `http://localhost:8080/obs/authenticate` with the following required parameters to
   authenticate to the Websocket Server:
    - `ipAdresse`: The ipAdresse of the Websocket Server.
    - `port`: The port of the Websocket Server.
6. Send a `POST` request to `http://localhost:8080/obs/startRecording` with the following required parameters to start
   recording:
    - `ipAdresse`: The ipAdresse of the Websocket Server.
    - `port`: The port of the Websocket Server.
7. Send a `POST` request to `http://localhost:8080/obs/stopRecording` with the following required parameters to stop
   recording:
    - `ipAdresse`: The ipAdresse of the Websocket Server.
    - `port`: The port of the Websocket Server.
8. Send a `POST` request to `http://localhost:8080/obs/disconnect` with the following required parameters to disconnect
   from the Websocket Server:
    - `ipAdresse`: The ipAdresse of the Websocket Server.
    - `port`: The port of the Websocket Server.

## Available Endpoints

This section lists the available API endpoints for the OBS integration service. Each endpoint is a `POST` request and
requires specific parameters as part of the query string.

### OBS Connection Management

1. **Connect to OBS**
    - `POST /obs/connect`
        - Parameters: `ipAdresse`, `port`, `password`
        - Example: `POST http://localhost:8080/obs/connect?ipAdresse=<ipAdresse>&port=<port>&password=<password>`

2. **Authenticate with OBS**
    - `POST /obs/authenticate`
        - Parameters: `ipAdresse`, `port`
        - Example: `POST http://localhost:8080/obs/authenticate?ipAdresse=<ipAdresse>&port=<port>`

3. **Disconnect from OBS**
    - `POST /obs/disconnect`
        - Parameters: `ipAdresse`, `port`
        - Example: `POST http://localhost:8080/obs/disconnect?ipAdresse=<ipAdresse>&port=<port>`

4. **Reconnect to OBS**
    - `POST /obs/reconnect`
        - Parameters: `ipAdresse`, `port`
        - Example: `POST http://localhost:8080/obs/reconnect?ipAdresse=<ipAdresse>&port=<port>`

### OBS Recording Control

1. **Start Recording**
    - `POST /obs/startRecording`
        - Parameters: `ipAdresse`, `port`
        - Example: `POST http://localhost:8080/obs/startRecording?ipAdresse=<ipAdresse>&port=<port>`

2. **Stop Recording**
    - `POST /obs/stopRecording`
        - Parameters: `ipAdresse`, `port`
        - Example: `POST http://localhost:8080/obs/stopRecording?ipAdresse=<ipAdresse>&port=<port>`

![swagger-doc.png](assets/swagger-doc.png)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
