# RNArtistCore

RNArtistCore uses RNArtist to create images of colored molecules 
### Build the docker locally
~~~
docker build --no-cache -t rnartist-core-microservice:1.0.0
~~~

### Run the docker

~~~
docker run -p5000:5000 rnartist-core
~~~
If you are with gRNArtist and gRNArtistS3Manager well you will need to connect them to the same network.
To create the network and then connect the docker to it:

~~~
docker network create g-rnartist-net
docker run -p5000:5000 --net g-rnartist-net rnartist-core
~~~

# API call

##exec-rnartsitcore-template - POST
the response should be the rna file in the selected format 
the parameters for that call should be in json format
~~~
{
    "molecule": "GATTTCCTCAGCGGTCACAGGUUUUAGAGCUAGAAAUAGCAAGUUAAAAUAAGGCUAGUCCGUUAUCAACUUGAAAAAGUGGCACCGAGUCGGUGC",
    "brackets": ".........(((((.(...(((((((..((((....)))).........))))))).).)))))....((((....)))).((((((...))))))",
    "format": "svg"
}
~~~
~~~
http://localhost:5002/store-art
~~~