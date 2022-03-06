FROM gcc:latest as GCCBuilder


#rnaview
RUN wget -qO RNAVIEW.tar.gz "http://ndbserver.rutgers.edu/ndbmodule/services/download/RNAVIEW.tar.gz"
RUN tar -xzvf RNAVIEW.tar.gz
WORKDIR RNAVIEW/
RUN make && make clean

WORKDIR /


FROM tiangolo/uwsgi-nginx-flask:python3.8 as FlackBuilder

WORKDIR /rac-service
COPY ./requirements.txt /rac-service/requirements.txt
RUN pip install --no-cache-dir -r /rac-service/requirements.txt

MAINTAINER Fabrice Jossinet (fjossinet@gmail.com)
RUN apt-get update && apt-get install -y git wget build-essential default-jdk maven

WORKDIR /
COPY --from=GCCBuilder RNAVIEW .

ENV RNAVIEW /RNAVIEW
ENV PATH /RNAVIEW/bin:$PATH
#rnartistcore
RUN git clone https://github.com/fjossinet/RNArtistCore.git
WORKDIR /RNArtistCore
RUN mvn clean package


RUN ln -s $(ls /RNArtistCore/target/rnartistcore*with-dependencies.jar) /rac-service/rnartistcore.jar

WORKDIR /rac-service
RUN mkdir /rac-service/resources
RUN mkdir /rac-service/resources/rnartis_files
RUN mkdir /rac-service/resources/kts_templates
COPY ./app/src/main/python /rac-service
COPY ./app/resources/kts_templates /rac-service/resources/kts_templates


CMD [ "python", "main.py" ]