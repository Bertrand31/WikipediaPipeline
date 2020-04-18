# Wikipedia pageview data pipeline

```
Wikip  iaWi  pedia ikiped  Wikipe   Wik pediaW kipediaWik  ediaWikiped   ikipedi     pediaW       aWi
ipedi  ikip  iaWik pediaW  ipedi    ipe iaWiki  diaWikiped aWikipediaWi  pediaWiki   iaWiki      ikipe
 iaWi  pedi  ikip   aWik    iaW    ed    kipe   Wiki    aWikipe    ikip  iaWikipedi   kipe       pedia
  kip   aWi  ped     ipe    iki   ia      dia   ipediaWikip dia          ikip   aWik   dia      diaWik
  edi  ikip  ia      edi    pediaWi       aWi   diaWikiped  Wikipe        edi   kipe   aWi      Wik ped
   Wi  pediaWik      Wik    iaWikip       ipe   Wikipedia   ipediaW       aWi    dia   ipe     kipediaWi
   ipediaWikipe      ipe    ikipedi       dia   ipe         diaWiki       kip   aWik   dia     ediaWikip
    iaWikipedia      dia    ped  Wik      Wik   dia         Wik          pedi  ikipe   Wik    iaWi   edia
     kip  iaWi      aWik    iaW  ipe     kipe   Wik         iped    kip diaWikipedia  kipe    iki     Wik
      d   ikip     ikiped  Wikip diaWikipediaW kipedi      ediaWik pedi WikipediaWi  pediaW kipedi   kipedi
      Wi   ed      pediaWikipedi WikipediaWikipediaWi      aWikipediaWi ipediaWik    iaWikipediaWi   ediaWi
```

## Statement of purpose

The goal is to build an application that will compute the top 25 pages on Wikipedia for each of the
Wikipedia sub-domains, for the requested hour(s) of a day.

## TODO

- integration, E2E testing

- error handling: right now, a "happy path" is assumed in many places. In a real-life setting, we
should be using error handling monads (like Try or Either) every time there's IO involved. And for
easier handling, we'd by using a [TryT](https://github.com/Bertrand31/TryT-monad-transformer) or
[EitherT](https://typelevel.org/cats/datatypes/eithert.html) monads transformer.

- logging should be added, for both successful tasks and failures.

## Miscellaneous

You'll notice that I'm using the `scala-newtype` library to wrap the WikiStat type. It provides an
equivalent of Haskell's `newtype` in Scala. The idea of a `newtype` is to wrap a type into another,
more specific type, so that we can enforce more type safety throughout the codebase. We could also
achieve that using a regular case class, however a newtype incurs no runtime overhead at all,
because it is removed altogether at compile time, and replaced by the underlying type: thus, we get
the best of both worlds. Type safety at no runtime cost.

## Running the app

The app takes the day and hours arguments as command-line arguments.
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
