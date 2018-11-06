FROM gradle:4.8.1-jdk8-alpine

USER gradle
WORKDIR /home/gradle

ADD src ./src
ADD gradle ./gradle
ADD gradlew .
ADD gradlew.bat .
ADD build.gradle .
ADD settings.gradle .

RUN ./gradlew build

CMD if [ $initialData ] ; then  ./gradlew bootRun -Pargs="--zonky.marketplace.initialDate=${initialDate}" ; else ./gradlew bootRun ; fi


