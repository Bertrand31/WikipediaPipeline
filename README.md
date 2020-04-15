# Airport Matching

```
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM0c;kMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMO;.dWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMk. lWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMx. :XMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWo  ,KMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKx'  .cONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKkdoooooo:.       ,loooooox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0dlllc.             ;llldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMX0OOOOOOOOOOOOOOOOOOOOo.             ;kOOOOOOOOOOOOOOOOOOO0MMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMXkl;......                                       .....',:oOMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMNK0OOkxxdo,     .''.              .',.     'dxkO00KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWd.    :KN0;            .dNNo     cNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXo'..c0MMMXl.         ,kWMMXo'..c0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXXWMMMMMWKxc:;,;:oONMMMMMWNXXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOkkkkkk0WWNKkkkkkOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOc.       lNWNo.     .cONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKo,         'OMMM0,        ,oKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXk:.           lWMMMWd.         .:kXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0l'             ,0MMMMMK;            'l0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMWKx;.              .dWMMMMMWx.             .;xXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMNOc.                 ;KMMMMMMMX:                .cONMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMWKo,                   .xWMMMMMMMMk.                  ,oKWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMx'                     ,KMMMMMMMMMX;                    'xNMMMMMMMMMMMMMMMMMMMMMMMMM
```

## Table of contents

- [Statement of purpose](#statement-of-purpose)
- [Problem summary](#problem-summary)
- [Performance](#performance)
- [Miscellaneous](#miscellaneous)
- [Future developments](#future-developments)
- [Packaging for production](#packaging-for-production)
- [Licensing](#licensing)

## Statement of purpose

The goal of this project is to build a small service consuming rows representing users and their
location, and matching each of them with the nearest airport.

For the purpose of this demonstration, we will be consuming rows from a CSV file, and printing them
to `stdout`. In a real-world setting, this service would likely consume a stream (Kafka, Kinesis,
RabbitMQ, etc.) and write its results to a storage medium such as a database (for example Cassandra)
or a filesystem.

This exercise only focuses on the part "in-between", where we match every user with an airport.
However, the codebase and the abstractions were crafted with a real-world situation in mind ; hence
why the "Bridge" abstraction was created. [Read more about bridges here](src/main/scala/airportmatching/Bridges/README.md).

## Problem summary

Our task can be summed up as such: we need to find the nearest neigbour of a two-dimensional point (with a little twist, more on that later).

It is important to note that our dataset (the world's airports) is not only small, but likely to
grow only marginally in the forseable future. This means that we can confidently fit it in memory,
and trade memory use to get as much speed as we can for our nearest neighbour queries.

The solution that was picked is to use a KD-Tree, but simplified and now tightly coupled to our
use-case: it only supports the types we need and operates with two-dimensional points. Thus, we
get maximum performance and [a rather simple implementation](src/main/scala/airportmatching/Artemis.scala).
It was named after [Artemis](https://upload.wikimedia.org/wikipedia/commons/2/2a/Diane_de_Versailles_Leochares_2.jpg),
the Greek goddess of the hunt, in reference to the task at hand.

So far so good, however here we are not talking about simple 2D points ; we are talking about GPS
coordinates, which are coordinates on a sphere (the Earth). Hence, this custom implementation does
not simple measure the distance between two two-dimensional points: it uses the Haversine formula
and the Earth radius to get a more accurate measurement of the distance between a user and the
world's airports.

The resulting data structure provides `Θ(n log² n)` time complexity for construction (in our case,
only performed once upon boot), and nearest-neighbour search in `Θ(log n)`.

## Performance

On a warm JVM, finding the nearest aiport takes **649 nanoseconds**.

Processing a **million record** takes **710 milliseconds** (which is roughly a million times the time it
takes to query the KDTree, plus some overhead likely due to secondary operations like formatting of
the output).

Note: If you run the service with its example use-case right now, you'll notice it takes much longer
than that to finish. This is due to the time taken to build the KD-Tree, and to the I/O operations
(reading the CSV and writing to `stdout`).

## Miscellaneous

You'll notice that I'm using the `scala-newtype` library to wrap some simple types. It provides an
equivalent of Haskell's `newtype` in Scala. The idea of a `newtype` is to wrap a type into another,
more specific type, so that we can enforce more type safety throughout the codebase. We could also
achieve that using a regular case class, however a newtype incurs no runtime overhead at all,
because it is removed altogether at compile time, and replaced by the underlying type: thus, we get
the best of both worlds. Type safety at no runtime cost.

Also, the sample implementation of the destination bridge uses batching: it accumulates rows until
a predefined threshold before flushing it to `stdout`. This is to exemplify a behaviour we might
need in production for better writing performance.

## Future developments

In order to make this service actually useful and ready for production, we would first have to
implement some actual bridges instead of the sample ones I've provided.

Once that is done, we'd be able to also get rid of most of the code inside of [Main.scala](src/main/scala/airportmatching/Main.scala). The code there was provided as an example, to manually pump data out of a CSV and then print the result progressively to `stdout`.

In a real-life setting, the main function would likely instantiate a source bridge that would
produce a stream, which would be piped to the destination bridge.

With real bridges in place, we would also be able to write proper integration and end-to-end tests,
which are lacking now, as I've chosen to write tests only for the parts with *actual code*, not
sample implementations.

Down the line, we may also have to investigate parallelism, to be able to handle bigger amounts of
input data. But for now, considering the volumes of data we're dealing with, it would probably cause
more harm than good.

## Packaging for production

First, clone the AirportMatching repo to a directory of your choice and `cd` into it.
Then, in order to generate a binary, run the following:
```
sbt universal:packageBin
```
It will generate a zip here:
```
./target/universal/airportmatching-%VERSION_NUMBER%.zip
```
This zip can now be shipped on the server. Once it is unzipped, this service can be started from
inside the resulting folder with:
```
./bin/airportmatching
```

## Licensing

- `user-geo-sample.csv.gz`

The longitude, latitude data in this sample was taken from a data-set provided by Maxmind inc.
This work is licensed under the [Creative CommonsAttribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/).

This database incorporates [GeoNames](http://www.geonames.org) geographical data, which is made
available under the [Creative Commons Attribution 3.0 License](http://www.creativecommons.org/licenses/by/3.0/us/).

- `optd-airports-sample.csv.gz`

Licensed under Creative Commons. For more information check [here](https://github.com/opentraveldata/optd/blob/trunk/LICENSE).
