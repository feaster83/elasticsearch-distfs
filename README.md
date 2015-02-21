# elasticsearch-distfs
Elasticsearch DistFS plugin - Simple distributed filesystem on Elasticsearch

*** This plugin is only written for educational purpose. Do not use it in production! ***

# Build
mvn package

# Install
bin/plugin --u file:///path/to/plugin.zip --i distfs

# Run with Maven

First time or after a mvn clean do ** mvn package **

Run Elasticsearch with the command:

** mvn exec:java ** --> Elasticsearch will run on localhost:9200

# Run/Debug in IntelliJ
Add run configuration:

Main class: org.elasticsearch.bootstrap.Bootstrap

VM Options: -Des.foreground=yes

# Usage
Upload a file to the DistFS:

POST http://localhost:9200/_distfs/&lt;index&gt;/&lt;type&gt;/&lt;id&gt; with as content the FILE.

for example:

POST http://localhost:9200/_distfs/fs/files/image.jpg


To GET a file:

GET http://localhost:9200/_distfs/&lt;index&gt;/&lt;type&gt;/&lt;id&gt;

For example

GET http://localhost:9200/_distfs/fs/files/website1 (returns html for example)

