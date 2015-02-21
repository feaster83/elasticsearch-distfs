# elasticsearch-distfs
Elasticsearch DistFS plugin - Simple distributed filesystem on Elasticsearch

*** This plugin is only written for educational purpose. Do not use it in production! ***

Upload a file to the DistFS:

POST http://localhost:9200/_distfs/<index>/<type>/<id> with as content the FILE.

for example:

POST http://localhost:9200/_distfs/fs/files/image.jpg


To GET a file:

GET http://localhost:9200/_distfs/<index>/<type>/<id> 

For example

GET http://localhost:9200/_distfs/fs/files/website1 (returns html for example)

