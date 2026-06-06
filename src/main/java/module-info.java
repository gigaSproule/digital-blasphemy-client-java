module com.benjaminsproule.digitalblasphemy.client {
    exports com.benjaminsproule.digitalblasphemy.client;
    exports com.benjaminsproule.digitalblasphemy.client.model;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires static com.github.spotbugs.annotations;
    requires java.net.http;
    requires org.slf4j;
}
