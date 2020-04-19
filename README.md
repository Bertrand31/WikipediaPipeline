# Wikipedia pageview data pipeline

```
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0k0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWd. .xWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWO' 'c0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNd..dXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMMW0:.;OWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM0c;;c0WMMMMMMMMNx..lXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNx'    .cd0NMMMMKc.,kNWWWWWWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0o,.;okkkdc'.,cdkd'.cKNx;,,,,,;xMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXk:.'lONMMMMMWN0d:.   cNMNc 'dxd, lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOc,..;xXWMMMMMMMMMMMW0c;c0MMNc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWX0xl;.  .oNMMMMMMMMMMMMMMMMMWWMMMNc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM0c;;;'',:okOk0WMMWKkxxxxxxxKWMMMMMMMMMNc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWx. .:kKNMMMMMMMMMWl .,,,,. lWMMMMMMMMMNc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0k0NMMMMMMMMMMMMWl cXWWXc lWWWWWWWWWWNc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWl cNMMNc lNx,,,,,,,xXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxxxxxxxKNl cNMMNc lXl ,dxd' cXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNl .,,,. lXl cNMMNc lXl lWMNc cXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKkxxxxxxKNc :XWXc cXl cNMMNc lXl lWMNc cXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMl .,,,. cXc cNMWl cXl cNMMNc lXl lWMNc cXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMl cXWXc cXc cNMWl cXl cNMMNc lXc lWMNc cXc cNMWl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMc lNMNc cXc cNMWl cXl cNMMNc lXc lWMNc cXc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMc lNMNc cXc cNMWl cXl cNMMNc lXc lWMNc cXc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMl lNMNc cXc cNMWl cXl cNMMNc lXc lWMNc cXc cNMNl lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMl ,dxd, cXc 'dxd, lXl ,dxxd, lXc ,dxd' cXc 'dxd, lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMl       cXc       lXl        lXl       cXc       lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
```

## Table of contents

- [Statement of purpose](#statement-of-purpose)
- [Technical solutions](#technical-solutions)
- [Miscellaneous](#miscellaneous)
- [Future developments](#future-developments)
- [Running the service](#running-the-service)
- [Packaging for production](#packaging-for-production)

## Statement of purpose

The goal is to build an application that will compute the top 25 pages on Wikipedia for each of the
Wikipedia sub-domains, for the requested hour(s) of a day.

## Technical solutions

For our sample use-case, we're downloading the hourly data over HTTP, in the shape of GZipped CSV
files, writing them in a temporary work location, and then deleting them after consumption.

We're writing the output data to a CSV file. Both the temporary work directory and the output
directory are [configurable through environment variables](./src/main/scala/wikipipeline/Types/Config.scala).

For the blacklist, I've decided to use a Bloom Filter. The reason why and the alternatives are
explained in great lengths [along the code itself](./src/main/scala/wikipipeline/BlacklistHandler.scala#L8).

Lastly, I chose to use Cats extensively in order to write code as functional, safe and declarative
as possible.

## Miscellaneous

You'll notice that I'm using the `scala-newtype` library to wrap the WikiStat type. It provides an
equivalent of Haskell's `newtype` in Scala. The idea of a `newtype` is to wrap a type into another,
more specific type, so that we can enforce more type safety throughout the codebase. We could also
achieve that using a regular case class, however a newtype incurs no runtime overhead at all,
because it is removed altogether at compile time, and replaced by the underlying type: thus, we get
the best of both worlds. Type safety at no runtime cost.

## Future developments

- testing: at the moment, only unit tests have been written. Before going to production, we'd have
to spend time writing integration and E2E testing, to also test the "impure" aspects of this
serivce.

- streaming of the input data: right now, the data dump from wikipedia is being download to the
disk, processed, then deleted. Ideally, we'd somehow stream, uncompress and process it directly
in one go.

- error handling: right now, a "happy path" is assumed in many places. In a real-life setting, we
should be using error handling monads (like Try or Either) every time there's I/O involved. And for
easier handling, we'd by using a [TryT](https://github.com/Bertrand31/TryT-monad-transformer) or
[EitherT](https://typelevel.org/cats/datatypes/eithert.html) monads transformer.

- logging: right now, this service is mute. Before deploying it to production, proper logging should
be added, for both successful tasks and failures.

- parallelism: even though I've encountered HTTP 503 errors when trying to make multiple queries
to wikimedia at the same time, the task at hand is inherently parallelisable. In the future, it is
definitely something that should be looked into. We could even envision mutliple machines working
in parallel, each processing multiple "hour chunks" and writing them to a single HDFS cluster.

## Running the service

The service takes the day and hours arguments as command-line arguments.
If it is not given any arguments, it will use the current date and hour as a default argument, and
substract 24 hours to it.

The format for the arguments is as following: `2019-03-12T12:00`.
And one can provide as many as needed, separated by spaces.

Without having to package the application, we can use SBT to run a sample use-case. For example:
```
sbt run 2019-03-12T12:00 2019-03-12T13:00
```

## Packaging for production

First, clone the WikipediaPipeline repo to a directory of your choice and `cd` into it.
Then, in order to generate a binary, run the following:
```
sbt universal:packageBin
```
It will generate a zip here:
```
./target/universal/wikipediapageviewpipeline-%VERSION_NUMBER%.zip
```
This zip can now be shipped on the server. Once it is unzipped, this service can be started from
inside the resulting folder with:
```
./bin/wikipediapageviewpipeline
```
