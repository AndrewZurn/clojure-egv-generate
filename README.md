This is kind of a hodge-podge of a template API and then some utilities I decided to create to generate egvs and insert
them into Spanner (or export them to a csv).

# my-api

FIXME

## Usage

### Run the application locally

`lein ring server`

### Run the tests

`lein test`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright ©  FIXME
