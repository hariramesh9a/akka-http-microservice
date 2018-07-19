# Akka HTTP microservice with CORS handler example

This project demonstrates the [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala.html) library and Scala to write a simple REST (micro)service. The project shows the following tasks that are typical for most Akka HTTP-based projects:

* starting standalone HTTP server,
* handling file-based configuration,
* logging,
* routing,
* deconstructing requests,
* unmarshalling JSON entities to Scala's case classes,
* marshaling Scala's case classes to JSON responses,
* error handling,
* issuing requests to external services,
* testing with mocking of external services.
* CORS Handler

The service in the template provides two REST endpoints - one which gives GeoIP info for given IP and another for calculating geographical distance between given pair of IPs. The project uses the service [ip-api](http://ip-api.com/) which offers JSON IP and GeoIP REST API for free for non-commercial use.

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Usage

Start services with sbt:

```
$ sbt
> ~re-start
```

With the service up, you can start sending HTTP requests:

```
$ curl http://localhost:9000/
[{
  "releaseDate": "March 19, 2016",
  "description": "Leaf rake with 48-inch wooden handle.",
  "price": 19.95,
  "starRating": 3.2,
  "productName": "Leaf Rake",
  "productId": 1.0,
  "imageUrl": "https://openclipart.org/image/300px/svg_to_png/26215/Anonymous_Leaf_Rake.png",
  "productCode": "GDN-0011"
}]
```

## Author e

If you have any questions regarding this project contact:

Hari Ramesh - haryramesh@hotmail.com.


# akka-http-microservice
