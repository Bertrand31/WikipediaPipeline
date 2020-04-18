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

# Wikipedia Page Views Pipeline

## Statement of purpose



## TODO

- test
- error handling

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
