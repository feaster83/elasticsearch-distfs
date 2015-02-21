# elasticsearch-distfs
Elasticsearch DistFS plugin - Simple distributed filesystem on Elasticsearch

***This plugin is only written for educational purpose. Do not use it in production!***

## Project build status
[![wercker status](https://app.wercker.com/status/1d899d8dce7aae8c7887d2d38af45830/m/master "wercker status")](https://app.wercker.com/project/bykey/1d899d8dce7aae8c7887d2d38af45830)

## Build
**`mvn package`**

## Install
Run with plugin executable found in the Elasticsearch bin directory:

`
./bin/plugin --u file:///path/to/plugin.zip --i distfs
`
## Run with Maven

First time or after a **`mvn clean`** do **`mvn package`**

Run Elasticsearch with the command:

**`mvn exec:java`**  --> Elasticsearch will run on localhost:9200

## Run/Debug in IntelliJ
Add run configuration:

Main class: org.elasticsearch.bootstrap.Bootstrap

VM Options: -Des.foreground=yes

## Usage
Upload a file to the DistFS:
```
POST http://localhost:9200/_distfs/<index>/<type>/<id>` (with as content the FILE)
```

for example:
```
POST http://localhost:9200/_distfs/fs/files/image.jpg
```
Response returns permalink-id:
```
HTTP/1.1 202 Accepted
Content-Type: text/plain; charset=UTF-8
Content-Length: 36

ad7ed078-7071-45c7-80c4-cab16849153c
```

To GET a file:
```
GET http://localhost:9200/_distfs/<index>/<type>/<id>
```
*or*

```
GET http://localhost:9200/_distfs/permalink/<permalink-id>
```


For example
```
GET http://localhost:9200/_distfs/fs/files/website1
```

